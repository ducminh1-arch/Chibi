#!/usr/bin/env python3
"""
Chibi Doll - Asset Pipeline Tool
Validates 1024x1536 assets, checks alpha channels and naming conventions,
generates 256x256 thumbnails, validates catalog.json schema (including Phase 4 group,
subcategory, colorable, palette), and updates catalog.json from folder structure.

Backgrounds:
- Full 1024x1536 images
- Formats: PNG, JPG, JPEG allowed
- Mode: RGB or RGBA allowed (no alpha channel required)

Character Layers:
- 1024x1536 transparent PNG
- Mode: RGBA strictly required

Usage:
    python tools/asset_pipeline.py [--validate] [--thumbnails] [--catalog] [--all]
"""

import argparse
import json
import os
import re
import sys
from pathlib import Path
from typing import Dict, List, Optional, Tuple

try:
    from PIL import Image
except ImportError:
    print("[ERROR] Pillow is required. Install it using: pip install Pillow")
    sys.exit(1)

TARGET_WIDTH = 1024
TARGET_HEIGHT = 1536
THUMBNAIL_MAX_DIM = 256

VALID_CATEGORIES = {
    "body": "body",
    "face": "face",
    "eyes": "eyes",
    "eyes_line": "eyes_line",
    "eyes_iris": "eyes_iris",
    "eyebrows": "eyebrows",
    "mouth": "mouth",
    "blush": "blush",
    "hair": "hair_front",
    "dress": "dress",
    "top": "top",
    "bottom": "bottom",
    "outerwear": "outerwear",
    "socks": "socks",
    "shoes": "shoes",
    "glasses": "glasses",
    "ears": "ears",
    "tail": "tail",
    "wings": "wings",
    "hat": "hat",
    "accessory": "accessory",
    "background": "background",
    "backgrounds": "background"
}

CATEGORY_TO_GROUP = {
    "dress": ("clothes", "dress"),
    "top": ("clothes", "top"),
    "bottom": ("clothes", "bottom"),
    "outerwear": ("clothes", "outerwear"),
    "socks": ("clothes", "socks"),
    "shoes": ("clothes", "shoes"),
    "hat": ("accessories", "hat"),
    "glasses": ("accessories", "glasses"),
    "ears": ("accessories", "ears"),
    "tail": ("accessories", "tail"),
    "wings": ("accessories", "wings"),
    "accessory": ("accessories", "accessory"),
    "background": ("scene", "background"),
    "hair": ("appearance", "hair"),
    "face": ("appearance", "face"),
    "eyes": ("appearance", "eyes"),
    "eyes_line": ("appearance", "eyes_line"),
    "eyes_iris": ("appearance", "eyes_iris"),
    "eyebrows": ("appearance", "eyebrows"),
    "mouth": ("appearance", "mouth"),
    "blush": ("appearance", "blush"),
    "body": ("appearance", "body")
}

CATEGORY_ORDER = [
    "body", "eyes", "eyebrows", "mouth", "blush", "face", "hair",
    "dress", "top", "bottom", "outerwear", "socks", "shoes",
    "glasses", "ears", "tail", "wings", "hat", "accessory", "background"
]

NAMING_PATTERN_PNG = re.compile(r"^[a-z0-9_]+\.png$")
NAMING_PATTERN_BG = re.compile(r"^[a-z0-9_]+\.(png|jpg|jpeg)$", re.IGNORECASE)

VALID_PALETTES = {"hair", "skin", "eyes", "general"}
VALID_GROUPS = {"clothes", "accessories", "scene", "appearance"}


