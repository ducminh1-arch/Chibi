# Chibi Doll - Artist & Asset Pipeline Guide

Welcome to the **Chibi Doll** art guide! This document specifies the canvas dimensions, coordinate system, layering hierarchy, color tinting system, idle blink mechanics, and delivery checklist for digital illustrators and 2D artists creating assets for the game.

---

## 1. Canvas Specification & Shared Origin

| Property | Requirement | Notes |
| :--- | :--- | :--- |
| **Canvas Dimensions** | **1024 × 1536 px** | Exactly 2:3 aspect ratio; strictly uniform across all character layers |
| **Color Mode** | **RGBA** (32-bit with Alpha) | Character layers **must** have transparent backgrounds |
| **Background Mode** | **RGB or RGBA** | Full-bleed scenes, no transparent areas required (JPG/PNG allowed) |
| **Origin & Alignment** | **Shared Origin (0, 0)** | **Do not crop or trim transparent margins!** Every layer is rendered at `(0, 0)` in an Android Compose `Box`. Positioning is 100% paper-doll pixel-aligned. |
| **Resolution / PPI** | **72–300 DPI** | Pixel dimensions (1024×1536) are what matter for in-game compositing |

> [!IMPORTANT]
> **Never auto-trim or crop transparent space around an item.**
> Every accessory, dress, hair strand, or shoe must sit on the full 1024×1536 canvas at the exact coordinates where it belongs on the character doll.

---

## 2. Complete Layer Hierarchy & Z-Order

All equipped items are sorted strictly by `zIndex` from back to front. A higher zIndex renders **over** lower ones:

| zIndex | Layer Key | Category | Colorable? | Alt File? | Description & Mutual Exclusion |
| :---: | :--- | :--- | :---: | :---: | :--- |
| **0** | `background` | `scene` | No | No | Scene wallpaper (solid color, gradient, or full illustration). |
| **5** | `wings` | `accessories` | Optional | No | Angel, fairy, or demon wings; sits behind doll body and hair. |
| **10** | `hair_back` | `appearance` | **Yes** (`hair`) | No | Back hair strands, tails, long tresses behind shoulders. |
| **15** | `tail` | `accessories` | Optional (`general`) | No | Cat, fox, or bunny tail extending behind character torso. |
| **20** | `body` | `appearance` | **Yes** (`skin`) | No | Doll base body (head, torso, arms, legs). Cannot be unequipped. |
| **25** | `socks` | `clothes` | Optional (`general`) | No | Knee-high, ankle, or frilly socks. Stacks under shoes! |
| **30** | `shoes` | `clothes` | Optional (`general`) | No | Boots, sneakers, Mary Janes. Covers socks where overlapping. |
| **40** | `bottom` | `clothes` | Optional (`general`) | No | Skirts, shorts, pants. Equipping dress removes bottom. |
| **50** | `top` | `clothes` | Optional (`general`) | No | Shirts, blouses, sweaters. Equipping dress removes top. |
| **60** | `dress` | `clothes` | Optional (`general`) | No | One-piece dresses. Equipping dress removes top & bottom. |
| **70** | `outerwear` | `clothes` | Optional (`general`) | No | Coats, cardigans, varsity jackets over clothing. |
| **72** | `blush` | `appearance` | No | No | Cheek blush, sparkles, heart accents on cheeks. |
| **75** | `mouth` | `appearance` | No | No | Smiles, open laughs, cat smiles (`:3`). Cannot be unequipped. |
| **78** | `face` | `appearance` | No | No | *(Legacy)* Combined face fallback. |
| **79** | `eyes_iris` | `appearance` | **Yes** (`eyes`) | **Yes** | Colored iris disc & pupil. Closed state is transparent PNG. |
| **80** | `eyes` | `appearance` | **Yes** (`eyes`) | **Yes** | *(Legacy composite)* Chibi eyes. |
| **82** | `eyes_line` | `appearance` | **No** | **Yes** | Sclera, dark lashes, outline, white sparkles. Closed lashes. |
| **85** | `eyebrows` | `appearance` | No | No | Gentle, curious, confident, or playful brow shapes. |
| **88** | `glasses` | `accessories` | Optional (`general`) | No | Eyeglasses, sunglasses; renders over eyes & brows. |
| **92** | `ears` | `accessories` | Optional (`skin`/`general`) | No | Elf ears or animal ears. Can conflict with full hats. |
| **95** | `hair_front` | `appearance` | **Yes** (`hair`) | No | Bangs, front locks, side framing hair. |
| **100** | `hat` | `accessories` | Optional (`general`) | No | Crowns, berets, beanies, giant ribbons. |
| **110** | `accessory` | `accessories` | Optional (`general`) | No | Handheld items (wands, bags), floating magic sparkles. |

