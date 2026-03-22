```markdown
# GAME_SPEC.md — Arkanoid Duel

---

## 1. Game Identity

| Field | Value |
|---|---|
| **Title** | Arkanoid Duel |
| **Package** | `com.factory.template` |
| **Main class** | `MainGame` |
| **Orientation** | Portrait only (`screenOrientation="portrait"` in AndroidManifest) |
| **Virtual viewport** | `FitViewport(360, 640)` — all coordinates in virtual units (vp) |
| **Target SDK** | minSdk 21, targetSdk 34 |
| **Summary** | Brick-breaker arcade: paddle deflects ball, Power Bricks fire projectiles, Special Bricks drop power-ups, 10 progressively harder levels |

---

## 2. Screen Inventory

| # | Java Class | Purpose | Entry Transitions | Exit Transitions |
|---|---|---|---|---|
| 1 | `SplashScreen` | Logo display + AssetManager background load | App launch | → `MainMenuScreen` (load complete or 3 s timeout) |
| 2 | `MainMenuScreen` | Play / Leaderboard / Settings entry | From `SplashScreen`, `LeaderboardScreen`, `SettingsScreen`, `GameOverScreen`, `PauseScreen` (Quit) | → `LevelSelectScreen` (Play); → `LeaderboardScreen`; → `SettingsScreen` |
| 3 | `LevelSelectScreen` | Grid of 10 levels with lock/star/score | From `MainMenuScreen`, `VictoryScreen` | → `GameScreen(levelNumber)`; → `MainMenuScreen` (Back) |
| 4 | `TutorialScreen` | 3-card swipe walkthrough, first launch only | From `MainMenuScreen` when `tutorial_done == false` | → `LevelSelectScreen` (Got it!) |
| 5 | `GameScreen` | Core gameplay | From `LevelSelectScreen` | → `PauseScreen` (overlay); → `VictoryScreen`; → `GameOverScreen` |
| 6 | `PauseScreen` | Frozen game overlay | From `GameScreen` (pause button or Android back) | → `GameScreen` (Resume); → `GameScreen` reinit (Restart); → `SettingsScreen`; → `MainMenuScreen` (Quit) |
| 7 | `VictoryScreen` | Level cleared summary, 1–3 stars | From `GameScreen` (all bricks cleared) | → `GameScreen(levelNumber+1)` (Next Level); → `GameScreen(same)` (Retry); → `MainMenuScreen` |
| 8 | `GameOverScreen` | Lives = 0; score, optional name entry | From `GameScreen` (lives = 0) | → `GameScreen(same)` (Retry); → `LevelSelectScreen`; → `MainMenuScreen` |
| 9 | `LeaderboardScreen` | Local top-10 list; Clear button | From `MainMenuScreen` | → `MainMenuScreen` (Back) |
| 10 | `SettingsScreen` | Volume sliders, vibration toggle | From `MainMenuScreen` or `PauseScreen` | → caller (Back, saves settings) |

---

## 3. Screen Flow Diagram

```
SplashScreen
  └─► MainMenuScreen ──[first launch]──► TutorialScreen ──► LevelSelectScreen
        ├─► LeaderboardScreen ──► MainMenuScreen
        ├─► SettingsScreen ──► MainMenuScreen
        └─► LevelSelectScreen
              └─► GameScreen
                    ├─── [pause btn / Android back] ──► PauseScreen
                    │       ├─► GameScreen          (Resume)
                    │       ├─► GameScreen(reinit)  (Restart)
                    │       ├─► SettingsScreen ──► PauseScreen
                    │       └─► MainMenuScreen      (Quit)
                    ├─── [all bricks cleared] ──► VictoryScreen
                    │       ├─► GameScreen(N+1)     (Next Level / level<10)
                    │       ├─► MainMenuScreen       (level=10, Finish)
                    │       ├─► GameScreen(same)     (Retry)
                    │       └─► MainMenuScreen
                    └─── [lives=0] ──► GameOverScreen
                            ├─► GameScreen(same)    (Retry)
                            ├─► LevelSelectScreen
                            └─► MainMenuScreen
