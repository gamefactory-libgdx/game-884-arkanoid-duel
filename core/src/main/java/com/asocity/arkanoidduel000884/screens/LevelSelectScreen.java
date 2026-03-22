package com.asocity.arkanoidduel000884.screens;

import com.asocity.arkanoidduel000884.Constants;
import com.asocity.arkanoidduel000884.MainGame;
import com.asocity.arkanoidduel000884.SaveData;
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

public class LevelSelectScreen implements Screen {

    private static final String BG = "backgrounds/menu/Bright_background.png";

    private final MainGame game;
    private final Stage    stage;
    private final StretchViewport viewport;

    // 2 columns × 5 rows = 10 levels
    private static final int   COLS     = 2;
    private static final int   ROWS     = 5;
    private static final float BTN_W    = 180f;
    private static final float BTN_H    = 70f;
    private static final float GAP_X    = 24f;
    private static final float GAP_Y    = 14f;
    private static final float GRID_TOP = 740f; // libgdx Y from top

    public LevelSelectScreen(MainGame game) {
        this.game = game;
        viewport  = new StretchViewport(Constants.WORLD_WIDTH, Constants.WORLD_HEIGHT);
        stage     = new Stage(viewport, game.batch);

        game.playMusic("sounds/music/music_menu.ogg");

        buildStage();
        Gdx.input.setInputProcessor(stage);
    }

    private void buildStage() {
        stage.clear();
        int maxUnlocked = SaveData.getMaxLevelUnlocked();
        TextButton.TextButtonStyle rectStyle = UiFactory.makeRectStyle(game.manager, game.fontBody);

        // Title
        Label.LabelStyle titleStyle = new Label.LabelStyle(game.fontTitle, Color.CYAN);
        Label titleLabel = new Label("SELECT LEVEL", titleStyle);
        titleLabel.setAlignment(Align.center);
        titleLabel.setWidth(Constants.WORLD_WIDTH);
        titleLabel.setPosition(0, 790f);
        stage.addActor(titleLabel);

        // Level grid
        float gridWidth  = COLS * BTN_W + (COLS - 1) * GAP_X;
        float startX     = (Constants.WORLD_WIDTH - gridWidth) / 2f;

        for (int i = 0; i < Constants.LEVEL_COUNT; i++) {
            final int levelNum = i + 1;
            int col = i % COLS;
            int row = i / COLS;

            float x = startX + col * (BTN_W + GAP_X);
            float y = GRID_TOP - (row + 1) * (BTN_H + GAP_Y);

            boolean locked = levelNum > maxUnlocked;
            int stars       = SaveData.getLevelStars(levelNum);
            int bestScore   = SaveData.getLevelBestScore(levelNum);

            String btnText = locked
                ? "LEVEL " + levelNum + "\nLOCKED"
                : "LEVEL " + levelNum + "\n" + starsText(stars);

            TextButton btn = UiFactory.makeButton(btnText, rectStyle, BTN_W, BTN_H);
            btn.setPosition(x, y);
            btn.getLabel().setAlignment(Align.center);
            if (locked) btn.setColor(0.4f, 0.4f, 0.4f, 1f);

            if (!locked) {
                btn.addListener(new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        game.playSound("sounds/sfx/sfx_button_click.ogg");
                        game.setScreen(new GameScreen(game, levelNum));
                    }
                });
            }
            stage.addActor(btn);
        }

        // Back button
        TextButton backBtn = UiFactory.makeButton("MAIN MENU", rectStyle, 240f, 52f);
        backBtn.setPosition((Constants.WORLD_WIDTH - 240f) / 2f, 40f);
        backBtn.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.playSound("sounds/sfx/sfx_button_back.ogg");
                game.setScreen(new MainMenuScreen(game));
            }
        });
        stage.addActor(backBtn);

        // Back key
        stage.addListener(new InputListener() {
            @Override
            public boolean keyDown(InputEvent event, int keycode) {
                if (keycode == Input.Keys.BACK) {
                    game.playSound("sounds/sfx/sfx_button_back.ogg");
                    game.setScreen(new MainMenuScreen(game));
                    return true;
                }
                return false;
            }
        });
    }

    private String starsText(int stars) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 3; i++) sb.append(i < stars ? "*" : "-");
        return sb.toString();
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);
        buildStage(); // refresh unlock state
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.03f, 0.03f, 0.08f, 1f);
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
