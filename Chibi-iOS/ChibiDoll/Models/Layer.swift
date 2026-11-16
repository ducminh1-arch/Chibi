import Foundation

/// Defines all 21+ paper-doll rendering layers with strict zIndex ordering.
/// Exactly replicates Android `com.chibidoll.dressup.domain.model.Layer`.
public enum Layer: Int, CaseIterable, Codable, Sendable, Comparable {
    case background = 0
    case wings = 5
    case hairBack = 10
    case tail = 15
    case body = 20
    case socks = 25
    case shoes = 30
    case bottom = 40
    case top = 50
    case dress = 60
    case outerwear = 70
    case blush = 72
    case mouth = 75
    case face = 78
    case eyesIris = 79
    case eyes = 80
    case eyesLine = 82
    case eyebrows = 85
    case glasses = 88
    case ears = 92
    case hairFront = 95
    case hat = 100
    case accessory = 110

    public var zIndex: Int { rawValue }

    public var key: String {
        switch self {
        case .background: return "background"
        case .wings: return "wings"
        case .hairBack: return "hair_back"
        case .tail: return "tail"
        case .body: return "body"
        case .socks: return "socks"
        case .shoes: return "shoes"
        case .bottom: return "bottom"
        case .top: return "top"
        case .dress: return "dress"
        case .outerwear: return "outerwear"
        case .blush: return "blush"
        case .mouth: return "mouth"
        case .face: return "face"
        case .eyesIris: return "eyes_iris"
        case .eyes: return "eyes"
        case .eyesLine: return "eyes_line"
        case .eyebrows: return "eyebrows"
        case .glasses: return "glasses"
        case .ears: return "ears"
        case .hairFront: return "hair_front"
        case .hat: return "hat"
        case .accessory: return "accessory"
        }
    }

    public static func fromKey(_ rawKey: String) -> Layer {
        switch rawKey.trimmingCharacters(in: .whitespacesAndNewlines).lowercased() {
        case "background", "backgrounds": return .background
        case "wings": return .wings
        case "hair_back", "hairback": return .hairBack
        case "tail": return .tail
        case "body": return .body
        case "socks": return .socks
        case "shoes": return .shoes
        case "bottom": return .bottom
        case "top": return .top
        case "dress": return .dress
        case "outerwear": return .outerwear
        case "blush": return .blush
        case "mouth": return .mouth
        case "face": return .face
        case "eyes_iris", "iris": return .eyesIris
        case "eyes_line", "line": return .eyesLine
        case "eyes": return .eyes
        case "eyebrows", "brows", "brow": return .eyebrows
        case "glasses": return .glasses
        case "ears": return .ears
        case "hair_front", "hairfront", "hair": return .hairFront
        case "hat": return .hat
        case "accessory": return .accessory
        default: return .accessory
        }
    }

    public static func < (lhs: Layer, rhs: Layer) -> Bool {
        lhs.zIndex < rhs.zIndex
    }
}