---

## 3. Eyes Split (`eyes_line` vs `eyes_iris`) & Idle Blink Specification

To allow players to dye iris colors without corrupting black eyelashes or white eye sparkles, eyes are split into two distinct layers:

1. **`eyes_iris` Layer (zIndex 79 - Behind Lineart):**
   - **Open State (`file`):** Colored or light neutral iris disc with pupil.
   - **Colorable:** **Yes** (`palette: "eyes"`). When tinted, only the iris changes color.
   - **Closed State (`alt_file`):** Empty transparent 1024×1536 PNG (`items/eyes/eyes_iris_closed.png`). When the character blinks, the iris vanishes cleanly behind closed eyelids.
2. **`eyes_line` Layer (zIndex 82 - In Front of Iris):**
   - **Open State (`file`):** White sclera framing, dark eyelash contours, and specular highlight sparkles (`#FFFFFF`) that overlay the iris.
   - **Colorable:** **Strictly NO**. Lineart and highlights never tint with palettes, maintaining pitch-black lashes and bright white reflections.
   - **Closed State (`alt_file`):** Curved closed eyelash arc (`_line_closed.png`). Swapped in for **~120ms** every **~3.8s** on the live canvas.

### Catalog Definition Options
An eye style can be configured as a dual-layer item (equipping both line and iris together):
```json
{
  "id": "eyes_purple",
  "category": "eyes",
  "layer": "eyes_line",
  "file": "items/eyes/eyes_purple_line_open.png",
  "alt_file": "items/eyes/eyes_purple_line_closed.png",
  "secondaryLayer": "eyes_iris",
  "secondaryFile": "items/eyes/eyes_purple_iris_open.png",
  "secondary_alt_file": "items/eyes/eyes_purple_iris_closed.png",
  "thumbnail": "items/thumbnails/thumb_eyes_purple.png",
  "nameEn": "Sparkle Purple Eyes",
  "nameVi": "Mắt Tím Lấp Lánh",
  "colorable": true,
  "palette": "eyes"
}
```
Or as standalone items in `eyes_line` and `eyes_iris` categories for independent eye shape & iris mixing.

---

## 4. Per-Item Z-Order Override (`z_override`)

Certain clothing items or accessories need to violate the default layer order based on artistic silhouette. For example:
- A bushy fox tail (`tail`) normally sits behind the character body (`zIndex = 15`), but a front-curling tail should wrap across the front of the skirt (`zIndex = 40`).
- By specifying `"z_override": 45`, the engine places this specific tail in front of `bottom` (40) while still behind `top` (50) or outerwear!

```json
{
  "id": "tail_fox_front",
  "category": "tail",
  "layer": "tail",
  "file": "items/tail/tail_fox_front.png",
  "thumbnail": "items/thumbnails/thumb_tail_fox_front.png",
  "nameEn": "Front Fox Tail",
  "nameVi": "Đuôi Cáo Trước Váy",
  "group": "accessories",
  "subcategory": "tail",
  "z_override": 45
}
```
`z_override` (and `secondary_z_override` for secondary layers) is fully supported in:
- The Compose interactive canvas (`CharacterDollCanvas`)
- Master 1024×1536 rendering (`DollImageExporter`)
- Room database serialization (`SavedLookMapper`)
- Automated asset pipeline validation (`tools/asset_pipeline.py`)

---

## 5. Recommended File Formats & Size Budgets

To keep the game snappy, prevent OutOfMemory crashes on 2 GB RAM budget devices, and maintain a compact APK (< 15–20 MB):

