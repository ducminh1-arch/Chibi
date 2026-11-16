#!/usr/bin/env python3
"""
Generate placeholder art for Phase 4C:
- Eyes (open and closed alt_file)
- Eyebrows
- Mouth
- Blush
- Socks
- Glasses
- Ears
- Tail
- Wings

All character layers are transparent 1024x1536 RGBA PNGs.
"""

import math
from pathlib import Path
from PIL import Image, ImageDraw

ASSETS_DIR = Path("app/src/main/assets")
ITEMS_DIR = ASSETS_DIR / "items"
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
    print(f"Generated: {rel_path}")

def generate_eyes():
    # Eye centers on chibi face
    left_x, right_x = 420, 604
    eye_y = 465

    # 1. Purple sparkle eyes (Luna default)
    # Open
    img, draw = new_canvas()
    for cx in (left_x, right_x):
        # White sclera
        draw.ellipse([cx - 48, eye_y - 48, cx + 48, eye_y + 48], fill=(255, 255, 255, 240), outline=(50, 40, 60, 255), width=3)
        # Iris (Colorable light base #E0D8F0 with dark purple accents)
        draw.ellipse([cx - 36, eye_y - 36, cx + 36, eye_y + 36], fill=(210, 200, 230, 255), outline=(90, 60, 130, 255), width=2)
        # Pupil
        draw.ellipse([cx - 18, eye_y - 18, cx + 18, eye_y + 18], fill=(50, 30, 70, 255))
        # Big sparkle
        draw.ellipse([cx - 24, eye_y - 28, cx - 8, eye_y - 12], fill=(255, 255, 255, 255))
        # Small sparkle
        draw.ellipse([cx + 10, eye_y + 8, cx + 18, eye_y + 16], fill=(255, 255, 255, 230))
        # Upper eyelashes
        draw.arc([cx - 52, eye_y - 54, cx + 52, eye_y + 42], start=200, end=340, fill=(40, 30, 50, 255), width=6)
    save_asset(img, "items/eyes/eyes_purple_open.png")

    # Closed (alt_file)
    img_c, draw_c = new_canvas()
    for cx in (left_x, right_x):
        # Happy closed eye curve
        draw_c.arc([cx - 45, eye_y - 30, cx + 45, eye_y + 40], start=200, end=340, fill=(40, 30, 50, 255), width=7)
        # Eyelashes
        draw_c.line([cx + 40, eye_y - 15, cx + 52, eye_y - 25], fill=(40, 30, 50, 255), width=5)
        draw_c.line([cx + 36, eye_y - 8, cx + 50, eye_y - 12], fill=(40, 30, 50, 255), width=4)
    save_asset(img_c, "items/eyes/eyes_purple_closed.png")

    # 2. Pink winking eyes (Sakura default: right eye winking closed, left eye open)
    # Open state
    img, draw = new_canvas()
    # Left eye open
    cx = left_x
    draw.ellipse([cx - 46, eye_y - 46, cx + 46, eye_y + 46], fill=(255, 255, 255, 240), outline=(50, 40, 60, 255), width=3)
    draw.ellipse([cx - 34, eye_y - 34, cx + 34, eye_y + 34], fill=(235, 205, 220, 255), outline=(160, 60, 100, 255), width=2)
    draw.ellipse([cx - 16, eye_y - 16, cx + 16, eye_y + 16], fill=(80, 20, 50, 255))
    draw.ellipse([cx - 22, eye_y - 26, cx - 8, eye_y - 12], fill=(255, 255, 255, 255))
    draw.arc([cx - 50, eye_y - 52, cx + 50, eye_y + 40], start=200, end=340, fill=(40, 30, 50, 255), width=6)
    # Right eye cheeky wink (curved closed)
    cx = right_x
    draw.arc([cx - 45, eye_y - 30, cx + 45, eye_y + 40], start=200, end=340, fill=(160, 40, 90, 255), width=7)
    draw.line([cx + 40, eye_y - 15, cx + 54, eye_y - 24], fill=(160, 40, 90, 255), width=5)
    save_asset(img, "items/eyes/eyes_wink_pink_open.png")

    # Closed state for wink (both eyes fully closed)
    img_c, draw_c = new_canvas()
    for cx in (left_x, right_x):
        draw_c.arc([cx - 45, eye_y - 30, cx + 45, eye_y + 40], start=200, end=340, fill=(40, 30, 50, 255), width=7)
        draw_c.line([cx + 40, eye_y - 15, cx + 52, eye_y - 25], fill=(40, 30, 50, 255), width=5)
    save_asset(img_c, "items/eyes/eyes_wink_pink_closed.png")

    # 3. Curious Amber/Brown eyes (Sunny default)
    img, draw = new_canvas()
    for cx in (left_x, right_x):
        draw.ellipse([cx - 46, eye_y - 46, cx + 46, eye_y + 46], fill=(255, 255, 255, 240), outline=(50, 40, 60, 255), width=3)
        draw.ellipse([cx - 34, eye_y - 34, cx + 34, eye_y + 34], fill=(225, 210, 190, 255), outline=(130, 80, 40, 255), width=2)
        draw.ellipse([cx - 16, eye_y - 16, cx + 16, eye_y + 16], fill=(60, 35, 20, 255))
        draw.ellipse([cx - 20, eye_y - 24, cx - 6, eye_y - 10], fill=(255, 255, 255, 255))
        draw.ellipse([cx + 8, eye_y + 8, cx + 16, eye_y + 16], fill=(255, 255, 255, 220))
        draw.arc([cx - 50, eye_y - 52, cx + 50, eye_y + 40], start=200, end=340, fill=(40, 30, 50, 255), width=6)
    save_asset(img, "items/eyes/eyes_brown_open.png")

    img_c, draw_c = new_canvas()
    for cx in (left_x, right_x):
        draw_c.arc([cx - 45, eye_y - 30, cx + 45, eye_y + 40], start=200, end=340, fill=(50, 30, 20, 255), width=7)
    save_asset(img_c, "items/eyes/eyes_brown_closed.png")

    # 4. Ocean Blue eyes (Mimi default)
    img, draw = new_canvas()
    for cx in (left_x, right_x):
        draw.ellipse([cx - 46, eye_y - 46, cx + 46, eye_y + 46], fill=(255, 255, 255, 240), outline=(50, 40, 60, 255), width=3)
        draw.ellipse([cx - 34, eye_y - 34, cx + 34, eye_y + 34], fill=(200, 220, 235, 255), outline=(50, 110, 160, 255), width=2)
        draw.ellipse([cx - 16, eye_y - 16, cx + 16, eye_y + 16], fill=(20, 45, 80, 255))
        draw.ellipse([cx - 20, eye_y - 24, cx - 6, eye_y - 10], fill=(255, 255, 255, 255))
        draw.arc([cx - 50, eye_y - 52, cx + 50, eye_y + 40], start=200, end=340, fill=(40, 30, 50, 255), width=6)
    save_asset(img, "items/eyes/eyes_blue_open.png")

    img_c, draw_c = new_canvas()
    for cx in (left_x, right_x):
        draw_c.arc([cx - 45, eye_y - 30, cx + 45, eye_y + 40], start=200, end=340, fill=(30, 30, 60, 255), width=7)
    save_asset(img_c, "items/eyes/eyes_blue_closed.png")

