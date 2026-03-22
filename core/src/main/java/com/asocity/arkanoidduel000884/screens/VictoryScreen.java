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

public class VictoryScreen implements Screen {

    private static final String BG = "backgrounds/menu/Bright_background.png";

    private final MainGame  game;
    private final int       levelNumber;
    private final int       score;
    private final int       livesLeft;
    private final Stage     stage;
    private final StretchViewport viewport;

    // Star animation
    private float elapsed = 0f;
    private int   starsEarned;
    private int   starsShown = 0;
    private float starTimer  = 0f;

    public VictoryScreen(MainGame game, int levelNumber, int score, int livesLeft) {
        this.game        = game;
        this.levelNumber = levelNumber;
        this.score       = score;
        this.livesLeft   = livesLeft;
        starsEarned      = livesLeft >= 3 ? 3 : livesLeft >= 2 ? 2 : 1;

        viewport = new StretchViewport(Constants.WORLD_WIDTH, Constants.WORLD_HEIGHT);
        stage    = new Stage(viewport, game.batch);

        buildStage();
        Gdx.input.setInputProcessor(stage);
    }

    private void buildStage() {
        stage.clear();
        TextButton.TextButtonStyle rectStyle = UiFactory.makeRectStyle(game.manager, game.fontBody);

        // "LEVEL X CLEAR" title
        Label.LabelStyle titleStyle = new Label.LabelStyle(game.fontTitle, Color.YELLOW);
        Label titleLabel = new Label("LEVEL " + levelNumber + "\nCLEAR!", titleStyle);
        titleLabel.setAlignment(Align.center);
        titleLabel.setWidth(Constants.WORLD_WIDTH);
        titleLabel.setPosition(0, 680f);
        stage.addActor(titleLabel);

        // Score
        Label.LabelStyle scoreStyle = new Label.LabelStyle(game.fontBody, Color.WHITE);
        Label scoreLabel = new Label("SCORE: " + score, scoreStyle);
        scoreLabel.setAlignment(Align.center);
        scoreLabel.setWidth(Constants.WORLD_WIDTH);
        scoreLabel.setPosition(0, 570f);
        stage.addActor(scoreLabel);

        // Stars label (will be updated in render)
        Label.LabelStyle starStyle = new Label.LabelStyle(game.fontTitle, Color.GOLD);
        Label starsLabel = new Label(buildStarsString(0), starStyle);
        starsLabel.setName("starsLabel");
        starsLabel.setAlignment(Align.center);
        starsLabel.setWidth(Constants.WORLD_WIDTH);
        starsLabel.setPosition(0, 480f);
        stage.addActor(starsLabel);

        // Next Level or Finish button
        if (levelNumber < Constants.LEVEL_COUNT) {
            TextButton nextBtn = UiFactory.makeButton("NEXT LEVEL", rectStyle, 280f, 60f);
            nextBtn.setPosition((Constants.WORLD_WIDTH - 280f) / 2f, 360f);
            nextBtn.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    game.playSound("sounds/sfx/sfx_button_click.ogg");
                    game.setScreen(new GameScreen(game, levelNumber + 1));
                }
            });
            stage.addActor(nextBtn);
        } else {
            TextButton finishBtn = UiFactory.makeButton("FINISH!", rectStyle, 280f, 60f);
            finishBtn.setPosition((Constants.WORLD_WIDTH - 280f) / 2f, 360f);
            finishBtn.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    game.playSound("sounds/sfx/sfx_button_click.ogg");
                    game.setScreen(new MainMenuScreen(game));
                }
            });
            stage.addActor(finishBtn);
        }

        // Retry button
        TextButton retryBtn = UiFactory.makeButton("RETRY", rectStyle, 280f, 52f);
        retryBtn.setPosition((Constants.WORLD_WIDTH - 280f) / 2f, 290f);
        retryBtn.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.playSound("sounds/sfx/sfx_button_click.ogg");
                game.setScreen(new GameScreen(game, levelNumber));
            }
        });
        stage.addActor(retryBtn);

        // Main Menu button
        TextButton menuBtn = UiFactory.makeButton("MAIN MENU", rectStyle, 280f, 52f);
        menuBtn.setPosition((Constants.WORLD_WIDTH - 280f) / 2f, 220f);
        menuBtn.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.playSound("sounds/sfx/sfx_button_back.ogg");
                game.setScreen(new MainMenuScreen(game));
            }
        });
        stage.addActor(menuBtn);

        // Level Select button
        TextButton selectBtn = UiFactory.makeButton("LEVEL SELECT", rectStyle, 280f, 52f);
        selectBtn.setPosition((Constants.WORLD_WIDTH - 280f) / 2f, 150f);
        selectBtn.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.playSound("sounds/sfx/sfx_button_back.ogg");
                game.setScreen(new LevelSelectScreen(game));
            }
        });
        stage.addActor(selectBtn);

        // Back key → main menu
        stage.addListener(new InputListener() {
            @Override
            public boolean keyDown(InputEvent event, int keycode) {
                if (keycode == Input.Keys.BACK) {
                    game.setScreen(new MainMenuScreen(game));
                    return true;
                }
                return false;
            }
        });
    }

    private String buildStarsString(int count) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 3; i++) {
            sb.append(i < count ? "★ " : "☆ ");
        }
        return sb.toString().trim();
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void render(float delta) {
        elapsed   += delta;
        starTimer += delta;

        // Animate star reveal
        if (starsShown < starsEarned && starTimer > 0.4f) {
            starsShown++;
            starTimer = 0f;
            game.playSound("sounds/sfx/sfx_jingle_win.ogg");
            Label starsLabel = stage.getRoot().findActor("starsLabel");
            if (starsLabel != null) {
                ((Label) starsLabel).setText(buildStarsString(starsShown));
            }
        }

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
