package com.asocity.arkanoidduel000884.screens;

import com.asocity.arkanoidduel000884.Ball;
import com.asocity.arkanoidduel000884.Brick;
import com.asocity.arkanoidduel000884.BrickGrid;
import com.asocity.arkanoidduel000884.BrickHitResult;
import com.asocity.arkanoidduel000884.BrickType;
import com.asocity.arkanoidduel000884.CollisionManager;
import com.asocity.arkanoidduel000884.Constants;
import com.asocity.arkanoidduel000884.LevelConfig;
import com.asocity.arkanoidduel000884.MainGame;
import com.asocity.arkanoidduel000884.Paddle;
import com.asocity.arkanoidduel000884.ParticlePool;
import com.asocity.arkanoidduel000884.PowerUpCapsule;
import com.asocity.arkanoidduel000884.PowerUpState;
import com.asocity.arkanoidduel000884.PowerUpType;
import com.asocity.arkanoidduel000884.Projectile;
import com.asocity.arkanoidduel000884.SaveData;
import com.asocity.arkanoidduel000884.ScoreManager;
import com.asocity.arkanoidduel000884.UiFactory;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.StretchViewport;

public class GameScreen implements Screen {

    private static final String BG_GAME    = "backgrounds/game/1.png";
    private static final String PADDLE_TEX = "sprites/object/Platform.png";
    private static final String BRICK_TEX  =
        "sprites/tileset/Cartoon_Medieval_Guard_Post_2D_Level_Set_Building-Wall-A-01.png";
    private static final String LIVES_TEX  = "sprites/ui/lifes.png";

    private final MainGame      game;
    private final int           levelNumber;
    private final StretchViewport viewport;
    private final Stage          stage;
    private final ShapeRenderer  sr;
    private final Vector3        touchPos = new Vector3();

    // Game objects
    private Paddle         paddle;
    private Array<Ball>    balls;
    private BrickGrid      brickGrid;
    private Array<Projectile>     projectiles;
    private Array<Projectile>     lasers;
    private Array<PowerUpCapsule> capsules;
    private PowerUpState   powerUpState;
    private ScoreManager   scoreManager;
    private ParticlePool   particles;

    // State
    private int     lives;
    private boolean paused;
    private boolean gameOver;
    private boolean levelWon;
    private float   glowTimer = 0f;

    // Level config
    private LevelConfig lvlCfg;

    // HUD labels (updated each frame)
    private Label scoreLabel;
    private Label levelLabel;

    // Colors per brick type
    private static final Color[] BRICK_COLORS = {
        new Color(0.4f, 0.7f, 1.0f, 1f),    // NORMAL  — blue
        new Color(0.6f, 0.3f, 0.9f, 1f),    // TOUGH   — purple
        new Color(1.0f, 0.3f, 0.2f, 1f),    // POWER   — red
        new Color(0.2f, 0.9f, 0.4f, 1f),    // SPECIAL — green
        new Color(0.5f, 0.5f, 0.6f, 1f),    // INDESTR — grey
    };

    // Particle colors per brick type
    private static final Color[] PARTICLE_COLORS = {
        Color.CYAN,  Color.VIOLET, Color.ORANGE, Color.GREEN, Color.GRAY
    };

    public GameScreen(MainGame game, int levelNumber) {
        this.game        = game;
        this.levelNumber = levelNumber;
        this.lvlCfg      = LevelConfig.forLevel(levelNumber);

        viewport = new StretchViewport(Constants.VP_WIDTH, Constants.VP_HEIGHT);
        stage    = new Stage(viewport, game.batch);
        sr       = new ShapeRenderer();

        game.playMusic("sounds/music/music_gameplay.ogg");

        initGame();
        buildHUD();
        setupInput();
    }

    private void initGame() {
        lives         = Constants.LIVES_PER_LEVEL;
        paused        = false;
        gameOver      = false;
        levelWon      = false;
        projectiles   = new Array<>();
        lasers        = new Array<>();
        capsules      = new Array<>();
        scoreManager  = new ScoreManager();
        powerUpState  = new PowerUpState();
        particles     = new ParticlePool();

        // Brick grid
        brickGrid = new BrickGrid();
        brickGrid.buildLevel(levelNumber);

        // Paddle
        paddle = new Paddle();

        // Ball — starts on paddle
        balls = new Array<>();
        resetBall();
    }

    private void resetBall() {
        Ball b = new Ball(paddle.getCenterX(), paddle.getTop() + Constants.BALL_RADIUS);
        b.isOnPaddle = true;
        balls.add(b);
    }

