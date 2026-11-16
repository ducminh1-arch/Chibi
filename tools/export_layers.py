#!/usr/bin/env python3
"""
Chibi Doll - Solo-Artist Layer Export Tool (Hardened & Robust)
Accepts OpenRaster (.ora, from Krita) or Photoshop (.psd) files with a 2048x3072 source canvas.
- Extracts layers/groups named after catalog item IDs.
- Scales down to exactly 1024x1536 using the Lanczos filter (preserving alpha, full canvas alignment, position, no cropping).
- Saves images to `assets/items/<category>/`.
- Handles dual layers (hair front/back) and alt_file for open/closed idle blink eyes.
- Updates `catalog.json` (preserving existing translations, palettes, and metadata).
- Regenerates 256x256 bounding-box cropped thumbnails via `tools/asset_pipeline.py`.
- Runs full asset and schema validation.
- Strictly 100% offline, zero network access.

Safety & Robustness:
- --dry-run flag: inspects and prints full plan, writes nothing to disk.
- Automatic timestamped backup of catalog.json and any asset files about to be overwritten.
- Refuses to run if input file is missing, corrupted, or cannot be parsed.
- Skips hidden layers (visibility='hidden').
- Bakes layer opacity < 100% into the alpha channel.
- Flags non-normal blend modes and layer masks as invalid so the artist flattens them.
- Supports arbitrary nested groups.
- Handles Krita's cropped layer offsets (x, y) preserving paper-doll alignment.
- Prints exact expected layer naming conventions whenever a layer name is not recognized.

Usage:
    python tools/export_layers.py --input <path_to_file.ora_or_psd> [--assets-dir <dir>] [--dry-run]
"""

import argparse
import io
import json
import os
import re
import shutil
import sys
import zipfile
import xml.etree.ElementTree as ET
from dataclasses import dataclass, field
from datetime import datetime
from pathlib import Path
from typing import Dict, List, Optional, Set, Tuple

try:
    from PIL import Image, PsdImagePlugin
except ImportError:
    print("[ERROR] Pillow is required. Install Pillow using: pip install Pillow")
    sys.exit(1)

# Import helper functions from asset_pipeline
tools_dir = Path(__file__).resolve().parent
if str(tools_dir) not in sys.path:
    sys.path.insert(0, str(tools_dir))

import asset_pipeline

TARGET_WIDTH = 1024
TARGET_HEIGHT = 1536
SOURCE_DEFAULT_WIDTH = 2048
SOURCE_DEFAULT_HEIGHT = 3072

CATEGORY_FOLDER_MAP = {
    "background": "backgrounds",
    "backgrounds": "backgrounds",
    "accessory": "accessory",
    "blush": "blush",
    "body": "body",
    "bottom": "bottom",
    "dress": "dress",
    "ears": "ears",
    "eyebrows": "eyebrows",
    "eyes": "eyes",
    "eyes_line": "eyes",
    "eyes_iris": "eyes",
    "face": "face",
    "glasses": "glasses",
    "hair": "hair",
    "hat": "hat",
    "mouth": "mouth",
    "outerwear": "outerwear",
    "shoes": "shoes",
    "socks": "socks",
    "tail": "tail",
    "top": "top",
    "wings": "wings"
}

PREFIX_TO_CATEGORY = [
    ("bg_", "background", "background"),
    ("background_", "background", "background"),
    ("dress_", "dress", "dress"),
    ("top_", "top", "top"),
    ("bottom_", "bottom", "bottom"),
    ("bot_", "bottom", "bottom"),
    ("outer_", "outerwear", "outerwear"),
    ("outerwear_", "outerwear", "outerwear"),
    ("socks_", "socks", "socks"),
    ("sock_", "socks", "socks"),
    ("shoes_", "shoes", "shoes"),
    ("shoe_", "shoes", "shoes"),
    ("hat_", "hat", "hat"),
    ("glasses_", "glasses", "glasses"),
    ("ears_", "ears", "ears"),
    ("ear_", "ears", "ears"),
    ("tail_", "tail", "tail"),
    ("wings_", "wings", "wings"),
    ("wing_", "wings", "wings"),
    ("acc_", "accessory", "accessory"),
    ("accessory_", "accessory", "accessory"),
    ("blush_", "blush", "blush"),
    ("mouth_", "mouth", "mouth"),
    ("brow_", "eyebrows", "eyebrows"),
    ("eyebrow_", "eyebrows", "eyebrows"),
    ("eyebrows_", "eyebrows", "eyebrows"),
    ("body_", "body", "body"),
    ("face_", "face", "face"),
    ("hair_", "hair", "hair_front"),
    ("eyes_", "eyes", "eyes_line"),
    ("eye_", "eyes", "eyes_line"),
]

