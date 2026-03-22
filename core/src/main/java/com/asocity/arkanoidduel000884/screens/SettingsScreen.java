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

public class SettingsScreen implements Screen {

    private static final String BG = "backgrounds/menu/Bright_background.png";

    private final MainGame   game;
    private final Stage      stage;
    private final Viewport   viewport;
    private final Preferences prefs;

    // Toggle state labels — updated on click
    private Label musicStateLabel;
    private Label sfxStateLabel;

    public SettingsScreen(MainGame game) {
        this.game  = game;
        this.prefs = Gdx.app.getPreferences(Constants.PREFS_NAME);

        // Load settings from prefs
        game.musicEnabled = prefs.getBoolean(Constants.PREF_MUSIC, true);
        game.sfxEnabled   = prefs.getBoolean(Constants.PREF_SFX,   true);

        if (!game.manager.isLoaded(BG)) {
            game.manager.load(BG, Texture.class);
            game.manager.finishLoading();
        }

        OrthographicCamera camera = new OrthographicCamera();
        viewport = new StretchViewport(Constants.WORLD_WIDTH, Constants.WORLD_HEIGHT, camera);
        stage    = new Stage(viewport, game.batch);

        buildUi();
        registerInput();
    }

    private void buildUi() {
        TextButton.TextButtonStyle rectStyle =
                UiFactory.makeRectStyle(game.manager, game.fontBody);

        // ── Title ──
        Label.LabelStyle titleStyle = new Label.LabelStyle(game.fontTitle, Color.WHITE);
        Label titleLabel = new Label("SETTINGS", titleStyle);
        titleLabel.setSize(380f, 60f);
        titleLabel.setPosition((Constants.WORLD_WIDTH - 380f) / 2f, 730f);
        titleLabel.setAlignment(Align.center);
        stage.addActor(titleLabel);

        Label.LabelStyle bodyStyle  = new Label.LabelStyle(game.fontBody,  Color.WHITE);
        Label.LabelStyle smallStyle = new Label.LabelStyle(game.fontSmall, Color.WHITE);

        // ── Music toggle row ──
        Label musicLabel = new Label("MUSIC", bodyStyle);
        musicLabel.setPosition(60f, 580f);
        stage.addActor(musicLabel);

        musicStateLabel = new Label(game.musicEnabled ? "ON" : "OFF", smallStyle);
        musicStateLabel.setPosition(300f, 584f);
        stage.addActor(musicStateLabel);

        TextButton musicBtn = UiFactory.makeButton(
                game.musicEnabled ? "ON" : "OFF", rectStyle, 120f, 48f);
        musicBtn.setPosition(300f, 570f);
        musicBtn.addListener(new ChangeListener() {
            @Override public void changed(ChangeEvent e, Actor a) {
                game.musicEnabled = !game.musicEnabled;
                prefs.putBoolean(Constants.PREF_MUSIC, game.musicEnabled);
                prefs.flush();
                if (game.currentMusic != null) {
                    if (game.musicEnabled) game.currentMusic.play();
                    else                   game.currentMusic.pause();
                }
                ((TextButton) a).setText(game.musicEnabled ? "ON" : "OFF");
                game.playSound("sounds/sfx/sfx_toggle.ogg");
            }
        });
        stage.addActor(musicBtn);

        // ── SFX toggle row ──
        Label sfxLabel = new Label("SFX", bodyStyle);
        sfxLabel.setPosition(60f, 490f);
        stage.addActor(sfxLabel);

        sfxStateLabel = new Label(game.sfxEnabled ? "ON" : "OFF", smallStyle);
        sfxStateLabel.setPosition(300f, 494f);
        stage.addActor(sfxStateLabel);

        TextButton sfxBtn = UiFactory.makeButton(
                game.sfxEnabled ? "ON" : "OFF", rectStyle, 120f, 48f);
        sfxBtn.setPosition(300f, 480f);
        sfxBtn.addListener(new ChangeListener() {
            @Override public void changed(ChangeEvent e, Actor a) {
                game.sfxEnabled = !game.sfxEnabled;
                prefs.putBoolean(Constants.PREF_SFX, game.sfxEnabled);
                prefs.flush();
                ((TextButton) a).setText(game.sfxEnabled ? "ON" : "OFF");
                game.playSound("sounds/sfx/sfx_toggle.ogg");
            }
        });
        stage.addActor(sfxBtn);

        // ── Back button ──
        TextButton backBtn = UiFactory.makeButton("BACK", rectStyle, 280f, 52f);
        backBtn.setPosition((Constants.WORLD_WIDTH - 280f) / 2f, 120f);
        backBtn.addListener(new ChangeListener() {
            @Override public void changed(ChangeEvent e, Actor a) {
                game.playSound("sounds/sfx/sfx_button_back.ogg");
                game.setScreen(new MainMenuScreen(game));
            }
        });
        stage.addActor(backBtn);
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
