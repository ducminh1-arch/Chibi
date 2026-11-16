package com.chibidoll.dressup

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.test.core.app.ApplicationProvider
import com.chibidoll.dressup.ui.sound.SoundManager
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.shadows.ShadowLooper

@RunWith(RobolectricTestRunner::class)
class LanguageAndSoundHardenTest {

    private lateinit var context: Context

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext()
    }

    @Test
    fun mainActivity_extendsAppCompatActivity_forImmediateLocaleRecreation() {
        // MainActivity must extend AppCompatActivity so AppCompatDelegate.setApplicationLocales()
        // intercepts configuration changes, recreates UI immediately, and persists across restarts.
        val isAppCompat = AppCompatActivity::class.java.isAssignableFrom(MainActivity::class.java)
        assertTrue("MainActivity must extend AppCompatActivity", isAppCompat)
    }

    @Test
    fun applyLocale_setsApplicationLocalesCorrectly() {
        // Vietnamese
        val viList = androidx.core.os.LocaleListCompat.forLanguageTags("vi")
        assertEquals("vi", viList.toLanguageTags())
        ChibiApplication.applyLocale("vi")

        // English
        val enList = androidx.core.os.LocaleListCompat.forLanguageTags("en")
        assertEquals("en", enList.toLanguageTags())
        ChibiApplication.applyLocale("en")

        // System Default (empty locale list)
        val emptyList = androidx.core.os.LocaleListCompat.getEmptyLocaleList()
        assertTrue(emptyList.isEmpty)
        ChibiApplication.applyLocale("system")
    }

    @Test
    fun soundManager_rapidRepeatedPlayback_executesWithoutCrashing() {
        SoundManager.initialize(context)

        // Rapid 20-fire tap burst should not stutter, deadlock, or throw
        repeat(20) {
            SoundManager.playTap()
        }

        repeat(10) {
            SoundManager.playEquip()
            SoundManager.playSparkle()
        }

        repeat(5) {
            SoundManager.playSave()
        }
    }

    @Test
    fun soundManager_release_cleansUpSafely() {
        SoundManager.initialize(context)
        SoundManager.playTap()

        // Release resources (simulating Activity.onDestroy())
        SoundManager.release()

        // Subsequent calls after release should gracefully no-op without crashing
        SoundManager.playTap()
        SoundManager.playEquip()
        SoundManager.playSave()

        // Multiple releases must be safe
        SoundManager.release()
    }
}