NORMAL_BLEND_MODES = {"svg:src-over", "src-over", "normal", "norm", ""}


@dataclass
class RawLayer:
    name: str
    image: Image.Image
    group_path: List[str] = field(default_factory=list)
    opacity: float = 1.0
    blend_mode: str = "normal"
    has_mask: bool = False


@dataclass
class ExportItem:
    item_id: str
    category: str
    primary_layer: str
    file_rel: str
    main_image: Image.Image
    alt_file_rel: Optional[str] = None
    alt_image: Optional[Image.Image] = None
    secondary_layer: Optional[str] = None
    secondary_file_rel: Optional[str] = None
    secondary_image: Optional[Image.Image] = None
    secondary_alt_file_rel: Optional[str] = None
    secondary_alt_image: Optional[Image.Image] = None


@dataclass
class ExportSummary:
    total_raw_layers: int = 0
    exported_items: int = 0
    saved_files: List[str] = field(default_factory=list)
    overwritten_files: List[str] = field(default_factory=list)
    skipped_layers: List[str] = field(default_factory=list)
    baked_opacity_layers: List[Tuple[str, float]] = field(default_factory=list)
    invalid_layers: List[str] = field(default_factory=list)
    unrecognized_names: List[str] = field(default_factory=list)
    backup_path: Optional[str] = None
    catalog_updated: bool = False
    validation_passed: bool = False
    parse_failed: bool = False


def clean_identifier(name: str) -> str:
    """Normalizes string to valid lowercase snake_case identifier."""
    cleaned = re.sub(r"[^a-zA-Z0-9_]", "_", name.lower().strip())
    cleaned = re.sub(r"_+", "_", cleaned).strip("_")
    return cleaned


def parse_opacity_value(val: Optional[str]) -> float:
    """Parses opacity string from XML attributes (e.g. '0.85', '85%', '1.0')."""
    if not val:
        return 1.0
    s = val.strip()
    if s.endswith("%"):
        try:
            return max(0.0, min(1.0, float(s[:-1]) / 100.0))
        except ValueError:
            return 1.0
    try:
        op = float(s)
        if op > 1.0 and op <= 100.0:
            op = op / 100.0
        return max(0.0, min(1.0, op))
    except ValueError:
        return 1.0


def print_expected_layer_names(name: str, group_path: List[str]):
    """Prints a clear warning with exact expected naming conventions when an identifier is not recognized."""
    grp_str = "/".join(group_path) if group_path else "<root>"
    print(f"\n  [WARN] Unrecognized layer name '{name}' (group hierarchy: '{grp_str}').")
    print(f"         Expected catalog naming prefixes:")
    print(f"           - Clothes:     dress_*, top_*, bottom_*, outer_*, socks_*, shoes_*")
    print(f"           - Accessories: hat_*, glasses_*, ears_*, tail_*, wings_*, acc_*")
    print(f"           - Appearance:  body_*, hair_*_front, hair_*_back, blush_*, mouth_*, brow_*")
    print(f"           - Eyes:        eyes_*_line_open, eyes_*_line_closed, eyes_*_iris_open, eyes_*_iris_closed")
    print(f"           - Scenes:      bg_*")
    print(f"         Or place the layer inside a group named after one of the categories above.\n")


def detect_category_and_layer(name: str, group_path: List[str], summary: Optional[ExportSummary] = None) -> Tuple[str, str]:
    """Determines category and primary layer key from layer name and group hierarchy."""
    clean = clean_identifier(name)

    # 1. Check direct prefix in layer name
    for prefix, cat, layer in PREFIX_TO_CATEGORY:
        if clean.startswith(prefix):
            return cat, layer

    # 2. Check parent group names
    for grp in reversed(group_path):
        grp_clean = clean_identifier(grp)
        for prefix, cat, layer in PREFIX_TO_CATEGORY:
            if grp_clean == cat or grp_clean.startswith(prefix):
                return cat, layer
        if grp_clean in CATEGORY_FOLDER_MAP:
            cat = CATEGORY_FOLDER_MAP[grp_clean]
            if cat == "backgrounds":
                cat = "background"
            default_layer = asset_pipeline.VALID_CATEGORIES.get(cat, cat)
            return cat, default_layer

    # Not recognized
    if summary is not None:
        summary.unrecognized_names.append(name)
    print_expected_layer_names(name, group_path)
    return "accessory", "accessory"