| Asset Type | Recommended Format | Target Size Budget | Compression Recommendation |
| :--- | :--- | :--- | :--- |
| **Character Layers** (1024×1536) | **Lossless WebP** or **Optimized PNG** | **< 40 – 80 KB** per item | `oxipng -o 4` or `pngquant --strip` / `cwebp -lossless` |
| **Scene Backgrounds** (1024×1536) | **Optimized WebP / JPG / PNG** | **< 120 – 180 KB** per scene | `cwebp -q 88` or MozJPEG `jpegtran` |
| **Thumbnails** (256×256) | **PNG** or **WebP** | **< 10 – 20 KB** each | Auto-generated by asset pipeline |
| **Total App Package (APK)** | Universal APK / AAB | **< 15 – 20 MB** for ~200 items | Downsampled decoding on-screen + off-thread master decodes |

### Performance Architecture:
- **On-Screen Canvas:** Decodes layers downsampled to the actual screen display size (e.g. 512×768 or 360×540) using Coil size constraints and in-memory bitmap cache limited to 25% heap.
- **Offline Master Exporter:** Decodes at full 1024×1536 resolution strictly one layer at a time, compositing onto the Android Canvas and immediately calling `.recycle()` on each source layer to keep transient memory usage under ~12 MB during export.

---

## 4. Color Tinting System (Multiply & Modulate)

Items marked `"colorable": true` allow young players to pick custom color swatches from an in-game palette.

### How Tinting Works
In both the Compose UI (`BlendMode.Modulate`) and offline export (`PorterDuff.Mode.MULTIPLY`), the pixel color is mathematically multiplied by the swatch color:
$$\text{Pixel}_{\text{final}} = \text{Pixel}_{\text{source}} \times \text{Color}_{\text{swatch}}$$

### Artist Guidelines for Colorable Assets:
- **Base Fill:** Use light gray to neutral off-white (e.g. `#DCDCE6` to `#F0F0F5`).
  - Pure white (`#FFFFFF`) will adopt the swatch color directly.
  - Off-white retains soft shading volume.
- **Outlines:** Use dark purple, navy, or deep brown outline (e.g. `#2D2235`). Dark lines remain dark and crisp when multiplied by bright pastel swatches!
- **Highlights & Sparkles:** Use pure white (`#FFFFFF`) on non-tinted accents if you want sparkles to stay bright.

### Palettes Available in `palettes.json`:
- `"hair"`: Lilac, pink, blonde, pastel mint, coral, raven, etc.
- `"skin"`: Fair porcelain, peach, honey warm, caramel, mocha, cocoa, etc.
- `"eyes"`: Amethyst purple, ocean blue, emerald, amber brown, ruby, etc.
- `"general"`: Full pastel & rainbow palette for clothes and accessories.

---

## 5. Dual-Layer Items (e.g. Hair)

Hairstyles typically consist of two parts:
- `file`: Front bangs & side locks (`layer = "hair_front"`, zIndex 95)
- `secondaryFile`: Back ponytails/hair flow (`secondaryLayer = "hair_back"`, zIndex 10)

```json
{
  "id": "hair_lilac_twin",
  "category": "hair",
  "layer": "hair_front",
  "file": "items/hair/hair_lilac_front.png",
  "secondaryLayer": "hair_back",
  "secondaryFile": "items/hair/hair_lilac_back.png",
  "colorable": true,
  "palette": "hair"
}
```
When a player picks a hair color swatch, the engine automatically tints **both** `hair_front` and `hair_back` identically.

---

## 6. Stacking & Mutual Exclusion Rules

1. **Socks & Shoes:**
   - `socks` sit at zIndex 25.
   - `shoes` sit at zIndex 30.
   - Both can be equipped together without conflict! Shoes will cover the foot/ankle part of socks, while the upper sock portion remains visible on the calf/knee.
2. **Dress vs. Top & Bottom:**
   - Equipping a `dress` automatically removes any equipped `top` and `bottom`.
   - Equipping a `top` or `bottom` automatically removes the `dress`.
