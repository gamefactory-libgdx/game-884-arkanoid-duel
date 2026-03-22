package com.asocity.arkanoidduel000884;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;

public class PowerUpState {

    // Maps PowerUpType to remaining timer (seconds) or shot-count for LASER/MULTI_BALL
    private final ObjectMap<PowerUpType, Float> activeEffects = new ObjectMap<>();

    public boolean shieldAlive = false;
    private float normalBallSpeed = Constants.BALL_SPEED_BASE;

    public void activate(PowerUpType type, Paddle paddle, Array<Ball> balls) {
        switch (type) {
            case WIDE_PADDLE:
                paddle.widthMultiplier = Constants.PADDLE_WIDE_MULT;
                activeEffects.put(PowerUpType.WIDE_PADDLE, Constants.POWERUP_WIDE_DURATION);
                break;
            case SLOW_BALL:
                for (Ball b : balls) {
                    normalBallSpeed = b.getSpeed();
                    b.setSpeed(b.getSpeed() * Constants.POWERUP_SLOW_FACTOR);
                }
                activeEffects.put(PowerUpType.SLOW_BALL, Constants.POWERUP_SLOW_DURATION);
                break;
            case MULTI_BALL:
                if (balls.size > 0 && balls.size < 4) {
                    Ball src = balls.first();
                    balls.add(new Ball(src));
                }
                activeEffects.put(PowerUpType.MULTI_BALL, 1f); // marker only
                break;
            case SHIELD:
                shieldAlive = true;
                activeEffects.put(PowerUpType.SHIELD, 1f);
                break;
            case LASER:
                paddle.laserShotsLeft = Constants.POWERUP_LASER_SHOTS;
                activeEffects.put(PowerUpType.LASER, (float) Constants.POWERUP_LASER_SHOTS);
                break;
            case FIREBALL:
                for (Ball b : balls) b.isFireball = true;
                activeEffects.put(PowerUpType.FIREBALL, Constants.POWERUP_FIREBALL_DURATION);
                break;
        }
    }

    public void update(float delta, Paddle paddle, Array<Ball> balls) {
        Array<PowerUpType> toRemove = new Array<>();
        for (ObjectMap.Entry<PowerUpType, Float> e : activeEffects.entries()) {
            if (e.key == PowerUpType.LASER) continue; // managed by shot count
            if (e.key == PowerUpType.SHIELD) continue; // managed by shield event
            if (e.key == PowerUpType.MULTI_BALL) continue; // permanent until balls gone
            e.value -= delta;
            if (e.value <= 0f) toRemove.add(e.key);
        }
        for (PowerUpType t : toRemove) {
            expire(t, paddle, balls);
        }
    }

    private void expire(PowerUpType type, Paddle paddle, Array<Ball> balls) {
        activeEffects.remove(type);
        switch (type) {
            case WIDE_PADDLE:
                paddle.widthMultiplier = 1f;
                float w = Constants.PADDLE_WIDTH;
                paddle.bounds.x     = paddle.getCenterX() - w / 2f;
                paddle.bounds.width = w;
                break;
            case SLOW_BALL:
                for (Ball b : balls) {
                    b.setSpeed(normalBallSpeed);
                }
                break;
            case FIREBALL:
                for (Ball b : balls) b.isFireball = false;
                break;
            default:
                break;
        }
    }

    public boolean isActive(PowerUpType type) {
        return activeEffects.containsKey(type);
    }

    public float getTimer(PowerUpType type) {
        return activeEffects.get(type, 0f);
    }

    public void consumeShield() {
        shieldAlive = false;
        activeEffects.remove(PowerUpType.SHIELD);
    }

    public void consumeLaserShot(Paddle paddle) {
        if (paddle.laserShotsLeft > 0) {
            paddle.laserShotsLeft--;
            if (paddle.laserShotsLeft <= 0) {
                activeEffects.remove(PowerUpType.LASER);
            }
        }
    }

    public ObjectMap<PowerUpType, Float> getActiveEffects() {
        return activeEffects;
    }
}
