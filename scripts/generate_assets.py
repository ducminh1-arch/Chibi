import os
import json
import math
from PIL import Image, ImageDraw

WIDTH = 1024
HEIGHT = 1536
THUMB_SIZE = (256, 256)

ASSETS_DIR = r"d:\Chibi\app\src\main\assets"
ITEMS_DIR = os.path.join(ASSETS_DIR, "items")
THUMBS_DIR = os.path.join(ITEMS_DIR, "thumbnails")

os.makedirs(ITEMS_DIR, exist_ok=True)
os.makedirs(THUMBS_DIR, exist_ok=True)

for folder in ["body", "hair", "face", "top", "bottom", "dress", "outerwear", "shoes", "hat", "accessory", "backgrounds"]:
    os.makedirs(os.path.join(ITEMS_DIR, folder), exist_ok=True)

def new_canvas():
    return Image.new("RGBA", (WIDTH, HEIGHT), (0, 0, 0, 0))

def save_item(img, rel_path, thumb_rel_path, crop_box=None):
    full_path = os.path.join(ASSETS_DIR, rel_path)
    os.makedirs(os.path.dirname(full_path), exist_ok=True)
    img.save(full_path, "PNG")
    
    # Generate thumbnail
    thumb_full_path = os.path.join(ASSETS_DIR, thumb_rel_path)
    os.makedirs(os.path.dirname(thumb_full_path), exist_ok=True)
    if crop_box:
        cropped = img.crop(crop_box)
    else:
        bbox = img.getbbox()
        if bbox:
            # Add padding
            bw = bbox[2] - bbox[0]
            bh = bbox[3] - bbox[1]
            pad = max(int(max(bw, bh) * 0.15), 20)
            cx = (bbox[0] + bbox[2]) // 2
            cy = (bbox[1] + bbox[3]) // 2
            side = max(bw, bh) // 2 + pad
            cropped = img.crop((cx - side, cy - side, cx + side, cy + side))
        else:
            cropped = img
    thumb = cropped.resize(THUMB_SIZE, Image.Resampling.LANCZOS)
    thumb.save(thumb_full_path, "PNG")

catalog = []

def add_catalog_item(item_id, category, layer, rel_path, thumb_rel_path, name_en, name_vi, locked=False, secondary_layer=None, secondary_file=None):
    catalog.append({
        "id": item_id,
        "category": category,
        "layer": layer,
        "file": rel_path.replace("\\", "/"),
        "thumbnail": thumb_rel_path.replace("\\", "/"),
        "nameEn": name_en,
        "nameVi": name_vi,
        "locked": locked,
        "secondaryLayer": secondary_layer,
        "secondaryFile": secondary_file.replace("\\", "/") if secondary_file else None
    })

# ==========================================
# 1. BODY BASE
# ==========================================
print("Generating Body Base...")
body_img = new_canvas()
draw = ImageDraw.Draw(body_img)

SKIN_FILL = (255, 224, 210, 255)
SKIN_SHADOW = (245, 200, 185, 255)
OUTLINE = (210, 160, 130, 255)
UNDER_FILL = (255, 255, 255, 255)
UNDER_OUTLINE = (230, 210, 225, 255)

# Ears
draw.ellipse([275, 410, 315, 480], fill=SKIN_FILL, outline=OUTLINE, width=4)
draw.ellipse([709, 410, 749, 480], fill=SKIN_FILL, outline=OUTLINE, width=4)

# Legs
# Left Leg
draw.rounded_rectangle([420, 930, 480, 1400], radius=25, fill=SKIN_FILL, outline=OUTLINE, width=4)
# Right Leg
draw.rounded_rectangle([544, 930, 604, 1400], radius=25, fill=SKIN_FILL, outline=OUTLINE, width=4)

# Torso & Hips
draw.polygon([(400, 640), (624, 640), (640, 960), (384, 960)], fill=SKIN_FILL)
draw.ellipse([384, 880, 640, 970], fill=SKIN_FILL)
draw.line([(400, 640), (384, 960)], fill=OUTLINE, width=4)
draw.line([(624, 640), (640, 960)], fill=OUTLINE, width=4)

# Arms
# Left Arm
draw.polygon([(380, 640), (330, 770), (320, 910), (360, 920), (380, 780), (410, 650)], fill=SKIN_FILL)
draw.ellipse([310, 890, 365, 940], fill=SKIN_FILL, outline=OUTLINE, width=4) # Left hand
draw.line([(380, 640), (330, 770), (320, 910)], fill=OUTLINE, width=4)
# Right Arm
draw.polygon([(644, 640), (694, 770), (704, 910), (664, 920), (644, 780), (614, 650)], fill=SKIN_FILL)
draw.ellipse([659, 890, 714, 940], fill=SKIN_FILL, outline=OUTLINE, width=4) # Right hand
draw.line([(644, 640), (694, 770), (704, 910)], fill=OUTLINE, width=4)

# Neck
draw.rectangle([485, 600, 539, 650], fill=SKIN_SHADOW)
draw.line([(485, 600), (485, 650)], fill=OUTLINE, width=3)
draw.line([(539, 600), (539, 650)], fill=OUTLINE, width=3)

# Head
draw.ellipse([292, 210, 732, 630], fill=SKIN_FILL, outline=OUTLINE, width=5)

# Undergarments (Cute white camisole and bloomers)
# Camisole
draw.polygon([(410, 680), (614, 680), (620, 840), (404, 840)], fill=UNDER_FILL)
draw.line([(410, 680), (614, 680)], fill=UNDER_OUTLINE, width=3)
draw.line([(404, 840), (620, 840)], fill=UNDER_OUTLINE, width=3)
# Bloomers
draw.ellipse([384, 860, 640, 970], fill=UNDER_FILL, outline=UNDER_OUTLINE, width=3)

save_item(body_img, "items/body/body_fair.png", "items/thumbnails/thumb_body_fair.png", crop_box=(250, 150, 774, 1450))
add_catalog_item("body_fair", "body", "body", "items/body/body_fair.png", "items/thumbnails/thumb_body_fair.png", "Fair Chibi Body", "Cơ thể Chibi Trắng Hồng")

