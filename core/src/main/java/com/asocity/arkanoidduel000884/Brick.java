package com.asocity.arkanoidduel000884;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;

public class Brick {

    public final Rectangle bounds;
    public final BrickType type;
    public int     hp;
    public boolean active;
    public boolean cracked;  // Tough brick after 1st hit

    public Brick(float x, float y, float w, float h, BrickType type) {
        this.bounds  = new Rectangle(x, y, w, h);
        this.type    = type;
        this.active  = true;
        this.cracked = false;
        switch (type) {
            case TOUGH:          hp = Constants.HP_TOUGH;   break;
            case NORMAL:         hp = Constants.HP_NORMAL;  break;
            case POWER:          hp = Constants.HP_POWER;   break;
            case SPECIAL:        hp = Constants.HP_SPECIAL; break;
            case INDESTRUCTIBLE: hp = Integer.MAX_VALUE;    break;
            default:             hp = 1;
        }
    }

    /** Process a hit. Returns a BrickHitResult describing what happened. */
    public BrickHitResult onHit(int projMin, int projMax) {
        if (!active) return BrickHitResult.NONE;
        if (type == BrickType.INDESTRUCTIBLE) return BrickHitResult.NONE;

        hp--;
        float cx = bounds.x + bounds.width / 2f;
        float cy = bounds.y + bounds.height / 2f;

        if (hp <= 0) {
            active = false;
            boolean fire = (type == BrickType.POWER);
            int projCount = fire ? MathUtils.random(projMin, projMax) : 0;
            boolean dropPU = (type == BrickType.SPECIAL);
            return new BrickHitResult(true, fire, projCount, dropPU, type, cx, cy);
        }

        if (type == BrickType.TOUGH) {
            cracked = true;
        }
        return new BrickHitResult(false, false, 0, false, type, cx, cy);
    }

    public boolean isAlive() {
        return active && (type == BrickType.INDESTRUCTIBLE || hp > 0);
    }
}