def load_layers_from_ora(ora_path: Path, summary: ExportSummary) -> Tuple[int, int, List[RawLayer]]:
    """
    Loads layers from an OpenRaster (.ora) archive with full safety validation.
    Handles nested groups, visibility, opacity baking, masks, blend modes, and cropped offsets.
    """
    if not zipfile.is_zipfile(ora_path):
        raise ValueError(f"Cannot parse input file: '{ora_path.name}' is not a valid OpenRaster zip archive.")

    layers: List[RawLayer] = []
    with zipfile.ZipFile(ora_path, "r") as zf:
        namelist = zf.namelist()
        if "mimetype" in namelist:
            mimetype = zf.read("mimetype").decode("utf-8", "ignore").strip()
            if mimetype != "image/openraster":
                print(f"[WARN] .ora mimetype is '{mimetype}', expected 'image/openraster'")

        if "stack.xml" not in namelist:
            raise ValueError(f"Cannot parse OpenRaster file: missing 'stack.xml' manifest in '{ora_path.name}'.")

        try:
            xml_data = zf.read("stack.xml")
            root = ET.fromstring(xml_data)
        except ET.ParseError as e:
            raise ValueError(f"Cannot parse OpenRaster file: invalid XML syntax in 'stack.xml' of '{ora_path.name}': {e}")

        root_tag = root.tag.split("}")[-1]
        if root_tag != "image":
            raise ValueError(f"Cannot parse OpenRaster file: root element is '<{root_tag}>', expected '<image>' in '{ora_path.name}'.")

        try:
            canvas_w = int(root.attrib.get("w", root.attrib.get("width", SOURCE_DEFAULT_WIDTH)))
            canvas_h = int(root.attrib.get("h", root.attrib.get("height", SOURCE_DEFAULT_HEIGHT)))
        except (ValueError, TypeError):
            raise ValueError(f"Cannot parse OpenRaster file: invalid or missing canvas dimensions (w, h) in '{ora_path.name}'.")

        print(f"[INFO] OpenRaster source canvas size: {canvas_w}x{canvas_h}")

        def parse_element(elem: ET.Element, current_groups: List[str], parent_hidden: bool, parent_opacity: float):
            tag = elem.tag.split("}")[-1]
            elem_hidden = parent_hidden or (elem.attrib.get("visibility", "visible").strip().lower() == "hidden")
            elem_opacity = parent_opacity * parse_opacity_value(elem.attrib.get("opacity"))

            if tag == "stack":
                group_name = elem.attrib.get("name")
                next_groups = list(current_groups)
                if group_name:
                    next_groups.append(group_name)
                for child in elem:
                    parse_element(child, next_groups, elem_hidden, elem_opacity)

            elif tag == "layer":
                name = elem.attrib.get("name", "")
                src = elem.attrib.get("src")

                # 1. Hidden layer check
                if elem_hidden:
                    print(f"  [SKIP] Hidden layer '{name or src}' skipped (visibility='hidden')")
                    summary.skipped_layers.append(name or str(src))
                    return

                if not src or src not in namelist:
                    print(f"  [WARN] Layer '{name}' references missing data entry '{src}' in archive. Skipping.")
                    return

                if not name:
                    name = Path(src).stem

                # 2. Blend mode check
                comp_op = elem.attrib.get("composite-op", "svg:src-over").strip().lower()
                if comp_op not in NORMAL_BLEND_MODES:
                    msg = f"Layer '{name}' uses non-normal blend mode '{comp_op}'. Please flatten or set blend mode to Normal (svg:src-over) before exporting."
                    print(f"  [INVALID] {msg}")
                    summary.invalid_layers.append(msg)

                # 3. Layer mask check
                has_mask = any(c.tag.split("}")[-1] == "mask" for c in elem) or bool(elem.attrib.get("mask"))
                if has_mask:
                    msg = f"Layer '{name}' contains a layer mask. Please apply or flatten masks before exporting."
                    print(f"  [INVALID] {msg}")
                    summary.invalid_layers.append(msg)

                # 4. Read coordinates (handles Krita's cropped layers)
                try:
                    x = int(round(float(elem.attrib.get("x", "0"))))
                    y = int(round(float(elem.attrib.get("y", "0"))))
                except (ValueError, TypeError):
                    x, y = 0, 0

                raw_bytes = zf.read(src)
                with Image.open(io.BytesIO(raw_bytes)) as layer_img:
                    converted = layer_img.convert("RGBA")

                    # 5. Opacity baking (< 100%)
                    if elem_opacity < 0.999:
                        r, g, b, a = converted.split()
                        a = a.point(lambda p: int(round(p * elem_opacity)))
                        converted = Image.merge("RGBA", (r, g, b, a))
                        print(f"  [INFO] Baked opacity {elem_opacity*100:.1f}% into alpha channel for '{name}'")
                        summary.baked_opacity_layers.append((name, elem_opacity))

                    # 6. Place cropped layer onto full canvas preserving paper-doll alignment
                    if converted.size != (canvas_w, canvas_h) or x != 0 or y != 0:
                        full_img = Image.new("RGBA", (canvas_w, canvas_h), (0, 0, 0, 0))
                        full_img.paste(converted, (x, y))
                    else:
                        full_img = converted.copy()

                    layers.append(RawLayer(
                        name=name,
                        image=full_img,
                        group_path=list(current_groups),
                        opacity=elem_opacity,
                        blend_mode=comp_op,
                        has_mask=has_mask
                    ))

        for child in root:
            parse_element(child, [], parent_hidden=False, parent_opacity=1.0)

    return canvas_w, canvas_h, layers