# ==========================================
# 2. FACE (EYES, BLUSH, MOUTH)
# ==========================================
print("Generating Faces...")
# Face 1: Sparkling Purple
face1 = new_canvas()
d1 = ImageDraw.Draw(face1)
# Rosy blush
d1.ellipse([340, 480, 420, 520], fill=(255, 175, 185, 120))
d1.ellipse([604, 480, 684, 520], fill=(255, 175, 185, 120))
# Left eye
d1.ellipse([380, 390, 460, 490], fill=(138, 43, 226, 255), outline=(50, 20, 80, 255), width=4)
d1.ellipse([395, 410, 445, 470], fill=(186, 85, 211, 255))
d1.ellipse([395, 400, 418, 425], fill=(255, 255, 255, 255)) # highlight
d1.ellipse([430, 445, 442, 460], fill=(255, 255, 255, 200)) # small shine
# Eyelashes
d1.arc([370, 375, 470, 420], start=180, end=360, fill=(50, 20, 80, 255), width=6)
# Right eye
d1.ellipse([564, 390, 644, 490], fill=(138, 43, 226, 255), outline=(50, 20, 80, 255), width=4)
d1.ellipse([579, 410, 629, 470], fill=(186, 85, 211, 255))
d1.ellipse([579, 400, 602, 425], fill=(255, 255, 255, 255)) # highlight
d1.ellipse([614, 445, 626, 460], fill=(255, 255, 255, 200)) # small shine
d1.arc([554, 375, 654, 420], start=180, end=360, fill=(50, 20, 80, 255), width=6)
# Cute smile
d1.arc([490, 520, 534, 550], start=10, end=170, fill=(180, 60, 80, 255), width=5)

save_item(face1, "items/face/face_purple_sparkle.png", "items/thumbnails/thumb_face_purple.png", crop_box=(320, 360, 704, 560))
add_catalog_item("face_purple", "face", "face", "items/face/face_purple_sparkle.png", "items/thumbnails/thumb_face_purple.png", "Sparkle Purple", "Mắt Tím Lấp Lánh")

# Face 2: Winking Pink
face2 = new_canvas()
d2 = ImageDraw.Draw(face2)
d2.ellipse([340, 480, 420, 520], fill=(255, 175, 185, 140))
d2.ellipse([604, 480, 684, 520], fill=(255, 175, 185, 140))
# Left eye - WINK
d2.arc([375, 410, 465, 460], start=190, end=350, fill=(60, 20, 50, 255), width=8)
d2.line([(455, 430), (475, 425)], fill=(60, 20, 50, 255), width=5)
# Right eye - Open Pink
d2.ellipse([564, 390, 644, 490], fill=(255, 105, 180, 255), outline=(60, 20, 50, 255), width=4)
d2.ellipse([579, 410, 629, 470], fill=(255, 182, 193, 255))
d2.ellipse([579, 400, 602, 425], fill=(255, 255, 255, 255))
d2.ellipse([614, 445, 626, 460], fill=(255, 255, 255, 200))
d2.arc([554, 375, 654, 420], start=180, end=360, fill=(60, 20, 50, 255), width=6)
# Open cat mouth
d2.pieslice([490, 520, 534, 560], start=0, end=180, fill=(255, 99, 132, 255), outline=(120, 30, 50, 255), width=3)

save_item(face2, "items/face/face_wink_pink.png", "items/thumbnails/thumb_face_wink.png", crop_box=(320, 360, 704, 560))
add_catalog_item("face_wink", "face", "face", "items/face/face_wink_pink.png", "items/thumbnails/thumb_face_wink.png", "Winking Cute", "Nháy Mắt Đáng Yêu")

# Face 3: Big Brown Anime
face3 = new_canvas()
d3 = ImageDraw.Draw(face3)
d3.ellipse([340, 480, 420, 520], fill=(255, 180, 180, 130))
d3.ellipse([604, 480, 684, 520], fill=(255, 180, 180, 130))
# Both Big Warm Amber Eyes
for ex in [380, 564]:
    d3.ellipse([ex, 388, ex + 80, 490], fill=(139, 69, 19, 255), outline=(40, 20, 10, 255), width=4)
    d3.ellipse([ex + 10, 415, ex + 70, 485], fill=(218, 165, 32, 255))
    d3.ellipse([ex + 15, 395, ex + 42, 425], fill=(255, 255, 255, 255))
    d3.ellipse([ex + 48, 445, ex + 62, 462], fill=(255, 255, 255, 220))
d3.arc([370, 370, 470, 415], start=180, end=360, fill=(40, 20, 10, 255), width=6)
d3.arc([554, 370, 654, 415], start=180, end=360, fill=(40, 20, 10, 255), width=6)
# Small cute mouth
d3.ellipse([502, 530, 522, 545], fill=(230, 100, 110, 255))

save_item(face3, "items/face/face_curious_brown.png", "items/thumbnails/thumb_face_brown.png", crop_box=(320, 360, 704, 560))
add_catalog_item("face_brown", "face", "face", "items/face/face_curious_brown.png", "items/thumbnails/thumb_face_brown.png", "Curious Amber", "Mắt Nâu Tròn Xoe")

# ==========================================
# 3. HAIRSTYLES (HAIR_BACK + HAIR_FRONT)
# ==========================================
print("Generating Hairstyles...")
# Hair 1: Lilac Twin Tails
h1_back = new_canvas()
hb1 = ImageDraw.Draw(h1_back)
# Left ponytail
hb1.polygon([(260, 380), (160, 580), (120, 850), (150, 1020), (220, 960), (230, 750), (320, 460)], fill=(180, 140, 230, 255), outline=(130, 90, 180, 255))
hb1.ellipse([110, 820, 230, 1030], fill=(195, 155, 240, 255))
# Right ponytail
hb1.polygon([(764, 380), (864, 580), (904, 850), (874, 1020), (804, 960), (794, 750), (704, 460)], fill=(180, 140, 230, 255), outline=(130, 90, 180, 255))
hb1.ellipse([794, 820, 914, 1030], fill=(195, 155, 240, 255))
# Back base hair
hb1.ellipse([270, 200, 754, 620], fill=(170, 130, 220, 255))

h1_front = new_canvas()
hf1 = ImageDraw.Draw(h1_front)
# Top hair dome
hf1.pieslice([260, 170, 764, 600], start=180, end=360, fill=(205, 165, 250, 255), outline=(130, 90, 180, 255), width=5)
# Bangs
hf1.polygon([(270, 360), (330, 420), (370, 380), (430, 430), (512, 390), (594, 430), (654, 380), (694, 420), (754, 360), (754, 250), (270, 250)], fill=(205, 165, 250, 255), outline=(130, 90, 180, 255), width=4)
# Left & right side strands
hf1.polygon([(270, 350), (250, 520), (290, 580), (310, 480)], fill=(195, 155, 240, 255), outline=(130, 90, 180, 255), width=3)
hf1.polygon([(754, 350), (774, 520), (734, 580), (714, 480)], fill=(195, 155, 240, 255), outline=(130, 90, 180, 255), width=3)
# Cute hair ties
hf1.ellipse([230, 360, 280, 410], fill=(255, 182, 193, 255), outline=(220, 100, 130, 255), width=3)
hf1.ellipse([744, 360, 794, 410], fill=(255, 182, 193, 255), outline=(220, 100, 130, 255), width=3)

