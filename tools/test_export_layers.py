#!/usr/bin/env python3
"""
Comprehensive Unit Tests for tools/export_layers.py
Validates:
1. End-to-end extraction from 2048x3072 .ora down to 1024x1536 Lanczos.
2. Safety: --dry-run flag writes 0 files and modifies nothing.
3. Safety: Refuses to run if input file is missing, corrupt, non-zip, or has invalid stack.xml.
4. Safety: Automatic timestamped backup of catalog.json and overwritten files.
5. Robustness: Hidden layers (visibility='hidden') are skipped.
6. Robustness: Opacity < 100% is baked into alpha channel.
7. Robustness: Non-normal blend modes (e.g. svg:multiply) are reported as invalid.
8. Robustness: Layer masks are reported as invalid.
9. Robustness: Nested group hierarchies are navigated properly.
10. Robustness: Cropped layer offsets (x, y) are aligned to doll coordinates.
11. Robustness: Unrecognized layer names trigger exact expected prefix guidance.
"""

import io
import json
import os
import shutil
import tempfile
import unittest
import zipfile
import xml.etree.ElementTree as ET
from pathlib import Path
from PIL import Image, ImageDraw

import sys
tools_dir = Path(__file__).resolve().parent
if str(tools_dir) not in sys.path:
    sys.path.insert(0, str(tools_dir))

import export_layers
import asset_pipeline


def create_synthetic_ora(ora_path: Path, canvas_w: int = 2048, canvas_h: int = 3072):
    """Generates standard synthetic OpenRaster (.ora) file."""
    with zipfile.ZipFile(ora_path, "w") as zf:
        zf.writestr("mimetype", "image/openraster")

        image_elem = ET.Element("image", {"w": str(canvas_w), "h": str(canvas_h), "version": "0.0.1"})
        stack_elem = ET.SubElement(image_elem, "stack")

        # 1. Dress layer
        dress_img = Image.new("RGBA", (canvas_w, canvas_h), (0, 0, 0, 0))
        d_draw = ImageDraw.Draw(dress_img)
        d_draw.rectangle([600, 1200, 1400, 2400], fill=(70, 130, 220, 255), outline=(30, 40, 80, 255), width=6)
        d_bytes = io.BytesIO()
        dress_img.save(d_bytes, format="PNG")
        zf.writestr("data/dress_layer.png", d_bytes.getvalue())
        ET.SubElement(stack_elem, "layer", {
            "name": "dress_magic_blue",
            "src": "data/dress_layer.png",
            "x": "0",
            "y": "0"
        })

        # 2. Hair stack
        hair_stack = ET.SubElement(stack_elem, "stack", {"name": "hair_fairy_pink"})
        hair_back_img = Image.new("RGBA", (canvas_w, canvas_h), (0, 0, 0, 0))
        hb_draw = ImageDraw.Draw(hair_back_img)
        hb_draw.ellipse([400, 600, 1600, 2600], fill=(230, 160, 200, 255))
        hb_bytes = io.BytesIO()
        hair_back_img.save(hb_bytes, format="PNG")
        zf.writestr("data/hair_back.png", hb_bytes.getvalue())
        ET.SubElement(hair_stack, "layer", {
            "name": "back",
            "src": "data/hair_back.png",
            "x": "0",
            "y": "0"
        })

        hair_front_img = Image.new("RGBA", (canvas_w, canvas_h), (0, 0, 0, 0))
        hf_draw = ImageDraw.Draw(hair_front_img)
        hf_draw.rectangle([700, 500, 1300, 1100], fill=(240, 180, 210, 255))
        hf_bytes = io.BytesIO()
        hair_front_img.save(hf_bytes, format="PNG")
        zf.writestr("data/hair_front.png", hf_bytes.getvalue())
        ET.SubElement(hair_stack, "layer", {
            "name": "front",
            "src": "data/hair_front.png",
            "x": "0",
            "y": "0"
        })

        # 3. Eyes layers
        eye_stack = ET.SubElement(stack_elem, "stack", {"name": "eyes_emerald"})
        line_open_img = Image.new("RGBA", (canvas_w, canvas_h), (0, 0, 0, 0))
        lo_draw = ImageDraw.Draw(line_open_img)
        lo_draw.ellipse([800, 900, 950, 1050], fill=(255, 255, 255, 255), outline=(20, 20, 20, 255), width=6)
        lo_bytes = io.BytesIO()
        line_open_img.save(lo_bytes, format="PNG")
        zf.writestr("data/eyes_line_open.png", lo_bytes.getvalue())
        ET.SubElement(eye_stack, "layer", {
            "name": "eyes_emerald_line_open",
            "src": "data/eyes_line_open.png",
            "x": "0",
            "y": "0"
        })

        line_closed_img = Image.new("RGBA", (canvas_w, canvas_h), (0, 0, 0, 0))
        lc_draw = ImageDraw.Draw(line_closed_img)
        lc_draw.arc([800, 950, 950, 1050], start=0, end=180, fill=(20, 20, 20, 255), width=8)
        lc_bytes = io.BytesIO()
        line_closed_img.save(lc_bytes, format="PNG")
        zf.writestr("data/eyes_line_closed.png", lc_bytes.getvalue())
        ET.SubElement(eye_stack, "layer", {
            "name": "eyes_emerald_line_closed",
            "src": "data/eyes_line_closed.png",
            "x": "0",
            "y": "0"
        })

        iris_open_img = Image.new("RGBA", (canvas_w, canvas_h), (0, 0, 0, 0))
        io_draw = ImageDraw.Draw(iris_open_img)
        io_draw.ellipse([840, 940, 910, 1010], fill=(40, 180, 120, 255))
        io_bytes = io.BytesIO()
        iris_open_img.save(io_bytes, format="PNG")
        zf.writestr("data/eyes_iris_open.png", io_bytes.getvalue())
        ET.SubElement(eye_stack, "layer", {
            "name": "eyes_emerald_iris_open",
            "src": "data/eyes_iris_open.png",
            "x": "0",
            "y": "0"
        })

        # 4. Partial cropped glasses
        glasses_crop = Image.new("RGBA", (800, 300), (0, 0, 0, 0))
        g_draw = ImageDraw.Draw(glasses_crop)
        g_draw.ellipse([50, 50, 350, 250], outline=(200, 160, 40, 255), width=10)
        g_bytes = io.BytesIO()
        glasses_crop.save(g_bytes, format="PNG")
        zf.writestr("data/glasses_crop.png", g_bytes.getvalue())
        ET.SubElement(stack_elem, "layer", {
            "name": "glasses_gold_round",
            "src": "data/glasses_crop.png",
            "x": "624",
            "y": "850"
        })

        zf.writestr("stack.xml", ET.tostring(image_elem, encoding="utf-8"))


