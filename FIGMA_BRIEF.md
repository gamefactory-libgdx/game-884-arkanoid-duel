# Figma AI Design Brief — Arkanoid Duel

---

## 1. Art Style & Color Palette

Arkanoid Duel uses a **neon-on-dark arcade aesthetic** — deep space void backgrounds lit by electric neon grids, glowing brick walls, and plasma-trail ball effects. The mood is retro-futuristic: think 1980s arcade cabinet reimagined on OLED. Every element has a soft outer glow; bricks pulse with inner light. The color language is high-contrast: cool dark voids vs. hot neon primaries. Typography is pixel/tech hybrid — readable but with arcade DNA.

| Role | Hex | Usage |
|---|---|---|
| Background void | `#07071A` | Screen base, dark canvas |
| Deep grid blue | `#0D1B3E` | Panel fills, secondary surfaces |
| Neon cyan | `#00F5FF` | Ball trail, paddle edge, highlights |
| Electric violet | `#9B00FF` | Power brick glow, power-up aura |
| Hot coral | `#FF3D5A` | Danger indicators, Power Brick fire |
| Acid green | `#39FF14` | Special brick, health pickups |
| Gold accent | `#FFD700` | Score text, star highlights |
| Off-white | `#E8F0FF` | Primary body text |

**Font mood:** Pixel-tech hybrid. Headlines in `PressStart2P` (pixel retro, all-caps). Body/scores in `Orbitron` (clean sci-fi). Both fonts reinforce arcade legitimacy without sacrificing legibility.

---

## 2. App Icon — `icon_512.png` (512×512px)

**Canvas:** 512×512px, no rounded corners applied (OS handles masking).

**Background:** Radial gradient from `#1A0040` (center-deep violet) to `#07071A` (edge-black), with a subtle hexagonal grid overlay at 8% opacity in `#9B00FF`.

**Central symbol:** A glowing neon-cyan ball (`#00F5FF`) mid-bounce above a chunky horizontal paddle rendered in electric violet (`#9B00FF`) with a bright gradient top edge. Above the ball, a compact 3×2 arrangement of colored bricks — left column in coral (`#FF3D5A`), middle in cyan (`#00F5FF`), right in acid green (`#39FF14`) — each brick outlined with a 2px inner glow matching its fill color.

**Glow / shadow:** The ball casts a dramatic radial lens-flare bloom in cyan. The paddle has a violet underside drop-shadow. The whole composition sits on a faint circular vignette darkening the corners. No text. No letterforms.

**Mood:** Instantly recognizable brick-breaker, premium neon energy, arcade nostalgia.

---

## 3. UI Screens (480×854 portrait)

---

### MenuScreen

#### A) BACKGROUND IMAGE — `ui/menu_screen.png`

A deep-space void (`#07071A`) fills the canvas. In the upper 60% of the screen, a stylized arrangement of semi-transparent glowing bricks in coral, cyan, and violet cascades in a gentle arc — no uniform grid, more like an explosion frozen in time, each brick emitting a soft colored bloom. A faint perspective grid (1px lines, `#0D1B3E` at 30% opacity) recedes toward the upper center, adding depth. The lower third is dominated by a dark frosted-glass panel shape — a rounded rectangle with a 1px neon-cyan border and subtle inner gradient from `#0D1B3E` to transparent — left entirely blank inside for the engine to draw buttons. A particle field of tiny floating light dots at 20% opacity populates the background throughout.

#### B) BUTTON LAYOUT (code-drawn, NOT in image)

```
ARKANOID DUEL  | top-Y=90px  | x=centered          | size=380x60   (title label, PressStart2P 18sp)
[score badge]  | top-Y=170px | x=centered          | size=220x36   (best score label, Orbitron 13sp)
PLAY           | top-Y=340px | x=centered          | size=280x60
LEADERBOARD    | top-Y=420px | x=centered          | size=280x52
SETTINGS       | top-Y=490px | x=centered          | size=280x52
```

---

### GameScreen

#### A) BACKGROUND IMAGE — `ui/game_screen.png`