# Combined preview for thumbnail
h1_combo = Image.alpha_composite(h1_back, h1_front)
save_item(h1_back, "items/hair/hair_lilac_back.png", "items/thumbnails/thumb_h1_ignore.png")
save_item(h1_front, "items/hair/hair_lilac_front.png", "items/thumbnails/thumb_hair_lilac.png", crop_box=(120, 150, 904, 800))
add_catalog_item("hair_lilac_twin", "hair", "hair_front", "items/hair/hair_lilac_front.png", "items/thumbnails/thumb_hair_lilac.png", "Lilac Twin Tails", "Tóc Hai Bên Tím Mộng Mơ", locked=False, secondary_layer="hair_back", secondary_file="items/hair/hair_lilac_back.png")

# Hair 2: Pastel Pink Long Wavy
h2_back = new_canvas()
hb2 = ImageDraw.Draw(h2_back)
hb2.ellipse([240, 200, 784, 640], fill=(255, 170, 195, 255))
hb2.polygon([(240, 450), (180, 750), (210, 1100), (340, 1150), (410, 850), (320, 550)], fill=(255, 160, 185, 255), outline=(220, 110, 140, 255))
hb2.polygon([(784, 450), (844, 750), (814, 1100), (684, 1150), (614, 850), (704, 550)], fill=(255, 160, 185, 255), outline=(220, 110, 140, 255))

h2_front = new_canvas()
hf2 = ImageDraw.Draw(h2_front)
hf2.pieslice([250, 160, 774, 580], start=180, end=360, fill=(255, 192, 203, 255), outline=(220, 110, 140, 255), width=5)
# Arched soft bangs
hf2.polygon([(260, 360), (320, 410), (400, 390), (480, 420), (544, 420), (624, 390), (704, 410), (764, 360), (764, 250), (260, 250)], fill=(255, 192, 203, 255), outline=(220, 110, 140, 255), width=4)
hf2.polygon([(250, 340), (220, 600), (280, 680), (310, 520)], fill=(255, 182, 193, 255), outline=(220, 110, 140, 255), width=3)
hf2.polygon([(774, 340), (804, 600), (744, 680), (714, 520)], fill=(255, 182, 193, 255), outline=(220, 110, 140, 255), width=3)

save_item(h2_back, "items/hair/hair_pink_back.png", "items/thumbnails/thumb_h2_ignore.png")
save_item(h2_front, "items/hair/hair_pink_front.png", "items/thumbnails/thumb_hair_pink.png", crop_box=(180, 150, 844, 800))
add_catalog_item("hair_pink_wavy", "hair", "hair_front", "items/hair/hair_pink_front.png", "items/thumbnails/thumb_hair_pink.png", "Pastel Pink Wavy", "Tóc Dài Uốn Sóng Hồng", locked=False, secondary_layer="hair_back", secondary_file="items/hair/hair_pink_back.png")

# Hair 3: Honey Blonde Pigtails
h3_back = new_canvas()
hb3 = ImageDraw.Draw(h3_back)
hb3.ellipse([270, 200, 754, 600], fill=(235, 185, 75, 255))
hb3.polygon([(260, 380), (140, 600), (100, 900), (200, 920), (240, 700), (310, 460)], fill=(245, 195, 85, 255), outline=(190, 140, 40, 255))
hb3.polygon([(764, 380), (884, 600), (924, 900), (824, 920), (784, 700), (714, 460)], fill=(245, 195, 85, 255), outline=(190, 140, 40, 255))

h3_front = new_canvas()
hf3 = ImageDraw.Draw(h3_front)
hf3.pieslice([260, 170, 764, 590], start=180, end=360, fill=(255, 215, 100, 255), outline=(190, 140, 40, 255), width=5)
hf3.polygon([(270, 360), (350, 420), (430, 380), (512, 425), (594, 380), (674, 420), (754, 360), (754, 250), (270, 250)], fill=(255, 215, 100, 255), outline=(190, 140, 40, 255), width=4)

save_item(h3_back, "items/hair/hair_blonde_back.png", "items/thumbnails/thumb_h3_ignore.png")
save_item(h3_front, "items/hair/hair_blonde_front.png", "items/thumbnails/thumb_hair_blonde.png", crop_box=(100, 150, 924, 800))
add_catalog_item("hair_blonde_twin", "hair", "hair_front", "items/hair/hair_blonde_front.png", "items/thumbnails/thumb_hair_blonde.png", "Honey Blonde Tails", "Tóc Vàng Năng Động", locked=False, secondary_layer="hair_back", secondary_file="items/hair/hair_blonde_back.png")

# ==========================================
# 4. TOPS
# ==========================================
print("Generating Tops...")
# Top 1: Sailor Blouse
top1 = new_canvas()
dt1 = ImageDraw.Draw(top1)
# Main body
dt1.polygon([(400, 640), (624, 640), (620, 840), (404, 840)], fill=(255, 255, 255, 255), outline=(180, 200, 230, 255), width=4)
# Puffy Sleeves
dt1.ellipse([340, 630, 420, 760], fill=(255, 255, 255, 255), outline=(180, 200, 230, 255), width=4)
dt1.ellipse([604, 630, 684, 760], fill=(255, 255, 255, 255), outline=(180, 200, 230, 255), width=4)
# Sailor blue collar
dt1.polygon([(450, 640), (512, 730), (574, 640), (630, 670), (512, 770), (394, 670)], fill=(100, 149, 237, 255), outline=(50, 80, 150, 255), width=3)
# Red necktie ribbon
dt1.polygon([(495, 730), (529, 730), (539, 810), (512, 830), (485, 810)], fill=(255, 69, 96, 255), outline=(180, 30, 50, 255), width=2)

save_item(top1, "items/top/top_sailor.png", "items/thumbnails/thumb_top_sailor.png", crop_box=(330, 610, 694, 860))
add_catalog_item("top_sailor", "top", "top", "items/top/top_sailor.png", "items/thumbnails/thumb_top_sailor.png", "Sailor Blouse", "Áo Thủy Thủ Dễ Thương")

