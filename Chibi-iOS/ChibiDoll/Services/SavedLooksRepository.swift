import Foundation
import UIKit
import SwiftData
import Observation

/// Repository managing saved dolls and their cached thumbnail image files in the Documents directory.
/// Exactly replicates Android `SavedLooksRepositoryImpl.kt`.
@Observable
public final class SavedLooksRepository: @unchecked Sendable {
    public static let shared = SavedLooksRepository()

    public private(set) var savedLooks: [SavedLookEntity] = []

    private let documentsDirectory: URL
    private let thumbnailsDirectory: URL
    private var modelContainer: ModelContainer?
    private let lock = NSLock()

    public init() {
        let fileManager = FileManager.default
        let docs = fileManager.urls(for: .documentDirectory, in: .userDomainMask).first!
        self.documentsDirectory = docs
        self.thumbnailsDirectory = docs.appendingPathComponent("saved_looks", isDirectory: true)

        try? fileManager.createDirectory(at: thumbnailsDirectory, withIntermediateDirectories: true)

        do {
            let schema = Schema(versionedSchema: SavedLookSchemaV3.self)
            let config = ModelConfiguration(schema: schema, isStoredInMemoryOnly: false)
            self.modelContainer = try ModelContainer(
                for: schema,
                migrationPlan: SavedLookMigrationPlan.self,
                configurations: [config]
            )
            loadAllLooks()
        } catch {
            // SwiftData fallback for preview/test
            loadAllLooks()
        }
    }

    public func loadAllLooks() {
        lock.lock()
        defer { lock.unlock() }

        guard let container = modelContainer else { return }
        let context = ModelContext(container)
        var descriptor = FetchDescriptor<SavedLookEntity>(sortBy: [SortDescriptor(\.createdAt, order: .reverse)])
        descriptor.fetchLimit = 100

        if let items = try? context.fetch(descriptor) {
            DispatchQueue.main.async {
                self.savedLooks = items
            }
        }
    }

    @discardableResult
    public func saveLook(
        look: CharacterLook,
        thumbnailImage: UIImage,
        existingId: Int64? = nil
    ) throws -> SavedLookEntity {
        lock.lock()
        defer { lock.unlock() }

        let lookId = existingId ?? Int64(Date().timeIntervalSince1970 * 1000)
        let thumbFileName = "thumb_\(lookId).png"
        let thumbUrl = thumbnailsDirectory.appendingPathComponent(thumbFileName)

        if let pngData = thumbnailImage.pngData() {
            try pngData.write(to: thumbUrl)
        }

        guard let container = modelContainer else {
            let entity = SavedLookMapper.toEntity(look: look, thumbnailFileName: thumbFileName, id: lookId)
            savedLooks.removeAll { $0.id == lookId }
            savedLooks.insert(entity, at: 0)
            return entity
        }

        let context = ModelContext(container)

        // If updating an existing look, find it
        if let existingId = existingId {
            let targetId = existingId
            let descriptor = FetchDescriptor<SavedLookEntity>(predicate: #Predicate { $0.id == targetId })
            if let existingEntity = (try? context.fetch(descriptor))?.first {
                let slotToItemId = look.equippedItems.mapValues { $0.id }
                let equippedJson = (try? String(data: JSONEncoder().encode(slotToItemId), encoding: .utf8)) ?? "{}"
                let colorsJson = (try? String(data: JSONEncoder().encode(look.layerColors), encoding: .utf8)) ?? "{}"

                existingEntity.equippedItemIdsJson = equippedJson
                existingEntity.layerColorsJson = colorsJson
                existingEntity.backgroundId = look.backgroundItem?.id
                existingEntity.presetId = look.presetId
                existingEntity.thumbnailFileName = thumbFileName
                try context.save()
                loadAllLooks()
                return existingEntity
            }
        }

        let newEntity = SavedLookMapper.toEntity(look: look, thumbnailFileName: thumbFileName, id: lookId)
        context.insert(newEntity)
        try context.save()
        loadAllLooks()
        return newEntity
    }

    public func deleteLook(id: Int64) throws {
        lock.lock()
        defer { lock.unlock() }

        guard let container = modelContainer else {
            savedLooks.removeAll { $0.id == id }
            return
        }

        let context = ModelContext(container)
        let targetId = id
        let descriptor = FetchDescriptor<SavedLookEntity>(predicate: #Predicate { $0.id == targetId })
        if let entity = (try? context.fetch(descriptor))?.first {
            let thumbUrl = thumbnailsDirectory.appendingPathComponent(entity.thumbnailFileName)
            try? FileManager.default.removeItem(at: thumbUrl)

            context.delete(entity)
            try context.save()
            loadAllLooks()
        }
    }

    public func loadThumbnailImage(fileName: String) -> UIImage? {
        let fileUrl = thumbnailsDirectory.appendingPathComponent(fileName)
        return UIImage(contentsOfFile: fileUrl.path)
    }
}