def validate_asset(image_path: Path, is_background: bool = False) -> Tuple[bool, List[str]]:
    """Validates an image file for dimensions, format, mode, and naming."""
    errors = []

    # 1. Naming convention
    pattern = NAMING_PATTERN_BG if is_background else NAMING_PATTERN_PNG
    if not pattern.match(image_path.name):
        expected = ".png, .jpg, or .jpeg" if is_background else ".png"
        errors.append(f"Invalid filename '{image_path.name}': must be lowercase alphanumeric with underscores ({expected})")

    try:
        with Image.open(image_path) as img:
            # 2. Dimensions
            w, h = img.size
            if w != TARGET_WIDTH or h != TARGET_HEIGHT:
                errors.append(f"Invalid dimensions {w}x{h}: must be exactly {TARGET_WIDTH}x{TARGET_HEIGHT}")

            # 3. Alpha channel mode
            if is_background:
                if img.mode not in ("RGB", "RGBA"):
                    errors.append(f"Invalid background mode '{img.mode}': must be RGB or RGBA")
            else:
                if img.mode != "RGBA":
                    errors.append(f"Invalid mode '{img.mode}': character layers must be RGBA with alpha channel")
    except Exception as e:
        errors.append(f"Could not open image: {e}")

    return len(errors) == 0, errors


def run_validation(assets_dir: Path) -> bool:
    """Scans and validates all item assets."""
    print("\n--- Validating Assets ---")
    items_dir = assets_dir / "items"
    if not items_dir.exists():
        print(f"[ERROR] Directory not found: {items_dir}")
        return False

    total_files = 0
    passed_files = 0
    failed_files = 0

    for root, dirs, files in os.walk(items_dir):
        if "thumbnails" in Path(root).parts:
            continue

        for file in sorted(files):
            ext = file.lower()
            if not (ext.endswith(".png") or ext.endswith(".jpg") or ext.endswith(".jpeg")):
                continue

            file_path = Path(root) / file
            is_bg = "background" in file_path.parts or file_path.name.startswith("bg_")

            total_files += 1
            valid, errs = validate_asset(file_path, is_background=is_bg)
            rel_path = file_path.relative_to(assets_dir).as_posix()

            if valid:
                passed_files += 1
                print(f"  [OK] {rel_path}")
            else:
                failed_files += 1
                print(f"  [FAIL] {rel_path}")
                for err in errs:
                    print(f"         ! {err}")

    print(f"\nAsset Validation Summary: {passed_files}/{total_files} passed, {failed_files} failed.")
    return failed_files == 0


def validate_catalog_schema(catalog_file: Path, assets_dir: Path) -> bool:
    """Validates the schema and completeness of catalog.json."""
    print("\n--- Validating catalog.json Schema ---")
    if not catalog_file.exists():
        print(f"[ERROR] Catalog file not found: {catalog_file}")
        return False

    try:
        with open(catalog_file, "r", encoding="utf-8") as f:
            items = json.load(f)
    except Exception as e:
        print(f"[ERROR] Failed to parse JSON: {e}")
        return False

    if not isinstance(items, list):
        print("[ERROR] Catalog root must be an array of items")
        return False

    required_fields = ["id", "category", "layer", "file", "thumbnail", "nameEn", "nameVi"]
    phase4_fields = ["group", "subcategory", "colorable"]

    errors = 0
    for idx, item in enumerate(items):
        item_id = item.get("id", f"index_{idx}")

        # Check required fields
        for field in required_fields:
            if field not in item or item[field] is None:
                print(f"  [FAIL] Item '{item_id}' is missing required field '{field}'")
                errors += 1

        # Check Phase 4 fields
        for field in phase4_fields:
            if field not in item:
                print(f"  [FAIL] Item '{item_id}' is missing Phase 4 field '{field}'")
                errors += 1

        # Group validation
        grp = item.get("group")
        if grp and grp not in VALID_GROUPS:
            print(f"  [WARN] Item '{item_id}' has unrecognized group '{grp}'")

        # Colorable & palette validation
        colorable = item.get("colorable", False)
        palette = item.get("palette")
        if colorable:
            if not palette:
                print(f"  [FAIL] Colorable item '{item_id}' must specify a palette")
                errors += 1
            elif palette not in VALID_PALETTES:
                print(f"  [WARN] Item '{item_id}' has unrecognized palette '{palette}'")

        # Check main asset existence
        main_file = item.get("file")
        if main_file and not (assets_dir / main_file).exists():
            print(f"  [FAIL] Item '{item_id}' references missing file: {main_file}")
            errors += 1

        # Check alt_file existence and validity
        alt_file = item.get("alt_file")
        if alt_file:
            alt_path = assets_dir / alt_file
            if not alt_path.exists():
                print(f"  [FAIL] Item '{item_id}' references missing alt_file: {alt_file}")
                errors += 1
            else:
                valid_alt, alt_errs = validate_asset(alt_path, is_background=False)
                if not valid_alt:
                    for err in alt_errs:
                        print(f"  [FAIL] Item '{item_id}' alt_file error: {err}")
                    errors += 1

        # Check secondary_alt_file existence and validity
        sec_alt_file = item.get("secondary_alt_file")
        if sec_alt_file:
            sec_alt_path = assets_dir / sec_alt_file
            if not sec_alt_path.exists():
                print(f"  [FAIL] Item '{item_id}' references missing secondary_alt_file: {sec_alt_file}")
                errors += 1
            else:
                valid_sec_alt, sec_alt_errs = validate_asset(sec_alt_path, is_background=False)
                if not valid_sec_alt:
                    for err in sec_alt_errs:
                        print(f"  [FAIL] Item '{item_id}' secondary_alt_file error: {err}")
                    errors += 1

        # Check z_override validity (must be int 0..200)
        z_override = item.get("z_override")
        if z_override is not None:
            if not isinstance(z_override, int) or z_override < 0 or z_override > 200:
                print(f"  [FAIL] Item '{item_id}' has invalid z_override '{z_override}'. Must be integer 0..200.")
                errors += 1

        # Check secondary_z_override validity (must be int 0..200)
        sec_z_override = item.get("secondary_z_override")
        if sec_z_override is not None:
            if not isinstance(sec_z_override, int) or sec_z_override < 0 or sec_z_override > 200:
                print(f"  [FAIL] Item '{item_id}' has invalid secondary_z_override '{sec_z_override}'. Must be integer 0..200.")
                errors += 1

    print(f"\nCatalog Schema Summary: {len(items)} items checked, {errors} errors found.")
    return errors == 0


