import Foundation
import SwiftData

/// SwiftData Versioned Schemas replicating Room database migrations 1 -> 2 -> 3.
public enum SavedLookSchemaV1: VersionedSchema {
    public static var versionIdentifier: Schema.Version = Schema.Version(1, 0, 0)
    public static var models: [any PersistentModel.Type] { [SavedLookEntity.self] }

    @Model
    public final class SavedLookEntity {
        @Attribute(.unique) public var id: Int64
        public var createdAt: Int64
        public var equippedItemIdsJson: String
        public var backgroundId: String?
        public var thumbnailFileName: String

        public init(
            id: Int64 = 0,
            createdAt: Int64 = Int64(Date().timeIntervalSince1970 * 1000),
            equippedItemIdsJson: String,
            backgroundId: String? = nil,
            thumbnailFileName: String
        ) {
            self.id = id
            self.createdAt = createdAt
            self.equippedItemIdsJson = equippedItemIdsJson
            self.backgroundId = backgroundId
            self.thumbnailFileName = thumbnailFileName
        }
    }
}

public enum SavedLookSchemaV2: VersionedSchema {
    public static var versionIdentifier: Schema.Version = Schema.Version(2, 0, 0)
    public static var models: [any PersistentModel.Type] { [SavedLookEntity.self] }

    @Model
    public final class SavedLookEntity {
        @Attribute(.unique) public var id: Int64
        public var createdAt: Int64
        public var equippedItemIdsJson: String
        public var backgroundId: String?
        public var thumbnailFileName: String
        public var layerColorsJson: String = "{}"

        public init(
            id: Int64 = 0,
            createdAt: Int64 = Int64(Date().timeIntervalSince1970 * 1000),
            equippedItemIdsJson: String,
            backgroundId: String? = nil,
            thumbnailFileName: String,
            layerColorsJson: String = "{}"
        ) {
            self.id = id
            self.createdAt = createdAt
            self.equippedItemIdsJson = equippedItemIdsJson
            self.backgroundId = backgroundId
            self.thumbnailFileName = thumbnailFileName
            self.layerColorsJson = layerColorsJson
        }
    }
}

public enum SavedLookSchemaV3: VersionedSchema {
    public static var versionIdentifier: Schema.Version = Schema.Version(3, 0, 0)
    public static var models: [any PersistentModel.Type] { [SavedLookEntity.self] }

    @Model
    public final class SavedLookEntity {
        @Attribute(.unique) public var id: Int64
        public var createdAt: Int64
        public var equippedItemIdsJson: String
        public var backgroundId: String?
        public var thumbnailFileName: String
        public var layerColorsJson: String = "{}"
        public var presetId: String? = nil

        public init(
            id: Int64 = 0,
            createdAt: Int64 = Int64(Date().timeIntervalSince1970 * 1000),
            equippedItemIdsJson: String,
            backgroundId: String? = nil,
            thumbnailFileName: String,
            layerColorsJson: String = "{}",
            presetId: String? = nil
        ) {
            self.id = id
            self.createdAt = createdAt
            self.equippedItemIdsJson = equippedItemIdsJson
            self.backgroundId = backgroundId
            self.thumbnailFileName = thumbnailFileName
            self.layerColorsJson = layerColorsJson
            self.presetId = presetId
        }
    }
}

public enum SavedLookMigrationPlan: SchemaMigrationPlan {
    public static var schemas: [any VersionedSchema.Type] {
        [SavedLookSchemaV1.self, SavedLookSchemaV2.self, SavedLookSchemaV3.self]
    }

    public static var stages: [MigrationStage] {
        [migrateV1toV2, migrateV2toV3]
    }

    public static let migrateV1toV2 = MigrationStage.lightweight(
        fromVersion: SavedLookSchemaV1.self,
        toVersion: SavedLookSchemaV2.self
    )

    public static let migrateV2toV3 = MigrationStage.lightweight(
        fromVersion: SavedLookSchemaV2.self,
        toVersion: SavedLookSchemaV3.self
    )
}

public typealias SavedLookEntity = SavedLookSchemaV3.SavedLookEntity

