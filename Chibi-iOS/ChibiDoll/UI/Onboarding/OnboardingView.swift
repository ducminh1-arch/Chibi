import SwiftUI

/// 3-page interactive onboarding tutorial replicating Android `OnboardingScreen.kt`.
public struct OnboardingView: View {
    public let onFinish: () -> Void

    @State private var currentPage: Int = 0

    private let pages: [OnboardingItem] = [
        OnboardingItem(
            titleKey: "onboarding_page1_title",
            descKey: "onboarding_page1_desc",
            defaultTitle: "Create Adorable Chibi Dolls",
            defaultDesc: "Mix and match hairstyles, outfits, and cute accessories to style your unique character!",
            icon: "face.smiling",
            secondaryIcon: "sparkles",
            gradient: [Color(red: 1.0, green: 0.70, blue: 0.78), Color(red: 1.0, green: 0.55, blue: 0.67)]
        ),
        OnboardingItem(
            titleKey: "onboarding_page2_title",
            descKey: "onboarding_page2_desc",
            defaultTitle: "Customize & Color",
            defaultDesc: "Choose your favorite colors for hair, clothes, and skin tone with rich palettes!",
            icon: "hanger",
            secondaryIcon: "paintpalette.fill",
            gradient: [Color(red: 0.78, green: 0.72, blue: 0.92), Color(red: 0.62, green: 0.52, blue: 0.84)]
        ),
        OnboardingItem(
            titleKey: "onboarding_page3_title",
            descKey: "onboarding_page3_desc",
            defaultTitle: "Save & Share",
            defaultDesc: "Keep your creations in your collection and save high-resolution pictures to Photos!",
            icon: "photo.stack.fill",
            secondaryIcon: "camera.fill",
            gradient: [Color(red: 0.70, green: 0.85, blue: 0.95), Color(red: 0.55, green: 0.75, blue: 0.90)]
        )
    ]

    public init(onFinish: @escaping () -> Void) {
        self.onFinish = onFinish
    }

    public var body: some View {
        ZStack {
            Color(red: 0.98, green: 0.96, blue: 0.99).ignoresSafeArea()

            VStack(spacing: 0) {
                // Top Bar with Skip Button
                HStack {
                    Spacer()
                    Button(action: {
                        SoundManager.shared.playTap()
                        UserPreferencesService.shared.setOnboardingCompleted(true)
                        onFinish()
                    }) {
                        Text(String(localized: "btn_skip", defaultValue: "Skip"))
                            .font(.system(size: 15, weight: .bold, design: .rounded))
                            .foregroundColor(Color(red: 0.6, green: 0.45, blue: 0.7))
                            .padding(.horizontal, 16)
                            .padding(.vertical, 8)
                            .background(Capsule().fill(Color.white.opacity(0.8)))
                    }
                }
                .padding(.horizontal, 20)
                .padding(.top, 12)

                // Page Swiper
                TabView(selection: $currentPage) {
                    ForEach(pages.indices, id: \.self) { idx in
                        let item = pages[idx]
                        VStack(spacing: 24) {
                            Spacer()

                            // Cute Rounded Graphic Card
                            ZStack {
                                RoundedRectangle(cornerRadius: 36)
                                    .fill(LinearGradient(colors: item.gradient, startPoint: .topLeading, endPoint: .bottomTrailing))
                                    .frame(width: 200, height: 200)
                                    .shadow(color: item.gradient.first!.opacity(0.4), radius: 14, x: 0, y: 8)

                                Image(systemName: item.icon)
                                    .font(.system(size: 72))
                                    .foregroundColor(.white)

                                Image(systemName: item.secondaryIcon)
                                    .font(.system(size: 28))
                                    .foregroundColor(.white.opacity(0.9))
                                    .offset(x: 60, y: -60)
                            }

                            VStack(spacing: 10) {
                                Text(String(localized: String.LocalizationValue(item.titleKey), defaultValue: item.defaultTitle))
                                    .font(.system(size: 24, weight: .heavy, design: .rounded))
                                    .foregroundColor(Color(red: 0.35, green: 0.25, blue: 0.45))
                                    .multilineTextAlignment(.center)
                                    .padding(.horizontal, 24)

                                Text(String(localized: String.LocalizationValue(item.descKey), defaultValue: item.defaultDesc))
                                    .font(.system(size: 15, weight: .medium, design: .rounded))
                                    .foregroundColor(Color(red: 0.55, green: 0.48, blue: 0.62))
                                    .multilineTextAlignment(.center)
                                    .padding(.horizontal, 32)
                                    .lineSpacing(4)
                            }

                            Spacer()
                        }
                        .tag(idx)
                    }
                }
                .tabViewStyle(.page(indexDisplayMode: .never))

                // Bottom Controls: Page Dots & Action Button
                VStack(spacing: 20) {
                    // Page Indicator Dots
                    HStack(spacing: 8) {
                        ForEach(pages.indices, id: \.self) { idx in
                            Capsule()
                                .fill(currentPage == idx ? Color(red: 1.0, green: 0.45, blue: 0.65) : Color(red: 0.85, green: 0.80, blue: 0.90))
                                .frame(width: currentPage == idx ? 24 : 8, height: 8)
                                .animation(.spring(response: 0.3, dampingFraction: 0.7), value: currentPage)
                        }
                    }

                    // Next / Get Started Button
                    Button(action: {
                        SoundManager.shared.playTap()
                        if currentPage < pages.count - 1 {
                            withAnimation { currentPage += 1 }
                        } else {
                            UserPreferencesService.shared.setOnboardingCompleted(true)
                            onFinish()
                        }
                    }) {
                        Text(currentPage == pages.count - 1
                             ? String(localized: "btn_get_started", defaultValue: "Get Started")
                             : String(localized: "btn_next", defaultValue: "Next"))
                            .font(.system(size: 18, weight: .bold, design: .rounded))
                            .foregroundColor(.white)
                            .frame(maxWidth: .infinity)
                            .frame(height: 54)
                            .background(
                                LinearGradient(
                                    colors: [Color(red: 1.0, green: 0.46, blue: 0.65), Color(red: 1.0, green: 0.55, blue: 0.75)],
                                    startPoint: .leading,
                                    endPoint: .trailing
                                )
                            )
                            .clipShape(Capsule())
                            .shadow(color: Color.pink.opacity(0.3), radius: 8, x: 0, y: 4)
                    }
                    .padding(.horizontal, 32)
                }
                .padding(.bottom, 36)
            }
        }
    }
}

private struct OnboardingItem {
    let titleKey: String
    let descKey: String
    let defaultTitle: String
    let defaultDesc: String
    let icon: String
    let secondaryIcon: String
    let gradient: [Color]
}

