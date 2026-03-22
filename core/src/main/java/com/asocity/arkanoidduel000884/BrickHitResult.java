package com.asocity.arkanoidduel000884;

public class BrickHitResult {
    public final boolean destroyed;
    public final boolean fireProjectiles;
    public final int     projectileCount;
    public final boolean dropPowerUp;
    public final BrickType brickType;
    public final float     brickCenterX;
    public final float     brickCenterY;

    public BrickHitResult(boolean destroyed, boolean fireProjectiles,
                          int projectileCount, boolean dropPowerUp,
                          BrickType brickType, float cx, float cy) {
        this.destroyed       = destroyed;
        this.fireProjectiles = fireProjectiles;
        this.projectileCount = projectileCount;
        this.dropPowerUp     = dropPowerUp;
        this.brickType       = brickType;
        this.brickCenterX    = cx;
        this.brickCenterY    = cy;
    }

    public static final BrickHitResult NONE = new BrickHitResult(
            false, false, 0, false, BrickType.NORMAL, 0, 0);
}
