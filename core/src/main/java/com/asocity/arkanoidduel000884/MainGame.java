package com.asocity.arkanoidduel000884;

import com.asocity.arkanoidduel000884.screens.MainMenuScreen;
import com.asocity.arkanoidduel000884.screens.SplashScreen;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;

public class MainGame extends Game {

    // ── Shared rendering resources ────────────────────────────────────────────
    public SpriteBatch  batch;
    public AssetManager manager;

    // ── Fonts ─────────────────────────────────────────────────────────────────
    /** Pixel/arcade font — titles and headers (PressStart2P.ttf) */
    public BitmapFont fontTitle;
    /** Clean readable font — body text and buttons (Cairovixel.ttf) */
    public BitmapFont fontBody;
    /** Small variant of body font — HUD labels, small captions */
    public BitmapFont fontSmall;
    /** Large score display font */
    public BitmapFont fontScore;

    // ── Audio state ───────────────────────────────────────────────────────────
    public boolean musicEnabled = true;
    public boolean sfxEnabled   = true;
    public Music   currentMusic = null;

    // ── Lifecycle ─────────────────────────────────────────────────────────────

    @Override
    public void create() {
        batch   = new SpriteBatch();
        manager = new AssetManager();

        generateFonts();
        loadCoreAssets();
        manager.finishLoading();

        setScreen(new SplashScreen(this));
    }

    // ── Font generation ───────────────────────────────────────────────────────

    private void generateFonts() {
        // Title font: PressStart2P (pixel/arcade)
        FreeTypeFontGenerator titleGen = new FreeTypeFontGenerator(
                Gdx.files.internal("fonts/PressStart2P.ttf"));
        // Body font: Cairovixel (clean readable)
        FreeTypeFontGenerator bodyGen = new FreeTypeFontGenerator(
                Gdx.files.internal("fonts/Cairovixel.ttf"));

        FreeTypeFontParameter p = new FreeTypeFontParameter();

        // fontTitle — 36px, thick outline
        p.size        = 36;
        p.borderWidth = 3;
        p.borderColor = new Color(0f, 0f, 0f, 0.85f);
        fontTitle = titleGen.generateFont(p);

        // fontScore — 48px, thick outline
        p.size        = 48;
        p.borderWidth = 3;
        fontScore = titleGen.generateFont(p);

        // fontBody — 28px, medium outline
        p.size        = 28;
        p.borderWidth = 2;
        p.borderColor = new Color(0f, 0f, 0f, 0.85f);
        fontBody = bodyGen.generateFont(p);

        // fontSmall — 20px, thin outline
        p.size        = 20;
        p.borderWidth = 2;
        fontSmall = bodyGen.generateFont(p);

        titleGen.dispose();
        bodyGen.dispose();
    }

    // ── Core asset loading ────────────────────────────────────────────────────

    private void loadCoreAssets() {
        // Button sprites (needed by UiFactory in every screen)
        manager.load("ui/buttons/button_rectangle_depth_gradient.png",
                com.badlogic.gdx.graphics.Texture.class);
        manager.load("ui/buttons/button_rectangle_depth_flat.png",
                com.badlogic.gdx.graphics.Texture.class);
        manager.load("ui/buttons/button_round_depth_gradient.png",
                com.badlogic.gdx.graphics.Texture.class);
        manager.load("ui/buttons/button_round_depth_flat.png",
                com.badlogic.gdx.graphics.Texture.class);

        // Music
        manager.load("sounds/music/music_menu.ogg",      Music.class);
        manager.load("sounds/music/music_gameplay.ogg",  Music.class);
        manager.load("sounds/music/music_game_over.ogg", Music.class);

        // SFX
        manager.load("sounds/sfx/sfx_button_click.ogg",   Sound.class);
        manager.load("sounds/sfx/sfx_button_back.ogg",    Sound.class);
        manager.load("sounds/sfx/sfx_toggle.ogg",         Sound.class);
        manager.load("sounds/sfx/sfx_hit.ogg",            Sound.class);
        manager.load("sounds/sfx/sfx_game_over.ogg",      Sound.class);
        manager.load("sounds/sfx/sfx_level_complete.ogg", Sound.class);
        manager.load("sounds/sfx/sfx_power_up.ogg",       Sound.class);
        manager.load("sounds/sfx/sfx_shoot.ogg",          Sound.class);
        manager.load("sounds/sfx/sfx_jingle_win.ogg",     Sound.class);
    }

    // ── Music helpers ─────────────────────────────────────────────────────────

    /**
     * Start looping music. Ignores the call if the requested track is already
     * playing — prevents music restarting when navigating between screens.
     */
    public void playMusic(String path) {
        if (!manager.isLoaded(path, Music.class)) return;
        Music requested = manager.get(path, Music.class);
        if (requested == currentMusic && currentMusic.isPlaying()) return;
        if (currentMusic != null) currentMusic.stop();
        currentMusic = requested;
        currentMusic.setLooping(true);
        currentMusic.setVolume(Constants.DEFAULT_MUSIC_VOLUME);
        if (musicEnabled) currentMusic.play();
    }

    /**
     * Play music once (game-over jingle). Does NOT loop.
     */
    public void playMusicOnce(String path) {
        if (!manager.isLoaded(path, Music.class)) return;
        if (currentMusic != null) currentMusic.stop();
        currentMusic = manager.get(path, Music.class);
        currentMusic.setLooping(false);
        currentMusic.setVolume(Constants.DEFAULT_MUSIC_VOLUME);
        if (musicEnabled) currentMusic.play();
    }

    // ── SFX helper ────────────────────────────────────────────────────────────

    public void playSound(String path) {
        if (sfxEnabled && manager.isLoaded(path, Sound.class)) {
            manager.get(path, Sound.class).play(1.0f);
        }
    }

    // ── Dispose ───────────────────────────────────────────────────────────────

    @Override
    public void dispose() {
        super.dispose();
        batch.dispose();
        manager.dispose();
        fontTitle.dispose();
        fontBody.dispose();
        fontSmall.dispose();
        fontScore.dispose();
    }
}
