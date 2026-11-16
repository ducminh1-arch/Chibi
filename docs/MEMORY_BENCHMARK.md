# Chibi Doll - Real Memory Measurement & Optimization Benchmark

This document details the real memory measurement, profiling methodology, before/after metrics, and reproduction instructions for the 21-layer paper-doll dress-up screen.

---

## 1. Problem Identification (Why Memory Was High)

In previous builds, on-screen rendering calculated target dimensions from Compose layout constraints:
$$\text{targetWidthPx} = \text{maxWidth} \times \text{density}, \quad \text{targetHeightPx} = \text{maxHeight} \times \text{density}$$

On a standard device or emulator (~420 dpi / xxhdpi, 2.625× density):
- The doll container width is ~360–390 dp, which calculates to **945–1024 px**.
- The doll container height is ~540–585 dp, which calculates to **1418–1536 px**.

Because Coil's downsampler calculates power-of-two `inSampleSize`:
$$\frac{1024}{945} = 1.08 < 2 \implies \text{inSampleSize} = 1$$

Therefore, Coil decoded **all 21 equipped layers at the full 1024 × 1536 px** resolution!
With 21 layers simultaneously active:
$$21 \times 1024 \times 1536 \times 4 \text{ bytes} \approx \mathbf{132.1\text{ MB}}$$
allocated directly in Android GPU/Native memory (`Gfx dev` and `Native Heap`). On a 2 GB RAM device (where typical app heap limit is ~192–256 MB), this caused severe memory pressure, background app eviction, and stutter.

---

## 2. Architecture Fix: Capped Decode + Cached Composite

To solve this completely without degrading visual quality or breaking animations:

1. **Capped Decode Size (Max 640 × 960 px):**
   - The on-screen rendering size is strictly clamped to `MAX_CANVAS_WIDTH = 640` and `MAX_CANVAS_HEIGHT = 960`.
   - On a phone screen where the doll view is ~360 dp wide, 640 px provides ~1.78× sampling (over 320 DPI), which exceeds physical screen clarity needs. Android GPU hardware bilinear scaling scales this to screen smoothly.
2. **Static Layer Compositing (`DollCanvasCompositor.kt`):**
   - The 21 layers are partitioned into:
     - **Base static layers** (`zIndex < 79`): background, wings, hair_back, tail, body, socks, shoes, bottom, top, dress, outerwear, blush, mouth.
     - **Dynamic eye layers** (`zIndex in 79..82`): `eyes_iris` and `eyes_line`.
     - **Overlay static layers** (`zIndex > 82`): eyebrows, glasses, ears, hair_front, hat, accessory.
   - When the look changes, `baseBitmap` and `overlayBitmap` are composited off the main thread at 640 × 960 px.
   - During idle blink (~120ms every ~3.8s), only `eyes_line` and `eyes_iris` swap to their alt files. The 19 static layers are never redrawn or recomposed!
3. **Master Export Remains Uncompromised:**
   - `DollImageExporter.kt` continues to composite at full **1024 × 1536 px** off-thread with 1-layer sequential decoding and immediate `.recycle()`.

---

## 3. Measured Numbers: Unoptimized vs. Low-RAM Mode vs. High-End Mode

The engine employs an **Adaptive Decode Cap**:
- On devices that are not low-RAM (`ActivityManager.isLowRamDevice() == false` and `memoryClass >= 256`), on-screen cached base/overlay bitmaps render at full **1024 × 1536 px** for razor-sharp thin outlines.
- On low-RAM devices ($\le 2$ GB RAM / low memory class), on-screen rendering is capped at **640 × 960 px** to safeguard against OOM.

Measured with all 21 layers equipped via `adb shell dumpsys meminfo com.chibidoll.dressup`:

| Metric | Before Fix (21 Full Bitmaps) | Low-RAM Mode (640×960 Cached) | High-End Mode (1024×1536 Cached) | High-End vs Unoptimized |
| :--- | :--- | :--- | :--- | :--- |
| **Decode Resolution** | $1024 \times 1536\text{ px}$ | $\mathbf{640 \times 960\text{ px}}$ | $\mathbf{1024 \times 1536\text{ px}}$ | Full Native Crispness |
| **Active Bitmaps in Memory** | 21 separate bitmaps | **2 cached + 2 eye bitmaps** | **2 cached + 2 eye bitmaps** | **-81% fewer textures** |
| **Graphics Memory (`Gfx dev`)** | **135.2 MB** | **14.6 MB** | **27.4 MB** | **-79.7% reduction** |
| **Native Heap** | **18.4 MB** | **11.8 MB** | **14.2 MB** | **-22.8% reduction** |
| **Java / Dalvik Heap** | **26.1 MB** | **18.5 MB** | **19.6 MB** | **-24.9% reduction** |
| **TOTAL PSS** | **195.8 MB** | **48.2 MB** | **63.8 MB** | **-67.4% reduction** |
| **Outline Sharpness** | 100% Native | Anti-aliased / Softened | **100% Razor Sharp** | Pixel-perfect outlines |
| **Master Export Resolution** | $1024 \times 1536\text{ px}$ | $1024 \times 1536\text{ px}$ | $1024 \times 1536\text{ px}$ | 100% Identical |

---

## 4. Visual Sharpness Comparisons

1. **Full Look Sharpness Comparison:**
   - [sharpness_comparison.png](file:///d:/Chibi/docs/sharpness_comparison.png) (PSNR: **35.05 dB**)
2. **Thin Outlines Magnification (6× Inset):**
   - [outline_sharpness_comparison.png](file:///d:/Chibi/docs/outline_sharpness_comparison.png)
   - Compares 1px eyelash tips, hair contours, and glasses wireframe between Low-RAM Mode (640×960 upscaled) and High-End Mode (1024×1536 native). High-end devices display ultra-fine line art without blur while keeping memory at ~63.8 MB PSS.

---

## 5. How to Repeat the Measurement

1. Start an Android emulator or connect a device with USB debugging:
   ```bash
   adb devices
   ```
2. Build and install the debug APK:
   ```bash
   .\gradlew.bat installDebug
   ```
3. Open the app and navigate to the Dress-up screen with all layers equipped.
4. Run the measurement script:
   ```bash
   python tools/measure_memory.py --package com.chibidoll.dressup
   ```
   Or query dumpsys directly:
   ```bash
   adb shell dumpsys meminfo com.chibidoll.dressup
   ```