def generate_eyebrows():
    brow_y = 398
    left_x, right_x = 420, 604

    # 1. Gentle brows
    img, draw = new_canvas()
    draw.arc([left_x - 45, brow_y - 25, left_x + 40, brow_y + 25], start=210, end=330, fill=(60, 45, 55, 255), width=6)
    draw.arc([right_x - 40, brow_y - 25, right_x + 45, brow_y + 25], start=210, end=330, fill=(60, 45, 55, 255), width=6)
    save_asset(img, "items/eyebrows/brow_gentle.png")

    # 2. Curious brows (one raised)
    img, draw = new_canvas()
    draw.arc([left_x - 45, brow_y - 35, left_x + 40, brow_y + 15], start=205, end=335, fill=(60, 45, 55, 255), width=6)
    draw.arc([right_x - 40, brow_y - 20, right_x + 45, brow_y + 30], start=210, end=330, fill=(60, 45, 55, 255), width=6)
    save_asset(img, "items/eyebrows/brow_curious.png")

    # 3. Confident brows
    img, draw = new_canvas()
    draw.line([left_x - 45, brow_y + 5, left_x + 35, brow_y - 8], fill=(60, 45, 55, 255), width=6)
    draw.line([right_x - 35, brow_y - 8, right_x + 45, brow_y + 5], fill=(60, 45, 55, 255), width=6)
    save_asset(img, "items/eyebrows/brow_confident.png")

    # 4. Playful brows
    img, draw = new_canvas()
    draw.arc([left_x - 40, brow_y - 15, left_x + 45, brow_y + 25], start=215, end=325, fill=(60, 45, 55, 255), width=6)
    draw.arc([right_x - 45, brow_y - 30, right_x + 40, brow_y + 20], start=205, end=335, fill=(60, 45, 55, 255), width=6)
    save_asset(img, "items/eyebrows/brow_playful.png")

