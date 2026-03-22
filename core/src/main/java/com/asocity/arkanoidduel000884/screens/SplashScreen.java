package com.asocity.arkanoidduel000884.screens;

import com.asocity.arkanoidduel000884.Constants;
import com.asocity.arkanoidduel000884.MainGame;
import com.asocity.arkanoidduel000884.SaveData;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.utils.viewport.StretchViewport;

public class SplashScreen extends ScreenAdapter {

    private static final String BG = "backgrounds/menu/Bright_background.png";

    private final MainGame       game;
    private final StretchViewport viewport;
    private float elapsed = 0f;
    private boolean assetsQueued = false;

    // Additional assets to queue during splash
    private static final String[] GAME_TEXTURES = {
        "backgrounds/game/1.png",
        "sprites/object/Platform.png",
        "sprites/tileset/Cartoon_Medieval_Guard_Post_2D_Level_Set_Building-Wall-A-01.png",
        "sprites/tileset/Cartoon_Medieval_Guard_Post_2D_Level_Set_Building-Wall-A-02.png",
        "sprites/tileset/Cartoon_Medieval_Guard_Post_2D_Level_Set_Building-Wall-A-03.png",
        "sprites/tileset/Cartoon_Medieval_Guard_Post_2D_Level_Set_Building-Wall-B-01.png",
        "sprites/tileset/Cartoon_Medieval_Guard_Post_2D_Level_Set_Building-Wall-C-01.png",
        "sprites/ui/lifes.png",
        "sprites/ui/coin_gold.png",
        "ui/menu_screen.png",
        "ui/game_screen.png",
        "ui/game_over_screen.png",
        "ui/pause_screen.png",
    };

    public SplashScreen(MainGame game) {
        this.game = game;
        viewport  = new StretchViewport(Constants.WORLD_WIDTH, Constants.WORLD_HEIGHT);

        // Load background immediately
        if (!game.manager.isLoaded(BG, Texture.class)) {
            game.manager.load(BG, Texture.class);
            game.manager.finishLoading();
        }
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(null);
        // Queue all game textures
        for (String path : GAME_TEXTURES) {
            if (!game.manager.isLoaded(path, Texture.class)) {
                game.manager.load(path, Texture.class);
            }
        }
        assetsQueued = true;
    }

    @Override
    public void render(float delta) {
        elapsed += delta;

        // Continue loading
        if (assetsQueued) {
            game.manager.update();
        }

        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        viewport.apply(true);
        game.batch.setProjectionMatrix(viewport.getCamera().combined);

        game.batch.begin();
        Texture bg = game.manager.get(BG, Texture.class);
        game.batch.draw(bg, 0, 0, Constants.WORLD_WIDTH, Constants.WORLD_HEIGHT);

        // Title
        GlyphLayout layout = new GlyphLayout(game.fontTitle, "ARKANOID DUEL");
        float tx = (Constants.WORLD_WIDTH - layout.width) / 2f;
        float ty = Constants.WORLD_HEIGHT / 2f + 80f;
        game.fontTitle.draw(game.batch, layout, tx, ty);

        // Loading indicator
        BitmapFont font = game.fontSmall;
        GlyphLayout loadLayout = new GlyphLayout(font, "LOADING...");
        font.draw(game.batch, loadLayout,
            (Constants.WORLD_WIDTH - loadLayout.width) / 2f,
            Constants.WORLD_HEIGHT / 2f - 40f);

        game.batch.end();

        // Transition when loaded or timed out
        boolean loaded = !assetsQueued || game.manager.isFinished();
        if ((loaded && elapsed > 1f) || elapsed >= Constants.SPLASH_MAX_DURATION) {
            if (!game.manager.isFinished()) game.manager.finishLoading();
            transition();
        }
    }

    private void transition() {
        if (!SaveData.isTutorialDone()) {
            game.setScreen(new TutorialScreen(game));
        } else {
            game.setScreen(new MainMenuScreen(game));
        }
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
    }

    @Override
    public void dispose() {}
}
