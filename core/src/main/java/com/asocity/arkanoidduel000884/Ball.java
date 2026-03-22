package com.asocity.arkanoidduel000884;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

public class Ball {

    public final Vector2 position;
    public final Vector2 velocity;
    public float   radius;
    public boolean isOnPaddle;
    public boolean isFireball;
    public boolean active;

    public Ball(float x, float y) {
        this.position   = new Vector2(x, y);
        this.velocity   = new Vector2(0, 0);
        this.radius     = Constants.BALL_RADIUS;
        this.isOnPaddle = true;
        this.isFireball = false;
        this.active     = true;
    }

    /** Copy constructor — for multi-ball. */
    public Ball(Ball source) {
        this.position   = new Vector2(source.position);
        this.velocity   = new Vector2(source.velocity).scl(-1f, 1f); // diverge left
        this.radius     = source.radius;
        this.isOnPaddle = false;
        this.isFireball = source.isFireball;
        this.active     = true;
    }

    public void update(float delta) {
        if (isOnPaddle) return;
        // Sub-step movement to prevent tunneling
        float speed = velocity.len();
        int steps = Math.max(1, (int) Math.ceil(speed * delta / (2f * radius)));
        float dt = delta / steps;
        for (int i = 0; i < steps; i++) {
            position.mulAdd(velocity, dt);
        }
    }

    public void launch(float angleDeg) {
        isOnPaddle = false;
        float speed = Constants.BALL_SPEED_BASE;
        velocity.set(
            speed * MathUtils.cosDeg(angleDeg),
            speed * MathUtils.sinDeg(angleDeg)
        );
    }

    public void bounceX() { velocity.x *= -1f; }
    public void bounceY() { velocity.y *= -1f; }

    public void setSpeed(float px) {
        float len = velocity.len();
        if (len == 0) return;
        float clamped = Math.min(px, Constants.BALL_MAX_SPEED);
        velocity.scl(clamped / len);
    }

    public float getSpeed() { return velocity.len(); }

    /** Reflect Y and compute angle based on hit offset from paddle center. */
    public void bounceOffPaddle(float paddleCenterX, float paddleWidth) {
        float offset = (position.x - paddleCenterX) / (paddleWidth / 2f);
        offset = MathUtils.clamp(offset, -1f, 1f);
        float angle = 90f + offset * -Constants.BALL_ANGLE_RANGE;
        float speed = Math.max(getSpeed(), Constants.BALL_SPEED_BASE);
        velocity.set(
            speed * MathUtils.cosDeg(angle),
            speed * MathUtils.sinDeg(angle)
        );
        // Ensure ball moves upward
        if (velocity.y < 0) velocity.y = -velocity.y;
    }
}
