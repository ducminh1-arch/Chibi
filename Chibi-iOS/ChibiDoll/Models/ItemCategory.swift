import Foundation

/// Item categories with display titles, SF Symbol icons, and group classifications.
/// 1:1 match with Android `ItemCategory`.
public enum ItemCategory: String, CaseIterable, Codable, Sendable {
    case hair = "hair"
    case face = "face"
    case eyes = "eyes"
    case eyesLine = "eyes_line"
    case eyesIris = "eyes_iris"
    case eyebrows = "eyebrows"
    case mouth = "mouth"
    case blush = "blush"
    case body = "body"
    case dress = "dress"
    case top = "top"
    case bottom = "bottom"
    case outerwear = "outerwear"
    case socks = "socks"
    case shoes = "shoes"
    case glasses = "glasses"
    case ears = "ears"
    case tail = "tail"
    case wings = "wings"
    case hat = "hat"
    case accessory = "accessory"
    case background = "background"

    public var key: String { rawValue }

    public var localizationKey: String {
        "category_\(key)"
    }

    public var defaultGroup: String {
        switch self {
        case .top, .bottom, .dress, .outerwear, .shoes, .socks:
            return "clothes"
        case .hat, .accessory, .glasses, .ears, .tail, .wings:
            return "accessories"
        case .hair, .face, .body, .eyes, .eyesLine, .eyesIris, .eyebrows, .mouth, .blush:
            return "appearance"
        case .background:
            return "scene"
        }
    }

    public var systemImage: String {
        switch self {
        case .hair: return "person.crop.circle"
        case .face: return "face.smiling"
        case .eyes, .eyesLine, .eyesIris: return "eye.fill"
        case .eyebrows: return "waveform.path"
        case .mouth: return "mouth.fill"
        case .blush: return "heart.fill"
        case .body: return "figure.stand"
        case .dress: return "tshirt.fill"
        case .top: return "hanger"
        case .bottom: return "bag.fill"
        case .outerwear: return "square.stack.3d.down.right.fill"
        case .socks: return "shoeprints.fill"
        case .shoes: return "figure.walk"
        case .glasses: return "eyeglasses"
        case .ears: return "hare.fill"
        case .tail: return "pawprint.fill"
        case .wings: return "wind"
        case .hat: return "crown.fill"
        case .accessory: return "sparkles"
        case .background: return "photo.fill"
        }
    }

    public static func fromKey(_ key: String) -> ItemCategory {
        let clean = key.trimmingCharacters(in: .whitespacesAndNewlines).lowercased()
        return ItemCategory(rawValue: clean) ?? .accessory
    }
}

