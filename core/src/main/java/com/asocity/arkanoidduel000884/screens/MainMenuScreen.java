package com.asocity.arkanoidduel000884.screens;

import com.asocity.arkanoidduel000884.Constants;
import com.asocity.arkanoidduel000884.MainGame;
import com.asocity.arkanoidduel000884.UiFactory;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputMultiplexer;
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

public class MainMenuScreen implements Screen {

    private static final String BG = "ui/menu_screen.png";

    private final MainGame game;
    private final Stage    stage;
    private final Viewport viewport;

    public MainMenuScreen(MainGame game) {
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

        // ── Title label (FIGMA top-Y=90, h=60 → libgdxY = 854-90-60 = 704) ──
        Label.LabelStyle titleStyle = new Label.LabelStyle(game.fontTitle, Color.WHITE);
        Label titleLabel = new Label("ARKANOID DUEL", titleStyle);
        titleLabel.setSize(380f, 60f);
        titleLabel.setPosition((Constants.WORLD_WIDTH - 380f) / 2f, 704f);
        titleLabel.setAlignment(Align.center);
        stage.addActor(titleLabel);

        // ── Best score badge (FIGMA top-Y=170, h=36 → libgdxY = 648) ──
        int best = Gdx.app.getPreferences(Constants.PREFS_NAME)
                .getInteger(Constants.PREF_HIGH_SCORE, 0);
        Label.LabelStyle smallStyle = new Label.LabelStyle(game.fontSmall, Color.WHITE);
        Label bestLabel = new Label("BEST: " + best, smallStyle);
        bestLabel.setSize(220f, 36f);
        bestLabel.setPosition((Constants.WORLD_WIDTH - 220f) / 2f, 648f);
        bestLabel.setAlignment(Align.center);
        stage.addActor(bestLabel);

        // ── PLAY (FIGMA top-Y=340, h=60 → libgdxY = 454) ──
        TextButton playBtn = UiFactory.makeButton("PLAY", rectStyle, 280f, 60f);
        playBtn.setPosition((Constants.WORLD_WIDTH - 280f) / 2f, 454f);
        playBtn.addListener(new ChangeListener() {
            @Override public void changed(ChangeEvent e, Actor a) {
                game.playSound("sounds/sfx/sfx_button_click.ogg");
                game.setScreen(new LevelSelectScreen(game));
            }
        });
        stage.addActor(playBtn);

        // ── LEADERBOARD (FIGMA top-Y=420, h=52 → libgdxY = 382) ──
        TextButton lbBtn = UiFactory.makeButton("LEADERBOARD", rectStyle, 280f, 52f);
        lbBtn.setPosition((Constants.WORLD_WIDTH - 280f) / 2f, 382f);
        lbBtn.addListener(new ChangeListener() {
            @Override public void changed(ChangeEvent e, Actor a) {
                game.playSound("sounds/sfx/sfx_button_click.ogg");
                game.setScreen(new LeaderboardScreen(game));
            }
        });
        stage.addActor(lbBtn);

        // ── SETTINGS (FIGMA top-Y=490, h=52 → libgdxY = 312) ──
        TextButton settingsBtn = UiFactory.makeButton("SETTINGS", rectStyle, 280f, 52f);
        settingsBtn.setPosition((Constants.WORLD_WIDTH - 280f) / 2f, 312f);
        settingsBtn.addListener(new ChangeListener() {
            @Override public void changed(ChangeEvent e, Actor a) {
                game.playSound("sounds/sfx/sfx_button_click.ogg");
                game.setScreen(new SettingsScreen(game));
            }
        });
        stage.addActor(settingsBtn);
    }

    private void registerInput() {
        Gdx.input.setInputProcessor(new InputMultiplexer(stage, new InputAdapter() {
            @Override public boolean keyDown(int k) {
                if (k == Input.Keys.BACK) { Gdx.app.exit(); return true; }
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