def load_layers_from_psd(psd_path: Path, summary: ExportSummary) -> Tuple[int, int, List[RawLayer]]:
    """Loads layers from a Photoshop (.psd) file using Pillow's PsdImagePlugin or psd-tools."""
    # Check if psd_tools is available
    try:
        from psd_tools import PSDImage
        print("[INFO] Using psd_tools to parse PSD")
        try:
            psd = PSDImage.open(psd_path)
        except Exception as e:
            raise ValueError(f"Cannot parse PSD file '{psd_path.name}': {e}")

        canvas_w, canvas_h = psd.width, psd.height
        print(f"[INFO] PSD canvas size: {canvas_w}x{canvas_h}")

        layers: List[RawLayer] = []

        def traverse_psd(node, groups: List[str], parent_hidden: bool):
            is_hidden = parent_hidden or (hasattr(node, "visible") and not node.visible)
            if node.is_group():
                next_groups = list(groups)
                if node.name:
                    next_groups.append(node.name)
                for child in node:
                    traverse_psd(child, next_groups, is_hidden)
            else:
                name = node.name or "layer"
                if is_hidden:
                    print(f"  [SKIP] Hidden layer '{name}' skipped (visibility='hidden')")
                    summary.skipped_layers.append(name)
                    return

                blend_mode = getattr(node, "blend_mode", "normal")
                if blend_mode and str(blend_mode).lower() not in ("normal", "norm", "blendmode.normal"):
                    msg = f"Layer '{name}' uses non-normal blend mode '{blend_mode}'. Please flatten or set blend mode to Normal before exporting."
                    print(f"  [INVALID] {msg}")
                    summary.invalid_layers.append(msg)

                has_mask = getattr(node, "has_mask", lambda: False)()
                if has_mask:
                    msg = f"Layer '{name}' contains a layer mask. Please apply or flatten masks before exporting."
                    print(f"  [INVALID] {msg}")
                    summary.invalid_layers.append(msg)

                layer_img = node.composite()
                if layer_img is not None:
                    full_img = Image.new("RGBA", (canvas_w, canvas_h), (0, 0, 0, 0))
                    full_img.paste(layer_img.convert("RGBA"), (node.left, node.top))
                    layers.append(RawLayer(name=name, image=full_img, group_path=list(groups)))

        for layer in psd:
            traverse_psd(layer, [], parent_hidden=False)

        return canvas_w, canvas_h, layers
    except ImportError:
        pass

    # Fallback to Pillow's built-in PSD reader
    print("[INFO] Using Pillow PsdImagePlugin to parse PSD")
    try:
        with Image.open(psd_path) as psd:
            canvas_w, canvas_h = psd.size
            print(f"[INFO] PSD canvas size: {canvas_w}x{canvas_h}")

            layers: List[RawLayer] = []
            if hasattr(psd, "layers") and psd.layers:
                for idx, (name, mode, (x0, y0, x1, y1), tile) in enumerate(psd.layers):
                    psd.seek(idx + 1)
                    tile_img = psd.copy().convert("RGBA")
                    full_img = Image.new("RGBA", (canvas_w, canvas_h), (0, 0, 0, 0))
                    full_img.paste(tile_img, (x0, y0))
                    layers.append(RawLayer(name=name, image=full_img, group_path=[]))
            else:
                layers.append(RawLayer(name=psd_path.stem, image=psd.convert("RGBA"), group_path=[]))

            return canvas_w, canvas_h, layers
    except Exception as e:
        raise ValueError(f"Cannot parse PSD file '{psd_path.name}': {e}")


def scale_to_target_canvas(image: Image.Image) -> Image.Image:
    """Scales image to 1024x1536 using Lanczos resampling (preserving alpha and position)."""
    if image.size == (TARGET_WIDTH, TARGET_HEIGHT):
        return image.copy()
    return image.resize((TARGET_WIDTH, TARGET_HEIGHT), Image.Resampling.LANCZOS)


