package com.asocity.arkanoidduel000884;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class Projectile {

    public final Rectangle bounds;
    public final Vector2   velocity;
    public boolean active;

    /** Normal downward projectile from Power Brick. */
    public Projectile(float cx, float cy) {
        float angle = 270f + MathUtils.random(
            -Constants.PROJECTILE_ANGLE_RANGE, Constants.PROJECTILE_ANGLE_RANGE);
        velocity = new Vector2(
            Constants.PROJECTILE_SPEED * MathUtils.cosDeg(angle),
            Constants.PROJECTILE_SPEED * MathUtils.sinDeg(angle)
        );
        bounds = new Rectangle(
            cx - Constants.PROJECTILE_WIDTH / 2f,
            cy - Constants.PROJECTILE_HEIGHT / 2f,
            Constants.PROJECTILE_WIDTH,
            Constants.PROJECTILE_HEIGHT
        );
        active = true;
    }

    /** Upward laser shot from paddle. */
    public Projectile(float x, float y, boolean isLaser) {
        velocity = new Vector2(0, Constants.LASER_SPEED);
        bounds   = new Rectangle(
            x - Constants.LASER_WIDTH / 2f,
            y,
            Constants.LASER_WIDTH,
            Constants.PROJECTILE_HEIGHT
        );
        active = true;
    }

    public void update(float delta) {
        if (!active) return;
        bounds.x += velocity.x * delta;
        bounds.y += velocity.y * delta;
        if (bounds.y < -bounds.height || bounds.y > Constants.VP_HEIGHT + bounds.height) {
            active = false;
        }
    }
}
