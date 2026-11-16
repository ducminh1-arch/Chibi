import SwiftUI

/// Settings screen replicating Android `SettingsScreen.kt`.
public struct SettingsView: View {
    public let onBack: () -> Void

    @State private var prefs = UserPreferencesService.shared

    private let languageOptions: [(code: String, nameKey: String, defaultName: String)] = [
        ("system", "settings_language_system", "System Default"),
        ("vi", "settings_language_vi", "Tiếng Việt"),
        ("en", "settings_language_en", "English")
    ]

    public init(onBack: @escaping () -> Void) {
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

                    Text(String(localized: "settings_title", defaultValue: "Settings"))
                        .font(.system(size: 20, weight: .heavy, design: .rounded))
                        .foregroundColor(Color(red: 0.35, green: 0.25, blue: 0.45))

                    Spacer()

                    Color.clear.frame(width: 40, height: 40)
                }
                .padding(.horizontal, 16)
                .padding(.vertical, 10)

                ScrollView {
                    VStack(spacing: 16) {
                        // Section 1: Sound
                        VStack(spacing: 0) {
                            HStack {
                                ZStack {
                                    Circle()
                                        .fill(Color(red: 1.0, green: 0.92, blue: 0.96))
                                        .frame(width: 44, height: 44)
                                    Image(systemName: prefs.soundEnabled ? "speaker.wave.2.fill" : "speaker.slash.fill")
                                        .font(.system(size: 18))
                                        .foregroundColor(Color(red: 1.0, green: 0.45, blue: 0.65))
                                }

                                VStack(alignment: .leading, spacing: 2) {
                                    Text(String(localized: "settings_sound_title", defaultValue: "Sound Effects"))
                                        .font(.system(size: 16, weight: .bold, design: .rounded))
                                        .foregroundColor(Color(red: 0.3, green: 0.2, blue: 0.4))
                                    Text(String(localized: "settings_sound_desc", defaultValue: "Play cheerful audio feedback on taps and styling"))
                                        .font(.system(size: 12, weight: .medium, design: .rounded))
                                        .foregroundColor(Color(red: 0.6, green: 0.5, blue: 0.65))
                                }
                                .padding(.leading, 8)

                                Spacer()

                                Toggle("", isOn: Binding(
                                    get: { prefs.soundEnabled },
                                    set: { newValue in
                                        prefs.setSoundEnabled(newValue)
                                        if newValue {
                                            SoundManager.shared.playTap()
                                        }
                                    }
                                ))
                                .labelsHidden()
                                .tint(Color(red: 1.0, green: 0.45, blue: 0.65))
                            }
                            .padding(16)
                        }
                        .background(
                            RoundedRectangle(cornerRadius: 20)
                                .fill(Color.white)
                                .shadow(color: Color.black.opacity(0.04), radius: 6, x: 0, y: 2)
                        )

                        // Section 2: Language
                        VStack(alignment: .leading, spacing: 12) {
                            HStack(spacing: 8) {
                                Image(systemName: "globe")
                                    .foregroundColor(Color(red: 0.6, green: 0.45, blue: 0.75))
                                Text(String(localized: "settings_language_title", defaultValue: "Language"))
                                    .font(.system(size: 16, weight: .bold, design: .rounded))
                                    .foregroundColor(Color(red: 0.3, green: 0.2, blue: 0.4))
                            }
                            .padding(.horizontal, 16)
                            .padding(.top, 16)

                            VStack(spacing: 0) {
                                ForEach(languageOptions, id: \.code) { opt in
                                    let isSelected = (prefs.languageCode == opt.code)
                                    Button(action: {
                                        SoundManager.shared.playTap()
                                        prefs.setLanguageCode(opt.code)
                                    }) {
                                        HStack {
                                            Text(String(localized: String.LocalizationValue(opt.nameKey), defaultValue: opt.defaultName))
                                                .font(.system(size: 15, weight: isSelected ? .bold : .medium, design: .rounded))
                                                .foregroundColor(isSelected ? Color(red: 1.0, green: 0.45, blue: 0.65) : Color(red: 0.35, green: 0.3, blue: 0.4))
                                            Spacer()
                                            if isSelected {
                                                Image(systemName: "checkmark.circle.fill")
                                                    .foregroundColor(Color(red: 1.0, green: 0.45, blue: 0.65))
                                            }
                                        }
                                        .padding(.horizontal, 16)
                                        .padding(.vertical, 12)
                                    }
                                    if opt.code != languageOptions.last?.code {
                                        Divider().padding(.horizontal, 16)
                                    }
                                }
                            }
                            .padding(.bottom, 8)
                        }
                        .background(
                            RoundedRectangle(cornerRadius: 20)
                                .fill(Color.white)
                                .shadow(color: Color.black.opacity(0.04), radius: 6, x: 0, y: 2)
                        )

                        // Section 3: Safe for Kids Guarantee
                        HStack(spacing: 12) {
                            Image(systemName: "shield.lefthalf.filled")
                                .font(.system(size: 24))
                                .foregroundColor(Color(red: 0.4, green: 0.7, blue: 0.5))

                            VStack(alignment: .leading, spacing: 2) {
                                Text("Kid-Safe Offline App")
                                    .font(.system(size: 14, weight: .bold, design: .rounded))
                                    .foregroundColor(Color(red: 0.2, green: 0.4, blue: 0.3))
                                Text("Zero network access, no tracking, and no external ads.")
                                    .font(.system(size: 12, weight: .medium, design: .rounded))
                                    .foregroundColor(Color(red: 0.4, green: 0.5, blue: 0.45))
                            }
                            Spacer()
                        }
                        .padding(16)
                        .background(
                            RoundedRectangle(cornerRadius: 20)
                                .fill(Color(red: 0.94, green: 0.98, blue: 0.95))
                        )
                    }
                    .padding(16)
                }
            }
        }
    }
}