```

---

## 4. Game Objects

### 4.1 `Constants.java`
Static final class — no instances. All magic numbers live here (see §6).

---

### 4.2 `Paddle`

| Field | Type | Description |
|---|---|---|
| `bounds` | `Rectangle` | Position and size in virtual units |
| `speed` | `float` | Max snap speed (not needed — follows touch directly) |
| `isInvincible` | `boolean` | True during 1 s post-hit grace period |
| `invincibleTimer` | `float` | Countdown in seconds |
| `widthMultiplier` | `float` | 1.0 normal; 1.6 with `wide_paddle` active |
| `laserShotsLeft` | `int` | Remaining laser shots (0 = inactive) |
| `texture` | `TextureRegion` | Loaded from `sprites/character/` pack |

| Method | Signature | Description |
|---|---|---|
| `update` | `void update(float delta, float touchX)` | Clamp `touchX` to `[PADDLE_HALF_WIDTH, VP_WIDTH - PADDLE_HALF_WIDTH]`; tick timers |
| `onHit` | `void onHit()` | Set `isInvincible = true`, reset `invincibleTimer` |
| `render` | `void render(SpriteBatch batch)` | Draw with alpha oscillation when invincible |
| `getBounds` | `Rectangle getBounds()` | Returns `bounds` |

---

### 4.3 `Ball`

| Field | Type | Description |
|---|---|---|
| `position` | `Vector2` | Center in virtual units |
| `velocity` | `Vector2` | px/s direction vector |
| `radius` | `float` | `BALL_RADIUS` constant |
| `isOnPaddle` | `boolean` | Pre-launch state |
| `isFireball` | `boolean` | Passes through bricks when true |
| `texture` | `TextureRegion` | |

| Method | Signature | Description |
|---|---|---|
| `update` | `void update(float delta)` | Sub-step movement: steps = `ceil(speed*delta / (2*radius))`; each step moves `velocity*(delta/steps)` |
| `launch` | `void launch(float angleDeg)` | Set `isOnPaddle = false`, apply initial velocity at `angleDeg` |
| `bounceX` | `void bounceX()` | `velocity.x *= -1` |
| `bounceY` | `void bounceY()` | `velocity.y *= -1` |
| `setSpeed` | `void setSpeed(float px)` | Normalise then scale `velocity` to `px`, clamped to `BALL_MAX_SPEED` |
| `getSpeed` | `float getSpeed()` | `velocity.len()` |
| `render` | `void render(SpriteBatch batch)` | |

---

### 4.4 `Brick` (base class)

| Field | Type | Description |
|---|---|---|
| `bounds` | `Rectangle` | Position and size |
| `type` | `BrickType` | Enum: `NORMAL, TOUGH, POWER, SPECIAL, INDESTRUCTIBLE` |
| `hp` | `int` | Current hit points |
| `maxHp` | `int` | Max hit points for type |
| `active` | `boolean` | False when destroyed |
| `cracked` | `boolean` | Tough brick after 1st hit |
| `textureNormal` | `TextureRegion` | |
| `textureCracked` | `TextureRegion` | Tough only |

| Method | Signature | Description |
|---|---|---|
| `onHit` | `BrickHitResult onHit()` | Decrement `hp`; return result struct |
| `isAlive` | `boolean isAlive()` | `active && hp > 0 \|\| type==INDESTRUCTIBLE` |
| `render` | `void render(SpriteBatch batch)` | Draw appropriate texture |

`BrickHitResult` record fields: `boolean destroyed`, `boolean fireProjectiles`, `int projectileCount`, `boolean dropPowerUp`.

---

### 4.5 `Projectile`

| Field | Type | Description |
|---|---|---|
| `bounds` | `Rectangle` | 8 × 20 vp |
| `velocity` | `Vector2` | Downward + random X offset within ±15° |
| `active` | `boolean` | |
| `texture` | `TextureRegion` | `sprites/object/projectile.png` |

| Method | Signature | Description |
|---|---|---|
| `update` | `void update(float delta)` | Move by velocity*delta; deactivate if `y < 0` |
| `render` | `void render(SpriteBatch batch)` | |

---

### 4.6 `PowerUpCapsule`

| Field | Type | Description |
|---|---|---|
| `bounds` | `Rectangle` | 20 × 20 vp |
| `type` | `PowerUpType` | Enum: `WIDE_PADDLE, SLOW_BALL, MULTI_BALL, SHIELD, LASER, FIREBALL` |
| `velocity` | `Vector2` | `(0, -POWERUP_FALL_SPEED)` |
| `active` | `boolean` | |
| `texture` | `TextureRegion` | Per-type texture |

| Method | Signature | Description |
|---|---|---|
| `update` | `void update(float delta)` | Move; deactivate if `y < 0` |
| `render` | `void render(SpriteBatch batch)` | |

---

### 4.7 `PowerUpState`

Tracks active effects on the paddle/ball.

| Field | Type | Description |
|---|---|---|
| `activeEffects` | `Map<PowerUpType, Float>` | Type → remaining seconds (or shot count for LASER/MULTI_BALL) |

| Method | Signature | Description |
|---|---|---|
| `activate` | `void activate(PowerUpType type, Paddle p, Array<Ball> balls)` | Apply effect, set timer |
| `update` | `void update(float delta, Paddle p, Array<Ball> balls)` | Tick timers; expire and revert |
| `isActive` | `boolean isActive(PowerUpType type)` | |

---

### 4.8 `BrickGrid`

| Field | Type | Description |
|---|---|---|
| `bricks` | `Array<Brick>` | All bricks in current level |
| `levelNumber` | `int` | 1–10 |

| Method | Signature | Description |
|---|---|---|
| `buildLevel` | `void buildLevel(int level)` | Populate `bricks` per difficulty table in §8 |
| `getActiveBricks` | `Array<Brick> getActiveBricks()` | Filter `active == true` |
| `allCleared` | `boolean allCleared()` | No NORMAL/TOUGH/POWER/SPECIAL bricks active |
| `render` | `void render(SpriteBatch batch)` | |

---

### 4.9 `ParticlePool`

| Method | Signature | Description |
|---|---|---|
| `spawn` | `void spawn(float x, float y, Color color)` | Emit 4–6 square particles at position |
| `update` | `void update(float delta)` | Tick fade timers |
| `render` | `void render(SpriteBatch batch)` | Draw active particles |

---

### 4.10 `ScoreManager`

| Field | Type | Description |
|---|---|---|
| `currentScore` | `int` | Score this session |
| `multiplier` | `float` | 1.0 or 1.5 (multi-ball combo) |

| Method | Signature | Description |
|---|---|---|
| `add` | `void add(int base)` | `currentScore += (int)(base * multiplier)` |
| `addLevelClear` | `void addLevelClear(int level, int livesLeft)` | `500*level + 200*livesLeft` |
| `reset` | `void reset()` | |

---

### 4.11 `CollisionManager`

| Method | Signature | Description |
|---|---|---|
| `ballVsWalls` | `void ballVsWalls(Ball b)` | Bounce off left/right/top edges; detect bottom fall |
| `ballVsPaddle` | `void ballVsPaddle(Ball b, Paddle p)` | Reflect Y; compute angle from hit position offset |
| `ballVsBricks` | `BrickHitResult ballVsBricks(Ball b, Array<Brick> bricks)` | AABB overlap; if fireball skip bounce; return first hit result |
| `projectileVsPaddle` | `boolean projectileVsPaddle(Projectile pr, Paddle p)` | Overlap check |
| `capsuleVsPaddle` | `boolean capsuleVsPaddle(PowerUpCapsule c, Paddle p)` | Overlap check |

---

### 4.12 `HUD`

| Method | Signature | Description |
|---|---|---|
| `render` | `void render(SpriteBatch batch, int lives, int score, int level, PowerUpState pus)` | Top bar: hearts, score, level number, pause icon; power-up icons + countdown |

---

### 4.13 `LevelConfig` (data record)

| Field | Type |
|---|---|
| `level` | `int` |
| `ballSpeed` | `float` |
| `brickRows` | `int` |
| `powerBrickPct` | `float` |
| `projectileCountMin` | `int` |
| `projectileCountMax` | `int` |
| `indestructiblePct` | `float` |

Static factory: `LevelConfig forLevel(int n)` — returns config from difficulty table.

---

### 4.14 `SaveData`

Static helper wrapping `Preferences`.

| Method | Signature | Description |
|---|---|---|
| `getHighScore` | `int getHighScore()` | |
| `setHighScore` | `void setHighScore(int v)` | |
| `getMaxLevelUnlocked` | `int getMaxLevelUnlocked()` | |
| `unlockLevel` | `void unlockLevel(int n)` | `max(current, n)` |
| `getLevelStars` | `int getLevelStars(int n)` | |
| `setLevelStars` | `void setLevelStars(int n, int stars)` | Only if `stars > current` |
| `getLevelBestScore` | `int getLevelBestScore(int n)` | |
| `setLevelBestScore` | `void setLevelBestScore(int n, int s)` | Only if `s > current` |
| `isTutorialDone` | `boolean isTutorialDone()` | |
| `setTutorialDone` | `void setTutorialDone()` | |
| `getLeaderboard` | `Array<LeaderboardEntry> getLeaderboard()` | Parse JSON; max 10 |
| `addLeaderboardEntry` | `void addLeaderboardEntry(String name, int score)` | Insert sorted; trim to 10 |
| `clearLeaderboard` | `void clearLeaderboard()` | |
| `getMusicVolume` | `float getMusicVolume()` | |
| `setMusicVolume` | `void setMusicVolume(float v)` | |
| `getSfxVolume` | `float getSfxVolume()` | |
| `setSfxVolume` | `void setSfxVolume(float v)` | |
| `isVibrationEnabled` | `boolean isVibrationEnabled()` | |
| `setVibrationEnabled` | `void setVibrationEnabled(boolean v)` | |

---

### 4.15 `AudioManager`

| Method | Description |
|---|---|
| `playMusic(String name)` | Stream named music at current music volume, looping |
| `stopMusic()` | |
| `playSound(String name)` | One-shot SFX at current SFX volume |
| `setMusicVolume(float v)` | Update live |
| `setSfxVolume(float v)` | Update live |

---

## 5. Asset Filenames

### Sprites

```
sprites/character/paddle.png
sprites/object/ball.png
sprites/object/brick_normal.png
sprites/object/brick_tough.png
sprites/object/brick_tough_crack.png
sprites/object/brick_power.png
sprites/object/brick_special.png
sprites/object/brick_indestructible.png
sprites/object/projectile.png
sprites/object/powerup_wide.png
sprites/object/powerup_slow.png
sprites/object/powerup_multi.png
sprites/object/powerup_shield.png
sprites/object/powerup_laser.png
sprites/object/powerup_fireball.png
sprites/effect/explosion.png          (spritesheet 4×2 frames)
sprites/effect/shield_line.png
sprites/effect/laser_beam.png         (4-frame flash strip)
backgrounds/menu/bg_menu.png
backgrounds/game/bg_game.png
ui/icon_heart.png
ui/icon_heart_empty.png
ui/icon_pause.png
ui/icon_star_full.png
ui/icon_star_empty.png
ui/icon_lock.png
```

### Fonts

```
fonts/font1.ttf          (pixel/arcade — title, headers)
fonts/font2.ttf          (clean readable — HUD, scores)
fonts/Roboto-Regular.ttf (fallback body)
```

### Audio

```
sounds/music_menu.ogg
sounds/music_game.ogg
sounds/sfx_brick_hit.ogg
sounds/sfx_brick_destroy.ogg
sounds/sfx_paddle_hit.ogg
sounds/sfx_wall_hit.ogg
sounds/sfx_projectile_fire.ogg
sounds/sfx_life_lost.ogg
sounds/sfx_powerup_collect.ogg
sounds/sfx_powerup_expire.ogg
sounds/sfx_level_clear.ogg
sounds/sfx_game_over.ogg
sounds/sfx_button_click.ogg
```

---

## 6. Constants (`Constants.java`)

```java
// Viewport
VP_WIDTH            = 360f
VP_HEIGHT           = 640f

