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

public class TutorialScreen implements Screen {

    private static final String BG = "backgrounds/menu/Bright_background.png";

    private static final String[] TITLES = {
        "MOVE PADDLE",
        "BREAK BRICKS!",
        "WATCH OUT!"
    };
    private static final String[] TEXTS = {
        "Drag your finger left\nand right to move\nthe paddle.",
        "Bounce the ball to\nbreak all the bricks\nand clear the level!",
        "Power Bricks shoot\nprojectiles at you.\nDodge them!"
    };

    private final MainGame   game;
    private final Stage      stage;
    private final StretchViewport viewport;
    private int currentCard = 0;

    private Label titleLabel;
    private Label bodyLabel;
    private Label progressLabel;
    private TextButton nextBtn;

    public TutorialScreen(MainGame game) {
        this.game  = game;
        viewport   = new StretchViewport(Constants.WORLD_WIDTH, Constants.WORLD_HEIGHT);
        stage      = new Stage(viewport, game.batch);

        game.playMusic("sounds/music/music_menu.ogg");

        buildStage();
        Gdx.input.setInputProcessor(stage);
    }

    private void buildStage() {
        stage.clear();
        TextButton.TextButtonStyle rectStyle = UiFactory.makeRectStyle(game.manager, game.fontBody);

        // Card background panel - draw as labels + positioned buttons
        // Title label
        Label.LabelStyle titleStyle = new Label.LabelStyle(game.fontTitle, Color.CYAN);
        titleLabel = new Label(TITLES[currentCard], titleStyle);
        titleLabel.setAlignment(Align.center);
        titleLabel.setWidth(400f);
        titleLabel.setWrap(true);
        titleLabel.setPosition(
            (Constants.WORLD_WIDTH - 400f) / 2f,
            Constants.WORLD_HEIGHT - 320f
        );
        stage.addActor(titleLabel);

        // Body label
        Label.LabelStyle bodyStyle = new Label.LabelStyle(game.fontBody, Color.WHITE);
        bodyLabel = new Label(TEXTS[currentCard], bodyStyle);
        bodyLabel.setAlignment(Align.center);
        bodyLabel.setWidth(380f);
        bodyLabel.setWrap(true);
        bodyLabel.setPosition(
            (Constants.WORLD_WIDTH - 380f) / 2f,
            Constants.WORLD_HEIGHT - 500f
        );
        stage.addActor(bodyLabel);

        // Progress dots
        Label.LabelStyle progStyle = new Label.LabelStyle(game.fontSmall, Color.LIGHT_GRAY);
        progressLabel = new Label(getProgressText(), progStyle);
        progressLabel.setAlignment(Align.center);
        progressLabel.setWidth(Constants.WORLD_WIDTH);
        progressLabel.setPosition(0, 280f);
        stage.addActor(progressLabel);

        // Next / Got it! button
        String btnLabel = (currentCard == TITLES.length - 1) ? "GOT IT!" : "NEXT";
        nextBtn = UiFactory.makeButton(btnLabel, rectStyle, 260f, 58f);
        nextBtn.setPosition((Constants.WORLD_WIDTH - 260f) / 2f, 190f);
        nextBtn.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.playSound("sounds/sfx/sfx_button_click.ogg");
                if (currentCard < TITLES.length - 1) {
                    currentCard++;
                    updateCard();
                } else {
                    SaveData.setTutorialDone();
                    game.setScreen(new LevelSelectScreen(game));
                }
            }
        });
        stage.addActor(nextBtn);

        // Skip button
        TextButton skipBtn = UiFactory.makeButton("SKIP", rectStyle, 180f, 46f);
        skipBtn.setPosition((Constants.WORLD_WIDTH - 180f) / 2f, 120f);
        skipBtn.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.playSound("sounds/sfx/sfx_button_back.ogg");
                SaveData.setTutorialDone();
                game.setScreen(new LevelSelectScreen(game));
            }
        });
        stage.addActor(skipBtn);

        // Swipe listener for navigation
        stage.addListener(new InputListener() {
            private float startX;
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                startX = x;
                return true;
            }
            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                float dx = x - startX;
                if (dx < -60f && currentCard < TITLES.length - 1) {
                    currentCard++;
                    updateCard();
                } else if (dx > 60f && currentCard > 0) {
                    currentCard--;
                    updateCard();
                }
            }
        });

        // Back key
        stage.addListener(new InputListener() {
            @Override
            public boolean keyDown(InputEvent event, int keycode) {
                if (keycode == Input.Keys.BACK) {
                    game.playSound("sounds/sfx/sfx_button_back.ogg");
                    SaveData.setTutorialDone();
                    game.setScreen(new MainMenuScreen(game));
                    return true;
                }
                return false;
            }
        });
    }

    private void updateCard() {
        titleLabel.setText(TITLES[currentCard]);
        bodyLabel.setText(TEXTS[currentCard]);
        progressLabel.setText(getProgressText());
        nextBtn.setText(currentCard == TITLES.length - 1 ? "GOT IT!" : "NEXT");
    }

    private String getProgressText() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < TITLES.length; i++) {
            sb.append(i == currentCard ? "● " : "○ ");
        }
        return sb.toString().trim();
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);
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

    @Override public void pause()   {}
    @Override public void resume()  {}

    @Override
    public void hide() {}

    @Override
    public void dispose() {
        stage.dispose();
    }
}