def generate_mouth():
    mouth_y = 560
    center_x = 512

    # 1. Smile
    img, draw = new_canvas()
    draw.arc([center_x - 28, mouth_y - 20, center_x + 28, mouth_y + 20], start=20, end=160, fill=(180, 50, 70, 255), width=6)
    save_asset(img, "items/mouth/mouth_smile.png")

    # 2. Happy open mouth
    img, draw = new_canvas()
    draw.pieslice([center_x - 32, mouth_y - 18, center_x + 32, mouth_y + 36], start=0, end=180, fill=(190, 50, 70, 255), outline=(130, 30, 45, 255), width=3)
    draw.pieslice([center_x - 20, mouth_y + 4, center_x + 20, mouth_y + 34], start=0, end=180, fill=(245, 130, 150, 255))
    save_asset(img, "items/mouth/mouth_happy_open.png")

    # 3. Cute cat :3 mouth
    img, draw = new_canvas()
    draw.arc([center_x - 30, mouth_y - 16, center_x, mouth_y + 14], start=20, end=170, fill=(180, 50, 70, 255), width=5)
    draw.arc([center_x, mouth_y - 16, center_x + 30, mouth_y + 14], start=10, end=160, fill=(180, 50, 70, 255), width=5)
    save_asset(img, "items/mouth/mouth_cat.png")

    # 4. Shy smile
    img, draw = new_canvas()
    draw.line([center_x - 16, mouth_y, center_x + 16, mouth_y], fill=(180, 60, 80, 255), width=5)
    save_asset(img, "items/mouth/mouth_shy.png")

def generate_blush():
    blush_y = 520
    left_x, right_x = 360, 664

    # 1. Pink soft oval blush
    img, draw = new_canvas()
    for cx in (left_x, right_x):
        draw.ellipse([cx - 36, blush_y - 18, cx + 36, blush_y + 18], fill=(255, 150, 180, 140))
        # Cheek shine
        draw.line([cx - 18, blush_y - 6, cx - 8, blush_y - 6], fill=(255, 255, 255, 180), width=3)
    save_asset(img, "items/blush/blush_pink.png")

    # 2. Peach warm blush
    img, draw = new_canvas()
    for cx in (left_x, right_x):
        draw.ellipse([cx - 36, blush_y - 18, cx + 36, blush_y + 18], fill=(255, 175, 140, 150))
    save_asset(img, "items/blush/blush_peach.png")

    # 3. Cute heart blush
    img, draw = new_canvas()
    for cx in (left_x, right_x):
        # Draw two small circles and a triangle for heart shape
        draw.ellipse([cx - 20, blush_y - 16, cx - 2, blush_y + 2], fill=(255, 130, 170, 160))
        draw.ellipse([cx, blush_y - 16, cx + 18, blush_y + 2], fill=(255, 130, 170, 160))
        draw.polygon([(cx - 20, blush_y - 6), (cx + 18, blush_y - 6), (cx - 1, blush_y + 18)], fill=(255, 130, 170, 160))
    save_asset(img, "items/blush/blush_hearts.png")

def generate_socks():
    # Legs are around left (420..490) and right (534..604)
    # Shoes are at y 1250..1420
    # Knee socks extend from y 1050 down to y 1340 (overlapping under shoes)
    left_leg = [422, 1060, 492, 1345]
    right_leg = [532, 1060, 602, 1345]

    # 1. White knee socks with lace top
    img, draw = new_canvas()
    for box in (left_leg, right_leg):
        draw.rounded_rectangle(box, radius=12, fill=(245, 245, 252, 255), outline=(190, 190, 210, 255), width=3)
        # Lace ruffle top
        draw.rectangle([box[0] - 4, box[1] - 4, box[2] + 4, box[1] + 16], fill=(255, 255, 255, 255), outline=(200, 200, 220, 255), width=2)
    save_asset(img, "items/socks/socks_white_knee.png")

    # 2. Striped pink knee socks
    img, draw = new_canvas()
    for box in (left_leg, right_leg):
        draw.rounded_rectangle(box, radius=12, fill=(255, 255, 255, 255), outline=(220, 160, 190, 255), width=3)
        for y_stripe in range(box[1] + 20, box[3] - 10, 36):
            draw.rectangle([box[0] + 2, y_stripe, box[2] - 2, y_stripe + 18], fill=(255, 165, 200, 255))
    save_asset(img, "items/socks/socks_striped_pink.png")

    # 3. Frilly ankle socks
    img, draw = new_canvas()
    for (x1, _, x2, _) in (left_leg, right_leg):
        box_ankle = [x1, 1190, x2, 1345]
        draw.rounded_rectangle(box_ankle, radius=10, fill=(225, 240, 255, 255), outline=(140, 180, 220, 255), width=3)
        draw.rectangle([x1 - 6, 1184, x2 + 6, 1204], fill=(255, 255, 255, 255), outline=(150, 190, 230, 255), width=2)
    save_asset(img, "items/socks/socks_frills_blue.png")