def organize_layers_into_items(raw_layers: List[RawLayer], summary: Optional[ExportSummary] = None) -> List[ExportItem]:
    """
    Groups raw layers into game items:
    - Normal single-layer items (dress, shoes, socks, hat, etc.)
    - Hair items with dual layers (front + back)
    - Eyes items with open/closed line and optional iris
    """
    scaled_layers: List[Tuple[str, str, Image.Image, List[str]]] = []
    for rl in raw_layers:
        scaled = scale_to_target_canvas(rl.image)
        scaled_layers.append((rl.name, clean_identifier(rl.name), scaled, rl.group_path))

    hair_groups: Dict[str, Dict[str, Image.Image]] = {}
    eye_groups: Dict[str, Dict[str, Image.Image]] = {}
    general_items: Dict[str, Tuple[str, str, Image.Image]] = {}

    for original_name, clean_name, img, group_path in scaled_layers:
        cat, layer = detect_category_and_layer(clean_name, group_path, summary)

        # Hair front/back detection
        if cat == "hair" or clean_name.startswith("hair_"):
            if clean_name.endswith("_front"):
                base_id = clean_name[:-6]
                hair_groups.setdefault(base_id, {})["front"] = img
                continue
            elif clean_name.endswith("_back"):
                base_id = clean_name[:-5]
                hair_groups.setdefault(base_id, {})["back"] = img
                continue
            elif any(g.lower().startswith("hair_") for g in group_path):
                parent_hair = [g for g in group_path if clean_identifier(g).startswith("hair_")][0]
                base_id = clean_identifier(parent_hair)
                if "front" in clean_name:
                    hair_groups.setdefault(base_id, {})["front"] = img
                    continue
                elif "back" in clean_name:
                    hair_groups.setdefault(base_id, {})["back"] = img
                    continue

        # Eyes detection
        if cat == "eyes" or clean_name.startswith("eyes_") or clean_name.startswith("eye_"):
            base_id = clean_name
            for suffix in ["_line_open", "_line_closed", "_line_blink",
                           "_iris_open", "_iris_closed", "_iris_blink",
                           "_open", "_closed", "_blink"]:
                if base_id.endswith(suffix):
                    base_id = base_id[:-len(suffix)]
                    break

            if "_line" in clean_name:
                if "_closed" in clean_name or "_blink" in clean_name:
                    eye_groups.setdefault(base_id, {})["line_closed"] = img
                else:
                    eye_groups.setdefault(base_id, {})["line_open"] = img
                continue
            elif "_iris" in clean_name:
                if "_closed" in clean_name or "_blink" in clean_name:
                    eye_groups.setdefault(base_id, {})["iris_closed"] = img
                else:
                    eye_groups.setdefault(base_id, {})["iris_open"] = img
                continue
            else:
                if "_closed" in clean_name or "_blink" in clean_name:
                    eye_groups.setdefault(base_id, {})["line_closed"] = img
                else:
                    eye_groups.setdefault(base_id, {})["line_open"] = img
                continue

        # General single-layer item
        # Check if inside an item-named group (e.g. hat_giant_ribbon, dress_summer)
        item_group = None
        for grp in reversed(group_path):
            grp_id = clean_identifier(grp)
            for p, _, _ in PREFIX_TO_CATEGORY:
                if grp_id.startswith(p):
                    item_group = grp_id
                    break
            if item_group:
                break

        if item_group and clean_name in ("front", "main", "layer", "art", "img", "image"):
            item_id = item_group
        else:
            item_id = clean_name
            if not any(item_id.startswith(p[0]) for p in PREFIX_TO_CATEGORY):
                folder = CATEGORY_FOLDER_MAP.get(cat, cat)
                prefix = "bg_" if cat == "background" else f"{cat}_"
                item_id = f"{prefix}{item_id}"

        general_items[item_id] = (cat, layer, img)

    items: List[ExportItem] = []

    # 1. Assemble hair items
    for hair_id, parts in hair_groups.items():
        front_img = parts.get("front")
        back_img = parts.get("back")
        main_img = front_img if front_img is not None else back_img
        if main_img is None:
            continue

        front_rel = f"items/hair/{hair_id}_front.png"
        back_rel = f"items/hair/{hair_id}_back.png" if back_img is not None else None

        items.append(ExportItem(
            item_id=hair_id,
            category="hair",
            primary_layer="hair_front",
            file_rel=front_rel,
            main_image=front_img if front_img is not None else main_img,
            secondary_layer="hair_back" if back_img is not None else None,
            secondary_file_rel=back_rel,
            secondary_image=back_img
        ))

    # 2. Assemble eye items
    for eye_id, parts in eye_groups.items():
        line_open = parts.get("line_open")
        line_closed = parts.get("line_closed")
        iris_open = parts.get("iris_open")
        iris_closed = parts.get("iris_closed")

        if line_open is None and iris_open is None:
            continue

        main_img = line_open if line_open is not None else iris_open
        file_rel = f"items/eyes/{eye_id}_line_open.png" if line_open is not None else f"items/eyes/{eye_id}_iris_open.png"
        alt_rel = f"items/eyes/{eye_id}_line_closed.png" if line_closed is not None else None

        has_iris = (iris_open is not None)
        sec_layer = "eyes_iris" if has_iris else None
        sec_file = f"items/eyes/{eye_id}_iris_open.png" if has_iris else None
        sec_alt_file = f"items/eyes/{eye_id}_iris_closed.png" if iris_closed is not None else ("items/eyes/eyes_iris_closed.png" if has_iris else None)

        items.append(ExportItem(
            item_id=eye_id,
            category="eyes",
            primary_layer="eyes_line",
            file_rel=file_rel,
            main_image=main_img,
            alt_file_rel=alt_rel,
            alt_image=line_closed,
            secondary_layer=sec_layer,
            secondary_file_rel=sec_file,
            secondary_image=iris_open,
            secondary_alt_file_rel=sec_alt_file,
            secondary_alt_image=iris_closed
        ))

    # 3. Assemble general items
    for item_id, (cat, layer, img) in general_items.items():
        folder = CATEGORY_FOLDER_MAP.get(cat, cat)
        file_rel = f"items/{folder}/{item_id}.png"
        items.append(ExportItem(
            item_id=item_id,
            category=cat,
            primary_layer=layer,
            file_rel=file_rel,
            main_image=img
        ))

    return items


