#!/usr/bin/env python3
import json
from pathlib import Path

CATALOG_PATH = Path("app/src/main/assets/catalog.json")

def update_catalog():
    with open(CATALOG_PATH, "r", encoding="utf-8") as f:
        items = json.load(f)

    # 1. Update existing eye items to be dual-layer (primary = eyes_line, secondary = eyes_iris)
    eye_updates = {
        "eyes_purple": {
            "layer": "eyes_line",
            "file": "items/eyes/eyes_purple_line_open.png",
            "alt_file": "items/eyes/eyes_purple_line_closed.png",
            "secondaryLayer": "eyes_iris",
            "secondaryFile": "items/eyes/eyes_purple_iris_open.png",
            "secondary_alt_file": "items/eyes/eyes_purple_iris_closed.png",
            "thumbnail": "items/thumbnails/thumb_eyes_purple.png",
            "colorable": True,
            "palette": "eyes",
            "group": "appearance",
            "subcategory": "eyes"
        },
        "eyes_wink_pink": {
            "layer": "eyes_line",
            "file": "items/eyes/eyes_wink_line_open.png",
            "alt_file": "items/eyes/eyes_wink_line_closed.png",
            "secondaryLayer": "eyes_iris",
            "secondaryFile": "items/eyes/eyes_wink_iris_open.png",
            "secondary_alt_file": "items/eyes/eyes_wink_iris_closed.png",
            "thumbnail": "items/thumbnails/thumb_eyes_wink.png",
            "colorable": True,
            "palette": "eyes",
            "group": "appearance",
            "subcategory": "eyes"
        },
        "eyes_brown": {
            "layer": "eyes_line",
            "file": "items/eyes/eyes_brown_line_open.png",
            "alt_file": "items/eyes/eyes_brown_line_closed.png",
            "secondaryLayer": "eyes_iris",
            "secondaryFile": "items/eyes/eyes_brown_iris_open.png",
            "secondary_alt_file": "items/eyes/eyes_brown_iris_closed.png",
            "thumbnail": "items/thumbnails/thumb_eyes_brown.png",
            "colorable": True,
            "palette": "eyes",
            "group": "appearance",
            "subcategory": "eyes"
        },
        "eyes_blue": {
            "layer": "eyes_line",
            "file": "items/eyes/eyes_blue_line_open.png",
            "alt_file": "items/eyes/eyes_blue_line_closed.png",
            "secondaryLayer": "eyes_iris",
            "secondaryFile": "items/eyes/eyes_blue_iris_open.png",
            "secondary_alt_file": "items/eyes/eyes_blue_iris_closed.png",
            "thumbnail": "items/thumbnails/thumb_eyes_blue.png",
            "colorable": True,
            "palette": "eyes",
            "group": "appearance",
            "subcategory": "eyes"
        }
    }

    for item in items:
        if item["id"] in eye_updates:
            item.update(eye_updates[item["id"]])

    # 2. Add standalone eyes_line items and eyes_iris items
    existing_ids = {it["id"] for it in items}

    new_items = [
        # Standalone eyes_line items (Shape & Lashes, NOT colorable)
        {
            "id": "eyes_line_purple",
            "category": "eyes_line",
            "layer": "eyes_line",
            "file": "items/eyes/eyes_purple_line_open.png",
            "alt_file": "items/eyes/eyes_purple_line_closed.png",
            "thumbnail": "items/thumbnails/thumb_eyes_purple_line.png",
            "nameEn": "Sparkle Eye Shape",
            "nameVi": "Dáng Mắt Lấp Lánh",
            "locked": False,
            "secondaryLayer": None,
            "secondaryFile": None,
            "group": "appearance",
            "subcategory": "eyes_line",
            "colorable": False,
            "palette": None
        },
        {
            "id": "eyes_line_wink",
            "category": "eyes_line",
            "layer": "eyes_line",
            "file": "items/eyes/eyes_wink_line_open.png",
            "alt_file": "items/eyes/eyes_wink_line_closed.png",
            "thumbnail": "items/thumbnails/thumb_eyes_wink_line.png",
            "nameEn": "Winking Eye Shape",
            "nameVi": "Dáng Mắt Nháy Nụ Cười",
            "locked": False,
            "secondaryLayer": None,
            "secondaryFile": None,
            "group": "appearance",
            "subcategory": "eyes_line",
            "colorable": False,
            "palette": None
        },
        {
            "id": "eyes_line_brown",
            "category": "eyes_line",
            "layer": "eyes_line",
            "file": "items/eyes/eyes_brown_line_open.png",
            "alt_file": "items/eyes/eyes_brown_line_closed.png",
            "thumbnail": "items/thumbnails/thumb_eyes_brown_line.png",
            "nameEn": "Round Eye Shape",
            "nameVi": "Dáng Mắt Tròn Xoe",
            "locked": False,
            "secondaryLayer": None,
            "secondaryFile": None,
            "group": "appearance",
            "subcategory": "eyes_line",
            "colorable": False,
            "palette": None
        },
        {
            "id": "eyes_line_blue",
            "category": "eyes_line",
            "layer": "eyes_line",
            "file": "items/eyes/eyes_blue_line_open.png",
            "alt_file": "items/eyes/eyes_blue_line_closed.png",
            "thumbnail": "items/thumbnails/thumb_eyes_blue_line.png",
            "nameEn": "Gentle Eye Shape",
            "nameVi": "Dáng Mắt Dịu Dàng",
            "locked": False,
            "secondaryLayer": None,
            "secondaryFile": None,
            "group": "appearance",
            "subcategory": "eyes_line",
            "colorable": False,
            "palette": None
        },

        # Standalone eyes_iris items (Iris color, Colorable with "eyes" palette)
        {
            "id": "eyes_iris_purple",
            "category": "eyes_iris",
            "layer": "eyes_iris",
            "file": "items/eyes/eyes_purple_iris_open.png",
            "alt_file": "items/eyes/eyes_purple_iris_closed.png",
            "thumbnail": "items/thumbnails/thumb_eyes_purple_iris.png",
            "nameEn": "Sparkle Iris",
            "nameVi": "Tròng Mắt Tím",
            "locked": False,
            "secondaryLayer": None,
            "secondaryFile": None,
            "group": "appearance",
            "subcategory": "eyes_iris",
            "colorable": True,
            "palette": "eyes"
        },
        {
            "id": "eyes_iris_pink",
            "category": "eyes_iris",
            "layer": "eyes_iris",
            "file": "items/eyes/eyes_wink_iris_open.png",
            "alt_file": "items/eyes/eyes_wink_iris_closed.png",
            "thumbnail": "items/thumbnails/thumb_eyes_wink_iris.png",
            "nameEn": "Cheeky Iris",
            "nameVi": "Tròng Mắt Hồng",
            "locked": False,
            "secondaryLayer": None,
            "secondaryFile": None,
            "group": "appearance",
            "subcategory": "eyes_iris",
            "colorable": True,
            "palette": "eyes"
        },
        {
            "id": "eyes_iris_brown",
            "category": "eyes_iris",
            "layer": "eyes_iris",
            "file": "items/eyes/eyes_brown_iris_open.png",
            "alt_file": "items/eyes/eyes_brown_iris_closed.png",
            "thumbnail": "items/thumbnails/thumb_eyes_brown_iris.png",
            "nameEn": "Amber Iris",
            "nameVi": "Tròng Mắt Nâu",
            "locked": False,
            "secondaryLayer": None,
            "secondaryFile": None,
            "group": "appearance",
            "subcategory": "eyes_iris",
            "colorable": True,
            "palette": "eyes"
        },
        {
            "id": "eyes_iris_blue",
            "category": "eyes_iris",
            "layer": "eyes_iris",
            "file": "items/eyes/eyes_blue_iris_open.png",
            "alt_file": "items/eyes/eyes_blue_iris_closed.png",
            "thumbnail": "items/thumbnails/thumb_eyes_blue_iris.png",
            "nameEn": "Ocean Iris",
            "nameVi": "Tròng Mắt Xanh",
            "locked": False,
            "secondaryLayer": None,
            "secondaryFile": None,
            "group": "appearance",
            "subcategory": "eyes_iris",
            "colorable": True,
            "palette": "eyes"
        },

        # Front Fox Tail with z_override = 45 (in front of bottom skirt zIndex 40)
        {
            "id": "tail_fox_front",
            "category": "tail",
            "layer": "tail",
            "file": "items/tail/tail_fox_front.png",
            "thumbnail": "items/thumbnails/thumb_tail_fox_front.png",
            "nameEn": "Front Fox Tail",
            "nameVi": "Đuôi Cáo Trước Váy",
            "locked": False,
            "secondaryLayer": None,
            "secondaryFile": None,
            "group": "accessories",
            "subcategory": "tail",
            "colorable": False,
            "palette": None,
            "z_override": 45
        }
    ]

    for ni in new_items:
        if ni["id"] not in existing_ids:
            items.append(ni)
        else:
            # Update
            for i, it in enumerate(items):
                if it["id"] == ni["id"]:
                    items[i] = ni
                    break

    with open(CATALOG_PATH, "w", encoding="utf-8") as f:
        json.dump(items, f, indent=2, ensure_ascii=False)

    print(f"Updated {CATALOG_PATH} with {len(items)} items total.")

if __name__ == "__main__":
    update_catalog()