# Top 2: Cozy Lavender Knit Sweater
top2 = new_canvas()
dt2 = ImageDraw.Draw(top2)
dt2.rounded_rectangle([390, 630, 634, 850], radius=20, fill=(216, 180, 248, 255), outline=(160, 120, 210, 255), width=4)
# Long sleeves
dt2.polygon([(390, 640), (310, 780), (310, 870), (370, 860), (410, 720)], fill=(216, 180, 248, 255), outline=(160, 120, 210, 255), width=3)
dt2.polygon([(634, 640), (714, 780), (714, 870), (654, 860), (614, 720)], fill=(216, 180, 248, 255), outline=(160, 120, 210, 255), width=3)
# Cute heart patch on chest
dt2.ellipse([495, 690, 529, 720], fill=(255, 105, 180, 255))

save_item(top2, "items/top/top_lavender_sweater.png", "items/thumbnails/thumb_top_sweater.png", crop_box=(300, 610, 724, 880))
add_catalog_item("top_lavender", "top", "top", "items/top/top_lavender_sweater.png", "items/thumbnails/thumb_top_sweater.png", "Lavender Sweater", "Áo Len Tím Ấm Áp")

# Top 3: Sunny Yellow Crop Tee
top3 = new_canvas()
dt3 = ImageDraw.Draw(top3)
dt3.polygon([(404, 640), (620, 640), (615, 800), (409, 800)], fill=(255, 230, 100, 255), outline=(220, 180, 50, 255), width=4)
# Short sleeves
dt3.polygon([(404, 640), (340, 710), (370, 750), (414, 690)], fill=(255, 230, 100, 255), outline=(220, 180, 50, 255), width=3)
dt3.polygon([(620, 640), (684, 710), (654, 750), (610, 690)], fill=(255, 230, 100, 255), outline=(220, 180, 50, 255), width=3)
# White duck print on shirt
dt3.ellipse([492, 700, 532, 740], fill=(255, 255, 255, 255))

save_item(top3, "items/top/top_yellow_tee.png", "items/thumbnails/thumb_top_yellow.png", crop_box=(330, 620, 694, 830))
add_catalog_item("top_yellow", "top", "top", "items/top/top_yellow_tee.png", "items/thumbnails/thumb_top_yellow.png", "Sunny Duck Tee", "Áo Phông Vịt Vàng")

# ==========================================
# 5. BOTTOMS
# ==========================================
print("Generating Bottoms...")
# Bottom 1: Navy Pleated Skirt
bot1 = new_canvas()
db1 = ImageDraw.Draw(bot1)
db1.polygon([(400, 830), (624, 830), (670, 1030), (354, 1030)], fill=(40, 60, 120, 255), outline=(20, 30, 70, 255), width=4)
# Pleat lines
for px in range(380, 650, 35):
    db1.line([(px, 830), (px - 20 if px < 512 else px + 20, 1030)], fill=(70, 100, 170, 255), width=3)

save_item(bot1, "items/bottom/bottom_navy_skirt.png", "items/thumbnails/thumb_bot_navy.png", crop_box=(340, 810, 684, 1050))
add_catalog_item("bot_navy_skirt", "bottom", "bottom", "items/bottom/bottom_navy_skirt.png", "items/thumbnails/thumb_bot_navy.png", "Pleated Navy Skirt", "Chân Váy Xếp Ly Xanh")

# Bottom 2: Denim Shorts
bot2 = new_canvas()
db2 = ImageDraw.Draw(bot2)
db2.rounded_rectangle([394, 830, 630, 980], radius=15, fill=(100, 149, 237, 255), outline=(50, 90, 170, 255), width=4)
db2.line([(512, 830), (512, 980)], fill=(255, 215, 0, 255), width=3) # stitch
# Pink belt
db2.rectangle([394, 830, 630, 860], fill=(255, 105, 180, 255), outline=(200, 60, 130, 255), width=2)
db2.rectangle([495, 825, 529, 865], fill=(255, 215, 0, 255), outline=(180, 140, 20, 255), width=2)

save_item(bot2, "items/bottom/bottom_denim_shorts.png", "items/thumbnails/thumb_bot_denim.png", crop_box=(380, 810, 644, 1000))
add_catalog_item("bot_denim_shorts", "bottom", "bottom", "items/bottom/bottom_denim_shorts.png", "items/thumbnails/thumb_bot_denim.png", "Denim Shorts", "Quần Sooc Bò")

# Bottom 3: Tiered Pink Frill Skirt
bot3 = new_canvas()
db3 = ImageDraw.Draw(bot3)
db3.polygon([(400, 830), (624, 830), (680, 1060), (344, 1060)], fill=(255, 182, 193, 255), outline=(220, 100, 130, 255), width=4)
# Frill scallops
for r in [900, 980, 1060]:
    db3.arc([340, r - 30, 684, r + 30], start=0, end=180, fill=(255, 255, 255, 255), width=4)

save_item(bot3, "items/bottom/bottom_pink_frills.png", "items/thumbnails/thumb_bot_pink.png", crop_box=(330, 810, 694, 1080))
add_catalog_item("bot_pink_frills", "bottom", "bottom", "items/bottom/bottom_pink_frills.png", "items/thumbnails/thumb_bot_pink.png", "Tiered Pink Skirt", "Chân Váy Bèo Tầng Hồng")

# ==========================================
# 6. DRESSES
# ==========================================
print("Generating Dresses...")
# Dress 1: Magical Princess Gown
dr1 = new_canvas()
dd1 = ImageDraw.Draw(dr1)
# Bodice
dd1.polygon([(400, 640), (624, 640), (610, 830), (414, 830)], fill=(255, 105, 180, 255), outline=(200, 50, 120, 255), width=4)
# Golden corset laces
dd1.line([(490, 660), (534, 720)], fill=(255, 215, 0, 255), width=3)
dd1.line([(534, 660), (490, 720)], fill=(255, 215, 0, 255), width=3)
dd1.line([(490, 720), (534, 780)], fill=(255, 215, 0, 255), width=3)
dd1.line([(534, 720), (490, 780)], fill=(255, 215, 0, 255), width=3)
# Puffy off-shoulder sleeves
dd1.ellipse([340, 630, 420, 730], fill=(255, 182, 193, 255), outline=(200, 50, 120, 255), width=3)
dd1.ellipse([604, 630, 684, 730], fill=(255, 182, 193, 255), outline=(200, 50, 120, 255), width=3)
# Huge princess ballgown skirt
dd1.polygon([(414, 830), (610, 830), (740, 1200), (284, 1200)], fill=(255, 192, 203, 255), outline=(200, 50, 120, 255), width=5)
# White lace overlay
dd1.polygon([(460, 830), (564, 830), (660, 1180), (364, 1180)], fill=(255, 240, 245, 220), outline=(255, 105, 180, 255), width=3)

