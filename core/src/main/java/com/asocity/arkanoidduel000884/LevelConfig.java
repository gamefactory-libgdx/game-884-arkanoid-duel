package com.asocity.arkanoidduel000884;

public class LevelConfig {

    public final int   level;
    public final float ballSpeed;
    public final int   brickRows;
    public final float powerBrickPct;
    public final int   projectileCountMin;
    public final int   projectileCountMax;
    public final float indestructiblePct;

    private LevelConfig(int level, float ballSpeed, int brickRows,
                        float powerBrickPct, int projMin, int projMax,
                        float indestructPct) {
        this.level              = level;
        this.ballSpeed          = ballSpeed;
        this.brickRows          = brickRows;
        this.powerBrickPct      = powerBrickPct;
        this.projectileCountMin = projMin;
        this.projectileCountMax = projMax;
        this.indestructiblePct  = indestructPct;
    }

    private static final LevelConfig[] TABLE = {
        new LevelConfig(1,  300f, 4, 0.05f, 1, 1, 0.00f),
        new LevelConfig(2,  330f, 5, 0.08f, 1, 1, 0.00f),
        new LevelConfig(3,  360f, 5, 0.10f, 1, 2, 0.05f),
        new LevelConfig(4,  390f, 6, 0.12f, 1, 2, 0.05f),
        new LevelConfig(5,  420f, 6, 0.15f, 2, 2, 0.08f),
        new LevelConfig(6,  450f, 7, 0.18f, 2, 2, 0.08f),
        new LevelConfig(7,  480f, 7, 0.20f, 2, 3, 0.10f),
        new LevelConfig(8,  510f, 8, 0.22f, 2, 3, 0.10f),
        new LevelConfig(9,  540f, 8, 0.25f, 3, 3, 0.12f),
        new LevelConfig(10, 600f, 9, 0.30f, 3, 3, 0.15f),
    };

    /** Returns config for 1-based level number. Clamps to [1,10]. */
    public static LevelConfig forLevel(int n) {
        int idx = Math.max(0, Math.min(n - 1, TABLE.length - 1));
        return TABLE[idx];
    }
}
