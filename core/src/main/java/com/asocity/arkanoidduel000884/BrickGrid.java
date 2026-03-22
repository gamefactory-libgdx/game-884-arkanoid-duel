package com.asocity.arkanoidduel000884;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;

public class BrickGrid {

    public final Array<Brick> bricks = new Array<>();
    private int levelNumber;
    private int projMin;
    private int projMax;

    // Grid layout constants (derived from Constants)
    private static final float START_X = (Constants.VP_WIDTH
        - Constants.BRICK_COLS * Constants.BRICK_WIDTH
        - (Constants.BRICK_COLS - 1) * Constants.BRICK_MARGIN_X) / 2f;

    public void buildLevel(int level) {
        levelNumber = level;
        bricks.clear();
        LevelConfig cfg = LevelConfig.forLevel(level);
        projMin = cfg.projectileCountMin;
        projMax = cfg.projectileCountMax;

        int cols = Constants.BRICK_COLS;
        int rows = cfg.brickRows;
        int total = cols * rows;

        // Assign types by percentage
        int indestructCount = Math.round(total * cfg.indestructiblePct);
        int powerCount      = Math.round(total * cfg.powerBrickPct);
        int specialCount    = Math.round(total * 0.10f);
        int toughCount      = Math.round(total * 0.15f);
        int normalCount     = total - indestructCount - powerCount - specialCount - toughCount;
        normalCount = Math.max(normalCount, 0);

        // Build a flat array of types
        BrickType[] types = new BrickType[total];
        int idx = 0;
        for (int i = 0; i < indestructCount && idx < total; i++) types[idx++] = BrickType.INDESTRUCTIBLE;
        for (int i = 0; i < powerCount && idx < total; i++)      types[idx++] = BrickType.POWER;
        for (int i = 0; i < specialCount && idx < total; i++)    types[idx++] = BrickType.SPECIAL;
        for (int i = 0; i < toughCount && idx < total; i++)      types[idx++] = BrickType.TOUGH;
        while (idx < total)                                       types[idx++] = BrickType.NORMAL;

        // Shuffle
        for (int i = total - 1; i > 0; i--) {
            int j = MathUtils.random(i);
            BrickType tmp = types[i];
            types[i] = types[j];
            types[j] = tmp;
        }

        // Place bricks
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                float x = START_X + col * (Constants.BRICK_WIDTH + Constants.BRICK_MARGIN_X);
                float y = Constants.BRICK_GRID_TOP_Y
                    - (row + 1) * (Constants.BRICK_HEIGHT + Constants.BRICK_MARGIN_Y)
                    + Constants.BRICK_MARGIN_Y;
                bricks.add(new Brick(x, y,
                    Constants.BRICK_WIDTH, Constants.BRICK_HEIGHT,
                    types[row * cols + col]));
            }
        }
    }

    public Array<Brick> getActiveBricks() {
        Array<Brick> active = new Array<>();
        for (Brick b : bricks) if (b.active) active.add(b);
        return active;
    }

    public boolean allCleared() {
        for (Brick b : bricks) {
            if (b.active && b.type != BrickType.INDESTRUCTIBLE) return false;
        }
        return true;
    }

    public int getLevelNumber()   { return levelNumber; }
    public int getProjMin()        { return projMin; }
    public int getProjMax()        { return projMax; }
}