Full canvas is a deep starfield void (`#07071A`) with a subtle animated-looking scan-line texture (1px horizontal bands at 4% opacity) suggesting a CRT display. The top 65% of the screen holds a ghosted architectural placeholder: a faint rectangular grid of uniform brick outlines drawn at 6% opacity in `#9B00FF` — pure decoration showing where bricks will live, but visually receding into the background. Along the left, right, and top edges, a 4px neon-cyan border strip (`#00F5FF` at 60% opacity) forms the game arena wall — beveled inward, glowing outward. The bottom quarter of the screen is slightly lighter (`#0D1B3E` gradient rising from the base) suggesting the play zone floor. No HUD elements, no icons, no life indicators — purely atmospheric arena art.

#### B) BUTTON LAYOUT (code-drawn, NOT in image)

```
PAUSE ❚❚         | top-Y=18px  | x=right@18px        | size=52x40    (icon button)
SCORE: 000000   | top-Y=22px  | x=centered          | size=200x32   (Orbitron 14sp)
LEVEL: 01       | top-Y=22px  | x=left@18px         | size=110x32   (Orbitron 12sp)
[lives row]     | top-Y=800px | x=centered          | size=160x28   (3× life pip icons, code-drawn)
```

---

### GameOverScreen

#### A) BACKGROUND IMAGE — `ui/game_over_screen.png`

The canvas starts with the same void base (`#07071A`) but the upper half is washed in a deep crimson vignette radiating from center-top (`#3D0010` at 70%), evoking failure and drama. Shattered brick fragments — angular polygon shards in coral (`#FF3D5A`) and dark violet — rain downward from the upper 40%, each with a glowing edge. A central dark frosted panel (rounded rectangle, `#0D1B3E` at 85% opacity, 2px coral border) occupies the middle of the screen — blank interior for score display. Below the panel, a second smaller frosted card shape sits empty for the button zone. The bottom edge fades to absolute black. A faint `GAME OVER` letterform ghost (40% opacity, massive, clipped) bleeds behind the panel in the background layer only as texture — no readable foreground text.

#### B) BUTTON LAYOUT (code-drawn, NOT in image)

```
GAME OVER       | top-Y=160px | x=centered          | size=380x64   (PressStart2P 20sp, coral #FF3D5A)
SCORE           | top-Y=270px | x=centered          | size=240x28   (label, Orbitron 12sp)
000000          | top-Y=306px | x=centered          | size=240x52   (value, Orbitron 32sp, gold)
BEST            | top-Y=368px | x=centered          | size=200x24   (label, Orbitron 11sp)
000000          | top-Y=398px | x=centered          | size=200x40   (value, Orbitron 24sp)
RETRY           | top-Y=510px | x=centered          | size=280x60
MENU            | top-Y=590px | x=centered          | size=280x52
```

---

### PauseScreen

#### A) BACKGROUND IMAGE — `ui/pause_screen.png`

Semi-transparent overlay design: the base is a full-canvas dark navy rectangle (`#07071A` at 80% opacity) meant to dim the live game beneath. A centered frosted-glass panel dominates — tall rounded rectangle, `#0D1B3E` fill at 92% opacity, 2px neon-cyan border (`#00F5FF`) with a soft outer glow (12px blur, 40% opacity). The top edge of the panel features a decorative horizontal divider bar in gradient cyan-to-violet. Corner accent marks (small L-shaped brackets in cyan, 3px) ornament each corner of the panel — a classic sci-fi HUD motif. The panel interior is entirely empty for engine-drawn content. Subtle radial bokeh lights float in the dark margins outside the panel. No text, no icons anywhere in the image.

#### B) BUTTON LAYOUT (code-drawn, NOT in image)

```
PAUSED          | top-Y=220px | x=centered          | size=300x52   (PressStart2P 16sp, cyan)
RESUME          | top-Y=330px | x=centered          | size=260x60
RESTART         | top-Y=410px | x=centered          | size=260x52
SETTINGS        | top-Y=480px | x=centered          | size=260x52
QUIT            | top-Y=556px | x=centered          | size=260x52
```

---

## 4. Export Checklist

```
- icon_512.png (512x512)
- ui/menu_screen.png (480x854)
- ui/game_screen.png (480x854)
- ui/game_over_screen.png (480x854)
- ui/pause_screen.png (480x854)
```