def generate_glasses():
    left_x, right_x = 420, 604
    eye_y = 465

    # 1. Round gold wireframe glasses
    img, draw = new_canvas()
    for cx in (left_x, right_x):
        draw.ellipse([cx - 56, eye_y - 56, cx + 56, eye_y + 56], fill=(255, 255, 255, 40), outline=(220, 180, 70, 255), width=6)
    # Bridge
    draw.line([left_x + 54, eye_y, right_x - 54, eye_y], fill=(220, 180, 70, 255), width=6)
    # Side temples
    draw.line([left_x - 56, eye_y, left_x - 110, eye_y - 12], fill=(220, 180, 70, 255), width=5)
    draw.line([right_x + 56, eye_y, right_x + 110, eye_y - 12], fill=(220, 180, 70, 255), width=5)
    save_asset(img, "items/glasses/glasses_round_gold.png")

    # 2. Heart sunglasses pink
    img, draw = new_canvas()
    for cx in (left_x, right_x):
        draw.ellipse([cx - 54, eye_y - 52, cx - 4, eye_y], fill=(255, 120, 170, 170), outline=(230, 60, 120, 255), width=6)
        draw.ellipse([cx + 4, eye_y - 52, cx + 54, eye_y], fill=(255, 120, 170, 170), outline=(230, 60, 120, 255), width=6)
        draw.polygon([(cx - 52, eye_y - 20), (cx + 52, eye_y - 20), (cx, eye_y + 54)], fill=(255, 120, 170, 170), outline=(230, 60, 120, 255))
    draw.line([left_x + 40, eye_y - 20, right_x - 40, eye_y - 20], fill=(230, 60, 120, 255), width=6)
    save_asset(img, "items/glasses/glasses_heart_pink.png")

    # 3. Star party glasses purple
    img, draw = new_canvas()
    for cx in (left_x, right_x):
        # 5-pointed star
        points = []
        for i in range(10):
            r = 60 if i % 2 == 0 else 28
            ang = i * math.pi / 5 - math.pi / 2
            points.append((cx + r * math.cos(ang), eye_y + r * math.sin(ang)))
        draw.polygon(points, fill=(200, 160, 240, 160), outline=(130, 60, 190, 255))
    draw.line([left_x + 35, eye_y, right_x - 35, eye_y], fill=(130, 60, 190, 255), width=6)
    save_asset(img, "items/glasses/glasses_star_purple.png")

def generate_ears():
    # 1. Cat ears white fluffy
    img, draw = new_canvas()
    # Left ear
    draw.polygon([(340, 290), (430, 270), (370, 140)], fill=(255, 255, 255, 255), outline=(60, 50, 70, 255), width=4)
    draw.polygon([(355, 275), (415, 260), (375, 170)], fill=(255, 180, 200, 255))
    # Right ear
    draw.polygon([(594, 270), (684, 290), (654, 140)], fill=(255, 255, 255, 255), outline=(60, 50, 70, 255), width=4)
    draw.polygon([(609, 260), (669, 275), (649, 170)], fill=(255, 180, 200, 255))
    save_asset(img, "items/ears/ears_cat_white.png")

    # 2. Bunny ears pink
    img, draw = new_canvas()
    draw.ellipse([345, 70, 425, 270], fill=(255, 255, 255, 255), outline=(60, 50, 70, 255), width=4)
    draw.ellipse([365, 100, 405, 240], fill=(255, 180, 200, 255))
    draw.ellipse([599, 70, 679, 270], fill=(255, 255, 255, 255), outline=(60, 50, 70, 255), width=4)
    draw.ellipse([619, 100, 659, 240], fill=(255, 180, 200, 255))
    save_asset(img, "items/ears/ears_bunny_pink.png")

    # 3. Elf ears (peeking out from head sides)
    img, draw = new_canvas()
    # Left elf ear
    draw.polygon([(310, 410), (300, 470), (220, 415)], fill=(255, 225, 215, 255), outline=(70, 50, 60, 255), width=4)
    draw.polygon([(295, 425), (290, 455), (240, 422)], fill=(255, 195, 185, 255))
    # Right elf ear
    draw.polygon([(714, 410), (724, 470), (804, 415)], fill=(255, 225, 215, 255), outline=(70, 50, 60, 255), width=4)
    draw.polygon([(729, 425), (734, 455), (784, 422)], fill=(255, 195, 185, 255))
    save_asset(img, "items/ears/ears_elf.png")

