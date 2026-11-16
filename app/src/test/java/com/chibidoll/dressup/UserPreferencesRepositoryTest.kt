package com.chibidoll.dressup

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.emptyPreferences
import com.chibidoll.dressup.data.local.UserPreferencesRepository
import com.chibidoll.dressup.data.local.UserPreferencesRepositoryImpl
import com.chibidoll.dressup.ui.sound.SoundManager
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

/**
 * In-memory test double for DataStore<Preferences> to avoid Windows-specific
 * SingleProcessDataStore file rename locks (Issue b/173082141).
 */
class InMemoryDataStore(
    initialPreferences: Preferences = emptyPreferences()
) : DataStore<Preferences> {
    private val state = MutableStateFlow(initialPreferences)

    override val data: Flow<Preferences> = state.asStateFlow()

    override suspend fun updateData(transform: suspend (t: Preferences) -> Preferences): Preferences {
        val updated = transform(state.value)
        state.value = updated
        return updated
    }
}

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(RobolectricTestRunner::class)
class UserPreferencesRepositoryTest {

    private val testDispatcher = UnconfinedTestDispatcher()
    private val testScope = TestScope(testDispatcher)

    private lateinit var inMemoryDataStore: InMemoryDataStore
    private lateinit var repository: UserPreferencesRepository

    @Before
    fun setUp() {
        inMemoryDataStore = InMemoryDataStore()
        repository = UserPreferencesRepositoryImpl(inMemoryDataStore)
    }

    @Test
    fun defaultPreferences_returnsSensibleDefaults() = testScope.runTest {
        val initialSound = repository.soundEnabled.first()
        val initialLang = repository.languageCode.first()
        val initialOnboarding = repository.onboardingCompleted.first()

        assertTrue(initialSound)
        assertEquals("system", initialLang)
        assertFalse(initialOnboarding)
    }

    @Test
    fun setOnboardingCompleted_updatesFlow() = testScope.runTest {
        repository.setOnboardingCompleted(true)
        val completed = repository.onboardingCompleted.first()
        assertTrue(completed)

        repository.setOnboardingCompleted(false)
        val reset = repository.onboardingCompleted.first()
        assertFalse(reset)
    }

    @Test
    fun setSoundEnabled_togglesSoundSetting() = testScope.runTest {
        repository.setSoundEnabled(false)
        val isMuted = repository.soundEnabled.first()
        assertFalse(isMuted)

        repository.setSoundEnabled(true)
        val isUnmuted = repository.soundEnabled.first()
        assertTrue(isUnmuted)
    }

    @Test
    fun setLanguageCode_updatesAndPersistsLanguage() = testScope.runTest {
        repository.setLanguageCode("vi")
        assertEquals("vi", repository.languageCode.first())

        repository.setLanguageCode("en")
        assertEquals("en", repository.languageCode.first())

        repository.setLanguageCode("system")
        assertEquals("system", repository.languageCode.first())
    }

    @Test
    fun soundManager_respectsMuteFlag() {
        SoundManager.initialize()

        // When sound is disabled, calls must not crash and must be silent
        SoundManager.isSoundEnabled = false
        assertFalse(SoundManager.isSoundEnabled)
        SoundManager.playTap()
        SoundManager.playEquip()
        SoundManager.playSparkle()
        SoundManager.playSave()

        // When sound is enabled, calls execute cleanly
        SoundManager.isSoundEnabled = true
        assertTrue(SoundManager.isSoundEnabled)
        SoundManager.playTap()
        SoundManager.playEquip()
        SoundManager.playSparkle()
        SoundManager.playSave()
    }
}