def crop_to_bounding_box_thumbnail(
    image: Image.Image,
    max_dim: int = 256,
    padding_pct: float = 0.08,
    is_background: bool = False
) -> Image.Image:
    """
    Crops to bounding box of non-transparent pixels, adds ~8% padding,
    scales to fit inside max_dim x max_dim preserving aspect ratio,
    and centers on a transparent max_dim x max_dim square.
    For backgrounds (full-bleed), scales directly into max_dim x max_dim.
    """
    img = image.convert("RGBA")

    if is_background:
        # Full-bleed background wallpaper: scale preserving aspect ratio into 256x256
        w, h = img.size
        scale = min(max_dim / w, max_dim / h)
        tw, th = max(1, int(round(w * scale))), max(1, int(round(h * scale)))
        scaled = img.resize((tw, th), Image.Resampling.LANCZOS)
        thumb = Image.new("RGBA", (max_dim, max_dim), (0, 0, 0, 0))
        thumb.paste(scaled, ((max_dim - tw) // 2, (max_dim - th) // 2))
        return thumb

    # Character layer: compute bounding box of visible content
    bbox = img.getbbox()
    if not bbox:
        return Image.new("RGBA", (max_dim, max_dim), (0, 0, 0, 0))

    left, top, right, bottom = bbox
    box_w = right - left
    box_h = bottom - top

    # Add ~8% padding based on max dimension of the object
    pad = max(1, int(round(max(box_w, box_h) * padding_pct)))
    crop_left = max(0, left - pad)
    crop_top = max(0, top - pad)
    crop_right = min(img.width, right + pad)
    crop_bottom = min(img.height, bottom + pad)

    cropped = img.crop((crop_left, crop_top, crop_right, crop_bottom))
    cw, ch = cropped.size

    # Scale to fit inside max_dim x max_dim preserving aspect ratio
    scale = min(max_dim / cw, max_dim / ch)
    target_w = max(1, int(round(cw * scale)))
    target_h = max(1, int(round(ch * scale)))

    scaled = cropped.resize((target_w, target_h), Image.Resampling.LANCZOS)

    # Center on a transparent max_dim x max_dim square
    thumb = Image.new("RGBA", (max_dim, max_dim), (0, 0, 0, 0))
    paste_x = (max_dim - target_w) // 2
    paste_y = (max_dim - target_h) // 2
    thumb.alpha_composite(scaled, (paste_x, paste_y))

    return thumb


def generate_thumbnails(assets_dir: Path, catalog_file: Optional[Path] = None) -> int:
    """
    Generates 256x256 thumbnails for all items using bounding-box cropping with ~8% padding.
    - For items with secondary layers (e.g. hair_front + hair_back, eyes_line + eyes_iris),
      composites them together before computing the thumbnail.
    - For eyes, uses the open-state files.
    """
    print("\n--- Generating Thumbnails (Bounding Box Zoom + 8% Padding) ---")
    items_dir = assets_dir / "items"
    thumbnails_dir = items_dir / "thumbnails"
    thumbnails_dir.mkdir(parents=True, exist_ok=True)

    count = 0
    generated_thumbs = set()

    # 1. Process catalog items first (handles secondary layer composition & open-state eyes)
    if catalog_file is None:
        catalog_file = assets_dir / "catalog.json"

    if catalog_file.exists():
        try:
            with open(catalog_file, "r", encoding="utf-8") as f:
                catalog_items = json.load(f)
            if isinstance(catalog_items, list):
                for item in catalog_items:
                    thumb_rel = item.get("thumbnail")
                    main_file = item.get("file")
                    if not thumb_rel or not main_file:
                        continue

                    main_path = assets_dir / main_file
                    if not main_path.exists():
                        continue

                    is_bg = item.get("category") == "background" or "backgrounds" in main_file

                    try:
                        base_img = Image.open(main_path).convert("RGBA")

                        # Handle secondary layer composition
                        sec_file = item.get("secondaryFile")
                        if sec_file and not is_bg:
                            sec_path = assets_dir / sec_file
                            if sec_path.exists():
                                sec_img = Image.open(sec_path).convert("RGBA")
                                sec_layer = item.get("secondaryLayer", "").lower()
                                # Back hair and iris sit behind front hair / lineart
                                if sec_layer in ("hair_back", "eyes_iris", "tail", "wings"):
                                    comp = Image.alpha_composite(sec_img, base_img)
                                else:
                                    comp = Image.alpha_composite(base_img, sec_img)
                                base_img = comp

                        thumb = crop_to_bounding_box_thumbnail(base_img, THUMBNAIL_MAX_DIM, 0.08, is_background=is_bg)
                        target_thumb_path = assets_dir / thumb_rel
                        target_thumb_path.parent.mkdir(parents=True, exist_ok=True)
                        thumb.save(target_thumb_path, format="PNG", optimize=True)
                        generated_thumbs.add(target_thumb_path.name)
                        count += 1
                        print(f"  [Catalog Composite] Generated: {thumb_rel}")
                    except Exception as e:
                        print(f"  [ERROR] Failed to generate thumbnail for catalog item '{item.get('id')}': {e}")
        except Exception as e:
            print(f"  [WARN] Failed to read catalog for thumbnails: {e}")

    # 2. Process any remaining loose files in items_dir
    for root, dirs, files in os.walk(items_dir):
        if "thumbnails" in Path(root).parts:
            continue

        for file in sorted(files):
            ext = file.lower()
            if not (ext.endswith(".png") or ext.endswith(".jpg") or ext.endswith(".jpeg")):
                continue

            file_path = Path(root) / file
            stem = file_path.stem
            if stem.endswith("_back") or stem.endswith("_closed"):
                continue  # hair_back and closed eye alt_file do not need standalone catalog thumbnail

            thumb_name = f"thumb_{stem}.png" if not stem.startswith("thumb_") else f"{stem}.png"
            if thumb_name in generated_thumbs:
                continue

            thumb_path = thumbnails_dir / thumb_name
            is_bg = "backgrounds" in Path(root).parts or "background" in stem

            try:
                with Image.open(file_path) as img:
                    thumb = crop_to_bounding_box_thumbnail(img, THUMBNAIL_MAX_DIM, 0.08, is_background=is_bg)
                    thumb.save(thumb_path, format="PNG", optimize=True)
                    generated_thumbs.add(thumb_name)
                    count += 1
                    print(f"  [Standalone] Generated: {thumb_path.relative_to(assets_dir).as_posix()}")
            except Exception as e:
                print(f"  [ERROR] Failed to generate thumbnail for {file_path.name}: {e}")

    print(f"Thumbnail generation complete: {count} thumbnails generated.")
    return count


def humanize_name(raw_name: str) -> str:
    """Converts a snake_case item identifier into a human-readable English name."""
    words = raw_name.replace("_", " ").split()
    prefixes_to_clean = [
        "dress", "hair", "face", "top", "bottom", "bot", "outer", "shoes", "socks",
        "hat", "acc", "accessory", "bg", "background", "eyes", "brow", "mouth",
        "blush", "glasses", "ears", "tail", "wings"
    ]
    if len(words) > 1 and words[0].lower() in prefixes_to_clean:
        words = words[1:]
    return " ".join(word.capitalize() for word in words)


def regenerate_catalog(assets_dir: Path, catalog_file: Path) -> bool:
    """Scans item directory and rebuilds/updates catalog.json while preserving Phase 4 fields."""
    print("\n--- Updating catalog.json ---")
    items_dir = assets_dir / "items"
    if not items_dir.exists():
        print(f"[ERROR] Items directory not found: {items_dir}")
        return False

    existing_items: Dict[str, dict] = {}
    if catalog_file.exists():
        try:
            with open(catalog_file, "r", encoding="utf-8") as f:
                data = json.load(f)
                if isinstance(data, list):
                    for item in data:
                        if "id" in item:
                            existing_items[item["id"]] = item
        except Exception as e:
            print(f"[WARNING] Could not read existing catalog: {e}")

    catalog_entries: List[dict] = []
    seen_ids = set()

    for root, dirs, files in os.walk(items_dir):
        if "thumbnails" in Path(root).parts:
            continue

        folder_name = Path(root).name.lower()
        category = VALID_CATEGORIES.get(folder_name, folder_name)

        hair_pairs: Dict[str, Dict[str, Path]] = {}

        for file in sorted(files):
            ext = file.lower()
            if not (ext.endswith(".png") or ext.endswith(".jpg") or ext.endswith(".jpeg")):
                continue

            file_path = Path(root) / file
            rel_file = file_path.relative_to(assets_dir).as_posix()
            stem = file_path.stem

            if category == "hair_front" or folder_name in ("hair", "hair_front"):
                if stem.endswith("_front"):
                    base = stem[:-6]
                    hair_pairs.setdefault(base, {})["front"] = file_path
                    continue
                elif stem.endswith("_back"):
                    base = stem[:-5]
                    hair_pairs.setdefault(base, {})["back"] = file_path
                    continue

            item_id = stem
            existing = existing_items.get(item_id, {})

            primary_layer = folder_name
            cat_name = "background" if folder_name in ("backgrounds", "background") else folder_name

            thumb_rel = f"items/thumbnails/thumb_{stem}.png"
            if not (assets_dir / thumb_rel).exists() and (assets_dir / f"items/thumbnails/{stem}.png").exists():
                thumb_rel = f"items/thumbnails/{stem}.png"

            # Derive group & subcategory defaults
            default_group, default_subcat = CATEGORY_TO_GROUP.get(cat_name, ("clothes", cat_name))

            entry = {
                "id": item_id,
                "category": existing.get("category", cat_name),
                "layer": existing.get("layer", primary_layer),
                "file": rel_file,
                "thumbnail": existing.get("thumbnail", thumb_rel),
                "nameEn": existing.get("nameEn", humanize_name(stem)),
                "nameVi": existing.get("nameVi", existing.get("nameEn", humanize_name(stem))),
                "locked": existing.get("locked", False),
                "secondaryLayer": existing.get("secondaryLayer", None),
                "secondaryFile": existing.get("secondaryFile", None),
                "group": existing.get("group", default_group),
                "subcategory": existing.get("subcategory", default_subcat),
                "colorable": existing.get("colorable", cat_name in ("hair", "dress", "top", "bottom")),
                "palette": existing.get("palette", "hair" if cat_name == "hair" else "general")
            }

            if item_id not in seen_ids:
                catalog_entries.append(entry)
                seen_ids.add(item_id)

        # Hair pairs
        for base, pair in sorted(hair_pairs.items()):
            item_id = base
            existing = existing_items.get(item_id, {})
            front_file = pair.get("front")
            back_file = pair.get("back")

            rel_front = front_file.relative_to(assets_dir).as_posix() if front_file else None
            rel_back = back_file.relative_to(assets_dir).as_posix() if back_file else None
            thumb_rel = f"items/thumbnails/thumb_{base}.png"

            default_group, default_subcat = CATEGORY_TO_GROUP.get("hair", ("appearance", "hair"))

            entry = {
                "id": item_id,
                "category": "hair",
                "layer": "hair_front",
                "file": rel_front or rel_back,
                "thumbnail": existing.get("thumbnail", thumb_rel),
                "nameEn": existing.get("nameEn", humanize_name(base)),
                "nameVi": existing.get("nameVi", existing.get("nameEn", humanize_name(base))),
                "locked": existing.get("locked", False),
                "secondaryLayer": "hair_back" if rel_back else None,
                "secondaryFile": rel_back,
                "group": existing.get("group", default_group),
                "subcategory": existing.get("subcategory", default_subcat),
                "colorable": existing.get("colorable", True),
                "palette": existing.get("palette", "hair")
            }

            if item_id not in seen_ids:
                catalog_entries.append(entry)
                seen_ids.add(item_id)

    # Sort catalog according to standard category layering order
    def sort_key(item):
        cat = item.get("category", "")
        cat_idx = CATEGORY_ORDER.index(cat) if cat in CATEGORY_ORDER else 999
        return (cat_idx, item.get("id", ""))

    catalog_entries.sort(key=sort_key)

    with open(catalog_file, "w", encoding="utf-8") as f:
        json.dump(catalog_entries, f, indent=2, ensure_ascii=False)

    print(f"Catalog successfully written to {catalog_file} ({len(catalog_entries)} entries)")
    return True


def main():
    parser = argparse.ArgumentParser(description="Chibi Doll Asset Pipeline & Catalog Generator")
    parser.add_argument("--assets-dir", type=Path, default=Path("app/src/main/assets"), help="Assets directory path")
    parser.add_argument("--catalog-file", type=Path, default=None, help="catalog.json file path")
    parser.add_argument("--validate", action="store_true", help="Validate asset size, alpha mode, naming and catalog schema")
    parser.add_argument("--thumbnails", action="store_true", help="Generate 256x256 thumbnails")
    parser.add_argument("--catalog", action="store_true", help="Regenerate catalog.json from folders")
    parser.add_argument("--all", action="store_true", help="Run validation, thumbnail generation and catalog update")

    args = parser.parse_args()

    assets_dir = args.assets_dir.resolve()
    catalog_file = args.catalog_file.resolve() if args.catalog_file else (assets_dir / "catalog.json")

    run_all = args.all or (not args.validate and not args.thumbnails and not args.catalog)

    print("Chibi Doll Asset Pipeline")
    print(f"  Assets Directory: {assets_dir}")
    print(f"  Catalog File:     {catalog_file}")

    success = True
    if run_all or args.validate:
        if not run_validation(assets_dir):
            success = False
        if not validate_catalog_schema(catalog_file, assets_dir):
            success = False

    if run_all or args.thumbnails:
        generate_thumbnails(assets_dir, catalog_file)

    if run_all or args.catalog:
        if not regenerate_catalog(assets_dir, catalog_file):
            success = False

    if success:
        print("\n[SUCCESS] Asset pipeline executed successfully.")
        sys.exit(0)
    else:
        print("\n[WARNING] Pipeline completed with warnings or validation errors.")
        sys.exit(1)


if __name__ == "__main__":
    main()
