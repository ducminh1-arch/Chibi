import Foundation
import SwiftUI
import Observation

/// Manages user settings (sound toggle, language code, onboarding status) backed by UserDefaults.
/// Exactly mirrors Android `UserPreferencesRepository.kt`.
@Observable
public final class UserPreferencesService: @unchecked Sendable {
    public static let shared = UserPreferencesService()

    private let userDefaults: UserDefaults

    public private(set) var onboardingCompleted: Bool {
        didSet { userDefaults.set(onboardingCompleted, forKey: Keys.onboardingCompleted) }
    }

    public private(set) var soundEnabled: Bool {
        didSet {
            userDefaults.set(soundEnabled, forKey: Keys.soundEnabled)
            SoundManager.shared.isSoundEnabled = soundEnabled
        }
    }

    public private(set) var languageCode: String {
        didSet { userDefaults.set(languageCode, forKey: Keys.languageCode) }
    }

    private enum Keys {
        static let onboardingCompleted = "onboarding_completed"
        static let soundEnabled = "sound_enabled"
        static let languageCode = "language_code"
    }

    public init(userDefaults: UserDefaults = .standard) {
        self.userDefaults = userDefaults
        self.onboardingCompleted = userDefaults.bool(forKey: Keys.onboardingCompleted)
        self.soundEnabled = userDefaults.object(forKey: Keys.soundEnabled) as? Bool ?? true
        self.languageCode = userDefaults.string(forKey: Keys.languageCode) ?? "system"
        SoundManager.shared.isSoundEnabled = self.soundEnabled
    }

    public func setOnboardingCompleted(_ completed: Bool) {
        self.onboardingCompleted = completed
    }

    public func setSoundEnabled(_ enabled: Bool) {
        self.soundEnabled = enabled
    }

    public func setLanguageCode(_ code: String) {
        self.languageCode = code
    }
}

