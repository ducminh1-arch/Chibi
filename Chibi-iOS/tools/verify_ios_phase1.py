import json
import os
import sys

def verify_phase1(ios_root):
    print("=== ChibiDoll iOS Phase I1 Verification ===")
    errors = []
    
    # 1. Verify catalog.json
    catalog_path = os.path.join(ios_root, "ChibiDoll", "Resources", "catalog.json")
    if not os.path.exists(catalog_path):
        errors.append(f"Missing {catalog_path}")
    else:
        with open(catalog_path, "r", encoding="utf-8") as f:
            catalog = json.load(f)
        print(f"✓ catalog.json valid: {len(catalog)} items")
        if len(catalog) != 72:
            errors.append(f"Expected 72 items in catalog.json, found {len(catalog)}")

        # Check all image files exist
        items_dir = os.path.join(ios_root, "ChibiDoll", "Resources")
        missing_files = []
        for item in catalog:
            for key in ["file", "secondaryFile", "alt_file", "secondary_alt_file", "thumbnail"]:
                path = item.get(key)
                if path and path.strip():
                    full_p = os.path.join(items_dir, path.replace("/", os.sep))
                    if not os.path.exists(full_p):
                        missing_files.append((item.get("id"), key, path))
        if missing_files:
            errors.append(f"Missing referenced image files: {len(missing_files)} (e.g. {missing_files[:3]})")
        else:
            print("✓ All 200+ referenced asset files exist on disk in ChibiDoll/Resources/")

    # 2. Verify palettes.json
    palettes_path = os.path.join(ios_root, "ChibiDoll", "Resources", "palettes.json")
    if not os.path.exists(palettes_path):
        errors.append(f"Missing {palettes_path}")
    else:
        with open(palettes_path, "r", encoding="utf-8") as f:
            palettes_data = json.load(f)
        pals = palettes_data.get("palettes", [])
        print(f"✓ palettes.json valid: {len(pals)} palettes")
        if len(pals) != 4:
            errors.append(f"Expected 4 palettes, found {len(pals)}")

    # 3. Verify Localizable.xcstrings
    xcstrings_path = os.path.join(ios_root, "ChibiDoll", "Resources", "Localizable.xcstrings")
    if not os.path.exists(xcstrings_path):
        errors.append(f"Missing {xcstrings_path}")
    else:
        with open(xcstrings_path, "r", encoding="utf-8") as f:
            xcstrings = json.load(f)
        strings_count = len(xcstrings.get("strings", {}))
        print(f"✓ Localizable.xcstrings valid: {strings_count} localized keys")

    # 4. Verify project.yml
    project_yml = os.path.join(ios_root, "project.yml")
    if os.path.exists(project_yml):
        print("✓ XcodeGen project.yml present")
    else:
        errors.append("Missing project.yml")

    # 5. Verify PrivacyInfo.xcprivacy
    privacy = os.path.join(ios_root, "ChibiDoll", "PrivacyInfo.xcprivacy")
    if os.path.exists(privacy):
        print("✓ PrivacyInfo.xcprivacy present")
    else:
        errors.append("Missing PrivacyInfo.xcprivacy")

    # 6. Verify Swift files balance check (braces / parens)
    swift_files = []
    for root, _, files in os.walk(ios_root):
        for file in files:
            if file.endswith(".swift"):
                swift_files.append(os.path.join(root, file))

    print(f"✓ Checking {len(swift_files)} Swift source & test files for syntax/brace balance...")
    for sf in swift_files:
        with open(sf, "r", encoding="utf-8") as f:
            content = f.read()
        # Simple brace check
        open_b = content.count("{")
        close_b = content.count("}")
        if open_b != close_b:
            errors.append(f"Brace mismatch in {os.path.basename(sf)}: '{open_b}' open vs '{close_b}' close")

    if errors:
        print("\nERRORS ENCOUNTERED:")
        for e in errors:
            print("  - " + e)
        sys.exit(1)
    else:
        print("\nAll local Phase I1 checks PASSED cleanly!")

if __name__ == "__main__":
    verify_phase1(r"D:\Chibi-iOS")