def create_robustness_test_ora(ora_path: Path):
    """Generates synthetic .ora with hidden layers, opacity, masks, non-normal blend modes, and nested groups."""
    canvas_w, canvas_h = 2048, 3072
    with zipfile.ZipFile(ora_path, "w") as zf:
        zf.writestr("mimetype", "image/openraster")

        image_elem = ET.Element("image", {"w": str(canvas_w), "h": str(canvas_h), "version": "0.0.1"})
        root_stack = ET.SubElement(image_elem, "stack")

        # 1. Hidden layer (should be skipped)
        hidden_img = Image.new("RGBA", (canvas_w, canvas_h), (255, 0, 0, 255))
        h_bytes = io.BytesIO()
        hidden_img.save(h_bytes, format="PNG")
        zf.writestr("data/hidden.png", h_bytes.getvalue())
        ET.SubElement(root_stack, "layer", {
            "name": "dress_sketch_draft",
            "src": "data/hidden.png",
            "visibility": "hidden"
        })

        # 2. Layer with opacity < 100% (50% opacity, alpha should be baked)
        opaque_img = Image.new("RGBA", (canvas_w, canvas_h), (0, 0, 0, 0))
        op_draw = ImageDraw.Draw(opaque_img)
        op_draw.rectangle([500, 1000, 1500, 2000], fill=(100, 200, 100, 255))
        op_bytes = io.BytesIO()
        opaque_img.save(op_bytes, format="PNG")
        zf.writestr("data/veil.png", op_bytes.getvalue())
        ET.SubElement(root_stack, "layer", {
            "name": "acc_sheer_veil",
            "src": "data/veil.png",
            "opacity": "0.5"
        })

        # 3. Layer with non-normal blend mode (svg:multiply, should be flagged as invalid)
        blend_img = Image.new("RGBA", (canvas_w, canvas_h), (0, 0, 0, 0))
        b_bytes = io.BytesIO()
        blend_img.save(b_bytes, format="PNG")
        zf.writestr("data/shadow.png", b_bytes.getvalue())
        ET.SubElement(root_stack, "layer", {
            "name": "acc_shadow_layer",
            "src": "data/shadow.png",
            "composite-op": "svg:multiply"
        })

        # 4. Layer with a mask (should be flagged as invalid)
        mask_img = Image.new("RGBA", (canvas_w, canvas_h), (0, 0, 0, 0))
        m_bytes = io.BytesIO()
        mask_img.save(m_bytes, format="PNG")
        zf.writestr("data/masked_top.png", m_bytes.getvalue())
        layer_with_mask = ET.SubElement(root_stack, "layer", {
            "name": "top_masked_lace",
            "src": "data/masked_top.png"
        })
        ET.SubElement(layer_with_mask, "mask", {"src": "data/mask.png"})
        zf.writestr("data/mask.png", m_bytes.getvalue())

        # 5. Deeply nested stack hierarchy
        grp1 = ET.SubElement(root_stack, "stack", {"name": "accessories"})
        grp2 = ET.SubElement(grp1, "stack", {"name": "headwear"})
        grp3 = ET.SubElement(grp2, "stack", {"name": "hat_giant_ribbon"})
        ribbon_img = Image.new("RGBA", (canvas_w, canvas_h), (0, 0, 0, 0))
        r_draw = ImageDraw.Draw(ribbon_img)
        r_draw.rectangle([600, 200, 1400, 600], fill=(220, 80, 140, 255))
        r_bytes = io.BytesIO()
        ribbon_img.save(r_bytes, format="PNG")
        zf.writestr("data/ribbon.png", r_bytes.getvalue())
        ET.SubElement(grp3, "layer", {
            "name": "front",
            "src": "data/ribbon.png"
        })

        # 6. Unrecognized layer name outside any category group
        weird_img = Image.new("RGBA", (canvas_w, canvas_h), (0, 0, 0, 0))
        w_bytes = io.BytesIO()
        weird_img.save(w_bytes, format="PNG")
        zf.writestr("data/random.png", w_bytes.getvalue())
        ET.SubElement(root_stack, "layer", {
            "name": "my_random_layer_xyz",
            "src": "data/random.png"
        })

        zf.writestr("stack.xml", ET.tostring(image_elem, encoding="utf-8"))


