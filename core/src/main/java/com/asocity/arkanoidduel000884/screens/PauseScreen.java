package com.asocity.arkanoidduel000884.screens;

import com.asocity.arkanoidduel000884.Constants;
import com.asocity.arkanoidduel000884.MainGame;
import com.asocity.arkanoidduel000884.UiFactory;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.StretchViewport;

public class PauseScreen implements Screen {

    private static final String BG = "ui/pause_screen.png";

    private final MainGame  game;
    private final Screen    previousScreen;
    private final Stage     stage;
    private final StretchViewport viewport;

    public PauseScreen(MainGame game, Screen previousScreen) {
        this.game           = game;
        this.previousScreen = previousScreen;
        viewport = new StretchViewport(Constants.WORLD_WIDTH, Constants.WORLD_HEIGHT);
        stage    = new Stage(viewport, game.batch);

        buildStage();
        Gdx.input.setInputProcessor(stage);
    }

    private void buildStage() {
        stage.clear();
        TextButton.TextButtonStyle rectStyle = UiFactory.makeRectStyle(game.manager, game.fontBody);

        // PAUSED title — FIGMA topY=220, h=52 → libgdxY = 854-220-52 = 582
        Label.LabelStyle titleStyle = new Label.LabelStyle(game.fontTitle, Color.CYAN);
        Label titleLabel = new Label("PAUSED", titleStyle);
        titleLabel.setAlignment(Align.center);
        titleLabel.setWidth(300f);
        titleLabel.setPosition((Constants.WORLD_WIDTH - 300f) / 2f, 582f);
        stage.addActor(titleLabel);

        // RESUME — FIGMA topY=330, h=60 → libgdxY = 854-330-60 = 464
        TextButton resumeBtn = UiFactory.makeButton("RESUME", rectStyle, 260f, 60f);
        resumeBtn.setPosition((Constants.WORLD_WIDTH - 260f) / 2f, 464f);
        resumeBtn.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.playSound("sounds/sfx/sfx_button_click.ogg");
                game.setScreen(previousScreen);
            }
        });
        stage.addActor(resumeBtn);

        // RESTART — FIGMA topY=410, h=52 → libgdxY = 854-410-52 = 392
        TextButton restartBtn = UiFactory.makeButton("RESTART", rectStyle, 260f, 52f);
        restartBtn.setPosition((Constants.WORLD_WIDTH - 260f) / 2f, 392f);
        restartBtn.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.playSound("sounds/sfx/sfx_button_click.ogg");
                int level = 1;
                if (previousScreen instanceof GameScreen) {
                    level = ((GameScreen) previousScreen).getLevelNumber();
                }
                game.setScreen(new GameScreen(game, level));
            }
        });
        stage.addActor(restartBtn);

        // SETTINGS — FIGMA topY=480, h=52 → libgdxY = 854-480-52 = 322
        TextButton settingsBtn = UiFactory.makeButton("SETTINGS", rectStyle, 260f, 52f);
        settingsBtn.setPosition((Constants.WORLD_WIDTH - 260f) / 2f, 322f);
        settingsBtn.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.playSound("sounds/sfx/sfx_button_click.ogg");
                game.setScreen(new SettingsScreen(game));
            }
        });
        stage.addActor(settingsBtn);

        // QUIT — FIGMA topY=556, h=52 → libgdxY = 854-556-52 = 246
        TextButton quitBtn = UiFactory.makeButton("MAIN MENU", rectStyle, 260f, 52f);
        quitBtn.setPosition((Constants.WORLD_WIDTH - 260f) / 2f, 246f);
        quitBtn.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.playSound("sounds/sfx/sfx_button_back.ogg");
                game.setScreen(new MainMenuScreen(game));
            }
        });
        stage.addActor(quitBtn);

        // Back key → resume
        stage.addListener(new InputListener() {
            @Override
            public boolean keyDown(InputEvent event, int keycode) {
                if (keycode == Input.Keys.BACK) {
                    game.playSound("sounds/sfx/sfx_button_back.ogg");
                    game.setScreen(previousScreen);
                    return true;
                }
                return false;
            }
        });
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        viewport.apply(true);
        game.batch.setProjectionMatrix(viewport.getCamera().combined);

        game.batch.begin();
        game.batch.draw(
            game.manager.get(BG, Texture.class),
            0, 0, Constants.WORLD_WIDTH, Constants.WORLD_HEIGHT
        );
        game.batch.end();

        stage.act(delta);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
    }

    @Override public void pause()  {}
    @Override public void resume() {}
    @Override public void hide()   {}

    @Override
    public void dispose() {
        stage.dispose();
    }
}