save_item(dr1, "items/dress/dress_princess_pink.png", "items/thumbnails/thumb_dress_pink.png", crop_box=(270, 620, 754, 1220))
add_catalog_item("dress_princess", "dress", "dress", "items/dress/dress_princess_pink.png", "items/thumbnails/thumb_dress_pink.png", "Princess Ballgown", "Váy Công Chúa Bồng Bềnh")

# Dress 2: Mint Sweet Polka Sundress
dr2 = new_canvas()
dd2 = ImageDraw.Draw(dr2)
dd2.polygon([(404, 650), (620, 650), (610, 820), (414, 820)], fill=(152, 251, 152, 255), outline=(80, 180, 100, 255), width=4)
# Shoulder straps
dd2.line([(430, 600), (430, 650)], fill=(152, 251, 152, 255), width=10)
dd2.line([(594, 600), (594, 650)], fill=(152, 251, 152, 255), width=10)
# Flared skirt
dd2.polygon([(414, 820), (610, 820), (690, 1120), (334, 1120)], fill=(152, 251, 152, 255), outline=(80, 180, 100, 255), width=4)
# Polka dots
for dy in range(850, 1100, 50):
    for dx in range(370, 660, 60):
        dd2.ellipse([dx, dy, dx + 16, dy + 16], fill=(255, 255, 255, 230))
# White waistband ribbon
dd2.rectangle([410, 810, 614, 835], fill=(255, 255, 255, 255), outline=(180, 180, 180, 255), width=2)
dd2.ellipse([492, 805, 532, 840], fill=(255, 255, 255, 255), outline=(180, 180, 180, 255), width=2)

save_item(dr2, "items/dress/dress_mint_polka.png", "items/thumbnails/thumb_dress_mint.png", crop_box=(320, 600, 704, 1140))
add_catalog_item("dress_mint", "dress", "dress", "items/dress/dress_mint_polka.png", "items/thumbnails/thumb_dress_mint.png", "Mint Polka Dress", "Váy Chấm Bi Màu Bạc Hà")

# ==========================================
# 7. OUTERWEAR
# ==========================================
print("Generating Outerwear...")
# Outerwear 1: Pastel Varsity Jacket
out1 = new_canvas()
do1 = ImageDraw.Draw(out1)
# Open jacket torso
do1.polygon([(370, 630), (440, 630), (420, 860), (350, 860)], fill=(255, 182, 193, 255), outline=(220, 100, 130, 255), width=4)
do1.polygon([(654, 630), (584, 630), (604, 860), (674, 860)], fill=(255, 182, 193, 255), outline=(220, 100, 130, 255), width=4)
# White sleeves
do1.polygon([(370, 630), (290, 770), (300, 890), (360, 880), (400, 730)], fill=(255, 255, 255, 255), outline=(200, 200, 210, 255), width=4)
do1.polygon([(654, 630), (734, 770), (724, 890), (664, 880), (624, 730)], fill=(255, 255, 255, 255), outline=(200, 200, 210, 255), width=4)
# Striped cuffs
do1.rectangle([295, 870, 355, 895], fill=(255, 105, 180, 255))
do1.rectangle([669, 870, 729, 895], fill=(255, 105, 180, 255))

save_item(out1, "items/outerwear/outer_varsity_pink.png", "items/thumbnails/thumb_outer_varsity.png", crop_box=(280, 610, 744, 910))
add_catalog_item("outer_varsity", "outerwear", "outerwear", "items/outerwear/outer_varsity_pink.png", "items/thumbnails/thumb_outer_varsity.png", "Varsity Jacket", "Áo Khoác Bóng Chày")

# Outerwear 2: Cozy Oversized Mint Cardigan
out2 = new_canvas()
do2 = ImageDraw.Draw(out2)
do2.polygon([(360, 620), (440, 620), (430, 910), (350, 910)], fill=(175, 238, 238, 255), outline=(100, 180, 180, 255), width=4)
do2.polygon([(664, 620), (584, 620), (594, 910), (674, 910)], fill=(175, 238, 238, 255), outline=(100, 180, 180, 255), width=4)
do2.polygon([(360, 620), (290, 770), (310, 920), (370, 910), (400, 730)], fill=(175, 238, 238, 255), outline=(100, 180, 180, 255), width=4)
do2.polygon([(664, 620), (734, 770), (714, 920), (654, 910), (624, 730)], fill=(175, 238, 238, 255), outline=(100, 180, 180, 255), width=4)

save_item(out2, "items/outerwear/outer_mint_cardigan.png", "items/thumbnails/thumb_outer_cardigan.png", crop_box=(280, 610, 744, 930))
add_catalog_item("outer_cardigan", "outerwear", "outerwear", "items/outerwear/outer_mint_cardigan.png", "items/thumbnails/thumb_outer_cardigan.png", "Mint Cardigan", "Áo Khoác Len Bạc Hà")

# ==========================================
# 8. SHOES
# ==========================================
print("Generating Shoes...")
# Shoes 1: Mary Jane with White Frill Socks
sh1 = new_canvas()
ds1 = ImageDraw.Draw(sh1)
# White ankle socks
ds1.rectangle([415, 1260, 485, 1370], fill=(255, 255, 255, 255), outline=(210, 210, 220, 255), width=3)
ds1.rectangle([539, 1260, 609, 1370], fill=(255, 255, 255, 255), outline=(210, 210, 220, 255), width=3)
# Sock lace frills
ds1.ellipse([410, 1250, 490, 1275], fill=(255, 255, 255, 255), outline=(200, 200, 210, 255), width=2)
ds1.ellipse([534, 1250, 614, 1275], fill=(255, 255, 255, 255), outline=(200, 200, 210, 255), width=2)
# Shiny black Mary Jane shoes
ds1.rounded_rectangle([405, 1350, 495, 1420], radius=15, fill=(30, 30, 40, 255), outline=(10, 10, 15, 255), width=4)
ds1.rounded_rectangle([529, 1350, 619, 1420], radius=15, fill=(30, 30, 40, 255), outline=(10, 10, 15, 255), width=4)
# Gloss highlights
ds1.ellipse([420, 1360, 440, 1375], fill=(255, 255, 255, 180))
ds1.ellipse([544, 1360, 564, 1375], fill=(255, 255, 255, 180))