    private void buildHUD() {
        stage.clear();

        // Score label
        Label.LabelStyle scoreStyle = new Label.LabelStyle(game.fontSmall, Color.WHITE);
        scoreLabel = new Label("0", scoreStyle);
        scoreLabel.setAlignment(Align.center);
        scoreLabel.setWidth(200f);
        scoreLabel.setPosition((Constants.VP_WIDTH - 200f) / 2f,
            Constants.VP_HEIGHT - 32f);
        stage.addActor(scoreLabel);

        // Level label
        Label.LabelStyle lvlStyle = new Label.LabelStyle(game.fontSmall, Color.CYAN);
        levelLabel = new Label("LVL " + levelNumber, lvlStyle);
        levelLabel.setPosition(8f, Constants.VP_HEIGHT - 32f);
        stage.addActor(levelLabel);

        // Pause button
        TextButton.TextButtonStyle roundStyle = UiFactory.makeRoundStyle(game.manager, game.fontSmall);
        TextButton pauseBtn = UiFactory.makeRoundButton("II", roundStyle, 36f);
        pauseBtn.setPosition(Constants.VP_WIDTH - 44f, Constants.VP_HEIGHT - 40f);
        pauseBtn.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.playSound("sounds/sfx/sfx_button_click.ogg");
                game.setScreen(new PauseScreen(game, GameScreen.this));
            }
        });
        stage.addActor(pauseBtn);
    }

    private void setupInput() {
        stage.addListener(new InputListener() {
            @Override
            public boolean keyDown(InputEvent event, int keycode) {
                if (keycode == Input.Keys.BACK) {
                    game.setScreen(new PauseScreen(game, GameScreen.this));
                    return true;
                }
                return false;
            }
        });
        Gdx.input.setInputProcessor(stage);
    }

    // ── Update ────────────────────────────────────────────────────────────────

    private void update(float delta) {
        if (gameOver || levelWon) return;

        delta = Math.min(delta, Constants.DELTA_CAP);
        glowTimer += delta;

        // Touch input — paddle movement (continuous)
        if (Gdx.input.isTouched()) {
            touchPos.set(Gdx.input.getX(), Gdx.input.getY(), 0);
            viewport.unproject(touchPos);
            paddle.update(delta, touchPos.x);
        } else {
            paddle.update(delta, paddle.getCenterX());
        }

        // Tap detection — launch ball or fire laser
        if (Gdx.input.justTouched()) {
            launchBallIfOnPaddle();
            if (powerUpState.isActive(PowerUpType.LASER)
                    && paddle.laserShotsLeft > 0 && noBallOnPaddle()) {
                fireLaser();
            }
        }

        // Update balls
        Array<Ball> toRemove = new Array<>();
        for (Ball b : balls) {
            if (!b.active) { toRemove.add(b); continue; }
            if (b.isOnPaddle) {
                b.position.set(paddle.getCenterX(), paddle.getTop() + b.radius);
                continue;
            }
            b.update(delta);

            // Wall collision
            boolean fell = CollisionManager.ballVsWalls(b);
            if (fell) {
                handleBallLost(b, toRemove);
                continue;
            }

            // Shield collision
            if (powerUpState.shieldAlive && b.position.y - b.radius < Constants.SHIELD_Y_OFFSET + paddle.getTop()) {
                if (b.velocity.y < 0) {
                    b.bounceY();
                    powerUpState.consumeShield();
                }
            }

            // Paddle collision
            if (CollisionManager.ballVsPaddle(b, paddle)) {
                game.playSound("sounds/sfx/sfx_hit.ogg");
            }

            // Brick collision
            BrickHitResult hit = CollisionManager.ballVsBricks(b, brickGrid.bricks,
                brickGrid.getProjMin(), brickGrid.getProjMax());
            processHitResult(hit);
        }
        balls.removeAll(toRemove, true);

        // Ensure at least one ball — add if all lost (handled in handleBallLost)
        if (balls.size == 0 && !gameOver) {
            lives--;
            if (lives <= 0) {
                triggerGameOver();
            } else {
                resetBall();
            }
        }

        // Multi-ball multiplier
        scoreManager.setMultiballMultiplier(balls.size > 1);

        // Projectiles (downward from Power Bricks)
        for (Projectile pr : projectiles) {
            if (!pr.active) continue;
            pr.update(delta);
            if (!paddle.isInvincible && CollisionManager.projectileVsPaddle(pr, paddle)) {
                pr.active = false;
                game.playSound("sounds/sfx/sfx_hit.ogg");
                paddle.onHit();
                lives--;
                if (lives <= 0) {
                    triggerGameOver();
                    return;
                }
            }
        }
        projectiles.removeAll(getInactive(projectiles), true);

        // Lasers (upward from paddle)
        for (Projectile laser : lasers) {
            if (!laser.active) continue;
            laser.update(delta);
            for (Brick brick : brickGrid.bricks) {
                BrickHitResult r = CollisionManager.laserVsBrick(laser,
                    brick, brickGrid.getProjMin(), brickGrid.getProjMax());
                processHitResult(r);
                if (!laser.active) break;
            }
        }
        lasers.removeAll(getInactive(lasers), true);

        // Power-up capsules
        for (PowerUpCapsule c : capsules) {
            if (!c.active) continue;
            c.update(delta);
            if (CollisionManager.capsuleVsPaddle(c, paddle)) {
                c.active = false;
                game.playSound("sounds/sfx/sfx_power_up.ogg");
                scoreManager.add(Constants.SCORE_POWERUP_COLLECT);
                powerUpState.activate(c.type, paddle, balls);
            }
        }
        capsules.removeAll(getInactiveCapsules(capsules), true);

        // Power-up timers
        powerUpState.update(delta, paddle, balls);

        // Level win check
        if (brickGrid.allCleared()) {
            triggerLevelWon();
        }

        // Update HUD
        scoreLabel.setText(String.valueOf(scoreManager.currentScore));
    }

    private boolean noBallOnPaddle() {
        for (Ball b : balls) if (b.isOnPaddle) return false;
        return true;
    }

    private void launchBallIfOnPaddle() {
        for (Ball b : balls) {
            if (b.isOnPaddle) {
                b.launch(Constants.BALL_LAUNCH_ANGLE_DEFAULT);
                b.setSpeed(lvlCfg.ballSpeed);
                break;
            }
        }
    }

    private void fireLaser() {
        Projectile laser = new Projectile(paddle.getCenterX(), paddle.getTop(), true);
        lasers.add(laser);
        powerUpState.consumeLaserShot(paddle);
        game.playSound("sounds/sfx/sfx_shoot.ogg");
    }

    private void handleBallLost(Ball b, Array<Ball> toRemove) {
        b.active = false;
        toRemove.add(b);
        // If this was the last ball, lives will be decremented in the balls.size==0 check
    }

    private void processHitResult(BrickHitResult hit) {
        if (hit == BrickHitResult.NONE) return;

        game.playSound("sounds/sfx/sfx_hit.ogg");

        if (hit.destroyed) {
            int points = 0;
            int typeIdx = 0;
            switch (hit.brickType) {
                case NORMAL:  points = Constants.SCORE_NORMAL;  typeIdx = 0; break;
                case TOUGH:   points = Constants.SCORE_TOUGH;   typeIdx = 1; break;
                case POWER:   points = Constants.SCORE_POWER;   typeIdx = 2; break;
                case SPECIAL: points = Constants.SCORE_SPECIAL; typeIdx = 3; break;
                default: break;
            }
            scoreManager.add(points);
            SaveData.setHighScore(scoreManager.currentScore);

            // Particles
            Color pc = PARTICLE_COLORS[Math.min(typeIdx, PARTICLE_COLORS.length - 1)];
            Color levelColor = hsvToColor(Constants.LEVEL_HUE[
                Math.min(levelNumber - 1, Constants.LEVEL_HUE.length - 1)]);
            pc = (hit.brickType == BrickType.NORMAL) ? levelColor : pc;
            particles.spawn(hit.brickCenterX, hit.brickCenterY, pc);

            if (hit.fireProjectiles) {
                game.playSound("sounds/sfx/sfx_shoot.ogg");
                for (int i = 0; i < hit.projectileCount; i++) {
                    projectiles.add(new Projectile(hit.brickCenterX, hit.brickCenterY));
                }
            }
            if (hit.dropPowerUp) {
                capsules.add(new PowerUpCapsule(hit.brickCenterX, hit.brickCenterY));
            }
        }
    }

    private void triggerLevelWon() {
        if (levelWon) return;
        levelWon = true;
        scoreManager.addLevelClear(levelNumber, lives);
        SaveData.setHighScore(scoreManager.currentScore);
        SaveData.setLevelBestScore(levelNumber, scoreManager.currentScore);
        int stars = lives >= 3 ? 3 : lives >= 2 ? 2 : 1;
        SaveData.setLevelStars(levelNumber, stars);
        if (levelNumber < Constants.LEVEL_COUNT) {
            SaveData.unlockLevel(levelNumber + 1);
        }
        game.playSound("sounds/sfx/sfx_level_complete.ogg");
        game.setScreen(new VictoryScreen(game, levelNumber, scoreManager.currentScore, lives));
    }

    private void triggerGameOver() {
        if (gameOver) return;
        gameOver = true;
        SaveData.setHighScore(scoreManager.currentScore);
        game.playMusicOnce("sounds/music/music_game_over.ogg");
        game.playSound("sounds/sfx/sfx_game_over.ogg");
        game.setScreen(new GameOverScreen(game, scoreManager.currentScore, levelNumber));
    }

    private Array<Projectile> getInactive(Array<Projectile> list) {
        Array<Projectile> dead = new Array<>();
        for (Projectile p : list) if (!p.active) dead.add(p);
        return dead;
    }

    private Array<PowerUpCapsule> getInactiveCapsules(Array<PowerUpCapsule> list) {
        Array<PowerUpCapsule> dead = new Array<>();
        for (PowerUpCapsule c : list) if (!c.active) dead.add(c);
        return dead;
    }

    // ── Render ────────────────────────────────────────────────────────────────

    @Override
    public void render(float delta) {
        if (!paused) {
            update(delta);
        }

        Gdx.gl.glClearColor(0.04f, 0.04f, 0.08f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        viewport.apply(true);
        game.batch.setProjectionMatrix(viewport.getCamera().combined);

        // 1. Background
        game.batch.begin();
        game.batch.draw(
            game.manager.get(BG_GAME, Texture.class),
            0, 0, Constants.VP_WIDTH, Constants.VP_HEIGHT
        );
        game.batch.end();

        // 2. Shield line
        if (powerUpState.shieldAlive) {
            sr.setProjectionMatrix(viewport.getCamera().combined);
            sr.begin(ShapeRenderer.ShapeType.Filled);
            sr.setColor(0.2f, 0.9f, 0.4f, 0.7f);
            float sy = paddle.getTop() + Constants.SHIELD_Y_OFFSET;
            sr.rect(0, sy, Constants.VP_WIDTH, Constants.SHIELD_HEIGHT);
            sr.end();
        }

        // 3. Bricks (SpriteBatch with tinting)
        renderBricks();

        // 4. Power-up capsules
        renderCapsules();

        // 5. Projectiles (ShapeRenderer)
        sr.setProjectionMatrix(viewport.getCamera().combined);
        sr.begin(ShapeRenderer.ShapeType.Filled);
        renderProjectiles();
        renderLasers();
        // 6. Ball(s)
        renderBalls();
        // 7. Particles
        particles.render(sr);
        sr.end();

        // 8. Paddle (SpriteBatch)
        renderPaddle();

        // 9. HUD
        renderLivesHUD();

        stage.act(delta);
        stage.draw();
    }

    private void renderBricks() {
        Texture brickTex = game.manager.get(BRICK_TEX, Texture.class);
        float glow = 0.8f + 0.2f * MathUtils.sin(glowTimer * Constants.GLOW_PULSE_FREQ * MathUtils.PI2);

        game.batch.begin();
        for (Brick b : brickGrid.bricks) {
            if (!b.active) continue;
            int typeIdx = b.type.ordinal();
            Color c = BRICK_COLORS[Math.min(typeIdx, BRICK_COLORS.length - 1)];

            if (b.type == BrickType.NORMAL) {
                // Level-based hue tint
                Color lvlColor = hsvToColor(Constants.LEVEL_HUE[
                    Math.min(levelNumber - 1, Constants.LEVEL_HUE.length - 1)]);
                game.batch.setColor(lvlColor.r * (b.cracked ? 0.6f : 1f),
                    lvlColor.g * (b.cracked ? 0.6f : 1f),
                    lvlColor.b * (b.cracked ? 0.6f : 1f), 1f);
            } else if (b.type == BrickType.POWER || b.type == BrickType.SPECIAL) {
                game.batch.setColor(c.r * glow, c.g * glow, c.b * glow, 1f);
            } else {
                game.batch.setColor(c.r * (b.cracked ? 0.6f : 1f),
                    c.g * (b.cracked ? 0.6f : 1f),
                    c.b * (b.cracked ? 0.6f : 1f), 1f);
            }
            game.batch.draw(brickTex, b.bounds.x, b.bounds.y, b.bounds.width, b.bounds.height);
        }
        game.batch.setColor(Color.WHITE);
        game.batch.end();
    }

    private void renderCapsules() {
        sr.setProjectionMatrix(viewport.getCamera().combined);
        sr.begin(ShapeRenderer.ShapeType.Filled);
        for (PowerUpCapsule c : capsules) {
            if (!c.active) continue;
            Color col = powerUpColor(c.type);
            sr.setColor(col);
            sr.rect(c.bounds.x, c.bounds.y, c.bounds.width, c.bounds.height);
        }
        sr.end();

        // Draw type labels on capsules
        game.batch.begin();
        for (PowerUpCapsule c : capsules) {
            if (!c.active) continue;
            String letter = powerUpLetter(c.type);
            game.fontSmall.draw(game.batch, letter,
                c.bounds.x + c.bounds.width / 2f - 5f,
                c.bounds.y + c.bounds.height - 2f);
        }
        game.batch.end();
    }

    private void renderProjectiles() {
        sr.setColor(1f, 0.2f, 0.2f, 0.9f);
        for (Projectile pr : projectiles) {
            if (!pr.active) continue;
            sr.rect(pr.bounds.x, pr.bounds.y, pr.bounds.width, pr.bounds.height);
        }
    }

    private void renderLasers() {
        sr.setColor(1f, 0.8f, 0.0f, 0.9f);
        for (Projectile laser : lasers) {
            if (!laser.active) continue;
            sr.rect(laser.bounds.x, laser.bounds.y, laser.bounds.width, laser.bounds.height);
        }
    }

    private void renderBalls() {
        for (Ball b : balls) {
            if (!b.active) continue;
            if (b.isFireball) {
                sr.setColor(1f, 0.5f, 0.0f, 1f);
            } else {
                sr.setColor(1f, 1f, 1f, 1f);
            }
            sr.circle(b.position.x, b.position.y, b.radius, 16);
        }
    }

    private void renderPaddle() {
        Texture tex = game.manager.get(PADDLE_TEX, Texture.class);
        game.batch.begin();
        game.batch.setColor(1f, 1f, 1f, paddle.alpha);
        game.batch.draw(tex, paddle.bounds.x, paddle.bounds.y,
            paddle.bounds.width, paddle.bounds.height);
        game.batch.setColor(Color.WHITE);
        game.batch.end();
    }

    private void renderLivesHUD() {
        Texture livesTex = game.manager.get(LIVES_TEX, Texture.class);
        float iconSize = Constants.HEART_SIZE;
        float totalW   = lives * (iconSize + Constants.HEART_SPACING) - Constants.HEART_SPACING;
        float startX   = (Constants.VP_WIDTH - totalW) / 2f;
        float y        = 8f;

        game.batch.begin();
        for (int i = 0; i < lives; i++) {
            game.batch.draw(livesTex,
                startX + i * (iconSize + Constants.HEART_SPACING),
                y, iconSize, iconSize);
        }
        game.batch.end();
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private static Color hsvToColor(float hDeg) {
        float h = hDeg / 360f;
        float s = 0.85f, v = 0.95f;
        int i = (int)(h * 6);
        float f = h * 6 - i;
        float p = v * (1 - s);
        float q = v * (1 - f * s);
        float t = v * (1 - (1 - f) * s);
        switch (i % 6) {
            case 0: return new Color(v, t, p, 1f);
            case 1: return new Color(q, v, p, 1f);
            case 2: return new Color(p, v, t, 1f);
            case 3: return new Color(p, q, v, 1f);
            case 4: return new Color(t, p, v, 1f);
            default: return new Color(v, p, q, 1f);
        }
    }

    private Color powerUpColor(PowerUpType type) {
        switch (type) {
            case WIDE_PADDLE: return Color.BLUE;
            case SLOW_BALL:   return Color.CYAN;
            case MULTI_BALL:  return Color.YELLOW;
            case SHIELD:      return Color.GREEN;
            case LASER:       return Color.RED;
            case FIREBALL:    return Color.ORANGE;
            default:          return Color.WHITE;
        }
    }

    private String powerUpLetter(PowerUpType type) {
        switch (type) {
            case WIDE_PADDLE: return "W";
            case SLOW_BALL:   return "S";
            case MULTI_BALL:  return "M";
            case SHIELD:      return "SH";
            case LASER:       return "L";
            case FIREBALL:    return "F";
            default:          return "?";
        }
    }

    // ── Public accessors ──────────────────────────────────────────────────────

    public int getLevelNumber() { return levelNumber; }

    // ── Screen lifecycle ──────────────────────────────────────────────────────

    @Override
    public void show() {
        paused = false;
        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
    }

    @Override public void pause()  { paused = true; }
    @Override public void resume() { paused = false; }
    @Override public void hide()   {}

    @Override
    public void dispose() {
        stage.dispose();
        sr.dispose();
    }
}
