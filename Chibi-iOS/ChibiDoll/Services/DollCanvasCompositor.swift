import Foundation
import UIKit

/// High-performance on-screen layer compositor replicating Android's `DollCanvasCompositor.kt`.
/// - Adaptive resolution: 1024x1536 on high-RAM devices, 640x960 on low-RAM devices.
/// - Two-pass composite:
///   1. `compositeBaseLayers`: Composites background and layers with zIndex < 79 into `baseImage`.
///   2. `compositeOverlayLayers`: Composites layers with zIndex > 82 into `overlayImage`.
/// - Dynamic eye layers (79 <= zIndex <= 82) are kept separate so blinking does not recomposite static layers.
public final class DollCanvasCompositor: Sendable {
    public static let shared = DollCanvasCompositor()

    public static let lowRamCanvasWidth: Int = 640
    public static let lowRamCanvasHeight: Int = 960
    public static let highResCanvasWidth: Int = 1024
    public static let highResCanvasHeight: Int = 1536

    public init() {}

    /// Checks if device has >= 3GB RAM for high-resolution compositing.
    public static func isHighEndDevice() -> Bool {
        ProcessInfo.processInfo.physicalMemory >= 3_000_000_000
    }

    /// Returns max dimensions for on-screen composite caching.
    public static func maxCanvasDimensions() -> (width: Int, height: Int) {
        if isHighEndDevice() {
            return (highResCanvasWidth, highResCanvasHeight)
        } else {
            return (lowRamCanvasWidth, lowRamCanvasHeight)
        }
    }

    /// Composites base layers (background + zIndex < 79) into a single UIImage.
    public func compositeBaseLayers(
        look: CharacterLook,
        imageCache: ImageCache = .shared,
        targetWidth: Int = lowRamCanvasWidth,
        targetHeight: Int = lowRamCanvasHeight
    ) -> UIImage? {
        let maxDim = Self.maxCanvasDimensions()
        let width = min(targetWidth, maxDim.width)
        let height = min(targetHeight, maxDim.height)
        let canvasSize = CGSize(width: width, height: height)
        let canvasRect = CGRect(origin: .zero, size: canvasSize)

        let renderer = UIGraphicsImageRenderer(size: canvasSize)
        return renderer.image { _ in
            // 1. Draw background
            if let bg = look.backgroundItem, !bg.file.isEmpty {
                if let bgImg = imageCache.loadDownsampledImage(relativePath: bg.file, maxPixelSize: max(width, height)) {
                    bgImg.draw(in: canvasRect)
                }
            }

            // 2. Draw static layers with zIndex < 79
            let allLayers = look.getDetailedRenderLayers()
            let baseLayers = allLayers.filter { $0.zIndex < 79 }

            for layerInfo in baseLayers {
                drawLayer(layerInfo: layerInfo, in: canvasRect, imageCache: imageCache, maxPixelSize: max(width, height))
            }
        }
    }

    /// Composites overlay layers (zIndex > 82) into a single UIImage.
    public func compositeOverlayLayers(
        look: CharacterLook,
        imageCache: ImageCache = .shared,
        targetWidth: Int = lowRamCanvasWidth,
        targetHeight: Int = lowRamCanvasHeight
    ) -> UIImage? {
        let allLayers = look.getDetailedRenderLayers()
        let overlayLayers = allLayers.filter { $0.zIndex > 82 }
        if overlayLayers.isEmpty {
            return nil
        }

        let maxDim = Self.maxCanvasDimensions()
        let width = min(targetWidth, maxDim.width)
        let height = min(targetHeight, maxDim.height)
        let canvasSize = CGSize(width: width, height: height)
        let canvasRect = CGRect(origin: .zero, size: canvasSize)

        let renderer = UIGraphicsImageRenderer(size: canvasSize)
        return renderer.image { _ in
            for layerInfo in overlayLayers {
                drawLayer(layerInfo: layerInfo, in: canvasRect, imageCache: imageCache, maxPixelSize: max(width, height))
            }
        }
    }

    private func drawLayer(
        layerInfo: RenderLayerInfo,
        in rect: CGRect,
        imageCache: ImageCache,
        maxPixelSize: Int
    ) {
        guard !layerInfo.filePath.isEmpty,
              let rawImage = imageCache.loadDownsampledImage(relativePath: layerInfo.filePath, maxPixelSize: maxPixelSize) else {
            return
        }

        if layerInfo.isColorable, let tint = layerInfo.colorTint {
            let tintedImage = Self.tintImage(rawImage, tintColor: tint)
            tintedImage.draw(in: rect)
        } else {
            rawImage.draw(in: rect)
        }
    }

    /// Tints an image using Multiply blend mode, matching Android's PorterDuffColorFilter(color, MULTIPLY).
    public static func tintImage(_ image: UIImage, tintColor: UInt64) -> UIImage {
        let size = image.size
        let renderer = UIGraphicsImageRenderer(size: size)
        return renderer.image { ctx in
            let rect = CGRect(origin: .zero, size: size)
            image.draw(in: rect)

            let a = CGFloat((tintColor >> 24) & 0xFF) / 255.0
            let r = CGFloat((tintColor >> 16) & 0xFF) / 255.0
            let g = CGFloat((tintColor >> 8) & 0xFF) / 255.0
            let b = CGFloat(tintColor & 0xFF) / 255.0
            let color = UIColor(red: r, green: g, blue: b, alpha: a)

            let cgCtx = ctx.cgContext
            cgCtx.saveGState()
            color.setFill()
            UIRectFillUsingBlendMode(rect, .multiply)
            cgCtx.restoreGState()

            // Mask back to the source image's alpha to preserve transparency
            cgCtx.setBlendMode(.destinationIn)
            image.draw(in: rect)
        }
    }
}

