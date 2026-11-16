import SwiftUI

/// Multi-row scrolling grid (~5 columns) of item thumbnails matching reference UI.
public struct ItemGridView: View {
    public let items: [CatalogItem]
    public let currentLook: CharacterLook
    public let onItemTap: (CatalogItem) -> Void

    private let columns = [
        GridItem(.adaptive(minimum: 62, maximum: 74), spacing: 8)
    ]

    public init(
        items: [CatalogItem],
        currentLook: CharacterLook,
        onItemTap: @escaping (CatalogItem) -> Void
    ) {
        self.items = items
        self.currentLook = currentLook
        self.onItemTap = onItemTap
    }

    public var body: some View {
        ScrollView {
            LazyVGrid(columns: columns, spacing: 8) {
                ForEach(items) { item in
                    let isEquipped = currentLook.isEquipped(item)
                    ItemThumbnailCell(
                        item: item,
                        isEquipped: isEquipped,
                        onTap: { onItemTap(item) }
                    )
                }
            }
            .padding(.horizontal, 14)
            .padding(.vertical, 8)
        }
    }
}

private struct ItemThumbnailCell: View {
    let item: CatalogItem
    let isEquipped: Bool
    let onTap: () -> Void

    @State private var thumbnailImage: UIImage? = nil

    var body: some View {
        Button(action: onTap) {
            ZStack {
                RoundedRectangle(cornerRadius: 14)
                    .fill(Color.white.opacity(0.85))
                    .shadow(color: Color.black.opacity(0.06), radius: 2, x: 0, y: 1)

                if let img = thumbnailImage {
                    Image(uiImage: img)
                        .resizable()
                        .aspectRatio(contentMode: .fit)
                        .padding(6)
                } else {
                    ProgressView()
                        .scaleEffect(0.6)
                }

                // Locked overlay badge
                if item.locked {
                    ZStack {
                        Color.black.opacity(0.3)
                            .clipShape(RoundedRectangle(cornerRadius: 14))
                        Image(systemName: "lock.fill")
                            .foregroundColor(.white)
                            .font(.system(size: 14))
                    }
                }
            }
            .frame(height: 68)
            .overlay(
                RoundedRectangle(cornerRadius: 14)
                    .stroke(isEquipped ? Color(red: 1.0, green: 0.35, blue: 0.65) : Color.white, lineWidth: isEquipped ? 3 : 1)
            )
            .scaleEffect(isEquipped ? 1.02 : 1.0)
        }
        .buttonStyle(SpringButtonStyle())
        .onAppear {
            loadThumbnail()
        }
    }

    private func loadThumbnail() {
        let path = item.thumbnail.isEmpty ? item.file : item.thumbnail
        thumbnailImage = ImageCache.shared.loadDownsampledImage(relativePath: path, maxPixelSize: 128)
    }
}

private struct SpringButtonStyle: ButtonStyle {
    func makeBody(configuration: Configuration) -> some View {
        configuration.label
            .scaleEffect(configuration.isPressed ? 0.92 : 1.0)
            .animation(.spring(response: 0.2, dampingFraction: 0.6), value: configuration.isPressed)
    }
}

