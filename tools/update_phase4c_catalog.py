#!/usr/bin/env python3
import json
from pathlib import Path

CATALOG_PATH = Path("app/src/main/assets/catalog.json")

NEW_ITEMS = [
    # Eyes
    {
        "id": "eyes_purple",
        "category": "eyes",
        "layer": "eyes",
        "file": "items/eyes/eyes_purple_open.png",
        "alt_file": "items/eyes/eyes_purple_closed.png",
        "thumbnail": "items/thumbnails/thumb_eyes_purple_open.png",
        "nameEn": "Sparkle Purple Eyes",
        "nameVi": "Mắt Tím Lấp Lánh",
        "locked": False,
        "secondaryLayer": None,
        "secondaryFile": None,
        "group": "appearance",
        "subcategory": "eyes",
        "colorable": True,
        "palette": "eyes"
    },
    {
        "id": "eyes_wink_pink",
        "category": "eyes",
        "layer": "eyes",
        "file": "items/eyes/eyes_wink_pink_open.png",
        "alt_file": "items/eyes/eyes_wink_pink_closed.png",
        "thumbnail": "items/thumbnails/thumb_eyes_wink_pink_open.png",
        "nameEn": "Pink Winking Eyes",
        "nameVi": "Mắt Hồng Nháy Mắt",
        "locked": False,
        "secondaryLayer": None,
        "secondaryFile": None,
        "group": "appearance",
        "subcategory": "eyes",
        "colorable": True,
        "palette": "eyes"
    },
    {
        "id": "eyes_brown",
        "category": "eyes",
        "layer": "eyes",
        "file": "items/eyes/eyes_brown_open.png",
        "alt_file": "items/eyes/eyes_brown_closed.png",
        "thumbnail": "items/thumbnails/thumb_eyes_brown_open.png",
        "nameEn": "Curious Amber Eyes",
        "nameVi": "Mắt Nâu Tròn Xoe",
        "locked": False,
        "secondaryLayer": None,
        "secondaryFile": None,
        "group": "appearance",
        "subcategory": "eyes",
        "colorable": True,
        "palette": "eyes"
    },
    {
        "id": "eyes_blue",
        "category": "eyes",
        "layer": "eyes",
        "file": "items/eyes/eyes_blue_open.png",
        "alt_file": "items/eyes/eyes_blue_closed.png",
        "thumbnail": "items/thumbnails/thumb_eyes_blue_open.png",
        "nameEn": "Ocean Blue Eyes",
        "nameVi": "Mắt Xanh Biển Biếc",
        "locked": False,
        "secondaryLayer": None,
        "secondaryFile": None,
        "group": "appearance",
        "subcategory": "eyes",
        "colorable": True,
        "palette": "eyes"
    },
    # Eyebrows
    {
        "id": "brow_gentle",
        "category": "eyebrows",
        "layer": "eyebrows",
        "file": "items/eyebrows/brow_gentle.png",
        "thumbnail": "items/thumbnails/thumb_brow_gentle.png",
        "nameEn": "Gentle Brows",
        "nameVi": "Lông Mày Dịu Dàng",
        "locked": False,
        "secondaryLayer": None,
        "secondaryFile": None,
        "group": "appearance",
        "subcategory": "eyebrows",
        "colorable": False,
        "palette": None
    },
    {
        "id": "brow_curious",
        "category": "eyebrows",
        "layer": "eyebrows",
        "file": "items/eyebrows/brow_curious.png",
        "thumbnail": "items/thumbnails/thumb_brow_curious.png",
        "nameEn": "Curious Brows",
        "nameVi": "Lông Mày Tò Mò",
        "locked": False,
        "secondaryLayer": None,
        "secondaryFile": None,
        "group": "appearance",
        "subcategory": "eyebrows",
        "colorable": False,
        "palette": None
    },
    {
        "id": "brow_confident",
        "category": "eyebrows",
        "layer": "eyebrows",
        "file": "items/eyebrows/brow_confident.png",
        "thumbnail": "items/thumbnails/thumb_brow_confident.png",
        "nameEn": "Confident Brows",
        "nameVi": "Lông Mày Tự Tin",
        "locked": False,
        "secondaryLayer": None,
        "secondaryFile": None,
        "group": "appearance",
        "subcategory": "eyebrows",
        "colorable": False,
        "palette": None
    },
    {
        "id": "brow_playful",
        "category": "eyebrows",
        "layer": "eyebrows",
        "file": "items/eyebrows/brow_playful.png",
        "thumbnail": "items/thumbnails/thumb_brow_playful.png",
        "nameEn": "Playful Brows",
        "nameVi": "Lông Mày Tinh Nghịch",
        "locked": False,
        "secondaryLayer": None,
        "secondaryFile": None,
        "group": "appearance",
        "subcategory": "eyebrows",
        "colorable": False,
        "palette": None
    },
    # Mouth
    {
        "id": "mouth_smile",
        "category": "mouth",
        "layer": "mouth",
        "file": "items/mouth/mouth_smile.png",
        "thumbnail": "items/thumbnails/thumb_mouth_smile.png",
        "nameEn": "Sweet Smile",
        "nameVi": "Nụ Cười Ngọt Ngào",
        "locked": False,
        "secondaryLayer": None,
        "secondaryFile": None,
        "group": "appearance",
        "subcategory": "mouth",
        "colorable": False,
        "palette": None
    },
    {
        "id": "mouth_happy_open",
        "category": "mouth",
        "layer": "mouth",
        "file": "items/mouth/mouth_happy_open.png",
        "thumbnail": "items/thumbnails/thumb_mouth_happy_open.png",
        "nameEn": "Happy Open Mouth",
        "nameVi": "Cười Vui Rạng Rỡ",
        "locked": False,
        "secondaryLayer": None,
        "secondaryFile": None,
        "group": "appearance",
        "subcategory": "mouth",
        "colorable": False,
        "palette": None
    },
    {
        "id": "mouth_cat",
        "category": "mouth",
        "layer": "mouth",
        "file": "items/mouth/mouth_cat.png",
        "thumbnail": "items/thumbnails/thumb_mouth_cat.png",
        "nameEn": "Cat Smile",
        "nameVi": "Miệng Mèo Đáng Yêu",
        "locked": False,
        "secondaryLayer": None,
        "secondaryFile": None,
        "group": "appearance",
        "subcategory": "mouth",
        "colorable": False,
        "palette": None
    },
    {
        "id": "mouth_shy",
        "category": "mouth",
        "layer": "mouth",
        "file": "items/mouth/mouth_shy.png",
        "thumbnail": "items/thumbnails/thumb_mouth_shy.png",
        "nameEn": "Shy Smile",
        "nameVi": "Nụ Cười Chúm Chím",
        "locked": False,
        "secondaryLayer": None,
        "secondaryFile": None,
        "group": "appearance",
        "subcategory": "mouth",
        "colorable": False,
        "palette": None
    },
    # Blush
    {
        "id": "blush_pink",
        "category": "blush",
        "layer": "blush",
        "file": "items/blush/blush_pink.png",
        "thumbnail": "items/thumbnails/thumb_blush_pink.png",
        "nameEn": "Pink Cheeks",
        "nameVi": "Má Hồng Phấn",
        "locked": False,
        "secondaryLayer": None,
        "secondaryFile": None,
        "group": "appearance",
        "subcategory": "blush",
        "colorable": False,
        "palette": None
    },
    {
        "id": "blush_peach",
        "category": "blush",
        "layer": "blush",
        "file": "items/blush/blush_peach.png",
        "thumbnail": "items/thumbnails/thumb_blush_peach.png",
        "nameEn": "Peach Glow",
        "nameVi": "Má Cam Đào",
        "locked": False,
        "secondaryLayer": None,
        "secondaryFile": None,
        "group": "appearance",
        "subcategory": "blush",
        "colorable": False,
        "palette": None
    },
    {
        "id": "blush_hearts",
        "category": "blush",
        "layer": "blush",
        "file": "items/blush/blush_hearts.png",
        "thumbnail": "items/thumbnails/thumb_blush_hearts.png",
        "nameEn": "Heart Blush",
        "nameVi": "Má Hồng Trái Tim",
        "locked": False,
        "secondaryLayer": None,
        "secondaryFile": None,
        "group": "appearance",
        "subcategory": "blush",
        "colorable": False,
        "palette": None
    },
    # Socks
    {
        "id": "socks_white_knee",
        "category": "socks",
        "layer": "socks",
        "file": "items/socks/socks_white_knee.png",
        "thumbnail": "items/thumbnails/thumb_socks_white_knee.png",
        "nameEn": "Lace Knee Socks",
        "nameVi": "Tất Cao Cổ Ren",
        "locked": False,
        "secondaryLayer": None,
        "secondaryFile": None,
        "group": "clothes",
        "subcategory": "socks",
        "colorable": True,
        "palette": "general"
    },
    {
        "id": "socks_striped_pink",
        "category": "socks",
        "layer": "socks",
        "file": "items/socks/socks_striped_pink.png",
        "thumbnail": "items/thumbnails/thumb_socks_striped_pink.png",
        "nameEn": "Striped Pink Socks",
        "nameVi": "Tất Kẻ Sọc Hồng",
        "locked": False,
        "secondaryLayer": None,
        "secondaryFile": None,
        "group": "clothes",
        "subcategory": "socks",
        "colorable": True,
        "palette": "general"
    },
    {
        "id": "socks_frills_blue",
        "category": "socks",
        "layer": "socks",
        "file": "items/socks/socks_frills_blue.png",
        "thumbnail": "items/thumbnails/thumb_socks_frills_blue.png",
        "nameEn": "Frilly Ankle Socks",
        "nameVi": "Vớ Viền Bèo Xanh",
        "locked": False,
        "secondaryLayer": None,
        "secondaryFile": None,
        "group": "clothes",
        "subcategory": "socks",
        "colorable": True,
        "palette": "general"
    },
    # Glasses
    {
        "id": "glasses_round_gold",
        "category": "glasses",
        "layer": "glasses",
        "file": "items/glasses/glasses_round_gold.png",
        "thumbnail": "items/thumbnails/thumb_glasses_round_gold.png",
        "nameEn": "Round Gold Glasses",
        "nameVi": "Kính Tròn Gọng Vàng",
        "locked": False,
        "secondaryLayer": None,
        "secondaryFile": None,
        "group": "accessories",
        "subcategory": "glasses",
        "colorable": True,
        "palette": "general"
    },
    {
        "id": "glasses_heart_pink",
        "category": "glasses",
        "layer": "glasses",
        "file": "items/glasses/glasses_heart_pink.png",
        "thumbnail": "items/thumbnails/thumb_glasses_heart_pink.png",
        "nameEn": "Heart Sunglasses",
        "nameVi": "Kính Mát Trái Tim",
        "locked": False,
        "secondaryLayer": None,
        "secondaryFile": None,
        "group": "accessories",
        "subcategory": "glasses",
        "colorable": True,
        "palette": "general"
    },
    {
        "id": "glasses_star_purple",
        "category": "glasses",
        "layer": "glasses",
        "file": "items/glasses/glasses_star_purple.png",
        "thumbnail": "items/thumbnails/thumb_glasses_star_purple.png",
        "nameEn": "Star Party Glasses",
        "nameVi": "Kính Ngôi Sao Tím",
        "locked": False,
        "secondaryLayer": None,
        "secondaryFile": None,
        "group": "accessories",
        "subcategory": "glasses",
        "colorable": True,
        "palette": "general"
    },
    # Ears
    {
        "id": "ears_cat_white",
        "category": "ears",
        "layer": "ears",
        "file": "items/ears/ears_cat_white.png",
        "thumbnail": "items/thumbnails/thumb_ears_cat_white.png",
        "nameEn": "Fluffy Cat Ears",
        "nameVi": "Tai Mèo Trắng",
        "locked": False,
        "secondaryLayer": None,
        "secondaryFile": None,
        "group": "accessories",
        "subcategory": "ears",
        "colorable": True,
        "palette": "general",
        "conflictsWith": ["hat"]
    },
    {
        "id": "ears_bunny_pink",
        "category": "ears",
        "layer": "ears",
        "file": "items/ears/ears_bunny_pink.png",
        "thumbnail": "items/thumbnails/thumb_ears_bunny_pink.png",
        "nameEn": "Cute Bunny Ears",
        "nameVi": "Tai Thỏ Hồng",
        "locked": False,
        "secondaryLayer": None,
        "secondaryFile": None,
        "group": "accessories",
        "subcategory": "ears",
        "colorable": True,
        "palette": "general",
        "conflictsWith": ["hat"]
    },
    {
        "id": "ears_elf",
        "category": "ears",
        "layer": "ears",
        "file": "items/ears/ears_elf.png",
        "thumbnail": "items/thumbnails/thumb_ears_elf.png",
        "nameEn": "Elf Ears",
        "nameVi": "Đôi Tai Elf Tinh Linh",
        "locked": False,
        "secondaryLayer": None,
        "secondaryFile": None,
        "group": "accessories",
        "subcategory": "ears",
        "colorable": True,
        "palette": "skin"
    },
    # Tail
    {
        "id": "tail_cat_white",
        "category": "tail",
        "layer": "tail",
        "file": "items/tail/tail_cat_white.png",
        "thumbnail": "items/thumbnails/thumb_tail_cat_white.png",
        "nameEn": "White Cat Tail",
        "nameVi": "Đuôi Mèo Trắng",
        "locked": False,
        "secondaryLayer": None,
        "secondaryFile": None,
        "group": "accessories",
        "subcategory": "tail",
        "colorable": True,
        "palette": "general"
    },
    {
        "id": "tail_fox_fluffy",
        "category": "tail",
        "layer": "tail",
        "file": "items/tail/tail_fox_fluffy.png",
        "thumbnail": "items/thumbnails/thumb_tail_fox_fluffy.png",
        "nameEn": "Fluffy Fox Tail",
        "nameVi": "Đuôi Cáo Xù Lớn",
        "locked": False,
        "secondaryLayer": None,
        "secondaryFile": None,
        "group": "accessories",
        "subcategory": "tail",
        "colorable": True,
        "palette": "general"
    },
    {
        "id": "tail_bunny_puff",
        "category": "tail",
        "layer": "tail",
        "file": "items/tail/tail_bunny_puff.png",
        "thumbnail": "items/thumbnails/thumb_tail_bunny_puff.png",
        "nameEn": "Bunny Puff Tail",
        "nameVi": "Đuôi Thỏ Cục Bông",
        "locked": False,
        "secondaryLayer": None,
        "secondaryFile": None,
        "group": "accessories",
        "subcategory": "tail",
        "colorable": True,
        "palette": "general"
    },
    # Wings
    {
        "id": "wings_angel_white",
        "category": "wings",
        "layer": "wings",
        "file": "items/wings/wings_angel_white.png",
        "thumbnail": "items/thumbnails/thumb_wings_angel_white.png",
        "nameEn": "Angel Wings",
        "nameVi": "Cánh Thiên Thần Trắng",
        "locked": False,
        "secondaryLayer": None,
        "secondaryFile": None,
        "group": "accessories",
        "subcategory": "wings",
        "colorable": True,
        "palette": "general"
    },
    {
        "id": "wings_fairy_sparkle",
        "category": "wings",
        "layer": "wings",
        "file": "items/wings/wings_fairy_sparkle.png",
        "thumbnail": "items/thumbnails/thumb_wings_fairy_sparkle.png",
        "nameEn": "Fairy Sparkle Wings",
        "nameVi": "Cánh Tiên Lấp Lánh",
        "locked": False,
        "secondaryLayer": None,
        "secondaryFile": None,
        "group": "accessories",
        "subcategory": "wings",
        "colorable": True,
        "palette": "general"
    },
    {
        "id": "wings_demon_bat",
        "category": "wings",
        "layer": "wings",
        "file": "items/wings/wings_demon_bat.png",
        "thumbnail": "items/thumbnails/thumb_wings_demon_bat.png",
        "nameEn": "Bat Demon Wings",
        "nameVi": "Cánh Dơi Huyền Bí",
        "locked": False,
        "secondaryLayer": None,
        "secondaryFile": None,
        "group": "accessories",
        "subcategory": "wings",
        "colorable": True,
        "palette": "general"
    }
]

def main():
    with open(CATALOG_PATH, "r", encoding="utf-8") as f:
        catalog = json.load(f)

    existing_ids = {item["id"]: item for item in catalog}

    # Also update body to ensure skin palette is set
    if "body_fair" in existing_ids:
        existing_ids["body_fair"]["colorable"] = True
        existing_ids["body_fair"]["palette"] = "skin"

    # Merge or append new items
    for item in NEW_ITEMS:
        existing_ids[item["id"]] = item

    # Category ordering for nice layout
    ORDER = [
        "body", "eyes", "eyebrows", "mouth", "blush", "face", "hair",
        "dress", "top", "bottom", "outerwear", "socks", "shoes",
        "glasses", "ears", "tail", "wings", "hat", "accessory", "background"
    ]

    result = list(existing_ids.values())
    result.sort(key=lambda x: (
        ORDER.index(x.get("category", "")) if x.get("category", "") in ORDER else 999,
        x.get("id", "")
    ))

    with open(CATALOG_PATH, "w", encoding="utf-8") as f:
        json.dump(result, f, indent=2, ensure_ascii=False)

    print(f"Updated catalog.json successfully! Total items: {len(result)}")

if __name__ == "__main__":
    main()

