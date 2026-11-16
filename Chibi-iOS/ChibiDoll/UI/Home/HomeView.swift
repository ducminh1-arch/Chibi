import SwiftUI

/// Main Home / Title screen replicating Android `HomeScreen.kt`.
public struct HomeView: View {
    public let onPlay: () -> Void
    public let onCollection: () -> Void
    public let onSettings: () -> Void

    @State private var defaultLook: CharacterLook = CharacterLook()
    @State private var baseImage: UIImage? = nil
    @State private var overlayImage: UIImage? = nil
    @State private var floatOffset: CGFloat = -6

    public init(
        onPlay: @escaping () -> Void,
        onCollection: @escaping () -> Void,
        onSettings: @escaping () -> Void
    ) {
        self.onPlay = onPlay
        self.onCollection = onCollection
        self.onSettings = onSettings
    }

    public var body: some View {
        ZStack {
            Color(red: 0.99, green: 0.96, blue: 0.98).ignoresSafeArea()

            VStack(spacing: 0) {
                // Header: Title & Settings Button
                ZStack {
                    VStack(spacing: 4) {
                        HStack(spacing: 6) {
                            Image(systemName: "sparkles")
                                .font(.system(size: 20, weight: .bold))
                                .foregroundColor(Color(red: 1.0, green: 0.45, blue: 0.65))

                            Text(String(localized: "app_name", defaultValue: "Chibi Doll"))
                                .font(.system(size: 28, weight: .heavy, design: .rounded))
                                .foregroundColor(Color(red: 0.35, green: 0.25, blue: 0.45))

                            Image(systemName: "sparkles")
                                .font(.system(size: 20, weight: .bold))
                                .foregroundColor(Color(red: 1.0, green: 0.45, blue: 0.65))
                        }

                        Text(String(localized: "tagline", defaultValue: "Cute Paper Doll Dress Up"))
                            .font(.system(size: 13, weight: .medium, design: .rounded))
                            .foregroundColor(Color(red: 0.6, green: 0.5, blue: 0.65))
                    }

                    HStack {
                        Spacer()
                        Button(action: {
                            SoundManager.shared.playTap()
                            onSettings()
                        }) {
                            Image(systemName: "gearshape.fill")
                                .font(.system(size: 18, weight: .semibold))
                                .foregroundColor(Color(red: 0.55, green: 0.45, blue: 0.68))
                                .frame(width: 42, height: 42)
                                .background(Circle().fill(Color.white).shadow(color: Color.black.opacity(0.08), radius: 3, x: 0, y: 2))
                        }
                    }
                }
                .padding(.horizontal, 20)
                .padding(.top, 10)

                // Doll Preview Card (Floating gently)
                Spacer()

                ZStack {
                    RoundedRectangle(cornerRadius: 28)
                        .fill(Color.white)
                        .shadow(color: Color.pink.opacity(0.12), radius: 12, x: 0, y: 6)
                        .overlay(
                            RoundedRectangle(cornerRadius: 28)
                                .stroke(Color.white, lineWidth: 3)
                        )

                    DollCanvasView(
                        look: defaultLook,
                        baseImage: baseImage,
                        overlayImage: overlayImage,
                        sparkleTrigger: 0
                    )
                    .clipShape(RoundedRectangle(cornerRadius: 26))
                    .padding(8)
                }
                .frame(maxWidth: 240, maxHeight: 360)
                .aspectRatio(2.0 / 3.0, contentMode: .fit)
                .offset(y: floatOffset)
                .onAppear {
                    withAnimation(.easeInOut(duration: 1.8).repeatForever(autoreverses: true)) {
                        floatOffset = 6
                    }
                }

                Spacer()

                // Action Buttons
                VStack(spacing: 14) {
                    // 1. Play Button (Primary Large Pill)
                    Button(action: {
                        SoundManager.shared.playTap()
                        onPlay()
                    }) {
                        HStack(spacing: 8) {
                            Image(systemName: "play.fill")
                                .font(.system(size: 22, weight: .bold))
                            Text(String(localized: "btn_play", defaultValue: "Dress Up Now"))
                                .font(.system(size: 20, weight: .bold, design: .rounded))
                        }
                        .foregroundColor(.white)
                        .frame(maxWidth: .infinity)
                        .frame(height: 58)
                        .background(
                            LinearGradient(
                                colors: [Color(red: 1.0, green: 0.46, blue: 0.65), Color(red: 1.0, green: 0.55, blue: 0.75)],
                                startPoint: .leading,
                                endPoint: .trailing
                            )
                        )
                        .clipShape(Capsule())
                        .overlay(Capsule().stroke(Color.white, lineWidth: 2))
                        .shadow(color: Color.pink.opacity(0.35), radius: 8, x: 0, y: 4)
                    }

                    // 2. Create Character (Disabled Coming Soon Placeholder)
                    HStack {
                        HStack(spacing: 8) {
                            Image(systemName: "sparkles")
                                .foregroundColor(Color(red: 0.65, green: 0.55, blue: 0.75))
                            Text(String(localized: "btn_create_character", defaultValue: "Create Character"))
                                .font(.system(size: 15, weight: .semibold, design: .rounded))
                                .foregroundColor(Color(red: 0.55, green: 0.5, blue: 0.6))
                        }

                        Spacer()

                        Text(String(localized: "coming_soon", defaultValue: "Coming soon"))
                            .font(.system(size: 11, weight: .bold, design: .rounded))
                            .foregroundColor(Color(red: 1.0, green: 0.45, blue: 0.65))
                            .padding(.horizontal, 8)
                            .padding(.vertical, 4)
                            .background(Capsule().fill(Color(red: 1.0, green: 0.90, blue: 0.95)))
                    }
                    .padding(.horizontal, 20)
                    .frame(maxWidth: .infinity)
                    .frame(height: 50)
                    .background(Color.white.opacity(0.7))
                    .clipShape(Capsule())
                    .overlay(Capsule().stroke(Color(red: 0.88, green: 0.82, blue: 0.95), lineWidth: 1.5))

                    // 3. My Collection Button
                    Button(action: {
                        SoundManager.shared.playTap()
                        onCollection()
                    }) {
                        HStack(spacing: 8) {
                            Image(systemName: "photo.stack.fill")
                                .font(.system(size: 18))
                                .foregroundColor(Color(red: 0.55, green: 0.45, blue: 0.75))
                            Text(String(localized: "btn_my_collection", defaultValue: "My Collection"))
                                .font(.system(size: 16, weight: .semibold, design: .rounded))
                                .foregroundColor(Color(red: 0.35, green: 0.25, blue: 0.45))
                        }
                        .frame(maxWidth: .infinity)
                        .frame(height: 50)
                        .background(Color.white)
                        .clipShape(Capsule())
                        .overlay(Capsule().stroke(Color(red: 0.85, green: 0.78, blue: 0.95), lineWidth: 2))
                        .shadow(color: Color.black.opacity(0.04), radius: 4, x: 0, y: 2)
                    }
                }
                .padding(.horizontal, 28)
                .padding(.bottom, 24)
            }
        }
        .onAppear {
            loadPreview()
        }
    }

    private func loadPreview() {
        if let look = try? CatalogService.shared.getDefaultLook() {
            self.defaultLook = look
            DispatchQueue.global(qos: .userInitiated).async {
                let base = DollCanvasCompositor.shared.compositeBaseLayers(look: look)
                let overlay = DollCanvasCompositor.shared.compositeOverlayLayers(look: look)
                DispatchQueue.main.async {
                    self.baseImage = base
                    self.overlayImage = overlay
                }
            }
        }
    }
}