save_item(sh1, "items/shoes/shoes_mary_jane.png", "items/thumbnails/thumb_shoes_mj.png", crop_box=(390, 1240, 634, 1440))
add_catalog_item("shoes_mary_jane", "shoes", "shoes", "items/shoes/shoes_mary_jane.png", "items/thumbnails/thumb_shoes_mj.png", "Mary Jane Shoes", "Giày Búp Bê Quai Ngang")

# Shoes 2: Pastel Pink Platform Sneakers
sh2 = new_canvas()
ds2 = ImageDraw.Draw(sh2)
# Pink sneakers
ds2.rounded_rectangle([400, 1340, 500, 1425], radius=20, fill=(255, 105, 180, 255), outline=(200, 50, 120, 255), width=4)
ds2.rounded_rectangle([524, 1340, 624, 1425], radius=20, fill=(255, 105, 180, 255), outline=(200, 50, 120, 255), width=4)
# White platform soles
ds2.rounded_rectangle([395, 1400, 505, 1430], radius=8, fill=(255, 255, 255, 255), outline=(180, 180, 190, 255), width=3)
ds2.rounded_rectangle([519, 1400, 629, 1430], radius=8, fill=(255, 255, 255, 255), outline=(180, 180, 190, 255), width=3)
# White laces
ds2.line([(425, 1360), (475, 1360)], fill=(255, 255, 255, 255), width=4)
ds2.line([(549, 1360), (599, 1360)], fill=(255, 255, 255, 255), width=4)

save_item(sh2, "items/shoes/shoes_pink_sneakers.png", "items/thumbnails/thumb_shoes_sneakers.png", crop_box=(385, 1320, 639, 1445))
add_catalog_item("shoes_sneakers", "shoes", "shoes", "items/shoes/shoes_pink_sneakers.png", "items/thumbnails/thumb_shoes_sneakers.png", "Pink Sneakers", "Giày Thể Thao Hồng")

# ==========================================
# 9. HATS & HEADWEAR
# ==========================================
print("Generating Hats & Headwear...")
# Hat 1: Giant Pastel Ribbon Bow
hat1 = new_canvas()
dh1 = ImageDraw.Draw(hat1)
# Headband band
dh1.arc([270, 210, 754, 550], start=210, end=330, fill=(255, 105, 180, 255), width=12)
# Center knot
dh1.rounded_rectangle([485, 150, 539, 210], radius=10, fill=(255, 20, 147, 255), outline=(180, 10, 100, 255), width=3)
# Left loop
dh1.polygon([(485, 180), (320, 100), (310, 240), (485, 195)], fill=(255, 105, 180, 255), outline=(200, 30, 120, 255), width=4)
# Right loop
dh1.polygon([(539, 180), (704, 100), (714, 240), (539, 195)], fill=(255, 105, 180, 255), outline=(200, 30, 120, 255), width=4)

save_item(hat1, "items/hat/hat_giant_bow.png", "items/thumbnails/thumb_hat_bow.png", crop_box=(300, 90, 724, 280))
add_catalog_item("hat_giant_bow", "hat", "hat", "items/hat/hat_giant_bow.png", "items/thumbnails/thumb_hat_bow.png", "Giant Ribbon Bow", "Nơ Cài Đầu Khổng Lồ")

# Hat 2: Golden Princess Crown
hat2 = new_canvas()
dh2 = ImageDraw.Draw(hat2)
dh2.polygon([(420, 210), (400, 130), (460, 170), (512, 100), (564, 170), (624, 130), (604, 210)], fill=(255, 215, 0, 255), outline=(200, 150, 0, 255), width=4)
# Jewels on peaks
dh2.ellipse([390, 120, 410, 140], fill=(255, 50, 100, 255))
dh2.ellipse([502, 90, 522, 110], fill=(50, 150, 255, 255))
dh2.ellipse([614, 120, 634, 140], fill=(255, 50, 100, 255))

save_item(hat2, "items/hat/hat_crown.png", "items/thumbnails/thumb_hat_crown.png", crop_box=(380, 80, 644, 230))
add_catalog_item("hat_crown", "hat", "hat", "items/hat/hat_crown.png", "items/thumbnails/thumb_hat_crown.png", "Princess Crown", "Vương Miện Công Chúa")

# Hat 3: Cat Ears Headband
hat3 = new_canvas()
dh3 = ImageDraw.Draw(hat3)
# Band
dh3.arc([270, 210, 754, 550], start=210, end=330, fill=(60, 60, 60, 255), width=10)
# Left ear
dh3.polygon([(340, 220), (320, 90), (430, 170)], fill=(255, 255, 255, 255), outline=(50, 50, 50, 255), width=4)
dh3.polygon([(350, 200), (340, 120), (410, 170)], fill=(255, 182, 193, 255))
# Right ear
dh3.polygon([(684, 220), (704, 90), (594, 170)], fill=(255, 255, 255, 255), outline=(50, 50, 50, 255), width=4)
dh3.polygon([(674, 200), (684, 120), (614, 170)], fill=(255, 182, 193, 255))

save_item(hat3, "items/hat/hat_cat_ears.png", "items/thumbnails/thumb_hat_cat.png", crop_box=(300, 80, 724, 240))
add_catalog_item("hat_cat_ears", "hat", "hat", "items/hat/hat_cat_ears.png", "items/thumbnails/thumb_hat_cat.png", "Fluffy Cat Ears", "Tai Mèo Lông Mềm")

# ==========================================
# 10. ACCESSORIES
# ==========================================
print("Generating Accessories...")
# Accessory 1: Heart Sunglasses
acc1 = new_canvas()
da1 = ImageDraw.Draw(acc1)
# Left heart lens
da1.polygon([(370, 420), (430, 390), (470, 430), (420, 490), (370, 440)], fill=(255, 105, 180, 210), outline=(200, 30, 100, 255), width=4)
# Right heart lens
da1.polygon([(554, 420), (614, 390), (654, 430), (604, 490), (554, 440)], fill=(255, 105, 180, 210), outline=(200, 30, 100, 255), width=4)
# Bridge
da1.line([(470, 430), (554, 430)], fill=(200, 30, 100, 255), width=4)

