package com.asocity.arkanoidduel000884;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;

public class ParticlePool {

    private static final int MAX = 256;

    private final float[] px       = new float[MAX];
    private final float[] py       = new float[MAX];
    private final float[] vx       = new float[MAX];
    private final float[] vy       = new float[MAX];
    private final float[] lifetime = new float[MAX];
    private final float[] r        = new float[MAX];
    private final float[] g        = new float[MAX];
    private final float[] b        = new float[MAX];
    private int count = 0;

    public void spawn(float x, float y, Color color) {
        int num = MathUtils.random(4, 6);
        for (int i = 0; i < num && count < MAX; i++) {
            float angle = MathUtils.random(360f);
            float speed = MathUtils.random(40f, 120f);
            px[count]       = x;
            py[count]       = y;
            vx[count]       = speed * MathUtils.cosDeg(angle);
            vy[count]       = speed * MathUtils.sinDeg(angle);
            lifetime[count] = Constants.PARTICLE_LIFETIME;
            r[count]        = color.r;
            g[count]        = color.g;
            b[count]        = color.b;
            count++;
        }
    }

    public void update(float delta) {
        int i = 0;
        while (i < count) {
            lifetime[i] -= delta;
            if (lifetime[i] <= 0f) {
                // Replace with last
                count--;
                px[i]       = px[count];
                py[i]       = py[count];
                vx[i]       = vx[count];
                vy[i]       = vy[count];
                lifetime[i] = lifetime[count];
                r[i] = r[count]; g[i] = g[count]; b[i] = b[count];
            } else {
                px[i] += vx[i] * delta;
                py[i] += vy[i] * delta;
                i++;
            }
        }
    }

    public void render(ShapeRenderer sr) {
        float half = Constants.PARTICLE_SIZE / 2f;
        for (int i = 0; i < count; i++) {
            float alpha = lifetime[i] / Constants.PARTICLE_LIFETIME;
            sr.setColor(r[i], g[i], b[i], alpha);
            sr.rect(px[i] - half, py[i] - half,
                    Constants.PARTICLE_SIZE, Constants.PARTICLE_SIZE);
        }
    }
}