def generate_tail():
    # 1. Cat tail white
    img, draw = new_canvas()
    draw.arc([190, 920, 440, 1260], start=40, end=240, fill=(255, 255, 255, 255), width=32)
    draw.arc([190, 920, 440, 1260], start=40, end=240, fill=(60, 50, 70, 255), width=4)
    save_asset(img, "items/tail/tail_cat_white.png")

    # 2. Fox tail fluffy
    img, draw = new_canvas()
    draw.ellipse([630, 880, 840, 1240], fill=(245, 135, 60, 255), outline=(60, 50, 70, 255), width=4)
    # White tip
    draw.pieslice([660, 880, 840, 1020], start=210, end=360, fill=(255, 255, 255, 255))
    save_asset(img, "items/tail/tail_fox_fluffy.png")

    # 3. Bunny puff tail
    img, draw = new_canvas()
    draw.ellipse([467, 1060, 557, 1150], fill=(255, 255, 255, 255), outline=(60, 50, 70, 255), width=4)
    draw.ellipse([482, 1075, 542, 1135], fill=(255, 230, 240, 255))
    save_asset(img, "items/tail/tail_bunny_puff.png")

def generate_wings():
    # Behind body at zIndex 5
    # 1. White angel wings
    img, draw = new_canvas()
    # Left wing
    draw.polygon([(460, 650), (140, 450), (100, 580), (160, 780), (280, 890), (450, 820)], fill=(255, 255, 255, 240), outline=(180, 190, 210, 255), width=4)
    # Feathers
    draw.arc([100, 480, 300, 750], start=100, end=260, fill=(200, 210, 230, 255), width=3)
    # Right wing
    draw.polygon([(564, 650), (884, 450), (924, 580), (864, 780), (744, 890), (574, 820)], fill=(255, 255, 255, 240), outline=(180, 190, 210, 255), width=4)
    draw.arc([724, 480, 924, 750], start=280, end=440, fill=(200, 210, 230, 255), width=3)
    save_asset(img, "items/wings/wings_angel_white.png")

    # 2. Fairy sparkle wings (translucent cyan/pink)
    img, draw = new_canvas()
    # Left wing
    draw.ellipse([140, 460, 460, 780], fill=(180, 240, 255, 150), outline=(120, 200, 240, 230), width=4)
    draw.ellipse([210, 720, 450, 940], fill=(255, 200, 230, 150), outline=(240, 150, 200, 230), width=4)
    # Right wing
    draw.ellipse([564, 460, 884, 780], fill=(180, 240, 255, 150), outline=(120, 200, 240, 230), width=4)
    draw.ellipse([574, 720, 814, 940], fill=(255, 200, 230, 150), outline=(240, 150, 200, 230), width=4)
    save_asset(img, "items/wings/wings_fairy_sparkle.png")

    # 3. Demon bat wings
    img, draw = new_canvas()
    # Left bat wing
    draw.polygon([(460, 650), (160, 510), (220, 680), (280, 760), (430, 770)], fill=(60, 40, 75, 255), outline=(40, 25, 50, 255), width=4)
    # Right bat wing
    draw.polygon([(564, 650), (864, 510), (804, 680), (744, 760), (594, 770)], fill=(60, 40, 75, 255), outline=(40, 25, 50, 255), width=4)
    save_asset(img, "items/wings/wings_demon_bat.png")

def main():
    print("Generating Phase 4C assets...")
    generate_eyes()
    generate_eyebrows()
    generate_mouth()
    generate_blush()
    generate_socks()
    generate_glasses()
    generate_ears()
    generate_tail()
    generate_wings()
    print("Phase 4C asset generation finished!")

if __name__ == "__main__":
    main()