3. **Facial Parts vs. Legacy Face:**
   - Equipping discrete `eyes`, `eyebrows`, `mouth`, or `blush` automatically removes any legacy monolithic `face` asset.
   - Dolls cannot be made faceless: tapping an equipped eye or mouth item swaps to another, but does not leave the doll blank.
4. **Hats vs. Large Ears/Accessories:**
   - Items can declare `"conflictsWith": ["hat"]` if an ear headband cannot be worn under a full bonnet or beanie.

---

## 7. Folder Structure & Naming Conventions

All assets are located in `app/src/main/assets/items/`:

```
app/src/main/assets/items/
├── accessory/        # acc_star_wand.png, acc_bear_bag.png
├── backgrounds/      # bg_pastel_dream.png, bg_cherry_blossom.jpg
├── blush/            # blush_pink.png, blush_peach.png, blush_hearts.png
├── body/             # body_fair.png
├── bottom/           # bottom_denim_shorts.png, bottom_navy_skirt.png
├── dress/            # dress_princess_pink.png, dress_mint_polka.png
├── ears/             # ears_cat_white.png, ears_bunny_pink.png, ears_elf.png
├── eyebrows/         # brow_gentle.png, brow_curious.png, brow_confident.png
├── eyes/             # eyes_purple_open.png, eyes_purple_closed.png
├── glasses/          # glasses_round_gold.png, glasses_heart_pink.png
├── hair/             # hair_lilac_front.png, hair_lilac_back.png
├── hat/              # hat_giant_bow.png, hat_crown.png
├── mouth/            # mouth_smile.png, mouth_happy_open.png, mouth_cat.png
├── outerwear/        # outer_varsity_pink.png, outer_mint_cardigan.png
├── shoes/            # shoes_mary_jane.png, shoes_pink_sneakers.png
├── socks/            # socks_white_knee.png, socks_striped_pink.png
├── tail/             # tail_cat_white.png, tail_fox_fluffy.png
├── thumbnails/       # thumb_*.png (256x256 auto-generated)
├── top/              # top_lavender_sweater.png, top_sailor.png
└── wings/            # wings_angel_white.png, wings_fairy_sparkle.png
```

### File Naming Rules:
- Lowercase alphanumeric with underscores only: `^[a-z0-9_]+\.png$`
- No spaces or special characters (`-`, `@`, `#`, `!`).
- Hair front/back pairs must end with `_front.png` and `_back.png`.
- Eye open/closed pairs must end with `_open.png` and `_closed.png`.

---

## 8. Delivery Checklist

Before submitting new artwork or committing to the repository:

- [ ] Canvas is exactly **1024 × 1536 px**.
- [ ] Character artwork is **RGBA PNG** with transparent background.
- [ ] Transparent margins have **NOT** been trimmed; item is aligned to doll coordinates.
- [ ] If colorable, base fill is neutral light gray (`#D8D8E0`) with dark outlines.
- [ ] If an `eyes` item, both `_open.png` and `_closed.png` are provided with identical coordinates.
- [ ] Filenames use only `lowercase_letters_and_numbers.png`.
- [ ] Run validation script from repository root:
  ```bash
  python tools/asset_pipeline.py --validate
  ```
  *(Must report `0 failed`)*
- [ ] Generate 256×256 thumbnails:
  ```bash
  python tools/asset_pipeline.py --thumbnails
  ```
- [ ] Test in the Android emulator / app.

---

## 9. For Solo Artists (Krita / Photoshop Workflow)

If you are a solo illustrator or 2D artist creating assets in **Krita** or **Adobe Photoshop**, you do **not** need to manually crop, downsample, configure paths in JSON, or generate thumbnails one by one. The project includes an automated single-command exporter (`tools/export_layers.py`).

### 9.1 Recommended Canvas Setup
- **Source Canvas:** **2048 × 3072 px** (exactly 2× the 1024×1536 game resolution).
- **Color Mode:** **RGBA** (8-bit per channel, with transparency).
- **DPI:** 300 DPI recommended for high-DPI drawing tablet precision.
- **Reference Body:** Place the base mannequin doll on a locked background layer while drawing clothes and hair so all items naturally align to the doll coordinates.

### 9.2 Layer & Group Naming Conventions
Name your layers or layer groups using the item ID or category prefix:

