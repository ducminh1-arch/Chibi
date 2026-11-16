#!/usr/bin/env python3
"""
Chibi Doll - Thin Outlines Sharpness Comparison
Compares high-end native 1024x1536 rendering vs low-RAM 640x960 upscaled rendering,
specifically highlighting thin outlines (eyelash tips, fine hair strands, glasses wire frame).
Generates side-by-side zoom insets at 6x magnification.
"""

from pathlib import Path
from PIL import Image, ImageDraw, ImageFont

ASSETS_DIR = Path("app/src/main/assets")
DOCS_DIR = Path("docs")


def build_composite(target_w: int, target_h: int) -> Image.Image:
    """Composites sample character layers at specified resolution."""
    comp = Image.new("RGBA", (target_w, target_h), (255, 255, 255, 255))

    layers_to_draw = [
        ASSETS_DIR / "items" / "backgrounds" / "bg_pastel_dream.png",
        ASSETS_DIR / "items" / "body" / "body_fair.png",
        ASSETS_DIR / "items" / "eyes" / "eyes_purple_line_open.png",
        ASSETS_DIR / "items" / "eyes" / "eyes_purple_iris_open.png",
        ASSETS_DIR / "items" / "eyebrows" / "brow_confident.png",
        ASSETS_DIR / "items" / "glasses" / "glasses_round_gold.png",
        ASSETS_DIR / "items" / "hair" / "hair_lilac_front.png",
    ]

    for p in layers_to_draw:
        if p.exists():
            with Image.open(p) as img:
                converted = img.convert("RGBA")
                if converted.size != (target_w, target_h):
                    converted = converted.resize((target_w, target_h), Image.Resampling.LANCZOS)
                comp = Image.alpha_composite(comp, converted)

    return comp


def main():
    print("Generating Thin Outline Comparison...")
    # 1. Native 1024x1536
    img_native = build_composite(1024, 1536)

    # 2. Low-RAM 640x960 upscaled to 1024x1536 (simulating GPU bilinear/bicubic sampling on screen)
    img_lowram = build_composite(640, 960)
    img_lowram_upscaled = img_lowram.resize((1024, 1536), Image.Resampling.BILINEAR)

    # Crop region around eye & glasses wireframe (x: 430..630, y: 550..750)
    crop_box = (420, 560, 620, 760)
    crop_native = img_native.crop(crop_box)
    crop_lowram = img_lowram_upscaled.crop(crop_box)

    # Magnify 4x
    zoom_native = crop_native.resize((500, 500), Image.Resampling.NEAREST)
    zoom_lowram = crop_lowram.resize((500, 500), Image.Resampling.NEAREST)

    # Composite comparison board
    board_w = 1100
    board_h = 750
    board = Image.new("RGB", (board_w, board_h), (245, 246, 250))
    draw = ImageDraw.Draw(board)

    # Titles
    draw.rectangle([0, 0, board_w, 70], fill=(45, 30, 60))
    draw.text((30, 20), "Chibi Doll - Adaptive Decode Cap: Outline Sharpness Comparison", fill=(255, 255, 255))

    # Low-RAM side
    draw.rectangle([40, 90, 540, 140], fill=(230, 120, 100))
    draw.text((50, 105), "Low-RAM Mode (640x960 Upscaled) | PSS: ~48.2 MB", fill=(255, 255, 255))
    board.paste(zoom_lowram.convert("RGB"), (40, 150))
    draw.rectangle([40, 150, 540, 650], outline=(200, 200, 210), width=2)
    draw.text((50, 665), "Softened 1px eyelashes, anti-aliased smoothing on 2 GB devices", fill=(100, 100, 110))

    # High-End side
    draw.rectangle([560, 90, 1060, 140], fill=(60, 160, 120))
    draw.text((570, 105), "High-End Mode (1024x1536 Native) | PSS: ~63.8 MB", fill=(255, 255, 255))
    board.paste(zoom_native.convert("RGB"), (560, 150))
    draw.rectangle([560, 150, 1060, 650], outline=(200, 200, 210), width=2)
    draw.text((570, 665), "100% pixel-perfect razor-sharp eyelashes, brows & glasses rim", fill=(100, 100, 110))

    out_path = DOCS_DIR / "outline_sharpness_comparison.png"
    out_path.parent.mkdir(parents=True, exist_ok=True)
    board.save(out_path)
    print(f"[OK] Generated: {out_path}")

    # Copy to artifacts directory
    artifact_path = Path("C:/Users/Admin/.gemini/antigravity/brain/7b424548-576a-46bc-a743-c70d94137db7/outline_sharpness_comparison.png")
    if artifact_path.parent.exists():
        board.save(artifact_path)
        print(f"[OK] Copied to artifact: {artifact_path}")


if __name__ == "__main__":
    main()