def create_automatic_backup(
    assets_dir: Path,
    catalog_file: Path,
    items: List[ExportItem]
) -> Optional[Path]:
    """
    Creates a timestamped backup of catalog.json and any asset files that are about to be overwritten.
    Backup directory is located at: <assets_dir>/../../backups/backup_YYYYMMDD_HHMMSS/
    """
    timestamp = datetime.now().strftime("%Y%m%d_%H%M%S")
    backup_root = assets_dir.parent.parent / "backups" / f"backup_{timestamp}"

    backed_up_count = 0

    # 1. Backup catalog.json
    if catalog_file.exists():
        backup_root.mkdir(parents=True, exist_ok=True)
        shutil.copy2(catalog_file, backup_root / "catalog.json")
        backed_up_count += 1

    # 2. Check each target asset file
    for itm in items:
        files_to_check = [itm.file_rel, itm.alt_file_rel, itm.secondary_file_rel, itm.secondary_alt_file_rel]
        for rel in files_to_check:
            if rel:
                target_path = assets_dir / rel
                if target_path.exists():
                    dest = backup_root / rel
                    dest.parent.mkdir(parents=True, exist_ok=True)
                    shutil.copy2(target_path, dest)
                    backed_up_count += 1

    if backed_up_count > 0:
        print(f"\n[BACKUP] Created automatic safety backup of {backed_up_count} files at:")
        print(f"         {backup_root.resolve()}")
        return backup_root
    return None


def update_catalog_file(catalog_file: Path, exported_items: List[ExportItem]) -> bool:
    """Updates catalog.json, preserving all existing metadata and translations."""
    existing_items: List[dict] = []
    existing_map: Dict[str, dict] = {}

    if catalog_file.exists():
        try:
            with open(catalog_file, "r", encoding="utf-8") as f:
                data = json.load(f)
                if isinstance(data, list):
                    existing_items = data
                    for item in data:
                        if "id" in item:
                            existing_map[item["id"]] = item
        except Exception as e:
            print(f"[WARN] Could not parse existing catalog.json: {e}")

    for exp in exported_items:
        existing = existing_map.get(exp.item_id)
        default_group, default_subcat = asset_pipeline.CATEGORY_TO_GROUP.get(exp.category, ("clothes", exp.category))

        is_colorable = exp.category in ("hair", "dress", "top", "bottom", "body") or exp.secondary_layer == "eyes_iris"
        palette = "hair" if exp.category == "hair" else "skin" if exp.category == "body" else "eyes" if exp.category == "eyes" else ("general" if is_colorable else None)

        thumb_rel = f"items/thumbnails/thumb_{exp.item_id}.png"

        if existing:
            existing["category"] = existing.get("category", exp.category)
            existing["layer"] = existing.get("layer", exp.primary_layer)
            existing["file"] = exp.file_rel
            existing["thumbnail"] = existing.get("thumbnail", thumb_rel)
            existing["nameEn"] = existing.get("nameEn", asset_pipeline.humanize_name(exp.item_id))
            existing["nameVi"] = existing.get("nameVi", existing["nameEn"])
            existing["locked"] = existing.get("locked", False)
            existing["group"] = existing.get("group", default_group)
            existing["subcategory"] = existing.get("subcategory", default_subcat)
            existing["colorable"] = existing.get("colorable", is_colorable)
            if "palette" not in existing or existing["palette"] is None:
                existing["palette"] = palette
            if exp.alt_file_rel:
                existing["alt_file"] = exp.alt_file_rel
            if exp.secondary_layer:
                existing["secondaryLayer"] = exp.secondary_layer
                existing["secondaryFile"] = exp.secondary_file_rel
            if exp.secondary_alt_file_rel:
                existing["secondary_alt_file"] = exp.secondary_alt_file_rel
        else:
            human_name = asset_pipeline.humanize_name(exp.item_id)
            new_entry = {
                "id": exp.item_id,
                "category": exp.category,
                "layer": exp.primary_layer,
                "file": exp.file_rel,
                "thumbnail": thumb_rel,
                "nameEn": human_name,
                "nameVi": human_name,
                "locked": False,
                "secondaryLayer": exp.secondary_layer,
                "secondaryFile": exp.secondary_file_rel,
                "group": default_group,
                "subcategory": default_subcat,
                "colorable": is_colorable,
                "palette": palette
            }
            if exp.alt_file_rel:
                new_entry["alt_file"] = exp.alt_file_rel
            if exp.secondary_alt_file_rel:
                new_entry["secondary_alt_file"] = exp.secondary_alt_file_rel

            existing_items.append(new_entry)
            existing_map[exp.item_id] = new_entry

    def sort_key(item):
        cat = item.get("category", "")
        cat_idx = asset_pipeline.CATEGORY_ORDER.index(cat) if cat in asset_pipeline.CATEGORY_ORDER else 999
        return (cat_idx, item.get("id", ""))

    existing_items.sort(key=sort_key)

    with open(catalog_file, "w", encoding="utf-8") as f:
        json.dump(existing_items, f, indent=2, ensure_ascii=False)

    print(f"[OK] Updated catalog.json with {len(exported_items)} exported items.")
    return True


