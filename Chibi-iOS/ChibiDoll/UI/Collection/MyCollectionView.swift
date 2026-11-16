import SwiftUI
import UIKit

/// My Collection screen displaying saved dolls replicating Android `MyCollectionScreen.kt`.
public struct MyCollectionView: View {
    public let onBack: () -> Void
    public let onCreateNew: () -> Void
    public let onEditLook: (CharacterLook, Int64) -> Void

    @State private var repository = SavedLooksRepository.shared
    @State private var catalog: [CatalogItem] = []
    @State private var selectedEntity: SavedLookEntity? = nil
    @State private var isDetailSheetPresented: Bool = false
    @State private var isDeleteAlertPresented: Bool = false
    @State private var shareImage: UIImage? = nil
    @State private var isSharePresented: Bool = false

    private let columns = [
        GridItem(.flexible(), spacing: 14),
        GridItem(.flexible(), spacing: 14)
    ]

    public init(
        onBack: @escaping () -> Void,
        onCreateNew: @escaping () -> Void,
        onEditLook: @escaping (CharacterLook, Int64) -> Void
    ) {
        self.onBack = onBack
        self.onCreateNew = onCreateNew
        self.onEditLook = onEditLook
    }

    public var body: some View {
        ZStack {
            Color(red: 0.99, green: 0.96, blue: 0.98).ignoresSafeArea()

            VStack(spacing: 0) {
                // Top Bar
                HStack {
                    Button(action: {
                        SoundManager.shared.playTap()
                        onBack()
                    }) {
                        Image(systemName: "chevron.backward")
                            .font(.system(size: 16, weight: .bold))
                            .foregroundColor(Color(red: 0.35, green: 0.25, blue: 0.45))
                            .frame(width: 40, height: 40)
                            .background(Circle().fill(Color.white).shadow(color: Color.black.opacity(0.08), radius: 3, x: 0, y: 1))
                    }

                    Spacer()

                    Text(String(localized: "collection_title", defaultValue: "My Collection"))
                        .font(.system(size: 20, weight: .heavy, design: .rounded))
                        .foregroundColor(Color(red: 0.35, green: 0.25, blue: 0.45))

                    Spacer()

                    Button(action: {
                        SoundManager.shared.playTap()
                        onCreateNew()
                    }) {
                        Image(systemName: "plus")
                            .font(.system(size: 16, weight: .bold))
                            .foregroundColor(Color(red: 0.35, green: 0.25, blue: 0.45))
                            .frame(width: 40, height: 40)
                            .background(Circle().fill(Color.white).shadow(color: Color.black.opacity(0.08), radius: 3, x: 0, y: 1))
                    }
                }
                .padding(.horizontal, 16)
                .padding(.vertical, 10)

                // Main Content
                if repository.savedLooks.isEmpty {
                    // Empty State
                    VStack(spacing: 16) {
                        Spacer()
                        ZStack {
                            Circle()
                                .fill(Color(red: 0.95, green: 0.90, blue: 0.98))
                                .frame(width: 110, height: 110)
                            Image(systemName: "photo.stack")
                                .font(.system(size: 48))
                                .foregroundColor(Color(red: 0.7, green: 0.6, blue: 0.8))
                        }

                        VStack(spacing: 6) {
                            Text(String(localized: "empty_collection_title", defaultValue: "No Dolls Saved Yet"))
                                .font(.system(size: 18, weight: .bold, design: .rounded))
                                .foregroundColor(Color(red: 0.35, green: 0.25, blue: 0.45))

                            Text(String(localized: "empty_collection_desc", defaultValue: "Dress up a doll and save your creations here!"))
                                .font(.system(size: 13, weight: .medium, design: .rounded))
                                .foregroundColor(Color(red: 0.6, green: 0.5, blue: 0.65))
                                .multilineTextAlignment(.center)
                                .padding(.horizontal, 32)
                        }

                        Button(action: {
                            SoundManager.shared.playTap()
                            onCreateNew()
                        }) {
                            Text(String(localized: "btn_create_doll", defaultValue: "Create Doll"))
                                .font(.system(size: 15, weight: .bold, design: .rounded))
                                .foregroundColor(.white)
                                .padding(.horizontal, 24)
                                .padding(.vertical, 12)
                                .background(Capsule().fill(Color(red: 1.0, green: 0.45, blue: 0.65)))
                                .shadow(color: Color.pink.opacity(0.3), radius: 6, x: 0, y: 3)
                        }
                        .padding(.top, 8)
                        Spacer()
                    }
                } else {
                    // Grid of Saved Dolls
                    ScrollView {
                        LazyVGrid(columns: columns, spacing: 16) {
                            ForEach(repository.savedLooks, id: \.id) { entity in
                                SavedLookCard(
                                    entity: entity,
                                    onTap: {
                                        SoundManager.shared.playTap()
                                        selectedEntity = entity
                                        isDetailSheetPresented = true
                                    }
                                )
                            }
                        }
                        .padding(16)
                    }
                }
            }
        }
        .onAppear {
            repository.loadAllLooks()
            if let c = try? CatalogService.shared.getCatalog() {
                self.catalog = c
            }
        }
        .sheet(item: $selectedEntity) { entity in
            SavedLookDetailSheet(
                entity: entity,
                catalog: catalog,
                onEdit: { restoredLook in
                    selectedEntity = nil
                    onEditLook(restoredLook, entity.id)
                },
                onShare: { img in
                    shareImage = img
                    isSharePresented = true
                },
                onDelete: {
                    try? repository.deleteLook(id: entity.id)
                    selectedEntity = nil
                }
            )
        }
        .sheet(isPresented: $isSharePresented) {
            if let img = shareImage {
                ActivityView(activityItems: [img])
            }
        }
    }
}

