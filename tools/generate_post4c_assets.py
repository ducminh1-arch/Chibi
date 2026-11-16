#!/usr/bin/env python3
"""
Generate placeholder art for Post-4C Hardening:
1. Split eyes:
   - eyes_*_line_open.png (sclera, lashes, specular highlights, NOT colorable)
   - eyes_*_line_closed.png (closed lash curves)
   - eyes_*_iris_open.png (colorable iris discs with pupil)
   - eyes_*_iris_closed.png (empty transparent 1024x1536)
2. Front tail with z_override:
   - tail_fox_front.png (fluffy tail wrapping in front of skirts)
3. 256x256 thumbnails for all new items.
"""

import os
from pathlib import Path
from PIL import Image, ImageDraw

ASSETS_DIR = Path("app/src/main/assets")
WIDTH = 1024
HEIGHT = 1536

def new_canvas() -> tuple[Image.Image, ImageDraw.ImageDraw]:
    img = Image.new("RGBA", (WIDTH, HEIGHT), (0, 0, 0, 0))
    draw = ImageDraw.Draw(img)
    return img, draw

def save_asset(img: Image.Image, rel_path: str):
    full_path = ASSETS_DIR / rel_path
    full_path.parent.mkdir(parents=True, exist_ok=True)
    img.save(full_path, format="PNG")
    print(f"Generated asset: {rel_path} ({img.size[0]}x{img.size[1]})")

def make_thumbnail(full_img: Image.Image, rel_path: str):
    thumb = full_img.resize((256, 256), Image.Resampling.LANCZOS)
    save_asset(thumb, rel_path)

def generate_split_eyes():
    left_x, right_x = 420, 604
    eye_y = 465

    # Universal closed iris (completely transparent)
    img_iris_closed, _ = new_canvas()
    save_asset(img_iris_closed, "items/eyes/eyes_iris_closed.png")

    styles = [
        ("purple", (210, 200, 230, 255), (90, 60, 130, 255), (50, 30, 70, 255), False),
        ("wink", (235, 205, 220, 255), (160, 60, 100, 255), (80, 20, 50, 255), True),
        ("brown", (225, 210, 190, 255), (130, 80, 40, 255), (60, 35, 20, 255), False),
        ("blue", (200, 225, 245, 255), (50, 110, 180, 255), (20, 50, 90, 255), False),
    ]

    for name, iris_base, iris_out, pupil_col, is_wink in styles:
        # 1. IRIS OPEN
        img_iris, draw_iris = new_canvas()
        centers = [left_x] if is_wink else [left_x, right_x]
        for cx in centers:
            # Iris outer disc
            draw_iris.ellipse([cx - 36, eye_y - 36, cx + 36, eye_y + 36], fill=iris_base, outline=iris_out, width=3)
            # Pupil
            draw_iris.ellipse([cx - 16, eye_y - 16, cx + 16, eye_y + 16], fill=pupil_col)

        save_asset(img_iris, f"items/eyes/eyes_{name}_iris_open.png")
        save_asset(img_iris_closed, f"items/eyes/eyes_{name}_iris_closed.png")

        # 2. LINE OPEN
        img_line, draw_line = new_canvas()
        for cx in (left_x, right_x):
            if is_wink and cx == right_x:
                # Winking eye right: cheeky closed curve
                draw_line.arc([cx - 45, eye_y - 30, cx + 45, eye_y + 40], start=200, end=340, fill=(160, 40, 90, 255), width=7)
                draw_line.line([cx + 40, eye_y - 15, cx + 54, eye_y - 24], fill=(160, 40, 90, 255), width=5)
            else:
                # Outer sclera framing
                draw_line.ellipse([cx - 48, eye_y - 48, cx + 48, eye_y + 48], fill=(255, 255, 255, 245), outline=(50, 40, 60, 255), width=3)
                # Punch cutout for iris to show through cleanly
                draw_line.ellipse([cx - 34, eye_y - 34, cx + 34, eye_y + 34], fill=(0, 0, 0, 0))
                # Upper lash band
                draw_line.arc([cx - 52, eye_y - 54, cx + 52, eye_y + 42], start=200, end=340, fill=(40, 30, 50, 255), width=6)
                # Lower lash rim
                draw_line.arc([cx - 38, eye_y - 20, cx + 38, eye_y + 48], start=20, end=160, fill=(40, 30, 50, 255), width=2)
                # Specular sparkle highlights (crisp white ON TOP of iris)
                draw_line.ellipse([cx - 24, eye_y - 28, cx - 8, eye_y - 12], fill=(255, 255, 255, 255))
                draw_line.ellipse([cx + 10, eye_y + 8, cx + 18, eye_y + 16], fill=(255, 255, 255, 230))

        save_asset(img_line, f"items/eyes/eyes_{name}_line_open.png")

        # 3. LINE CLOSED
        img_line_c, draw_line_c = new_canvas()
        for cx in (left_x, right_x):
            draw_line_c.arc([cx - 45, eye_y - 30, cx + 45, eye_y + 40], start=200, end=340, fill=(40, 30, 50, 255), width=7)
            draw_line_c.line([cx + 40, eye_y - 15, cx + 52, eye_y - 25], fill=(40, 30, 50, 255), width=5)
            draw_line_c.line([cx + 36, eye_y - 8, cx + 50, eye_y - 12], fill=(40, 30, 50, 255), width=4)

        save_asset(img_line_c, f"items/eyes/eyes_{name}_line_closed.png")

        # Thumbnails
        # Composite preview for thumbnail
        comp = Image.alpha_composite(img_iris, img_line)
        make_thumbnail(comp, f"items/thumbnails/thumb_eyes_{name}.png")
        make_thumbnail(img_line, f"items/thumbnails/thumb_eyes_{name}_line.png")
        make_thumbnail(img_iris, f"items/thumbnails/thumb_eyes_{name}_iris.png")

def generate_front_tail():
    img, draw = new_canvas()
    # Fluffy fox tail curling across hips (in front of bottom/skirt, z_override = 45)
    # Curves from back-left (x: 360, y: 880) to front right (x: 580, y: 920)
    tail_pts = [
        (370, 840), (320, 890), (340, 960), (430, 990),
        (520, 980), (590, 930), (580, 880), (510, 890),
        (440, 930), (380, 900)
    ]
    draw.polygon(tail_pts, fill=(245, 140, 60, 255), outline=(180, 85, 30, 255))
    # Fluffy white tip
    tip_pts = [(520, 980), (590, 930), (580, 880), (540, 920)]
    draw.polygon(tip_pts, fill=(255, 250, 240, 255), outline=(180, 85, 30, 255))

    save_asset(img, "items/tail/tail_fox_front.png")
    make_thumbnail(img, "items/thumbnails/thumb_tail_fox_front.png")

if __name__ == "__main__":
    generate_split_eyes()
    generate_front_tail()
    print("Post-4C assets generated successfully!")