def export_layers(
    input_file: Path,
    assets_dir: Path,
    catalog_file: Optional[Path] = None,
    dry_run: bool = False,
    run_thumbnails: bool = True,
    run_validation_check: bool = True
) -> ExportSummary:
    """Core export function for solo-artist PSD / ORA files."""
    summary = ExportSummary()

    # Refuse to run if input file is missing
    if not input_file.exists():
        summary.parse_failed = True
        raise FileNotFoundError(f"Input file not found: '{input_file.resolve()}'")

    if catalog_file is None:
        catalog_file = assets_dir / "catalog.json"

    ext = input_file.suffix.lower()
    print(f"\n=======================================================")
    print(f"Chibi Doll - Solo-Artist Layer Exporter (Hardened)")
    print(f"  Input:       {input_file.resolve()}")
    print(f"  Assets Dir:  {assets_dir.resolve()}")
    print(f"  Catalog:     {catalog_file.resolve()}")
    print(f"  Dry Run:     {dry_run}")
    print(f"=======================================================\n")

    # Load and parse with refusal on failure
    try:
        if ext == ".ora":
            canvas_w, canvas_h, raw_layers = load_layers_from_ora(input_file, summary)
        elif ext == ".psd":
            canvas_w, canvas_h, raw_layers = load_layers_from_psd(input_file, summary)
        else:
            summary.parse_failed = True
            raise ValueError(f"Unsupported file format '{ext}'. Must be .ora (OpenRaster) or .psd")
    except Exception as e:
        summary.parse_failed = True
        print(f"\n[FATAL ERROR] Refusing to run: input file could not be parsed.")
        print(f"              {e}")
        raise

    summary.total_raw_layers = len(raw_layers)
    print(f"[INFO] Loaded {len(raw_layers)} active layers from canvas {canvas_w}x{canvas_h}")

    items = organize_layers_into_items(raw_layers, summary)
    summary.exported_items = len(items)
    print(f"[INFO] Organized into {len(items)} game items.")

    for itm in items:
        target_path = assets_dir / itm.file_rel
        status_tag = "[OVERWRITE]" if target_path.exists() else "[NEW]"
        if target_path.exists():
            summary.overwritten_files.append(str(target_path))
        print(f"  - Item '{itm.item_id}' [{itm.category}] -> {status_tag} '{itm.file_rel}'")
        if itm.alt_file_rel:
            alt_status = "[OVERWRITE]" if (assets_dir / itm.alt_file_rel).exists() else "[NEW]"
            print(f"      alt_file: {alt_status} {itm.alt_file_rel}")
        if itm.secondary_file_rel:
            sec_status = "[OVERWRITE]" if (assets_dir / itm.secondary_file_rel).exists() else "[NEW]"
            print(f"      secondary: {sec_status} {itm.secondary_file_rel}")

    # Check for invalid layers (masks, unsupported blend modes)
    if summary.invalid_layers:
        print(f"\n[WARNING] {len(summary.invalid_layers)} layer warning(s) detected:")
        for warn in summary.invalid_layers:
            print(f"  ! {warn}")

    # DRY-RUN EXIT
    if dry_run:
        print("\n=======================================================")
        print("  [DRY-RUN SUMMARY]")
        print(f"  Raw layers parsed:      {summary.total_raw_layers}")
        print(f"  Hidden layers skipped:  {len(summary.skipped_layers)}")
        print(f"  Opacities baked:        {len(summary.baked_opacity_layers)}")
        print(f"  Invalid layer warnings: {len(summary.invalid_layers)}")
        print(f"  Game items to export:   {len(items)}")
        print(f"  Files to overwrite:     {len(summary.overwritten_files)}")
        print(f"  Catalog file target:    {catalog_file.resolve()}")
        print("\n  [DRY-RUN] No files were modified or written to disk.")
        print("=======================================================\n")
        summary.validation_passed = (len(summary.invalid_layers) == 0)
        return summary

    # AUTOMATIC TIMESTAMPED BACKUP
    backup_path = create_automatic_backup(assets_dir, catalog_file, items)
    if backup_path:
        summary.backup_path = str(backup_path)

    # Save images to assets
    for itm in items:
        # 1. Main image
        main_dest = assets_dir / itm.file_rel
        main_dest.parent.mkdir(parents=True, exist_ok=True)
        itm.main_image.save(main_dest, format="PNG", optimize=True)
        summary.saved_files.append(str(main_dest))

        # 2. Alt image (e.g. closed eyes)
        if itm.alt_image and itm.alt_file_rel:
            alt_dest = assets_dir / itm.alt_file_rel
            alt_dest.parent.mkdir(parents=True, exist_ok=True)
            itm.alt_image.save(alt_dest, format="PNG", optimize=True)
            summary.saved_files.append(str(alt_dest))

        # 3. Secondary image (e.g. hair back, eyes iris)
        if itm.secondary_image and itm.secondary_file_rel:
            sec_dest = assets_dir / itm.secondary_file_rel
            sec_dest.parent.mkdir(parents=True, exist_ok=True)
            itm.secondary_image.save(sec_dest, format="PNG", optimize=True)
            summary.saved_files.append(str(sec_dest))

        # 4. Secondary alt image (e.g. iris closed)
        if itm.secondary_alt_image and itm.secondary_alt_file_rel:
            sec_alt_dest = assets_dir / itm.secondary_alt_file_rel
            sec_alt_dest.parent.mkdir(parents=True, exist_ok=True)
            itm.secondary_alt_image.save(sec_alt_dest, format="PNG", optimize=True)
            summary.saved_files.append(str(sec_alt_dest))

    print(f"\n[OK] Wrote {len(summary.saved_files)} PNG files to {assets_dir.as_posix()}/items/")

    # Update catalog.json
    summary.catalog_updated = update_catalog_file(catalog_file, items)

    # Regenerate thumbnails using Task 2 bounding-box method
    if run_thumbnails:
        thumb_count = asset_pipeline.generate_thumbnails(assets_dir, catalog_file)
        print(f"[OK] Generated {thumb_count} thumbnails with bounding-box cropping.")

    # Run validation
    if run_validation_check:
        val_assets = asset_pipeline.run_validation(assets_dir)
        val_schema = asset_pipeline.validate_catalog_schema(catalog_file, assets_dir)
        summary.validation_passed = val_assets and val_schema and (len(summary.invalid_layers) == 0)
        if summary.validation_passed:
            print("\n[SUCCESS] All exported assets and catalog schema passed validation!")
        else:
            print("\n[WARNING] Validation reported warnings or errors.")
    else:
        summary.validation_passed = (len(summary.invalid_layers) == 0)

    return summary