// Paddle
PADDLE_WIDTH        = 120f
PADDLE_HEIGHT       = 18f
PADDLE_Y            = 72f          // distance from bottom
PADDLE_INVINCIBLE_DURATION = 1.0f  // seconds
PADDLE_FLASH_FREQ   = 10f          // Hz
PADDLE_ALPHA_MIN    = 0.3f
PADDLE_ALPHA_MAX    = 1.0f
PADDLE_WIDE_MULT    = 1.6f

// Ball
BALL_RADIUS         = 10f
BALL_SPEED_BASE     = 300f         // level 1 px/s
BALL_SPEED_STEP     = 30f          // per level increment
BALL_MAX_SPEED      = 600f
BALL_ANGLE_RANGE    = 60f          // degrees from center
BALL_LAUNCH_ANGLE_DEFAULT = 90f    // straight up

// Brick grid
BRICK_COLS          = 8
BRICK_WIDTH         = 38f          // (360 - margins) / 8
BRICK_HEIGHT        = 16f
BRICK_MARGIN_X      = 4f
BRICK_MARGIN_Y      = 4f
BRICK_GRID_TOP_Y    = 560f         // top of brick area in VP coords
BRICK_TOP_PADDING   = 40f          // gap below HUD bar

// Brick HP
HP_NORMAL           = 1
HP_TOUGH            = 2
HP_POWER            = 1
HP_SPECIAL          = 1

