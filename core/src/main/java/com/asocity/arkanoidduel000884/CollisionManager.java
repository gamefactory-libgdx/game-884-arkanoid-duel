package com.asocity.arkanoidduel000884;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;

public final class CollisionManager {

    private CollisionManager() {}

    /**
     * Check ball vs walls and ceiling. Returns true if ball fell below y=0.
     */
    public static boolean ballVsWalls(Ball b) {
        // Left wall
        if (b.position.x - b.radius < 0) {
            b.position.x = b.radius;
            b.bounceX();
        }
        // Right wall
        if (b.position.x + b.radius > Constants.VP_WIDTH) {
            b.position.x = Constants.VP_WIDTH - b.radius;
            b.bounceX();
        }
        // Ceiling
        if (b.position.y + b.radius > Constants.VP_HEIGHT) {
            b.position.y = Constants.VP_HEIGHT - b.radius;
            b.bounceY();
        }
        // Bottom — ball lost
        return b.position.y - b.radius < 0;
    }

    /**
     * Check ball vs paddle. Returns true if collision occurred.
     */
    public static boolean ballVsPaddle(Ball b, Paddle p) {
        if (b.isOnPaddle) return false;
        Rectangle pb = p.bounds;
        // Ball must be moving downward and near the paddle
        if (b.velocity.y > 0) return false;

        float ballLeft   = b.position.x - b.radius;
        float ballRight  = b.position.x + b.radius;
        float ballBottom = b.position.y - b.radius;
        float ballTop    = b.position.y + b.radius;

        if (ballRight < pb.x || ballLeft > pb.x + pb.width) return false;
        if (ballBottom > pb.y + pb.height || ballTop < pb.y) return false;

        // Resolve overlap
        b.position.y = pb.y + pb.height + b.radius;
        b.bounceOffPaddle(p.getCenterX(), p.bounds.width);
        return true;
    }

    /**
     * Check ball vs bricks. Returns first BrickHitResult or NONE.
     */
    public static BrickHitResult ballVsBricks(Ball b, Array<Brick> bricks,
                                              int projMin, int projMax) {
        float bLeft   = b.position.x - b.radius;
        float bRight  = b.position.x + b.radius;
        float bBottom = b.position.y - b.radius;
        float bTop    = b.position.y + b.radius;

        BrickHitResult result = BrickHitResult.NONE;

        for (Brick brick : bricks) {
            if (!brick.active) continue;

            Rectangle r = brick.bounds;
            if (bRight < r.x || bLeft > r.x + r.width) continue;
            if (bTop   < r.y || bBottom > r.y + r.height) continue;

            // Determine bounce direction (hit from side or top/bottom)
            float overlapLeft   = bRight - r.x;
            float overlapRight  = r.x + r.width - bLeft;
            float overlapBottom = bTop - r.y;
            float overlapTop    = r.y + r.height - bBottom;

            float minH = Math.min(overlapLeft, overlapRight);
            float minV = Math.min(overlapBottom, overlapTop);

            if (!b.isFireball) {
                if (minH < minV) {
                    b.bounceX();
                    if (overlapLeft < overlapRight) b.position.x = r.x - b.radius;
                    else                            b.position.x = r.x + r.width + b.radius;
                } else {
                    b.bounceY();
                    if (overlapBottom < overlapTop) b.position.y = r.y - b.radius;
                    else                            b.position.y = r.y + r.height + b.radius;
                }
            }

            BrickHitResult hit = brick.onHit(projMin, projMax);
            if (hit != BrickHitResult.NONE) result = hit;

            if (!b.isFireball) break; // Fireball continues through bricks
        }
        return result;
    }

    /**
     * Check projectile vs paddle. Returns true if hit.
     */
    public static boolean projectileVsPaddle(Projectile pr, Paddle p) {
        return pr.active && pr.bounds.overlaps(p.bounds);
    }

    /**
     * Check power-up capsule vs paddle. Returns true if caught.
     */
    public static boolean capsuleVsPaddle(PowerUpCapsule c, Paddle p) {
        return c.active && c.bounds.overlaps(p.bounds);
    }

    /**
     * Check laser (upward projectile) vs a brick. Returns hit result or NONE.
     */
    public static BrickHitResult laserVsBrick(Projectile laser, Brick brick,
                                              int projMin, int projMax) {
        if (!laser.active || !brick.active || brick.type == BrickType.INDESTRUCTIBLE) {
            return BrickHitResult.NONE;
        }
        if (laser.bounds.overlaps(brick.bounds)) {
            laser.active = false;
            return brick.onHit(projMin, projMax);
        }
        return BrickHitResult.NONE;
    }
}