1. **Single-Layer Items (Dresses, Shoes, Hats, Wings, etc.):**
   - Layer name: `dress_strawberry_cake`, `shoes_pink_sneakers`, `hat_witch_purple`, `wings_fairy_sparkle`.
   - Or group named after the category (e.g. group `dress` containing layer `strawberry_cake`).

2. **Dual-Layer Hair:**
   - Group named `hair_twintail_pink` with sublayers:
     - `front`: Bangs, forehead locks, and side curls (renders over face/head).
     - `back`: Twin tails and flowing back strands (renders behind shoulders).
   - *Alternatively, flat layers:* `hair_twintail_pink_front` and `hair_twintail_pink_back`.

3. **Eyes & Idle Blink (`eyes_line` + `eyes_iris`):**
   - Group named `eyes_emerald` with sublayers:
     - `line_open`: Eyelashes, sclera, and white highlights (open state).
     - `line_closed`: Closed eyelash arc for the idle blink (alt state).
     - `iris_open`: Colored iris disc and pupil (tinted with the `"eyes"` palette).
     - `iris_closed`: *(Optional)* Closed iris (if omitted, the pipeline defaults to the transparent shared asset).
   - *Alternatively, flat layers:* `eyes_emerald_line_open`, `eyes_emerald_line_closed`, `eyes_emerald_iris_open`.

4. **Scene Backgrounds:**
   - Layer name: `bg_cherry_blossom`, `bg_starry_twilight` (full-bleed 2048×3072 scene artwork).

### 9.3 Solo-Artist Export Command
Save your master painting file directly as an **OpenRaster (.ora)** file in Krita (`File > Save As > OpenRaster (*.ora)`) or Photoshop (`.psd`), then run from the project root:

```bash
python tools/export_layers.py --input path/to/character_sheet.ora
```

#### What the Tool Does Automatically:
1. **Reads `.ora` or `.psd`** without requiring any external network access. Refuses to run if the file is missing or corrupted.
2. **Safety Backups:** Automatically creates a timestamped safety backup of `catalog.json` and any asset files about to be overwritten in `backups/backup_YYYYMMDD_HHMMSS/`.
3. **Dry-Run Mode (`--dry-run`):** Inspects the master file, prints all planned file additions/overwrites and catalog changes, and modifies nothing on disk.
4. **Handles Special Layer States:**
   - **Hidden Layers:** Layers marked `visibility="hidden"` are automatically skipped.
   - **Opacity < 100%:** Transparent layer opacities (e.g. 50% sheer veil) are automatically baked into the alpha channel.
   - **Non-Normal Blend Modes & Masks:** Flags any layer masks or non-normal blend modes (e.g. Multiply, Overlay) with an `[INVALID]` warning so the artist can flatten them before export.
   - **Nested Groups & Cropped Layers:** Traverses arbitrary group nesting and positions Krita's cropped layer offsets `(x, y)` onto the full doll canvas.
5. **Preserves Alpha & Exact Alignment:** Takes each layer's coordinates and resamples by 50% to **1024 × 1536 px** using the high-quality **Lanczos** filter.
6. **Saves Target PNGs:** Writes each item to its appropriate category subfolder in `app/src/main/assets/items/<category>/`.
7. **Updates `catalog.json`:** Inserts new items with sensible defaults (groups, subcategories, colorable flags, palettes) while preserving all existing English and Vietnamese translations.
8. **Generates Thumbnails:** Automatically creates 256×256 thumbnails cropped tightly around each item's non-transparent pixels with 8% padding. For dual-layer hair and eyes, it composites the layers together before cropping.
9. **Validates Output:** Runs full asset resolution, RGBA mode, and JSON schema checks.

### 9.4 Solo Artist Delivery Workflow
```
[ Draw in Krita at 2048x3072 ]
             │
             ▼
   [ File > Save As .ora ]
             │
             ▼
[ python tools/export_layers.py -i my_art.ora --dry-run ]  (Inspect plan)
             │
             ▼
[ python tools/export_layers.py -i my_art.ora ]            (Export & Backup)
             │
             ▼
[ Open Android App / Emulator to preview look! ]
```