// Projectile
PROJECTILE_WIDTH    = 8f
PROJECTILE_HEIGHT   = 20f
PROJECTILE_SPEED    = 250f         // px/s downward
PROJECTILE_ANGLE_RANGE = 15f       // degrees random X offset

// Power-up capsules
POWERUP_SIZE        = 20f
POWERUP_FALL_SPEED  = 150f         // px/s

// Power-up durations
POWERUP_WIDE_DURATION     = 10f    // seconds
POWERUP_SLOW_DURATION     = 8f     // seconds
POWERUP_SLOW_FACTOR       = 0.60f  // ball speed multiplier (−40%)
POWERUP_LASER_SHOTS       = 5      // shot count
POWERUP_FIREBALL_DURATION = 6f     // seconds

// Laser
LASER_WIDTH         = 4f
LASER_SPEED         = 500f         // px/s upward

// Shield
SHIELD_Y_OFFSET     = 20f          // px above paddle center
SHIELD_HEIGHT       = 4f

// Scoring
SCORE_NORMAL        = 10
SCORE_TOUGH         = 20
SCORE_POWER         = 30
SCORE_SPECIAL       = 25
SCORE_POWERUP_COLLECT = 50
SCORE_LEVEL_CLEAR_BASE = 500       // × level number
SCORE_LIFE_BONUS    = 200          // per remaining life
SCORE_MULTIBALL_MULT = 1.5f

