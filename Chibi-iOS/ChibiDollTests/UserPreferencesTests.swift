import XCTest
@testable import ChibiDoll

final class UserPreferencesTests: XCTestCase {

    private var tempDefaults: UserDefaults!

    override func setUp() {
        super.setUp()
        tempDefaults = UserDefaults(suiteName: "test_preferences_\(UUID().uuidString)")!
    }

    override func tearDown() {
        tempDefaults.removePersistentDomain(forName: tempDefaults.description)
        super.tearDown()
    }

    func testUserPreferences_defaultValues() {
        let service = UserPreferencesService(userDefaults: tempDefaults)
        XCTAssertFalse(service.onboardingCompleted)
        XCTAssertTrue(service.soundEnabled)
        XCTAssertEqual("system", service.languageCode)
    }

    func testUserPreferences_persistsMutations() {
        let service = UserPreferencesService(userDefaults: tempDefaults)

        service.setOnboardingCompleted(true)
        service.setSoundEnabled(false)
        service.setLanguageCode("vi")

        XCTAssertTrue(service.onboardingCompleted)
        XCTAssertFalse(service.soundEnabled)
        XCTAssertEqual("vi", service.languageCode)

        // Read again from new instance backed by same suite
        let service2 = UserPreferencesService(userDefaults: tempDefaults)
        XCTAssertTrue(service2.onboardingCompleted)
        XCTAssertFalse(service2.soundEnabled)
        XCTAssertEqual("vi", service2.languageCode)
    }
}