save_item(acc1, "items/accessory/acc_heart_glasses.png", "items/thumbnails/thumb_acc_glasses.png", crop_box=(350, 380, 674, 510))
add_catalog_item("acc_glasses", "accessory", "accessory", "items/accessory/acc_heart_glasses.png", "items/thumbnails/thumb_acc_glasses.png", "Heart Sunglasses", "Kính Mát Trái Tim")

# Accessory 2: Magical Star Wand
acc2 = new_canvas()
da2 = ImageDraw.Draw(acc2)
# Golden rod held in right hand
da2.line([(680, 760), (780, 1100)], fill=(255, 215, 0, 255), width=8)
# Big glowing star at top
da2.polygon([(680, 700), (695, 740), (740, 740), (705, 765), (720, 805), (680, 780), (640, 805), (655, 765), (620, 740), (665, 740)], fill=(255, 255, 100, 255), outline=(230, 180, 20, 255), width=3)
# Pink jewel in center of star
da2.ellipse([670, 750, 690, 770], fill=(255, 105, 180, 255))

save_item(acc2, "items/accessory/acc_star_wand.png", "items/thumbnails/thumb_acc_wand.png", crop_box=(610, 690, 800, 1120))
add_catalog_item("acc_wand", "accessory", "accessory", "items/accessory/acc_star_wand.png", "items/thumbnails/thumb_acc_wand.png", "Star Magic Wand", "Gậy Phép Ngôi Sao")

# Accessory 3: Bear Crossbody Bag
acc3 = new_canvas()
da3 = ImageDraw.Draw(acc3)
# Strap across shoulder
da3.line([(420, 640), (630, 890)], fill=(160, 82, 45, 255), width=6)
# Bear head bag
da3.circle((640, 900), 55, fill=(244, 164, 96, 255), outline=(160, 82, 45, 255), width=4)
# Bear ears
da3.circle((600, 860), 20, fill=(244, 164, 96, 255), outline=(160, 82, 45, 255), width=3)
da3.circle((680, 860), 20, fill=(244, 164, 96, 255), outline=(160, 82, 45, 255), width=3)
# Bear snout & face
da3.ellipse([625, 895, 655, 915], fill=(255, 240, 220, 255))
da3.circle((640, 902), 5, fill=(40, 20, 10, 255))

save_item(acc3, "items/accessory/acc_bear_bag.png", "items/thumbnails/thumb_acc_bag.png", crop_box=(410, 630, 710, 970))
add_catalog_item("acc_bear_bag", "accessory", "accessory", "items/accessory/acc_bear_bag.png", "items/thumbnails/thumb_acc_bag.png", "Cute Bear Bag", "Túi Đeo Chéo Gấu Nâu")

# ==========================================
# 11. BACKGROUNDS
# ==========================================
print("Generating Backgrounds...")
# Background 1: Pastel Pink Dream Gradient
bg1 = Image.new("RGBA", (WIDTH, HEIGHT), (255, 235, 245, 255))
dbg1 = ImageDraw.Draw(bg1)
for i in range(HEIGHT):
    factor = i / HEIGHT
    r = int(255 * (1 - factor) + 225 * factor)
    g = int(235 * (1 - factor) + 215 * factor)
    b = int(245 * (1 - factor) + 255 * factor)
    dbg1.line([(0, i), (WIDTH, i)], fill=(r, g, b, 255))
# Soft decorative stars
for sx, sy in [(150, 200), (850, 300), (200, 800), (880, 1100), (120, 1300)]:
    dbg1.ellipse([sx - 15, sy - 15, sx + 15, sy + 15], fill=(255, 255, 255, 180))

save_item(bg1, "items/backgrounds/bg_pastel_dream.png", "items/thumbnails/thumb_bg_dream.png", crop_box=(0, 0, WIDTH, HEIGHT))
add_catalog_item("bg_dream", "background", "background", "items/backgrounds/bg_pastel_dream.png", "items/thumbnails/thumb_bg_dream.png", "Pastel Dream", "Mộng Mơ Pastel")

# Background 2: Cherry Blossom Park
bg2 = Image.new("RGBA", (WIDTH, HEIGHT), (240, 248, 255, 255))
dbg2 = ImageDraw.Draw(bg2)
# Sky gradient
for i in range(int(HEIGHT * 0.7)):
    factor = i / (HEIGHT * 0.7)
    r = int(220 * (1 - factor) + 255 * factor)
    g = int(235 * (1 - factor) + 230 * factor)
    b = int(255 * (1 - factor) + 245 * factor)
    dbg2.line([(0, i), (WIDTH, i)], fill=(r, g, b, 255))
# Stone path ground
dbg2.rectangle([0, int(HEIGHT * 0.7), WIDTH, HEIGHT], fill=(230, 220, 215, 255))
# Falling sakura blossoms
for px, py in [(180, 250), (320, 180), (750, 220), (850, 420), (210, 600), (800, 750), (150, 1100), (850, 1250)]:
    dbg2.ellipse([px, py, px + 25, py + 35], fill=(255, 182, 193, 220))

save_item(bg2, "items/backgrounds/bg_cherry_blossom.png", "items/thumbnails/thumb_bg_sakura.png", crop_box=(0, 0, WIDTH, HEIGHT))
add_catalog_item("bg_sakura", "background", "background", "items/backgrounds/bg_cherry_blossom.png", "items/thumbnails/thumb_bg_sakura.png", "Cherry Blossom", "Hoa Anh Đào Nở")

# Background 3: Starry Twilight
bg3 = Image.new("RGBA", (WIDTH, HEIGHT), (40, 30, 80, 255))
dbg3 = ImageDraw.Draw(bg3)
for i in range(HEIGHT):
    factor = i / HEIGHT
    r = int(35 * (1 - factor) + 80 * factor)
    g = int(25 * (1 - factor) + 60 * factor)
    b = int(70 * (1 - factor) + 120 * factor)
    dbg3.line([(0, i), (WIDTH, i)], fill=(r, g, b, 255))
# Crescent moon
dbg3.ellipse([700, 150, 850, 300], fill=(255, 250, 205, 255))
dbg3.ellipse([670, 130, 810, 280], fill=(45, 35, 85, 255))
# Glowing stars
for sx, sy in [(120, 150), (300, 220), (500, 100), (250, 450), (850, 500), (150, 750), (780, 850), (220, 1150), (800, 1200)]:
    dbg3.circle((sx, sy), 5, fill=(255, 255, 255, 240))
    dbg3.circle((sx, sy), 10, fill=(255, 255, 200, 80))

