import os
from PIL import Image, ImageDraw, ImageFilter

def generate_all_icons(android_root, ios_roots):
    print("=== Generating Production-Grade App Icons for Android & iOS ===")

    # 1. Composite Chibi Doll Character from asset layers
    full_canvas = Image.new('RGBA', (1024, 1536), (0, 0, 0, 0))

    def paste_layer(rel_path):
        p = os.path.join(android_root, "app", "src", "main", "assets", rel_path)
        if os.path.exists(p):
            layer = Image.open(p).convert('RGBA')
            full_canvas.alpha_composite(layer)

    paste_layer('items/hair/hair_lilac_back.png')
    paste_layer('items/body/body_fair.png')
    paste_layer('items/dress/dress_princess_pink.png')
    paste_layer('items/blush/blush_pink.png')
    paste_layer('items/mouth/mouth_smile.png')
    paste_layer('items/eyes/eyes_purple_open.png')
    paste_layer('items/eyebrows/brow_gentle.png')
    paste_layer('items/hair/hair_lilac_front.png')
    paste_layer('items/hat/hat_giant_bow.png')

    # Crop head and upper body
    crop_box = (72, 80, 952, 960) # 880x880 square
    doll_crop = full_canvas.crop(crop_box)
    doll_resized = doll_crop.resize((780, 780), Image.Resampling.LANCZOS)

    padded_doll = Image.new('RGBA', (900, 900), (0, 0, 0, 0))
    padded_doll.paste(doll_resized, (60, 60))

    alpha = padded_doll.split()[3]
    shadow_color = Image.new('RGBA', (900, 900), (45, 15, 55, 110))
    shadow_color.putalpha(alpha)
    shadow = shadow_color.filter(ImageFilter.GaussianBlur(18))

    # 2. Master Background: Vibrant pastel gradient
    master_icon = Image.new('RGBA', (1024, 1024), (255, 255, 255, 255))
    draw = ImageDraw.Draw(master_icon)

    for y in range(1024):
        ratio = y / 1023.0
        if ratio < 0.45:
            sub = ratio / 0.45
            r = 255
            g = int(120 + (168 - 120) * sub)
            b = int(145 + (192 - 145) * sub)
        elif ratio < 0.8:
            sub = (ratio - 0.45) / 0.35
            r = int(255 + (205 - 255) * sub)
            g = int(168 + (182 - 168) * sub)
            b = int(192 + (246 - 192) * sub)
        else:
            sub = (ratio - 0.8) / 0.2
            r = int(205 + (170 - 205) * sub)
            g = int(182 + (222 - 182) * sub)
            b = int(246 + (252 - 246) * sub)
        draw.line([(0, y), (1023, y)], fill=(r, g, b, 255))

    # Soft glowing circular badge in background
    badge = Image.new('RGBA', (1024, 1024), (0, 0, 0, 0))
    bdraw = ImageDraw.Draw(badge)
    bdraw.ellipse([46, 46, 978, 978], fill=(255, 255, 255, 65), outline=(255, 255, 255, 190), width=15)
    master_icon.alpha_composite(badge)

    def draw_sparkle(cx, cy, r_out, r_in, fill_color):
        pts = [
            (cx, cy - r_out),
            (cx + r_in, cy - r_in),
            (cx + r_out, cy),
            (cx + r_in, cy + r_in),
            (cx, cy + r_out),
            (cx - r_in, cy + r_in),
            (cx - r_out, cy),
            (cx - r_in, cy - r_in),
        ]
        draw.polygon(pts, fill=fill_color)

    draw_sparkle(135, 165, 52, 15, (255, 255, 255, 245))
    draw_sparkle(885, 175, 46, 13, (255, 255, 255, 245))
    draw_sparkle(895, 790, 38, 11, (255, 255, 255, 225))
    draw_sparkle(125, 780, 35, 10, (255, 255, 255, 225))

    for pt in [(200, 235), (830, 250), (95, 705), (925, 705), (512, 65)]:
        draw.ellipse([pt[0]-7, pt[1]-7, pt[0]+7, pt[1]+7], fill=(255, 255, 255, 235))

    master_icon.alpha_composite(shadow, (62, 140 + 8))
    master_icon.alpha_composite(padded_doll, (62, 140))

    # Create Round Variant (Circular mask)
    round_mask = Image.new('L', (1024, 1024), 0)
    rm_draw = ImageDraw.Draw(round_mask)
    rm_draw.ellipse([0, 0, 1024, 1024], fill=255)
    
    round_icon = Image.new('RGBA', (1024, 1024), (0, 0, 0, 0))
    round_icon.paste(master_icon, (0, 0), round_mask)

    master_rgb = master_icon.convert('RGB')

    # Save Google Play Store 512x512 icon
    store_icon_path = os.path.join(android_root, "app", "src", "main", "ic_launcher-web.png")
    master_rgb.resize((512, 512), Image.Resampling.LANCZOS).save(store_icon_path, 'PNG')
    print("✓ Saved Android Play Store icon (512x512):", store_icon_path)

    # 3. Android Mipmap Densities
    densities = {
        "mipmap-mdpi": 48,
        "mipmap-hdpi": 72,
        "mipmap-xhdpi": 96,
        "mipmap-xxhdpi": 144,
        "mipmap-xxxhdpi": 192,
    }

    res_dir = os.path.join(android_root, "app", "src", "main", "res")
    for folder, size in densities.items():
        folder_path = os.path.join(res_dir, folder)
        os.makedirs(folder_path, exist_ok=True)

        # Standard icon
        square_path = os.path.join(folder_path, "ic_launcher.png")
        master_rgb.resize((size, size), Image.Resampling.LANCZOS).save(square_path, 'PNG')

        # Round icon
        round_path = os.path.join(folder_path, "ic_launcher_round.png")
        round_icon.resize((size, size), Image.Resampling.LANCZOS).save(round_path, 'PNG')
        print(f"✓ Saved Android {folder} ({size}x{size}): ic_launcher.png & ic_launcher_round.png")

    # 4. Android Adaptive Icon Drawables (432x432)
    # Background (gradient + circle + stars)
    adaptive_bg = Image.new('RGBA', (1024, 1024), (255, 255, 255, 255))
    bg_draw = ImageDraw.Draw(adaptive_bg)
    for y in range(1024):
        ratio = y / 1023.0
        if ratio < 0.45:
            sub = ratio / 0.45
            r = 255
            g = int(120 + (168 - 120) * sub)
            b = int(145 + (192 - 145) * sub)
        elif ratio < 0.8:
            sub = (ratio - 0.45) / 0.35
            r = int(255 + (205 - 255) * sub)
            g = int(168 + (182 - 168) * sub)
            b = int(192 + (246 - 192) * sub)
        else:
            sub = (ratio - 0.8) / 0.2
            r = int(205 + (170 - 205) * sub)
            g = int(182 + (222 - 182) * sub)
            b = int(246 + (252 - 246) * sub)
        bg_draw.line([(0, y), (1023, y)], fill=(r, g, b, 255))

    adaptive_bg.alpha_composite(badge)
    adaptive_bg_432 = adaptive_bg.resize((432, 432), Image.Resampling.LANCZOS).convert('RGB')
    
    # Save adaptive background to drawable/ic_launcher_background.png
    adaptive_bg_file = os.path.join(res_dir, "drawable", "ic_launcher_background.png")
    adaptive_bg_432.save(adaptive_bg_file, 'PNG')
    # Remove old vector xml if present
    old_bg_xml = os.path.join(res_dir, "drawable", "ic_launcher_background.xml")
    if os.path.exists(old_bg_xml):
        os.remove(old_bg_xml)

    # Foreground: Doll character sized safely inside 66dp of 108dp (61% = ~264px of 432px)
    adaptive_fg = Image.new('RGBA', (432, 432), (0, 0, 0, 0))
    fg_doll_crop = doll_resized.resize((270, 270), Image.Resampling.LANCZOS)
    
    # Add subtle shadow
    fg_shadow_mask = fg_doll_crop.split()[3]
    fg_shadow = Image.new('RGBA', (270, 270), (45, 15, 55, 90))
    fg_shadow.putalpha(fg_shadow_mask)
    fg_shadow = fg_shadow.filter(ImageFilter.GaussianBlur(8))
    
    fg_x = (432 - 270) // 2
    fg_y = (432 - 270) // 2 + 10 # slightly centered
    adaptive_fg.alpha_composite(fg_shadow, (fg_x, fg_y + 4))
    adaptive_fg.alpha_composite(fg_doll_crop, (fg_x, fg_y))
    
    adaptive_fg_file = os.path.join(res_dir, "drawable", "ic_launcher_foreground.png")
    adaptive_fg.save(adaptive_fg_file, 'PNG')
    # Remove old vector xml if present
    old_fg_xml = os.path.join(res_dir, "drawable", "ic_launcher_foreground.xml")
    if os.path.exists(old_fg_xml):
        os.remove(old_fg_xml)

    print("✓ Replaced ic_launcher_background and ic_launcher_foreground with high-res PNGs in drawable/")

    # 5. iOS App Icons across all locations
    for root_dir in ios_roots:
        if not os.path.exists(root_dir):
            continue
        ios_appicon_dir = os.path.join(root_dir, "ChibiDoll", "Resources", "Assets.xcassets", "AppIcon.appiconset")
        os.makedirs(ios_appicon_dir, exist_ok=True)

        ios_icon_path = os.path.join(ios_appicon_dir, "AppIcon.png")
        master_rgb.save(ios_icon_path, 'PNG')
        print(f"✓ Saved iOS AppIcon 1024x1024 in {ios_icon_path}")

        contents_json = """{
  "images" : [
    {
      "filename" : "AppIcon.png",
      "idiom" : "universal",
      "platform" : "ios",
      "size" : "1024x1024"
    }
  ],
  "info" : {
    "author" : "xcode",
    "version" : 1
  }
}
"""
        with open(os.path.join(ios_appicon_dir, "Contents.json"), "w", encoding="utf-8") as f:
            f.write(contents_json)

    print("\n✓ ALL ICONS SUCCESSFULLY GENERATED AND PLACED!")

if __name__ == "__main__":
    generate_all_icons(
        android_root=r"D:\Chibi",
        ios_roots=[r"D:\Chibi-iOS", r"D:\Chibi\Chibi-iOS"]
    )
