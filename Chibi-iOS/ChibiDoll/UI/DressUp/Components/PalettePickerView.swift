import SwiftUI

/// Floating color palette swatch grid allowing users to customize layer dyes.
public struct PalettePickerView: View {
    public let palette: Palette
    public let onSelectSwatch: (ColorSwatch) -> Void
    public let onClose: () -> Void

    private let columns = [
        GridItem(.adaptive(minimum: 36, maximum: 44), spacing: 8)
    ]

    public init(
        palette: Palette,
        onSelectSwatch: @escaping (ColorSwatch) -> Void,
        onClose: @escaping () -> Void
    ) {
        self.palette = palette
        self.onSelectSwatch = onSelectSwatch
        self.onClose = onClose
    }

    public var body: some View {
        VStack(spacing: 8) {
            HStack {
                Text(palette.displayName())
                    .font(.system(size: 14, weight: .bold))
                    .foregroundColor(Color(red: 0.3, green: 0.2, blue: 0.4))
                Spacer()
                Button(action: onClose) {
                    Image(systemName: "xmark.circle.fill")
                        .font(.system(size: 18))
                        .foregroundColor(.gray)
                }
            }
            .padding(.horizontal, 10)
            .padding(.top, 8)

            LazyVGrid(columns: columns, spacing: 8) {
                ForEach(palette.swatches) { swatch in
                    Button(action: { onSelectSwatch(swatch) }) {
                        Circle()
                            .fill(swatch.swiftUIColor)
                            .frame(width: 36, height: 36)
                            .overlay(
                                Circle()
                                    .stroke(Color.white, lineWidth: 2)
                                    .shadow(color: Color.black.opacity(0.15), radius: 1)
                            )
                    }
                }
            }
            .padding(8)
        }
        .frame(maxWidth: 220)
        .background(
            RoundedRectangle(cornerRadius: 16)
                .fill(Color.white.opacity(0.96))
                .shadow(color: Color.black.opacity(0.15), radius: 8, x: 0, y: 4)
        )
    }
}

