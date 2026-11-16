#!/usr/bin/env python3
"""
Chibi Doll - Asset & Data Synchronizer (Android -> iOS)
Synchronizes art assets, catalog definitions, color palettes, and string localizations
from the source Android project into the native iOS project (D:\\Chibi-iOS).

Keeps Android as the single source of truth for:
- catalog.json & palettes.json
- 1024x1536 layer PNGs and 256x256 bounding-box thumbnails
- String localizations (converts Android strings.xml to iOS Localizable.xcstrings)

Usage:
    python tools/sync_assets.py [--android-root <path>] [--ios-root <path>]
"""

import argparse
import json
import os
import shutil
import sys
import xml.etree.ElementTree as ET
from pathlib import Path


def parse_android_strings(xml_path: Path) -> dict:
    """Parses an Android strings.xml file into a key-value dictionary."""
    if not xml_path.exists():
        print(f"[WARN] String file not found: {xml_path}")
        return {}

    tree = ET.parse(xml_path)
    root = tree.getroot()
    strings_map = {}

    for elem in root.findall("string"):
        key = elem.attrib.get("name")
        text = elem.text or ""
        # Unescape Android string formatting
        text = text.replace(r"\'", "'").replace(r'\"', '"').replace(r"\&amp;", "&")
        if key:
            strings_map[key] = text

    return strings_map


def build_xcstrings(en_strings: dict, vi_strings: dict) -> dict:
    """Constructs Apple Localizable.xcstrings JSON structure (iOS 17+ String Catalog)."""
    all_keys = sorted(set(list(en_strings.keys()) + list(vi_strings.keys())))
    strings_catalog = {}

    for k in all_keys:
        entry = {
            "extractionState": "manual",
            "localizations": {}
        }
        if k in en_strings:
            entry["localizations"]["en"] = {
                "stringUnit": {
                    "state": "translated",
                    "value": en_strings[k]
                }
            }
        if k in vi_strings:
            entry["localizations"]["vi"] = {
                "stringUnit": {
                    "state": "translated",
                    "value": vi_strings[k]
                }
            }
        strings_catalog[k] = entry

    return {
        "sourceLanguage": "en",
        "strings": strings_catalog,
        "version": "1.0"
    }


def sync_assets(android_root: Path, ios_root: Path):
    print("=======================================================")
    print("Chibi Doll - Asset Synchronizer (Android -> iOS)")
    print(f"  Android Source: {android_root.resolve()}")
    print(f"  iOS Target:     {ios_root.resolve()}")
    print("=======================================================\n")

    android_assets = android_root / "app" / "src" / "main" / "assets"
    if not android_assets.exists():
        raise FileNotFoundError(f"Android assets folder not found at: {android_assets}")

    ios_resources = ios_root / "ChibiDoll" / "Resources"
    ios_resources.mkdir(parents=True, exist_ok=True)

    # 1. Sync catalog.json
    src_catalog = android_assets / "catalog.json"
    dst_catalog = ios_resources / "catalog.json"
    if src_catalog.exists():
        shutil.copy2(src_catalog, dst_catalog)
        with open(dst_catalog, "r", encoding="utf-8") as f:
            cat_data = json.load(f)
        print(f"[OK] Synced catalog.json ({len(cat_data)} items)")
    else:
        print(f"[ERROR] Source catalog.json missing at: {src_catalog}")

    # 2. Sync palettes.json
    src_palettes = android_assets / "palettes.json"
    dst_palettes = ios_resources / "palettes.json"
    if src_palettes.exists():
        shutil.copy2(src_palettes, dst_palettes)
        with open(dst_palettes, "r", encoding="utf-8") as f:
            pal_data = json.load(f)
        pal_count = len(pal_data.get("palettes", []))
        print(f"[OK] Synced palettes.json ({pal_count} palettes)")
    else:
        print(f"[ERROR] Source palettes.json missing at: {src_palettes}")

    # 3. Sync items/ directory
    src_items = android_assets / "items"
    dst_items = ios_resources / "items"
    if src_items.exists():
        # Copy directory tree
        if dst_items.exists():
            shutil.rmtree(dst_items)
        shutil.copytree(src_items, dst_items)
        
        # Count files
        total_pngs = sum(1 for _ in dst_items.rglob("*.png"))
        print(f"[OK] Synced items/ directory tree ({total_pngs} PNG assets)")
    else:
        print(f"[ERROR] Source items folder missing at: {src_items}")

    # 4. Convert strings.xml to Localizable.xcstrings
    en_xml = android_root / "app" / "src" / "main" / "res" / "values" / "strings.xml"
    vi_xml = android_root / "app" / "src" / "main" / "res" / "values-vi" / "strings.xml"

    en_dict = parse_android_strings(en_xml)
    vi_dict = parse_android_strings(vi_xml)

    xcstrings = build_xcstrings(en_dict, vi_dict)
    dst_xcstrings = ios_resources / "Localizable.xcstrings"
    with open(dst_xcstrings, "w", encoding="utf-8") as f:
        json.dump(xcstrings, f, indent=2, ensure_ascii=False)
    print(f"[OK] Generated Localizable.xcstrings ({len(xcstrings['strings'])} localized keys)")

    print("\n[SUCCESS] Synchronization complete.")


def main():
    parser = argparse.ArgumentParser(description="Sync Android assets & data to iOS")
    parser.add_argument("--android-root", type=Path, default=Path("D:/Chibi"), help="Path to Android project root")
    parser.add_argument("--ios-root", type=Path, default=Path("D:/Chibi-iOS"), help="Path to iOS project root")

    args = parser.parse_args()
    try:
        sync_assets(args.android_root, args.ios_root)
    except Exception as e:
        print(f"\n[ERROR] Sync failed: {e}")
        sys.exit(1)


if __name__ == "__main__":
    main()