save_item(bg3, "items/backgrounds/bg_starry_twilight.png", "items/thumbnails/thumb_bg_stars.png", crop_box=(0, 0, WIDTH, HEIGHT))
add_catalog_item("bg_stars", "background", "background", "items/backgrounds/bg_starry_twilight.png", "items/thumbnails/thumb_bg_stars.png", "Starry Twilight", "Đêm Sao Huyền Ảo")

# Background 4: Sunny Sunshine
bg4 = Image.new("RGBA", (WIDTH, HEIGHT), (255, 250, 210, 255))
dbg4 = ImageDraw.Draw(bg4)
for i in range(HEIGHT):
    factor = i / HEIGHT
    r = int(255 * (1 - factor) + 255 * factor)
    g = int(245 * (1 - factor) + 215 * factor)
    b = int(180 * (1 - factor) + 225 * factor)
    dbg4.line([(0, i), (WIDTH, i)], fill=(r, g, b, 255))
# Fluffy cloud
for cx, cy in [(200, 220), (260, 200), (330, 200), (390, 220), (750, 350), (810, 330), (880, 350)]:
    dbg4.circle((cx, cy), 60, fill=(255, 255, 255, 210))
# Golden sunny sparkles
for sx, sy in [(150, 500), (850, 700), (200, 1100), (880, 1300)]:
    dbg4.polygon([(sx, sy - 25), (sx + 8, sy - 8), (sx + 25, sy), (sx + 8, sy + 8), (sx, sy + 25), (sx - 8, sy + 8), (sx - 25, sy), (sx - 8, sy - 8)], fill=(255, 215, 0, 200))

save_item(bg4, "items/backgrounds/bg_sunny_sunshine.png", "items/thumbnails/thumb_bg_sunshine.png", crop_box=(0, 0, WIDTH, HEIGHT))
add_catalog_item("bg_sunshine", "background", "background", "items/backgrounds/bg_sunny_sunshine.png", "items/thumbnails/thumb_bg_sunshine.png", "Sunny Glow", "Nắng Ấm Áp")

# Background 5: Mint Candy Stripes
bg5 = Image.new("RGBA", (WIDTH, HEIGHT), (230, 250, 240, 255))
dbg5 = ImageDraw.Draw(bg5)
stripe_w = 64
for x in range(0, WIDTH, stripe_w * 2):
    dbg5.rectangle([x, 0, x + stripe_w, HEIGHT], fill=(185, 240, 215, 255))

save_item(bg5, "items/backgrounds/bg_mint_stripes.png", "items/thumbnails/thumb_bg_mint_stripes.png", crop_box=(0, 0, WIDTH, HEIGHT))
add_catalog_item("bg_mint_stripes", "background", "background", "items/backgrounds/bg_mint_stripes.png", "items/thumbnails/thumb_bg_mint_stripes.png", "Mint Stripes", "Kẻ Sọc Bạc Hà")

# Background 6: Lavender Polka Dots
bg6 = Image.new("RGBA", (WIDTH, HEIGHT), (235, 225, 250, 255))
dbg6 = ImageDraw.Draw(bg6)
step = 120
for y in range(0, HEIGHT, step):
    offset = (step // 2) if (y // step) % 2 == 1 else 0
    for x in range(0, WIDTH + step, step):
        dbg6.circle((x + offset, y), 20, fill=(255, 255, 255, 200))

save_item(bg6, "items/backgrounds/bg_lavender_polka.png", "items/thumbnails/thumb_bg_polka.png", crop_box=(0, 0, WIDTH, HEIGHT))
add_catalog_item("bg_polka", "background", "background", "items/backgrounds/bg_lavender_polka.png", "items/thumbnails/thumb_bg_polka.png", "Lavender Polka", "Chấm Bi Tím Mơ")

# Background 7: Pink Sweet Hearts
bg7 = Image.new("RGBA", (WIDTH, HEIGHT), (255, 230, 238, 255))
dbg7 = ImageDraw.Draw(bg7)
h_step_x = 160
h_step_y = 180
for row, y in enumerate(range(60, HEIGHT, h_step_y)):
    offset = 80 if row % 2 == 1 else 0
    for x in range(60 + offset, WIDTH, h_step_x):
        # Draw cute heart
        dbg7.circle((x - 14, y), 16, fill=(255, 160, 185, 210))
        dbg7.circle((x + 14, y), 16, fill=(255, 160, 185, 210))
        dbg7.polygon([(x - 28, y + 4), (x + 28, y + 4), (x, y + 36)], fill=(255, 160, 185, 210))

save_item(bg7, "items/backgrounds/bg_sweet_hearts.png", "items/thumbnails/thumb_bg_hearts.png", crop_box=(0, 0, WIDTH, HEIGHT))
add_catalog_item("bg_hearts", "background", "background", "items/backgrounds/bg_sweet_hearts.png", "items/thumbnails/thumb_bg_hearts.png", "Sweet Hearts", "Trái Tim Ngọt Ngào")

# Background 8: Baby Blue Grid
bg8 = Image.new("RGBA", (WIDTH, HEIGHT), (235, 245, 255, 255))
dbg8 = ImageDraw.Draw(bg8)
grid_size = 50
for x in range(0, WIDTH, grid_size):
    dbg8.line([(x, 0), (x, HEIGHT)], fill=(195, 220, 245, 255), width=2)
for y in range(0, HEIGHT, grid_size):
    dbg8.line([(0, y), (WIDTH, y)], fill=(195, 220, 245, 255), width=2)
# Cute sticker stars on grid
for sx, sy in [(150, 200), (820, 350), (250, 850), (800, 1150), (300, 1350)]:
    dbg8.polygon([(sx, sy - 20), (sx + 6, sy - 6), (sx + 20, sy), (sx + 6, sy + 6), (sx, sy + 20), (sx - 6, sy + 6), (sx - 20, sy), (sx - 6, sy - 6)], fill=(255, 182, 193, 230))

save_item(bg8, "items/backgrounds/bg_blue_grid.png", "items/thumbnails/thumb_bg_grid.png", crop_box=(0, 0, WIDTH, HEIGHT))
add_catalog_item("bg_grid", "background", "background", "items/backgrounds/bg_blue_grid.png", "items/thumbnails/thumb_bg_grid.png", "Aesthetic Grid", "Ô Vở Xanh Pastel")

# Save catalog.json
catalog_path = os.path.join(ASSETS_DIR, "catalog.json")
with open(catalog_path, "w", encoding="utf-8") as f:
    json.dump(catalog, f, indent=2, ensure_ascii=False)

print(f"Generated {len(catalog)} items successfully into {ASSETS_DIR}!")

