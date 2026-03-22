```markdown
# GDD.md — Arkanoid Duel

---

## 1. Overview

| Field | Value |
|---|---|
| **Title** | Arkanoid Duel |
| **Platform** | Android |
| **Engine** | LibGDX |
| **Genre** | Arcade / Brick-breaker |
| **Target audience** | Casual players, ages 10+ |
| **Session length** | 3–8 minutes per run |
| **Orientation** | Portrait |

### Concept

Classic brick-breaker with a twist: the bricks fight back. A paddle sits at the bottom of the screen; a rectangular wall of bricks fills the upper two-thirds. The player bounces a ball to destroy bricks, but **Power Bricks** periodically fire projectiles downward that the player must dodge. **Special Bricks** drop power-ups on destruction. The game progresses through increasingly dense and aggressive levels until the player loses all lives or clears the final stage.

---

## 2. Screen List

| # | Screen Class | Purpose |
|---|---|---|
| 1 | `SplashScreen` | Studio logo + asset loading |
| 2 | `MainMenuScreen` | Entry point — Play, Leaderboard, Settings |
| 3 | `LevelSelectScreen` | Choose starting level (levels 1–10 unlocked progressively) |
| 4 | `TutorialScreen` | One-time interactive walkthrough on first launch |
| 5 | `GameScreen` | Core gameplay |
| 6 | `PauseScreen` | Overlay — Resume, Restart, Settings, Quit |
| 7 | `VictoryScreen` | Level cleared — score summary, Next Level button |
| 8 | `GameOverScreen` | All lives lost — final score, Retry, Menu |
| 9 | `LeaderboardScreen` | Local top-10 high scores |
| 10 | `SettingsScreen` | Music/SFX volume, vibration toggle |

---

## 3. Screen Flow

```
SplashScreen
    └─► MainMenuScreen
            ├─► LevelSelectScreen ──► GameScreen ──► VictoryScreen ──► LevelSelectScreen
            │                    │                └─► GameOverScreen ──► MainMenuScreen
            │                    └─► (PauseScreen overlay) ──► Resume / Restart / MainMenuScreen
            ├─► LeaderboardScreen ──► MainMenuScreen
            └─► SettingsScreen ──► MainMenuScreen

First launch only:
    MainMenuScreen ──► TutorialScreen ──► LevelSelectScreen
```

---

## 4. Core Gameplay Loop

```
1. Ball launches from paddle center on tap/swipe.
2. Ball bounces off walls, ceiling, paddle, and bricks.
3. Brick types:
      Normal Brick   — one hit, destroyed silently.
      Tough Brick    — two hits; cracks on first hit.
      Power Brick    — one hit; fires 1–3 projectiles downward before being destroyed.
      Special Brick  — one hit; drops a power-up capsule.
      Indestructible — never destroyed; acts as permanent deflector.
4. Power Brick projectiles travel downward; paddle must dodge sideways to avoid.
      Hit by projectile → lose 1 life, brief invincibility (1 s).
