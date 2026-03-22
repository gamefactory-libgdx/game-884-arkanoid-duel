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

public class GameOverScreen implements Screen {

    private static final String BG = "ui/game_over_screen.png";

    private final MainGame game;
    private final int      score;
    private final int      levelNumber; // "extra" param = level number for retry

    private final Stage    stage;
    private final Viewport viewport;

    /** @param score       final score for this run
     *  @param levelNumber level that was being played (used for Retry) */
    public GameOverScreen(MainGame game, int score, int levelNumber) {
        this.game        = game;
        this.score       = score;
        this.levelNumber = levelNumber;

        // Persist high score
        Preferences prefs = Gdx.app.getPreferences(Constants.PREFS_NAME);
        int prev = prefs.getInteger(Constants.PREF_HIGH_SCORE, 0);
        if (score > prev) {
            prefs.putInteger(Constants.PREF_HIGH_SCORE, score);
            prefs.flush();
        }

        // Add to leaderboard
        LeaderboardScreen.addScore(score);

        if (!game.manager.isLoaded(BG)) {
            game.manager.load(BG, Texture.class);
            game.manager.finishLoading();
        }

        OrthographicCamera camera = new OrthographicCamera();
        viewport = new StretchViewport(Constants.WORLD_WIDTH, Constants.WORLD_HEIGHT, camera);
        stage    = new Stage(viewport, game.batch);

        buildUi();
        registerInput();
        game.playMusicOnce("sounds/music/music_game_over.ogg");
    }

    private void buildUi() {
        int best = Gdx.app.getPreferences(Constants.PREFS_NAME)
                .getInteger(Constants.PREF_HIGH_SCORE, 0);

        TextButton.TextButtonStyle rectStyle =
                UiFactory.makeRectStyle(game.manager, game.fontBody);

        // ── GAME OVER title (FIGMA top-Y=160, h=64 → libgdxY = 854-160-64 = 630) ──
        Color coral = new Color(1f, 0.239f, 0.353f, 1f); // #FF3D5A
        Label.LabelStyle titleStyle = new Label.LabelStyle(game.fontTitle, coral);
        Label titleLabel = new Label("GAME OVER", titleStyle);
        titleLabel.setSize(380f, 64f);
        titleLabel.setPosition((Constants.WORLD_WIDTH - 380f) / 2f, 630f);
        titleLabel.setAlignment(Align.center);
        stage.addActor(titleLabel);

        Label.LabelStyle bodyStyle  = new Label.LabelStyle(game.fontBody,  Color.WHITE);
        Label.LabelStyle scoreStyle = new Label.LabelStyle(game.fontScore,
                new Color(1f, 0.843f, 0f, 1f)); // gold #FFD700

        // ── SCORE label (FIGMA top-Y=270, h=28 → libgdxY = 556) ──
        Label scoreLabel = new Label("SCORE", bodyStyle);
        scoreLabel.setSize(240f, 28f);
        scoreLabel.setPosition((Constants.WORLD_WIDTH - 240f) / 2f, 556f);
        scoreLabel.setAlignment(Align.center);
        stage.addActor(scoreLabel);

        // ── Score value (FIGMA top-Y=306, h=52 → libgdxY = 496) ──
        Label scoreValue = new Label(String.valueOf(score), scoreStyle);
        scoreValue.setSize(240f, 52f);
        scoreValue.setPosition((Constants.WORLD_WIDTH - 240f) / 2f, 496f);
        scoreValue.setAlignment(Align.center);
        stage.addActor(scoreValue);

        // ── BEST label (FIGMA top-Y=368, h=24 → libgdxY = 462) ──
        Label bestLabel = new Label("BEST", bodyStyle);
        bestLabel.setSize(200f, 24f);
        bestLabel.setPosition((Constants.WORLD_WIDTH - 200f) / 2f, 462f);
        bestLabel.setAlignment(Align.center);
        stage.addActor(bestLabel);

        // ── Best value (FIGMA top-Y=398, h=40 → libgdxY = 416) ──
        Label.LabelStyle bestValStyle = new Label.LabelStyle(game.fontBody, Color.WHITE);
        Label bestValue = new Label(String.valueOf(best), bestValStyle);
        bestValue.setSize(200f, 40f);
        bestValue.setPosition((Constants.WORLD_WIDTH - 200f) / 2f, 416f);
        bestValue.setAlignment(Align.center);
        stage.addActor(bestValue);

        // ── RETRY (FIGMA top-Y=510, h=60 → libgdxY = 284) ──
        TextButton retryBtn = UiFactory.makeButton("RETRY", rectStyle, 280f, 60f);
        retryBtn.setPosition((Constants.WORLD_WIDTH - 280f) / 2f, 284f);
        retryBtn.addListener(new ChangeListener() {
            @Override public void changed(ChangeEvent e, Actor a) {
                game.playSound("sounds/sfx/sfx_button_click.ogg");
                game.setScreen(new GameScreen(game, levelNumber));
            }
        });
        stage.addActor(retryBtn);

        // ── MENU (FIGMA top-Y=590, h=52 → libgdxY = 212) ──
        TextButton menuBtn = UiFactory.makeButton("MENU", rectStyle, 280f, 52f);
        menuBtn.setPosition((Constants.WORLD_WIDTH - 280f) / 2f, 212f);
        menuBtn.addListener(new ChangeListener() {
            @Override public void changed(ChangeEvent e, Actor a) {
                game.playSound("sounds/sfx/sfx_button_click.ogg");
                game.setScreen(new MainMenuScreen(game));
            }
        });
        stage.addActor(menuBtn);
    }

    private void registerInput() {
        Gdx.input.setInputProcessor(new InputMultiplexer(stage, new InputAdapter() {
            @Override public boolean keyDown(int k) {
                if (k == Input.Keys.BACK) {
                    game.setScreen(new MainMenuScreen(game));
                    return true;
                }
                return false;
            }
        }));
    }

    @Override public void show() {
        registerInput();
        game.playMusicOnce("sounds/music/music_game_over.ogg");
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
