import SwiftUI

/// Preset character model picker screen replicating Android `CharacterSelectScreen.kt`.
public struct CharacterSelectView: View {
    public let onSelectPreset: (CharacterPreset) -> Void
    public let onBack: () -> Void

    @State private var presets: [CharacterPreset] = []
    @State private var presetLooks: [String: CharacterLook] = [:]
    @State private var presetBaseImages: [String: UIImage] = [:]
    @State private var presetOverlayImages: [String: UIImage] = [:]

    private let columns = [
        GridItem(.flexible(), spacing: 14),
        GridItem(.flexible(), spacing: 14)
    ]

    public init(
        onSelectPreset: @escaping (CharacterPreset) -> Void,
        onBack: @escaping () -> Void
    ) {
        self.onSelectPreset = onSelectPreset
        self.onBack = onBack
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

                    Text(String(localized: "select_character_title", defaultValue: "Select Model"))
                        .font(.system(size: 20, weight: .heavy, design: .rounded))
                        .foregroundColor(Color(red: 0.35, green: 0.25, blue: 0.45))

                    Spacer()

                    Color.clear.frame(width: 40, height: 40)
                }
                .padding(.horizontal, 16)
                .padding(.vertical, 10)

                // Presets Grid
                ScrollView {
                    LazyVGrid(columns: columns, spacing: 16) {
                        ForEach(presets) { preset in
                            PresetCard(
                                preset: preset,
                                look: presetLooks[preset.id],
                                baseImage: presetBaseImages[preset.id],
                                overlayImage: presetOverlayImages[preset.id],
                                onSelect: {
                                    SoundManager.shared.playTap()
                                    onSelectPreset(preset)
                                }
                            )
                        }
                    }
                    .padding(16)
                }
            }
        }
        .onAppear {
            loadPresets()
        }
    }

    private func loadPresets() {
        let list = CatalogService.shared.getPresetCharacters()
        self.presets = list

        for preset in list {
            if let look = try? CatalogService.shared.getLookForPreset(preset) {
                self.presetLooks[preset.id] = look
                DispatchQueue.global(qos: .userInitiated).async {
                    let base = DollCanvasCompositor.shared.compositeBaseLayers(look: look, targetWidth: 320, targetHeight: 480)
                    let overlay = DollCanvasCompositor.shared.compositeOverlayLayers(look: look, targetWidth: 320, targetHeight: 480)
                    DispatchQueue.main.async {
                        self.presetBaseImages[preset.id] = base
                        self.presetOverlayImages[preset.id] = overlay
                    }
                }
            }
        }
    }
}

private struct PresetCard: View {
    let preset: CharacterPreset
    let look: CharacterLook?
    let baseImage: UIImage?
    let overlayImage: UIImage?
    let onSelect: () -> Void

    var body: some View {
        Button(action: onSelect) {
            VStack(spacing: 8) {
                // Miniature Canvas
                ZStack {
                    RoundedRectangle(cornerRadius: 18)
                        .fill(Color(red: 0.97, green: 0.95, blue: 0.99))

                    if let look = look {
                        DollCanvasView(
                            look: look,
                            baseImage: baseImage,
                            overlayImage: overlayImage,
                            sparkleTrigger: 0
                        )
                        .clipShape(RoundedRectangle(cornerRadius: 16))
                        .padding(4)
                    } else {
                        ProgressView().scaleEffect(0.7)
                    }
                }
                .frame(height: 180)
                .aspectRatio(2.0 / 3.0, contentMode: .fit)

                VStack(spacing: 4) {
                    Text(preset.displayName())
                        .font(.system(size: 16, weight: .bold, design: .rounded))
                        .foregroundColor(Color(red: 0.35, green: 0.25, blue: 0.45))

                    Text(preset.displayDescription())
                        .font(.system(size: 11, weight: .medium, design: .rounded))
                        .foregroundColor(Color(red: 0.6, green: 0.5, blue: 0.65))
                        .multilineTextAlignment(.center)
                        .lineLimit(2)
                        .frame(height: 28)
                }
                .padding(.horizontal, 4)

                // Select Pill Button
                Text(String(localized: "btn_dress_up", defaultValue: "Dress Up"))
                    .font(.system(size: 13, weight: .bold, design: .rounded))
                    .foregroundColor(.white)
                    .frame(maxWidth: .infinity)
                    .frame(height: 34)
                    .background(
                        LinearGradient(
                            colors: [Color(red: 1.0, green: 0.46, blue: 0.65), Color(red: 1.0, green: 0.55, blue: 0.75)],
                            startPoint: .leading,
                            endPoint: .trailing
                        )
                    )
                    .clipShape(Capsule())
                    .shadow(color: Color.pink.opacity(0.2), radius: 3, x: 0, y: 2)
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
}