def main():
    parser = argparse.ArgumentParser(description="Chibi Doll - Solo-Artist Layer Exporter (.ora / .psd)")
    parser.add_argument("--input", "-i", type=Path, required=True, help="Path to .ora or .psd source file")
    parser.add_argument("--assets-dir", type=Path, default=Path("app/src/main/assets"), help="Target assets directory")
    parser.add_argument("--catalog-file", type=Path, default=None, help="Path to catalog.json (defaults to assets-dir/catalog.json)")
    parser.add_argument("--dry-run", action="store_true", help="Inspect and simulate export without writing to disk")
    parser.add_argument("--no-thumbnails", action="store_true", help="Skip thumbnail regeneration")
    parser.add_argument("--no-validation", action="store_true", help="Skip running validation")

    args = parser.parse_args()

    try:
        summary = export_layers(
            input_file=args.input.resolve(),
            assets_dir=args.assets_dir.resolve(),
            catalog_file=args.catalog_file.resolve() if args.catalog_file else None,
            dry_run=args.dry_run,
            run_thumbnails=not args.no_thumbnails,
            run_validation_check=not args.no_validation
        )
        if not summary.validation_passed and not args.dry_run:
            sys.exit(1)
    except Exception as e:
        print(f"\n[ERROR] Export failed: {e}")
        sys.exit(1)


if __name__ == "__main__":
    main()
