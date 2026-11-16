import Foundation
import UIKit
import Photos

/// Exporter service that renders full-resolution 1024x1536 master images, generates thumbnails,
/// and saves dolls to the iOS Photos library.
/// Replicates Android `DollImageExporter.kt` 1:1.
public final class DollImageExporter: Sendable {
    public static let shared = DollImageExporter()

    public static let masterWidth: CGFloat = 1024
    public static let masterHeight: CGFloat = 1536
    public static let thumbnailWidth: CGFloat = 341
    public static let thumbnailHeight: CGFloat = 512

    public init() {}

    /// Renders master 1024x1536 UIImage using the provided image loader closure.
    /// Can be tested in unit tests with synthetic in-memory images.
    public func renderMasterImageWithLoader(
        look: CharacterLook,
        imageLoader: (String) -> UIImage?
    ) -> UIImage {
        let masterSize = CGSize(width: Self.masterWidth, height: Self.masterHeight)
        let masterRect = CGRect(origin: .zero, size: masterSize)

        let renderer = UIGraphicsImageRenderer(size: masterSize)
        return renderer.image { ctx in
            // 1. Render Background
            var bgDrawn = false
            if let bg = look.backgroundItem, !bg.file.isEmpty {
                if let bgImage = imageLoader(bg.file) {
                    bgImage.draw(in: masterRect)
                    bgDrawn = true
                }
            }
            if !bgDrawn {
                drawDefaultBackground(in: masterRect, context: ctx.cgContext)
            }

            // 2. Render Layers in strict z-order with multiply tint support
            let detailedLayers = look.getDetailedRenderLayers()
            for layerInfo in detailedLayers {
                let filePath = layerInfo.filePath
                guard !filePath.isEmpty, let layerImage = imageLoader(filePath) else {
                    continue
                }

                if layerInfo.isColorable, let tint = layerInfo.colorTint {
                    let tinted = DollCanvasCompositor.tintImage(layerImage, tintColor: tint)
                    tinted.draw(in: masterRect)
                } else {
                    layerImage.draw(in: masterRect)
                }
            }
        }
    }

    /// Renders master image from disk/bundle via `ImageCache`.
    public func renderMasterImage(
        look: CharacterLook,
        imageCache: ImageCache = .shared
    ) -> UIImage {
        renderMasterImageWithLoader(look: look) { path in
            imageCache.loadFullImage(relativePath: path)
        }
    }

    /// Creates a 341x512 thumbnail from the master image.
    public func createThumbnail(masterImage: UIImage) -> UIImage {
        let targetSize = CGSize(width: Self.thumbnailWidth, height: Self.thumbnailHeight)
        let renderer = UIGraphicsImageRenderer(size: targetSize)
        return renderer.image { _ in
            masterImage.draw(in: CGRect(origin: .zero, size: targetSize))
        }
    }

    /// Default gradient background when no background is selected.
    private func drawDefaultBackground(in rect: CGRect, context: CGContext) {
        let colors = [
            UIColor(red: 1.0, green: 0.949, blue: 0.965, alpha: 1.0).cgColor, // #FFF2F6
            UIColor(red: 0.922, green: 0.953, blue: 0.980, alpha: 1.0).cgColor  // #EBF3FA
        ] as CFArray
        let colorSpace = CGColorSpaceCreateDeviceRGB()
        if let gradient = CGGradient(colorsSpace: colorSpace, colors: colors, locations: [0.0, 1.0]) {
            context.drawLinearGradient(
                gradient,
                start: CGPoint(x: rect.midX, y: rect.minY),
                end: CGPoint(x: rect.midX, y: rect.maxY),
                options: []
            )
        }
    }

    /// Saves master image to iOS Photos library without requiring full read permissions on iOS 14+.
    public func saveToPhotoLibrary(image: UIImage) async throws {
        try await PHPhotoLibrary.shared().performChanges {
            PHAssetChangeRequest.creationRequestForAsset(from: image)
        }
    }
}