5. Ball falls below paddle → lose 1 life.
6. All bricks cleared → VictoryScreen.
7. Lives reach 0 → GameOverScreen.
8. Score saved; if top-10, prompt name entry on GameOverScreen.
```

---

## 5. Per-Screen Detail

### 5.1 SplashScreen
- Displays studio logo centered on black background.
- Loads all assets via AssetManager in background.
- Auto-transitions to MainMenuScreen after load completes (max 3 s).
- No user interaction.

### 5.2 MainMenuScreen
- Background: animated parallax stars or the `bg_main.png` background.
- Game logo at top third.
- Three buttons: **Play**, **Leaderboard**, **Settings**.
- Looping menu music: `music_menu.ogg`.
- Version number bottom-right (read from `game.properties`).

### 5.3 LevelSelectScreen
- Grid of level buttons (2 × 5, levels 1–10).
- Locked levels shown with padlock icon; unlocked show star rating (0–3 stars earned).
- Level 1 always unlocked; levels 2–10 unlock on completing the previous level.
- Each cell shows level number + best score.
- Back button → MainMenuScreen.

### 5.4 TutorialScreen
- Shown only on first launch (`prefs.getBoolean("tutorial_done", false) == false`).
- Three tutorial cards (swipe through):
  1. "Move your paddle — drag left/right."
  2. "Break all the bricks with your ball!"
  3. "Watch out — Power Bricks shoot back. Dodge!"
- Tap **Got it!** on last card → sets `tutorial_done = true` → LevelSelectScreen.

### 5.5 GameScreen
- **Layout:**
  - Top bar: lives (heart icons), current score, level number, pause button.
  - Play field: full width, top bar to bottom edge.
  - Paddle at y = 72 dp from bottom, centered.
- **Brick grid** occupies rows 1–N from the top (N increases with level).
- **HUD** overlaid; all game objects below it.
- Pause button (top-right) → PauseScreen overlay.
- Sound: `music_game.ogg` loops; hit SFX on brick, life-loss, power-up.

### 5.6 PauseScreen
- Semi-transparent dark overlay on frozen GameScreen.
- Buttons: **Resume**, **Restart**, **Settings** (opens SettingsScreen, returns here), **Main Menu**.
- No score impact on resume.

### 5.7 VictoryScreen
- Animated stars (1–3) based on remaining lives:
  - 3 lives → 3 stars; 2 lives → 2 stars; 1 life → 1 star.
- Displays: level number, score earned, time taken.
- Saves star rating (only if better than previous).
- Buttons: **Next Level** (disabled on level 10 — shows **Finish**), **Retry**, **Menu**.
- Unlocks next level in SharedPreferences.

### 5.8 GameOverScreen
- Shows skull/explosion animation.
- Final score, level reached.
- If score qualifies for top-10: TextField prompt for player name (max 12 chars).
- Buttons: **Retry** (same level), **Level Select**, **Main Menu**.

### 5.9 LeaderboardScreen
- Scrollable list: rank, name, score (local SharedPreferences, 10 entries).
- Tapping an entry does nothing.
- **Clear** button (confirmation dialog) resets board.
- Back → MainMenuScreen.

### 5.10 SettingsScreen
- Music volume: slider 0–100 (default 70).
- SFX volume: slider 0–100 (default 100).
- Vibration: toggle (default on).
- Back button saves and returns to caller (MainMenuScreen or PauseScreen).

---

## 6. Game Objects

### 6.1 Paddle
| Property | Value |
|---|---|
| Size | 120 × 18 dp |
| Start X | Screen center |
| Movement | Drag finger horizontally; paddle follows touch X, clamped to screen edges |
| Sprite | `character/paddle.png` (or first suitable sprite from `character/` pack) |
| Invincibility flash | 1 s after being hit, alpha oscillates 0.3–1.0 at 10 Hz |

### 6.2 Ball
| Property | Value |
|---|---|
| Radius | 10 dp |
| Start speed | 300 px/s (level 1), increases 30 px/s per level |
| Max speed | 600 px/s |
| Angle on paddle bounce | Reflects based on hit position: ±60° range from center |
| Sprite | `object/ball.png` or circle drawn procedurally |

### 6.3 Brick Types

| Type | HP | Color hint | Behavior |
|---|---|---|---|
| Normal | 1 | Solid color (level-palette) | Destroyed on hit |
| Tough | 2 | Darker shade + crack overlay | First hit → crack sprite; second → destroy |
| Power | 1 | Glowing red/orange | On first hit: fires 1–3 projectiles toward paddle, then destroyed |
| Special | 1 | Glowing green/gold | Drops a power-up capsule on destroy |
| Indestructible | ∞ | Metallic grey | Never destroyed; reflects ball |

### 6.4 Power Brick Projectile
- Size: 8 × 20 dp, red capsule.
- Speed: 250 px/s downward, slight random X offset (±15°).
- On contact with paddle: lose 1 life; projectile disappears.
- On contact with bottom wall: disappears.

### 6.5 Power-Up Capsules

| ID | Icon | Effect | Duration |
|---|---|---|---|
| `wide_paddle` | Blue W | Paddle width ×1.6 | 10 s |
| `slow_ball` | Cyan S | Ball speed −40% | 8 s |
| `multi_ball` | Yellow M | Spawns 2 extra balls | Until balls lost |
| `shield` | Green ■ | Temporary floor line — ball bounces once | One save |
| `laser` | Red L | Paddle fires laser upward on tap — destroys one brick | 5 shots |
| `fireball` | Orange F | Ball passes through bricks (destroys on contact, no bounce) | 6 s |

Capsules fall at 150 px/s. Player catches by overlapping with paddle. Active power-ups shown as small icons in HUD with countdown timers.

---

## 7. Controls

| Action | Input |
|---|---|
| Move paddle | Drag finger horizontally anywhere on screen |
| Launch ball | Tap screen when ball is on paddle (pre-launch state) |
| Pause | Tap pause icon (top-right) |
| Aim before launch | While ball is on paddle, swipe to set initial direction (optional — defaults to straight up) |

---

## 8. Scoring & Difficulty

### Scoring

| Event | Points |
|---|---|
| Normal brick destroyed | 10 |
| Tough brick (2nd hit) | 20 |
| Power brick destroyed | 30 |
| Special brick destroyed | 25 |
| Power-up collected | 50 |
| Multi-ball combo (brick hit by extra ball) | ×1.5 multiplier |
| Level cleared | 500 × level number |
| Life remaining at level end | 200 per life |

### Difficulty Scaling (per level)

| Level | Ball speed (px/s) | Brick rows | Power Brick % | Projectile count | Indestructible % |
|---|---|---|---|---|---|
| 1 | 300 | 4 | 5% | 1 | 0% |
| 2 | 330 | 5 | 8% | 1 | 0% |
| 3 | 360 | 5 | 10% | 1–2 | 5% |
| 4 | 390 | 6 | 12% | 1–2 | 5% |
| 5 | 420 | 6 | 15% | 2 | 8% |
| 6 | 450 | 7 | 18% | 2 | 8% |
| 7 | 480 | 7 | 20% | 2–3 | 10% |
| 8 | 510 | 8 | 22% | 2–3 | 10% |
| 9 | 540 | 8 | 25% | 3 | 12% |
| 10 | 600 | 9 | 30% | 3 | 15% |

**Lives:** Player starts each level with 3 lives; lives do **not** carry between levels (reset to 3 each stage).

---

## 9. Asset List

### Sprites

| Filename (relative to `assets/`) | Description |
|---|---|
| `sprites/character/paddle.png` | Player paddle — rectangular, rounded ends, metallic or neon style |
| `sprites/object/ball.png` | Round ball — glowing or solid; ~20×20 px |
| `sprites/object/brick_normal.png` | Plain rectangular brick, one solid color |
| `sprites/object/brick_tough.png` | Brick with subtle raised edge; darker palette |
| `sprites/object/brick_tough_crack.png` | Same brick with crack texture overlay |
| `sprites/object/brick_power.png` | Brick with glowing red border or lightning pattern |
| `sprites/object/brick_special.png` | Brick with star/gem highlight, gold/green tint |
| `sprites/object/brick_indestructible.png` | Steel/iron textured brick, no glow |
| `sprites/object/projectile.png` | Small red energy bolt / elongated capsule |
| `sprites/object/powerup_wide.png` | Blue capsule with "W" label |
| `sprites/object/powerup_slow.png` | Cyan capsule with "S" label |
| `sprites/object/powerup_multi.png` | Yellow capsule with "M" label |
| `sprites/object/powerup_shield.png` | Green capsule with shield icon |
| `sprites/object/powerup_laser.png` | Red capsule with "L" label |
| `sprites/object/powerup_fireball.png` | Orange capsule with flame icon |
| `sprites/effect/explosion.png` | Spritesheet: 4×2 frames, brick destruction burst |
| `sprites/effect/shield_line.png` | Horizontal translucent barrier across screen bottom |
| `sprites/effect/laser_beam.png` | Thin vertical beam, 4 frames flash |
| `backgrounds/menu/bg_menu.png` | Dark space or neon grid — static or slow-scroll |
| `backgrounds/game/bg_game.png` | Dark, minimal — should not distract from gameplay |
| `ui/icon_heart.png` | Filled red heart — life indicator |
| `ui/icon_heart_empty.png` | Empty heart outline |
| `ui/icon_pause.png` | Standard pause symbol |
| `ui/icon_star_full.png` | Filled yellow star |
| `ui/icon_star_empty.png` | Empty star outline |
| `ui/icon_lock.png` | Padlock for locked levels |

### Fonts

| Filename | Usage |
|---|---|
| `fonts/font1.ttf` | Game title, screen headers (pixel/arcade style) |
| `fonts/font2.ttf` | Body text, scores, HUD labels (clean, readable) |
| `fonts/Roboto-Regular.ttf` | Fallback for all body text |

### Audio

| Filename | Type | Description |
|---|---|---|
| `sounds/music_menu.ogg` | Music | Upbeat chiptune loop for menus |
| `sounds/music_game.ogg` | Music | Tense driving loop during gameplay |
| `sounds/sfx_brick_hit.ogg` | SFX | Short click/thud on brick hit |
| `sounds/sfx_brick_destroy.ogg` | SFX | Crack/pop on brick destruction |
| `sounds/sfx_paddle_hit.ogg` | SFX | Ball bouncing off paddle |
| `sounds/sfx_wall_hit.ogg` | SFX | Ball bouncing off wall or ceiling |
| `sounds/sfx_projectile_fire.ogg` | SFX | Power Brick fires projectile |
| `sounds/sfx_life_lost.ogg` | SFX | Ball lost / hit by projectile |
| `sounds/sfx_powerup_collect.ogg` | SFX | Catching a power-up capsule |
| `sounds/sfx_powerup_expire.ogg` | SFX | Power-up wears off |
| `sounds/sfx_level_clear.ogg` | SFX | Level victory jingle |
| `sounds/sfx_game_over.ogg` | SFX | Game over sting |
| `sounds/sfx_button_click.ogg` | SFX | UI button press |

---

## 10. Visual Style

- **Palette:** Dark background (near-black `#0A0A14`), neon accent colors — cyan, magenta, electric blue.
- **Bricks:** Each level uses a distinct hue rotation (level 1 = cyan, 2 = purple, 3 = orange, …).
- **Glow effects:** Power Brick and Special Brick outlines pulse at ~1 Hz using alpha shader or bloom-like tinting.
- **Paddle:** Metallic gradient, blue highlight at center indicating sweet-spot.
- **Ball:** White core with soft glow halo.
- **HUD:** Top bar on semi-transparent dark strip; icons in white/gold.
- **Fonts:** Pixel/retro for headers; clean sans for scores and labels.
- **Particles:** Brick destruction spawns 4–6 colored square particles that fade over 0.4 s.

