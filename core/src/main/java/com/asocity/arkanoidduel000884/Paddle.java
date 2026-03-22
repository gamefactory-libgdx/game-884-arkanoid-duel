package com.asocity.arkanoidduel000884;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;

public class Paddle {

    public final Rectangle bounds;
    public boolean isInvincible;
    public float   invincibleTimer;
    public float   widthMultiplier;
    public int     laserShotsLeft;

    // Current rendered alpha (for flash effect)
    public float alpha = 1f;

    public Paddle() {
        bounds = new Rectangle(
            Constants.VP_WIDTH / 2f - Constants.PADDLE_WIDTH / 2f,
            Constants.PADDLE_Y,
            Constants.PADDLE_WIDTH,
            Constants.PADDLE_HEIGHT
        );
        widthMultiplier = 1f;
        laserShotsLeft  = 0;
        isInvincible    = false;
        invincibleTimer = 0f;
    }

    public void update(float delta, float touchX) {
        float w = Constants.PADDLE_WIDTH * widthMultiplier;
        float half = w / 2f;
        float clampedX = MathUtils.clamp(touchX, half, Constants.VP_WIDTH - half);
        bounds.x = clampedX - half;
        bounds.width = w;

        if (isInvincible) {
            invincibleTimer -= delta;
            float phase = (invincibleTimer * Constants.PADDLE_FLASH_FREQ) % 1f;
            alpha = Constants.PADDLE_ALPHA_MIN +
                    (Constants.PADDLE_ALPHA_MAX - Constants.PADDLE_ALPHA_MIN) * phase;
            if (invincibleTimer <= 0f) {
                isInvincible = false;
                alpha = 1f;
            }
        }
    }

    public void onHit() {
        isInvincible    = true;
        invincibleTimer = Constants.PADDLE_INVINCIBLE_DURATION;
    }

    public float getCenterX() {
        return bounds.x + bounds.width / 2f;
    }

    public float getTop() {
        return bounds.y + bounds.height;
    }

    public void resetPosition() {
        float w = Constants.PADDLE_WIDTH * widthMultiplier;
        bounds.x     = Constants.VP_WIDTH / 2f - w / 2f;
        bounds.width = w;
    }
}