private struct SavedLookCard: View {
    let entity: SavedLookEntity
    let onTap: () -> Void

    var body: some View {
        Button(action: onTap) {
            VStack(spacing: 8) {
                ZStack {
                    RoundedRectangle(cornerRadius: 18)
                        .fill(Color(red: 0.97, green: 0.95, blue: 0.99))

                    if let img = SavedLooksRepository.shared.loadThumbnailImage(fileName: entity.thumbnailFileName) {
                        Image(uiImage: img)
                            .resizable()
                            .aspectRatio(contentMode: .fit)
                            .clipShape(RoundedRectangle(cornerRadius: 16))
                            .padding(4)
                    } else {
                        Image(systemName: "photo")
                            .font(.system(size: 32))
                            .foregroundColor(.gray)
                    }
                }
                .frame(height: 180)
                .aspectRatio(2.0 / 3.0, contentMode: .fit)

                Text(formatDate(entity.createdAt))
                    .font(.system(size: 11, weight: .medium, design: .rounded))
                    .foregroundColor(Color(red: 0.6, green: 0.5, blue: 0.65))
            }
            .padding(10)
            .background(
                RoundedRectangle(cornerRadius: 22)
                    .fill(Color.white)
                    .shadow(color: Color.black.opacity(0.06), radius: 6, x: 0, y: 3)
            )
        }
        .buttonStyle(PlainButtonStyle())
    }

    private func formatDate(_ timestamp: Int64) -> String {
        let date = Date(timeIntervalSince1970: Double(timestamp) / 1000.0)
        let formatter = DateFormatter()
        formatter.dateStyle = .short
        formatter.timeStyle = .short
        return formatter.string(from: date)
    }
}

private struct SavedLookDetailSheet: View {
    let entity: SavedLookEntity
    let catalog: [CatalogItem]
    let onEdit: (CharacterLook) -> Void
    let onShare: (UIImage) -> Void
    let onDelete: () -> Void

    @Environment(\.dismiss) private var dismiss
    @State private var isDeleteAlertShowing = false

    var body: some View {
        NavigationStack {
            VStack(spacing: 16) {
                if let img = SavedLooksRepository.shared.loadThumbnailImage(fileName: entity.thumbnailFileName) {
                    Image(uiImage: img)
                        .resizable()
                        .aspectRatio(contentMode: .fit)
                        .frame(maxHeight: 320)
                        .clipShape(RoundedRectangle(cornerRadius: 20))
                        .shadow(color: Color.black.opacity(0.1), radius: 10, x: 0, y: 4)
                        .padding(.top, 12)
                }

                HStack(spacing: 16) {
                    // Edit Button
                    Button(action: {
                        let look = SavedLookMapper.toCharacterLook(entity: entity, catalogItems: catalog)
                        dismiss()
                        onEdit(look)
                    }) {
                        HStack(spacing: 6) {
                            Image(systemName: "pencil")
                            Text("Edit")
                        }
                        .font(.system(size: 15, weight: .bold))
                        .foregroundColor(.white)
                        .frame(maxWidth: .infinity)
                        .frame(height: 48)
                        .background(Capsule().fill(Color(red: 1.0, green: 0.45, blue: 0.65)))
                    }

                    // Share Button
                    Button(action: {
                        if let img = SavedLooksRepository.shared.loadThumbnailImage(fileName: entity.thumbnailFileName) {
                            onShare(img)
                        }
                    }) {
                        Image(systemName: "square.and.arrow.up")
                            .font(.system(size: 18, weight: .bold))
                            .foregroundColor(Color(red: 0.35, green: 0.25, blue: 0.45))
                            .frame(width: 48, height: 48)
                            .background(Circle().fill(Color.gray.opacity(0.15)))
                    }

                    // Delete Button
                    Button(action: {
                        isDeleteAlertShowing = true
                    }) {
                        Image(systemName: "trash.fill")
                            .font(.system(size: 18, weight: .bold))
                            .foregroundColor(.red)
                            .frame(width: 48, height: 48)
                            .background(Circle().fill(Color.red.opacity(0.12)))
                    }
                }
                .padding(.horizontal, 24)
                .padding(.top, 8)

                Spacer()
            }
            .navigationTitle("Saved Doll")
            .navigationBarTitleDisplayMode(.inline)
            .toolbar {
                ToolbarItem(placement: .topBarTrailing) {
                    Button("Done") { dismiss() }
                }
            }
            .alert("Delete Doll?", isPresented: $isDeleteAlertShowing) {
                Button("Delete", role: .destructive) {
                    dismiss()
                    onDelete()
                }
                Button("Cancel", role: .cancel) {}
            } message: {
                Text("Are you sure you want to delete this saved doll?")
            }
        }
    }
}

private struct ActivityView: UIViewControllerRepresentable {
    let activityItems: [Any]

    func makeUIViewController(context: Context) -> UIActivityViewController {
        UIActivityViewController(activityItems: activityItems, applicationActivities: nil)
    }

    func updateUIViewController(_ uiViewController: UIActivityViewController, context: Context) {}
}