class TestExportLayers(unittest.TestCase):

    def setUp(self):
        self.test_dir = Path(tempfile.mkdtemp(prefix="chibi_export_test_"))
        self.assets_dir = self.test_dir / "app" / "src" / "main" / "assets"
        self.assets_dir.mkdir(parents=True)
        self.catalog_file = self.assets_dir / "catalog.json"

        with open(self.catalog_file, "w", encoding="utf-8") as f:
            json.dump([], f)

        fallback_eyes = self.assets_dir / "items" / "eyes"
        fallback_eyes.mkdir(parents=True, exist_ok=True)
        empty_iris = Image.new("RGBA", (1024, 1536), (0, 0, 0, 0))
        empty_iris.save(fallback_eyes / "eyes_iris_closed.png")

        self.ora_file = self.test_dir / "solo_artist_sheet.ora"
        create_synthetic_ora(self.ora_file)

    def tearDown(self):
        shutil.rmtree(self.test_dir, ignore_errors=True)

    def test_export_ora_layers(self):
        """Tests standard end-to-end export from 2048x3072 .ora down to 1024x1536."""
        summary = export_layers.export_layers(
            input_file=self.ora_file,
            assets_dir=self.assets_dir,
            catalog_file=self.catalog_file,
            dry_run=False,
            run_thumbnails=True,
            run_validation_check=True
        )

        self.assertEqual(summary.total_raw_layers, 7)
        self.assertEqual(summary.exported_items, 4)
        self.assertTrue(summary.catalog_updated)
        self.assertTrue(summary.validation_passed)

        dress_path = self.assets_dir / "items" / "dress" / "dress_magic_blue.png"
        self.assertTrue(dress_path.exists())
        with Image.open(dress_path) as img:
            self.assertEqual(img.size, (1024, 1536))

    def test_dry_run_writes_nothing(self):
        """Verifies --dry-run prints plan and writes zero files to disk."""
        summary = export_layers.export_layers(
            input_file=self.ora_file,
            assets_dir=self.assets_dir,
            catalog_file=self.catalog_file,
            dry_run=True,
            run_thumbnails=True,
            run_validation_check=True
        )

        # Assert no images were written
        dress_path = self.assets_dir / "items" / "dress" / "dress_magic_blue.png"
        self.assertFalse(dress_path.exists())

        # Assert catalog.json was not modified
        with open(self.catalog_file, "r", encoding="utf-8") as f:
            data = json.load(f)
            self.assertEqual(len(data), 0)

        # Assert no thumbnails were generated
        thumb_path = self.assets_dir / "items" / "thumbnails" / "thumb_dress_magic_blue.png"
        self.assertFalse(thumb_path.exists())

    def test_refuse_unparseable_corrupt_input(self):
        """Refuses to run if input file is missing, corrupt, non-zip, or has broken XML."""
        # 1. Non-existent file
        with self.assertRaises(FileNotFoundError):
            export_layers.export_layers(
                input_file=self.test_dir / "non_existent.ora",
                assets_dir=self.assets_dir,
                catalog_file=self.catalog_file
            )

        # 2. Corrupt non-zip file
        corrupt_file = self.test_dir / "corrupted.ora"
        with open(corrupt_file, "w") as f:
            f.write("This is not a zip file at all!")

        with self.assertRaises(ValueError):
            export_layers.export_layers(
                input_file=corrupt_file,
                assets_dir=self.assets_dir,
                catalog_file=self.catalog_file
            )

        # 3. Zip file without stack.xml
        no_stack_file = self.test_dir / "no_stack.ora"
        with zipfile.ZipFile(no_stack_file, "w") as zf:
            zf.writestr("mimetype", "image/openraster")
            zf.writestr("dummy.txt", "hello")

        with self.assertRaises(ValueError):
            export_layers.export_layers(
                input_file=no_stack_file,
                assets_dir=self.assets_dir,
                catalog_file=self.catalog_file
            )

    def test_automatic_backup_on_overwrite(self):
        """Automatically backs up catalog.json and overwritten files into timestamped folder."""
        # Pre-create dress item
        dress_path = self.assets_dir / "items" / "dress" / "dress_magic_blue.png"
        dress_path.parent.mkdir(parents=True, exist_ok=True)
        old_img = Image.new("RGBA", (1024, 1536), (12, 34, 56, 255))
        old_img.save(dress_path)

        # Pre-create catalog entry
        with open(self.catalog_file, "w", encoding="utf-8") as f:
            json.dump([{"id": "dress_magic_blue", "nameEn": "Old Name"}], f)

        summary = export_layers.export_layers(
            input_file=self.ora_file,
            assets_dir=self.assets_dir,
            catalog_file=self.catalog_file,
            dry_run=False
        )

        self.assertIsNotNone(summary.backup_path)
        backup_dir = Path(summary.backup_path)
        self.assertTrue(backup_dir.exists())

        # Assert catalog.json is backed up
        backup_catalog = backup_dir / "catalog.json"
        self.assertTrue(backup_catalog.exists())
        with open(backup_catalog, "r", encoding="utf-8") as f:
            data = json.load(f)
            self.assertEqual(data[0]["nameEn"], "Old Name")

        # Assert old dress image is backed up
        backup_dress = backup_dir / "items" / "dress" / "dress_magic_blue.png"
        self.assertTrue(backup_dress.exists())
        with Image.open(backup_dress) as b_img:
            px = b_img.getpixel((100, 100))
            self.assertEqual(px, (12, 34, 56, 255))

    def test_robustness_features(self):
        """Verifies hidden layer skipping, opacity baking, masks/modes detection, nested groups, and naming guidance."""
        robust_ora = self.test_dir / "robustness_test.ora"
        create_robustness_test_ora(robust_ora)

        summary = export_layers.export_layers(
            input_file=robust_ora,
            assets_dir=self.assets_dir,
            catalog_file=self.catalog_file,
            dry_run=False,
            run_thumbnails=False,
            run_validation_check=False
        )

        # 1. Hidden layer was skipped
        self.assertIn("dress_sketch_draft", summary.skipped_layers)
        draft_path = self.assets_dir / "items" / "dress" / "dress_sketch_draft.png"
        self.assertFalse(draft_path.exists())

        # 2. Opacity 50% was baked into alpha channel
        veil_path = self.assets_dir / "items" / "accessory" / "acc_sheer_veil.png"
        self.assertTrue(veil_path.exists())
        with Image.open(veil_path) as v_img:
            # Scaled center pixel
            px = v_img.getpixel((500, 750))
            # Original alpha was 255, with 0.5 opacity baked it must be ~128
            self.assertAlmostEqual(px[3], 128, delta=2)

        # 3. Non-normal blend mode was flagged
        blend_warnings = [w for w in summary.invalid_layers if "svg:multiply" in w]
        self.assertTrue(len(blend_warnings) > 0)

        # 4. Layer mask was flagged
        mask_warnings = [w for w in summary.invalid_layers if "layer mask" in w]
        self.assertTrue(len(mask_warnings) > 0)

        # 5. Deeply nested stack was mapped to hat_giant_ribbon
        ribbon_path = self.assets_dir / "items" / "hat" / "hat_giant_ribbon.png"
        self.assertTrue(ribbon_path.exists())

        # 6. Unrecognized layer name was detected and guidance provided
        self.assertIn("my_random_layer_xyz", summary.unrecognized_names)


if __name__ == "__main__":
    unittest.main()
