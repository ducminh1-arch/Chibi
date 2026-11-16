import Foundation

/// Item specification matching catalog.json schema exactly.
/// 1:1 match with Android `CatalogItem`.
public struct CatalogItem: Codable, Identifiable, Hashable, Sendable {
    public let id: String
    public let category: String
    public let layer: String
    public let file: String
    public let thumbnail: String
    public let nameEn: String
    public let nameVi: String
    public let locked: Bool
    public let secondaryLayer: String?
    public let secondaryFile: String?
    public let group: String
    public let subcategory: String
    public let colorable: Bool
    public let palette: String?
    public let altFile: String?
    public let secondaryAltFile: String?
    public let zOverride: Int?
    public let secondaryZOverride: Int?
    public let conflictsWith: [String]

    enum CodingKeys: String, CodingKey {
        case id
        case category
        case layer
        case file
        case thumbnail
        case nameEn
        case nameVi
        case locked
        case secondaryLayer
        case secondaryFile
        case group
        case subcategory
        case colorable
        case palette
        case altFile = "alt_file"
        case secondaryAltFile = "secondary_alt_file"
        case zOverride = "z_override"
        case secondaryZOverride = "secondary_z_override"
        case conflictsWith
    }

    public init(
        id: String,
        category: String,
        layer: String,
        file: String,
        thumbnail: String,
        nameEn: String = "",
        nameVi: String = "",
        locked: Bool = false,
        secondaryLayer: String? = nil,
        secondaryFile: String? = nil,
        group: String = "",
        subcategory: String = "",
        colorable: Bool = false,
        palette: String? = nil,
        altFile: String? = nil,
        secondaryAltFile: String? = nil,
        zOverride: Int? = nil,
        secondaryZOverride: Int? = nil,
        conflictsWith: [String] = []
    ) {
        self.id = id
        self.category = category
        self.layer = layer
        self.file = file
        self.thumbnail = thumbnail
        self.nameEn = nameEn
        self.nameVi = nameVi
        self.locked = locked
        self.secondaryLayer = secondaryLayer
        self.secondaryFile = secondaryFile
        self.group = group
        self.subcategory = subcategory
        self.colorable = colorable
        self.palette = palette
        self.altFile = altFile
        self.secondaryAltFile = secondaryAltFile
        self.zOverride = zOverride
        self.secondaryZOverride = secondaryZOverride
        self.conflictsWith = conflictsWith
    }

    public init(from decoder: Decoder) throws {
        let container = try decoder.container(keyedBy: CodingKeys.self)
        self.id = try container.decode(String.self, forKey: .id)
        self.category = try container.decode(String.self, forKey: .category)
        self.layer = try container.decode(String.self, forKey: .layer)
        self.file = try container.decode(String.self, forKey: .file)
        self.thumbnail = try container.decodeIfPresent(String.self, forKey: .thumbnail) ?? ""
        self.nameEn = try container.decodeIfPresent(String.self, forKey: .nameEn) ?? ""
        self.nameVi = try container.decodeIfPresent(String.self, forKey: .nameVi) ?? ""
        self.locked = try container.decodeIfPresent(Bool.self, forKey: .locked) ?? false
        self.secondaryLayer = try container.decodeIfPresent(String.self, forKey: .secondaryLayer)
        self.secondaryFile = try container.decodeIfPresent(String.self, forKey: .secondaryFile)
        self.group = try container.decodeIfPresent(String.self, forKey: .group) ?? ""
        self.subcategory = try container.decodeIfPresent(String.self, forKey: .subcategory) ?? ""
        self.colorable = try container.decodeIfPresent(Bool.self, forKey: .colorable) ?? false
        self.palette = try container.decodeIfPresent(String.self, forKey: .palette)
        self.altFile = try container.decodeIfPresent(String.self, forKey: .altFile)
        self.secondaryAltFile = try container.decodeIfPresent(String.self, forKey: .secondaryAltFile)
        self.zOverride = try container.decodeIfPresent(Int.self, forKey: .zOverride)
        self.secondaryZOverride = try container.decodeIfPresent(Int.self, forKey: .secondaryZOverride)
        self.conflictsWith = try container.decodeIfPresent([String].self, forKey: .conflictsWith) ?? []
    }

    public var primaryLayer: Layer {
        Layer.fromKey(layer)
    }

    public var secondaryLayerEnum: Layer? {
        guard let secondaryLayer, !secondaryLayer.isEmpty else { return nil }
        return Layer.fromKey(secondaryLayer)
    }

    public var categoryEnum: ItemCategory {
        ItemCategory.fromKey(category)
    }

    public var effectiveGroup: String {
        if !group.isEmpty { return group }
        return categoryEnum.defaultGroup
    }

    public var effectiveSubcategory: String {
        if !subcategory.isEmpty { return subcategory }
        return category
    }

    public func displayName(isVietnamese: Bool = false) -> String {
        if isVietnamese && !nameVi.isEmpty {
            return nameVi
        }
        return nameEn.isEmpty ? id : nameEn
    }
}

