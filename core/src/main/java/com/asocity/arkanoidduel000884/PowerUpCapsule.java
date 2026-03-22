package com.asocity.arkanoidduel000884;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class PowerUpCapsule {

    public final Rectangle   bounds;
    public final PowerUpType type;
    public final Vector2     velocity;
    public boolean active;

    public PowerUpCapsule(float cx, float cy) {
        PowerUpType[] values = PowerUpType.values();
        type = values[MathUtils.random(values.length - 1)];
        velocity = new Vector2(0, -Constants.POWERUP_FALL_SPEED);
        bounds = new Rectangle(
            cx - Constants.POWERUP_SIZE / 2f,
            cy,
            Constants.POWERUP_SIZE,
            Constants.POWERUP_SIZE
        );
        active = true;
    }

    public void update(float delta) {
        if (!active) return;
        bounds.y += velocity.y * delta;
        if (bounds.y < -bounds.height) active = false;
    }
}
