import Foundation
import SwiftUI

/// Color swatch representation from palettes.json.
public struct ColorSwatch: Codable, Identifiable, Hashable, Sendable {
    public let id: String
    public let nameEn: String
    public let nameVi: String
    public let color: String

    public init(id: String, nameEn: String, nameVi: String, color: String) {
        self.id = id
        self.nameEn = nameEn
        self.nameVi = nameVi
        self.color = color
    }

    /// Parses 0xAARRGGBB or #AARRGGBB hex into a 64-bit unsigned integer.
    public var colorValue: UInt64 {
        var hex = color.trimmingCharacters(in: .whitespacesAndNewlines)
        if hex.hasPrefix("0x") || hex.hasPrefix("0X") {
            hex = String(hex.dropFirst(2))
        } else if hex.hasPrefix("#") {
            hex = String(hex.dropFirst(1))
        }
        return UInt64(hex, radix: 16) ?? 0xFFFFFFFF
    }

    /// Converts ARGB UInt64 to SwiftUI Color.
    public var swiftUIColor: Color {
        let val = colorValue
        let a = Double((val >> 24) & 0xFF) / 255.0
        let r = Double((val >> 16) & 0xFF) / 255.0
        let g = Double((val >> 8) & 0xFF) / 255.0
        let b = Double(val & 0xFF) / 255.0
        return Color(.sRGB, red: r, green: g, blue: b, opacity: a)
    }

    public func displayName(isVietnamese: Bool = false) -> String {
        isVietnamese && !nameVi.isEmpty ? nameVi : nameEn
    }
}

/// Palette definition grouping swatches (hair, skin, eyes, general).
public struct Palette: Codable, Identifiable, Hashable, Sendable {
    public let id: String
    public let nameEn: String
    public let nameVi: String
    public let swatches: [ColorSwatch]

    public func displayName(isVietnamese: Bool = false) -> String {
        isVietnamese && !nameVi.isEmpty ? nameVi : nameEn
    }
}

public struct PalettesContainer: Codable, Sendable {
    public let palettes: [Palette]
}