---

## 11. Data Persistence (SharedPreferences)

| Key | Type | Default | Description |
|---|---|---|---|
| `high_score` | int | 0 | All-time highest score |
| `leaderboard_json` | String | `"[]"` | JSON array of `{name, score}` objects, max 10 |
| `max_level_unlocked` | int | 1 | Highest level the player has access to |
| `level_{N}_stars` | int | 0 | Stars earned on level N (0–3) |
| `level_{N}_best_score` | int | 0 | Best score on level N |
| `tutorial_done` | boolean | false | Whether tutorial has been shown |
| `music_volume` | float | 0.7 | Master music volume (0.0–1.0) |
| `sfx_volume` | float | 1.0 | SFX volume (0.0–1.0) |
| `vibration_enabled` | boolean | true | Haptic feedback toggle |

---

## 12. Technical Constraints

- **Target API:** Android API 21+ (minSdk 21, targetSdk 34).
- **Orientation:** Portrait only; locked via AndroidManifest.
- **Resolution handling:** Viewport — `FitViewport(360, 640)` virtual units; all positions in virtual units.
- **Physics:** Manual AABB collision detection (no Box2D); ball uses float velocity vector.
- **Rendering:** Single `SpriteBatch`; brick grid rendered as `Array<Brick>`; culled if off-screen.
- **AssetManager:** All textures and sounds loaded via LibGDX `AssetManager`; unloaded on screen dispose.
- **Frame rate:** Target 60 FPS; `delta` capped at 0.05 s per frame to prevent tunneling.
- **Ball tunneling prevention:** Sub-step collision — if ball speed × delta > ball diameter, subdivide movement into steps.
- **Memory:** All atlases packed into `sprites.atlas`; max texture size 2048 × 2048.
- **Audio:** Music streamed (`Music`); SFX loaded fully into memory (`Sound`).
- **Back button:** Android back → PauseScreen when in GameScreen; normal back behavior elsewhere.

---

## 13. Out of Scope

- Online leaderboards or multiplayer.
- In-app purchases or ads.
- Level editor or custom levels.
- Achievements / Google Play Games integration.
- Cloud save or account system.
- Controller or keyboard input.
- Landscape mode.
- More than 10 levels in initial release.
- Procedural level generation (levels are hand-designed grid configurations).
- Story mode or narrative elements.
```