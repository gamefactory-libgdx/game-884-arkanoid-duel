package com.asocity.arkanoidduel000884.screens;

import com.asocity.arkanoidduel000884.Constants;
import com.asocity.arkanoidduel000884.MainGame;
import com.asocity.arkanoidduel000884.UiFactory;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public class LeaderboardScreen implements Screen {

    private static final String BG        = "backgrounds/menu/Bright_background.png";
    private static final String PREF_KEY  = "lb_scores";   // comma-separated, top-10, desc
    private static final int    MAX_ENTRIES = Constants.LEADERBOARD_MAX_ENTRIES;

    private final MainGame game;
    private final Stage    stage;
    private final Viewport viewport;

    // ── Static API ───────────────────────────────────────────────────────────

    /** Insert score into the top-10 list stored in SharedPreferences. */
    public static void addScore(int score) {
        Preferences prefs  = Gdx.app.getPreferences(Constants.PREFS_NAME);
        int[]       scores = loadScores(prefs);

        // Find insertion point (descending)
        int insertAt = scores.length; // default: beyond end (won't insert)
        for (int i = 0; i < scores.length; i++) {
            if (score > scores[i]) { insertAt = i; break; }
        }
        if (insertAt >= MAX_ENTRIES) return; // doesn't make top-10

        // Shift down and insert
        int newLen = Math.min(scores.length + 1, MAX_ENTRIES);
        int[] updated = new int[newLen];
        for (int i = 0; i < insertAt && i < newLen; i++)       updated[i] = scores[i];
        if (insertAt < newLen)                                  updated[insertAt] = score;
        for (int i = insertAt + 1; i < newLen; i++)            updated[i] = scores[i - 1];

        saveScores(prefs, updated);
    }

    private static int[] loadScores(Preferences prefs) {
        String raw = prefs.getString(PREF_KEY, "");
        if (raw.isEmpty()) return new int[0];
        String[] parts = raw.split(",");
        int[] arr = new int[parts.length];
        for (int i = 0; i < parts.length; i++) {
            try { arr[i] = Integer.parseInt(parts[i].trim()); }
            catch (NumberFormatException ex) { arr[i] = 0; }
        }
        return arr;
    }

    private static void saveScores(Preferences prefs, int[] scores) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < scores.length; i++) {
            if (i > 0) sb.append(',');
            sb.append(scores[i]);
        }
        prefs.putString(PREF_KEY, sb.toString());
        prefs.flush();
    }

    // ── Constructor ──────────────────────────────────────────────────────────

    public LeaderboardScreen(MainGame game) {
        this.game = game;

        if (!game.manager.isLoaded(BG)) {
            game.manager.load(BG, Texture.class);
            game.manager.finishLoading();
        }

        OrthographicCamera camera = new OrthographicCamera();
        viewport = new StretchViewport(Constants.WORLD_WIDTH, Constants.WORLD_HEIGHT, camera);
        stage    = new Stage(viewport, game.batch);

        buildUi();
        registerInput();
        game.playMusic("sounds/music/music_menu.ogg");
    }

    private void buildUi() {
        TextButton.TextButtonStyle rectStyle =
                UiFactory.makeRectStyle(game.manager, game.fontBody);

        // ── Title ──
        Label.LabelStyle titleStyle = new Label.LabelStyle(game.fontTitle, Color.WHITE);
        Label titleLabel = new Label("TOP 10", titleStyle);
        titleLabel.setSize(380f, 60f);
        titleLabel.setPosition((Constants.WORLD_WIDTH - 380f) / 2f, 760f);
        titleLabel.setAlignment(Align.center);
        stage.addActor(titleLabel);

        // ── Score rows ──
        int[] scores = loadScores(Gdx.app.getPreferences(Constants.PREFS_NAME));
        Label.LabelStyle rowStyle  = new Label.LabelStyle(game.fontBody,  Color.WHITE);
        Label.LabelStyle rankStyle = new Label.LabelStyle(game.fontSmall,
                new Color(1f, 0.533f, 0f, 1f)); // primary #FF8800

        float startY  = 680f;
        float rowH    = 52f;
        float rankX   = 50f;
        float scoreX  = 200f;

        for (int i = 0; i < MAX_ENTRIES; i++) {
            float y = startY - i * rowH;

            Label rankLabel = new Label("#" + (i + 1), rankStyle);
            rankLabel.setPosition(rankX, y);
            stage.addActor(rankLabel);

            String val = (i < scores.length && scores[i] > 0)
                    ? String.valueOf(scores[i]) : "---";
            Label scoreLabel = new Label(val, rowStyle);
            scoreLabel.setPosition(scoreX, y);
            stage.addActor(scoreLabel);
        }

        // ── Main Menu button ──
        TextButton menuBtn = UiFactory.makeButton("MAIN MENU", rectStyle, 280f, 52f);
        menuBtn.setPosition((Constants.WORLD_WIDTH - 280f) / 2f, 60f);
        menuBtn.addListener(new ChangeListener() {
            @Override public void changed(ChangeEvent e, Actor a) {
                game.playSound("sounds/sfx/sfx_button_back.ogg");
                game.setScreen(new MainMenuScreen(game));
            }
        });
        stage.addActor(menuBtn);
    }

    private void registerInput() {
        Gdx.input.setInputProcessor(new InputMultiplexer(stage, new InputAdapter() {
            @Override public boolean keyDown(int k) {
                if (k == Input.Keys.BACK) {
                    game.playSound("sounds/sfx/sfx_button_back.ogg");
                    game.setScreen(new MainMenuScreen(game));
                    return true;
                }
                return false;
            }
        }));
    }

    @Override public void show() {
        registerInput();
        game.playMusic("sounds/music/music_menu.ogg");
    }

    @Override public void render(float delta) {
        Gdx.gl.glClearColor(0f, 0f, 0f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        game.batch.begin();
        game.batch.draw(game.manager.get(BG, Texture.class),
                0f, 0f, Constants.WORLD_WIDTH, Constants.WORLD_HEIGHT);
        game.batch.end();

        stage.act(delta);
        stage.draw();
    }

    @Override public void resize(int w, int h) { viewport.update(w, h, true); }
    @Override public void pause()  {}
    @Override public void resume() {}
    @Override public void hide()   {}

    @Override public void dispose() {
        stage.dispose();
    }
}
