package com.asocity.arkanoidduel000884;

public final class Constants {

    private Constants() {}

    // ── Viewport ──────────────────────────────────────────────────────────────
    public static final float WORLD_WIDTH  = 480f;
    public static final float WORLD_HEIGHT = 854f;

    // Virtual play-field (game-logic coords)
    public static final float VP_WIDTH  = 360f;
    public static final float VP_HEIGHT = 640f;

    // ── Paddle ────────────────────────────────────────────────────────────────
    public static final float PADDLE_WIDTH              = 120f;
    public static final float PADDLE_HEIGHT             = 18f;
    public static final float PADDLE_Y                  = 72f;
    public static final float PADDLE_INVINCIBLE_DURATION = 1.0f;
    public static final float PADDLE_FLASH_FREQ         = 10f;
    public static final float PADDLE_ALPHA_MIN          = 0.3f;
    public static final float PADDLE_ALPHA_MAX          = 1.0f;
    public static final float PADDLE_WIDE_MULT          = 1.6f;

    // ── Ball ──────────────────────────────────────────────────────────────────
    public static final float BALL_RADIUS               = 10f;
    public static final float BALL_SPEED_BASE           = 300f;
    public static final float BALL_SPEED_STEP           = 30f;
    public static final float BALL_MAX_SPEED            = 600f;
    public static final float BALL_ANGLE_RANGE          = 60f;
    public static final float BALL_LAUNCH_ANGLE_DEFAULT = 90f;

    // ── Brick grid ────────────────────────────────────────────────────────────
    public static final int   BRICK_COLS         = 8;
    public static final float BRICK_WIDTH        = 38f;
    public static final float BRICK_HEIGHT       = 16f;
    public static final float BRICK_MARGIN_X     = 4f;
    public static final float BRICK_MARGIN_Y     = 4f;
    public static final float BRICK_GRID_TOP_Y   = 560f;
    public static final float BRICK_TOP_PADDING  = 40f;

    // ── Brick HP ──────────────────────────────────────────────────────────────
    public static final int HP_NORMAL  = 1;
    public static final int HP_TOUGH   = 2;
    public static final int HP_POWER   = 1;
    public static final int HP_SPECIAL = 1;

    // ── Projectile ────────────────────────────────────────────────────────────
    public static final float PROJECTILE_WIDTH       = 8f;
    public static final float PROJECTILE_HEIGHT      = 20f;
    public static final float PROJECTILE_SPEED       = 250f;
    public static final float PROJECTILE_ANGLE_RANGE = 15f;

    // ── Power-up capsules ─────────────────────────────────────────────────────
    public static final float POWERUP_SIZE       = 20f;
    public static final float POWERUP_FALL_SPEED = 150f;

    // ── Power-up durations ────────────────────────────────────────────────────
    public static final float POWERUP_WIDE_DURATION     = 10f;
    public static final float POWERUP_SLOW_DURATION     = 8f;
    public static final float POWERUP_SLOW_FACTOR       = 0.60f;
    public static final int   POWERUP_LASER_SHOTS       = 5;
    public static final float POWERUP_FIREBALL_DURATION = 6f;

    // ── Laser ─────────────────────────────────────────────────────────────────
    public static final float LASER_WIDTH = 4f;
    public static final float LASER_SPEED = 500f;

    // ── Shield ────────────────────────────────────────────────────────────────
    public static final float SHIELD_Y_OFFSET = 20f;
    public static final float SHIELD_HEIGHT   = 4f;

    // ── Scoring ───────────────────────────────────────────────────────────────
    public static final int   SCORE_NORMAL           = 10;
    public static final int   SCORE_TOUGH            = 20;
    public static final int   SCORE_POWER            = 30;
    public static final int   SCORE_SPECIAL          = 25;
    public static final int   SCORE_POWERUP_COLLECT  = 50;
    public static final int   SCORE_LEVEL_CLEAR_BASE = 500;
    public static final int   SCORE_LIFE_BONUS       = 200;
    public static final float SCORE_MULTIBALL_MULT   = 1.5f;

    // ── Lives ─────────────────────────────────────────────────────────────────
    public static final int LIVES_PER_LEVEL = 3;

    // ── Physics ───────────────────────────────────────────────────────────────
    public static final float DELTA_CAP = 0.05f;

    // ── HUD ───────────────────────────────────────────────────────────────────
    public static final float HUD_HEIGHT   = 40f;
    public static final float HEART_SIZE   = 18f;
    public static final float HEART_SPACING = 4f;

    // ── Particles ─────────────────────────────────────────────────────────────
    public static final int   PARTICLE_COUNT    = 5;
    public static final float PARTICLE_SIZE     = 5f;
    public static final float PARTICLE_LIFETIME = 0.4f;

    // ── Glow ──────────────────────────────────────────────────────────────────
    public static final float GLOW_PULSE_FREQ = 1f;

    // ── Splash ────────────────────────────────────────────────────────────────
    public static final float SPLASH_MAX_DURATION = 3.0f;

    // ── Leaderboard ───────────────────────────────────────────────────────────
    public static final int LEADERBOARD_MAX_ENTRIES    = 10;
    public static final int LEADERBOARD_NAME_MAX_CHARS = 12;

    // ── Level count ───────────────────────────────────────────────────────────
    public static final int LEVEL_COUNT = 10;

    // ── Colors (RGBA8888) ────────────────────────────────────────────────────
    public static final int COLOR_BACKGROUND     = 0x0A0A14FF;
    public static final int COLOR_HUD_BAR        = 0x00000099;
    public static final int COLOR_INDESTRUCTIBLE = 0x888899FF;

    // ── Audio defaults ────────────────────────────────────────────────────────
    public static final float DEFAULT_MUSIC_VOLUME = 0.7f;
    public static final float DEFAULT_SFX_VOLUME   = 1.0f;

    // ── Level palette hues (HSV H, levels 1–10) ───────────────────────────────
    public static final float[] LEVEL_HUE = {
        180f, 270f, 30f, 300f, 60f, 210f, 0f, 120f, 45f, 330f
    };

    // ── SharedPreferences ─────────────────────────────────────────────────────
    public static final String PREFS_NAME          = "ArkanoidDuelPrefs";
    public static final String PREF_HIGH_SCORE     = "high_score";
    public static final String PREF_LEADERBOARD    = "leaderboard_json";
    public static final String PREF_MAX_LEVEL      = "max_level_unlocked";
    public static final String PREF_TUTORIAL_DONE  = "tutorial_done";
    public static final String PREF_MUSIC_VOLUME   = "music_volume";
    public static final String PREF_SFX_VOLUME     = "sfx_volume";
    public static final String PREF_VIBRATION      = "vibration_enabled";
    public static final String PREF_MUSIC          = "musicEnabled";
    public static final String PREF_SFX            = "sfxEnabled";

    /** Returns "level_N_stars" key for level N (1-based). */
    public static String prefLevelStars(int n)     { return "level_" + n + "_stars"; }
    /** Returns "level_N_best_score" key for level N (1-based). */
    public static String prefLevelBest(int n)      { return "level_" + n + "_best_score"; }
}