// Lives
LIVES_PER_LEVEL     = 3

// Physics
DELTA_CAP           = 0.05f        // max frame delta seconds

// HUD
HUD_HEIGHT          = 40f          // top bar height in VP units
HEART_SIZE          = 18f
HEART_SPACING       = 4f

// Particles
PARTICLE_COUNT      = 5            // per brick destruction (randomised 4–6)
PARTICLE_SIZE       = 5f
PARTICLE_LIFETIME   = 0.4f

// Glow pulse
GLOW_PULSE_FREQ     = 1f           // Hz for Power/Special brick alpha pulse

// Splash
SPLASH_MAX_DURATION = 3.0f         // seconds

// Leaderboard
LEADERBOARD_MAX_ENTRIES = 10
LEADERBOARD_NAME_MAX_CHARS = 12

// Level count
LEVEL_COUNT         = 10

// Colors (hex RGBA8888)
COLOR_BACKGROUND    = 0x0A0A14FF
COLOR_HUD_BAR       = 0x00000099   // semi-transparent
COLOR_INDESTRUCTIBLE= 0x888899FF

// Audio defaults
DEFAULT_MUSIC_VOLUME = 0.7f
DEFAULT_SFX_VOLUME   = 1.0f

// Level palette hues (HSV H, one per level 1–10)
LEVEL_HUE = { 180f, 270f, 30f, 300f, 60f, 210f, 0f, 120f, 45f, 330f }
// Cyan, Purple, Orange, Magenta, Yellow, Blue, Red, Green, Amber, Pink
```

---

## 7. Difficulty Table (`LevelConfig.forLevel`)

| Level | `ballSpeed` | `brickRows` | `powerBrickPct` | `projMin` | `projMax` | `indestructPct` |
|---|---|---|---|---|---|---|
| 1 | 300 | 4 | 0.05 | 1 | 1 | 0.00 |
| 2 | 330 | 5 | 0.08 | 1 | 1 | 0.00 |
| 3 | 360 | 5 | 0.10 | 1 | 2 | 0.05 |
| 4 | 390 | 6 | 0.12 | 1 | 2 | 0.05 |
| 5 | 420 | 6 | 0.15 | 2 | 2 | 0.08 |
| 6 | 450 | 7 | 0.18 | 2 | 2 | 0.08 |
| 7 | 480 | 7 | 0.20 | 2 | 3 | 0.10 |
| 8 | 510 | 8 | 0.22 | 2 | 3 | 0.10 |
| 9 | 540 | 8 | 0.25 | 3 | 3 | 0.12 |
| 10 | 600 | 9 | 0.30 | 3 | 3 | 0.15 |

Special brick fraction: fixed 10% of remaining cells after Power + Indestructible allocation.
Tough brick fraction: fixed 15% of remaining cells.
Normal brick: remainder.

---

## 8. Data Persistence (SharedPreferences)

Preferences name: `"ArkanoidDuelPrefs"`

| Key | Type | Default | Notes |
|---|---|---|---|
| `high_score` | int | 0 | |
| `leaderboard_json` | String | `"[]"` | JSON array `[{name:String,score:int}]`, max 10, sorted desc |
| `max_level_unlocked` | int | 1 | |
| `level_1_stars` … `level_10_stars` | int | 0 | 0–3 |
| `level_1_best_score` … `level_10_best_score` | int | 0 | |
| `tutorial_done` | boolean | false | |
| `music_volume` | float | 0.7 | |
| `sfx_volume` | float | 1.0 | |
| `vibration_enabled` | boolean | true | |

---

## 9. Rendering Order (back → front)

1. Background texture (`bg_game.png` or `bg_menu.png`)
2. Shield line (if active)
3. Brick grid
4. Power-up capsules
5. Projectiles
6. Ball(s)
7. Paddle
8. Particle effects
9. Explosion animations
10. HUD (top bar, hearts, score, power-up icons)
11. PauseScreen overlay (if paused)

---

## 10. Out of Scope

- Online leaderboards or multiplayer
- In-app purchases or ads
- Level editor or custom levels
- Achievements / Google Play Games integration
- Cloud save or account system
- Controller or keyboard input
- Landscape mode
- More than 10 levels in initial release
- Procedural level generation
- Story mode or narrative elements
- Box2D physics (manual AABB only)
- Texture atlas packing at build time (load individual PNGs via AssetManager)
```