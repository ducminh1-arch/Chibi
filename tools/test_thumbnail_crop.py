#!/usr/bin/env python3
"""
Unit test for Task 2: Thumbnail fix
Verifies bounding box cropping, ~8% padding, 256x256 canvas centering,
and secondary layer compositing for small items.
"""

import sys
import unittest
from pathlib import Path
from PIL import Image, ImageDraw

# Add tools to path
sys.path.insert(0, str(Path(__file__).parent))
from asset_pipeline import crop_to_bounding_box_thumbnail

class TestThumbnailCrop(unittest.TestCase):

    def test_small_item_zoomed_prominently_in_thumbnail(self):
        """
        A small 120x40 item (e.g. glasses/hairclip) on a 1024x1536 canvas
        must NOT be shrunken to ~20px, but zoomed to fill ~220-245px of the 256x256 thumbnail.
        """
        # Create full 1024x1536 canvas
        canvas = Image.new("RGBA", (1024, 1536), (0, 0, 0, 0))
        draw = ImageDraw.Draw(canvas)

        # Draw a small pair of glasses in the upper face region
        # Coordinates: x: 450..570 (width: 120), y: 480..520 (height: 40)
        draw.rectangle([450, 480, 570, 520], fill=(255, 100, 150, 255))

        thumb = crop_to_bounding_box_thumbnail(canvas, max_dim=256, padding_pct=0.08)

        # 1. Dimensions must be 256x256
        self.assertEqual(thumb.size, (256, 256))

        # 2. Check bounding box in the resulting thumbnail
        bbox = thumb.getbbox()
        self.assertIsNotNone(bbox, "Thumbnail must not be empty")
        left, top, right, bottom = bbox
        thumb_w = right - left
        thumb_h = bottom - top

        # Item width was 120, height 40 (aspect ratio 3:1).
        # When scaled with 8% padding to fit inside 256:
        # width should occupy ~215 - 245 pixels of the 256 width!
        self.assertTrue(
            thumb_w >= 210,
            f"Expected item width in thumbnail to be >= 210px (zoomed), but got {thumb_w}px"
        )
        self.assertTrue(
            thumb_w <= 245,
            f"Expected item width with 8% padding to be <= 245px, but got {thumb_w}px"
        )

        # Centering check: horizontal margins should be roughly equal
        margin_left = left
        margin_right = 256 - right
        self.assertTrue(
            abs(margin_left - margin_right) <= 2,
            f"Thumbnail should be centered horizontally: left={margin_left}, right={margin_right}"
        )

    def test_secondary_layer_composite_thumbnail(self):
        """
        For dual-layer items (e.g. hair_back + hair_front), compositing before
        cropping captures the full silhouette of both layers.
        """
        # Hair back: wide ponytails at bottom-left and bottom-right
        back = Image.new("RGBA", (1024, 1536), (0, 0, 0, 0))
        draw_b = ImageDraw.Draw(back)
        draw_b.rectangle([200, 600, 300, 900], fill=(150, 100, 220, 255))
        draw_b.rectangle([724, 600, 824, 900], fill=(150, 100, 220, 255))

        # Hair front: center bangs at top
        front = Image.new("RGBA", (1024, 1536), (0, 0, 0, 0))
        draw_f = ImageDraw.Draw(front)
        draw_f.rectangle([450, 350, 574, 450], fill=(180, 130, 250, 255))

        # Composite together
        comp = Image.alpha_composite(back, front)
        thumb = crop_to_bounding_box_thumbnail(comp, max_dim=256, padding_pct=0.08)

        self.assertEqual(thumb.size, (256, 256))
        bbox = thumb.getbbox()
        self.assertIsNotNone(bbox)

        # Composite width spans from x=200 to x=824 (width: 624)
        # Height spans from y=350 to y=900 (height: 550)
        # In thumbnail, both width and height should occupy substantial portion of 256
        tw = bbox[2] - bbox[0]
        th = bbox[3] - bbox[1]
        self.assertTrue(tw >= 210, f"Expected composite width >= 210, got {tw}")
        self.assertTrue(th >= 180, f"Expected composite height >= 180, got {th}")

    def test_background_preserves_full_bleed(self):
        """Backgrounds are full 1024x1536 scenes and must scale without clipping."""
        bg = Image.new("RGB", (1024, 1536), (180, 220, 240))
        thumb = crop_to_bounding_box_thumbnail(bg, max_dim=256, padding_pct=0.08, is_background=True)
        self.assertEqual(thumb.size, (256, 256))

if __name__ == "__main__":
    unittest.main()

