import Foundation
import UIKit
import ImageIO

/// Memory-efficient image loader and cache using `NSCache` and `CGImageSource` downsampling.
/// Prevents high memory pressure during on-screen paper-doll layering.
public final class ImageCache: @unchecked Sendable {
    public static let shared = ImageCache()

    private let cache = NSCache<NSString, UIImage>()
    private let bundle: Bundle

    public init(bundle: Bundle = .main, countLimit: Int = 200, totalCostLimit: Int = 80 * 1024 * 1024) {
        self.bundle = bundle
        self.cache.countLimit = countLimit
        self.cache.totalCostLimit = totalCostLimit
    }

    /// Clears memory cache (e.g. on low memory warning).
    public func clear() {
        cache.removeAllObjects()
    }

    /// Resolves file URL for an item path (e.g. "items/top/top_sailor.png").
    public func fileURL(for relativePath: String) -> URL? {
        let clean = relativePath.trimmingCharacters(in: .whitespacesAndNewlines)
        if clean.isEmpty { return nil }

        // 1. Direct bundle URL
        if let url = bundle.url(forResource: clean, withExtension: nil) ??
                     Bundle.main.url(forResource: clean, withExtension: nil) {
            return url
        }

        // 2. URL by splitting directory and filename
        let nsString = clean as NSString
        let fileName = (nsString.lastPathComponent as NSString).deletingPathExtension
        let fileExt = nsString.pathExtension
        let dir = nsString.deletingLastPathComponent

        if let url = bundle.url(forResource: fileName, withExtension: fileExt, subdirectory: dir) ??
                     Bundle.main.url(forResource: fileName, withExtension: fileExt, subdirectory: dir) {
            return url
        }

        // 3. Fallback searching by filename only across bundle
        if let url = bundle.url(forResource: fileName, withExtension: fileExt) ??
                     Bundle.main.url(forResource: fileName, withExtension: fileExt) {
            return url
        }

        // 4. Fallback in test bundles
        let testBundle = Bundle(for: ImageCache.self)
        if let url = testBundle.url(forResource: clean, withExtension: nil) ??
                     testBundle.url(forResource: fileName, withExtension: fileExt, subdirectory: dir) ??
                     testBundle.url(forResource: fileName, withExtension: fileExt) {
            return url
        }

        return nil
    }

    /// Loads image at full original size.
    public func loadFullImage(relativePath: String) -> UIImage? {
        let cacheKey = "full_\(relativePath)" as NSString
        if let cached = cache.object(forKey: cacheKey) {
            return cached
        }

        guard let url = fileURL(for: relativePath),
              let data = try? Data(contentsOf: url),
              let image = UIImage(data: data) else {
            return nil
        }

        let cost = Int(image.size.width * image.size.height * 4)
        cache.setObject(image, forKey: cacheKey, cost: cost)
        return image
    }

    /// Loads downsampled image constrained to `maxPixelSize` via CoreGraphics/ImageIO.
    public func loadDownsampledImage(relativePath: String, maxPixelSize: Int) -> UIImage? {
        let cacheKey = "\(maxPixelSize)_\(relativePath)" as NSString
        if let cached = cache.object(forKey: cacheKey) {
            return cached
        }

        guard let url = fileURL(for: relativePath) else {
            return nil
        }

        guard let source = CGImageSourceCreateWithURL(url as CFURL, nil) else {
            return nil
        }

        let options: [CFString: Any] = [
            kCGImageSourceCreateThumbnailFromImageAlways: true,
            kCGImageSourceShouldCacheImmediately: true,
            kCGImageSourceCreateThumbnailWithTransform: true,
            kCGImageSourceThumbnailMaxPixelSize: maxPixelSize
        ]

        guard let thumbnailCgImage = CGImageSourceCreateThumbnailAtIndex(source, 0, options as CFDictionary) else {
            return nil
        }

        let downsampled = UIImage(cgImage: thumbnailCgImage)
        let cost = Int(downsampled.size.width * downsampled.size.height * 4)
        cache.setObject(downsampled, forKey: cacheKey, cost: cost)
        return downsampled
    }
}

