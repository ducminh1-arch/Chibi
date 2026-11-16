package com.chibidoll.dressup

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import coil.ImageLoader
import coil.ImageLoaderFactory
import coil.disk.DiskCache
import coil.memory.MemoryCache
import com.chibidoll.dressup.data.local.AppDatabase
import com.chibidoll.dressup.data.local.UserPreferencesRepository
import com.chibidoll.dressup.data.local.UserPreferencesRepositoryImpl
import com.chibidoll.dressup.data.repository.CatalogRepository
import com.chibidoll.dressup.data.repository.CatalogRepositoryImpl
import com.chibidoll.dressup.data.repository.SavedLooksRepository
import com.chibidoll.dressup.data.repository.SavedLooksRepositoryImpl
import com.chibidoll.dressup.domain.export.DollImageExporter
import com.chibidoll.dressup.ui.sound.SoundManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ChibiApplication : Application(), ImageLoaderFactory {

    lateinit var catalogRepository: CatalogRepository
        private set

    lateinit var savedLooksRepository: SavedLooksRepository
        private set

    lateinit var userPreferencesRepository: UserPreferencesRepository
        private set

    override fun newImageLoader(): ImageLoader {
        return ImageLoader.Builder(this)
            .memoryCache {
                MemoryCache.Builder(this)
                    // Explicit memory cache limit: 25% of available RAM budget
                    .maxSizePercent(0.25)
                    .build()
            }
            .diskCache {
                DiskCache.Builder()
                    .directory(cacheDir.resolve("image_cache"))
                    .maxSizeBytes(32L * 1024 * 1024)
                    .build()
            }
            .respectCacheHeaders(false)
            .build()
    }

    override fun onCreate() {
        super.onCreate()
        catalogRepository = CatalogRepositoryImpl(this)
        val database = AppDatabase.getInstance(this)
        savedLooksRepository = SavedLooksRepositoryImpl(database.savedLookDao(), this)
        userPreferencesRepository = UserPreferencesRepositoryImpl(this)

        // Initialize offline sound synthesizer with SoundPool and AudioTrack
        SoundManager.initialize(this)

        // Sync sound preferences
        CoroutineScope(Dispatchers.Main).launch {
            userPreferencesRepository.soundEnabled.collect { isEnabled ->
                SoundManager.isSoundEnabled = isEnabled
            }
        }

        // Apply saved locale preference
        CoroutineScope(Dispatchers.Main).launch {
            userPreferencesRepository.languageCode.collect { langCode ->
                applyLocale(langCode)
            }
        }

        // Clean up old cached share images (>24 hours) on app start
        CoroutineScope(Dispatchers.IO).launch {
            DollImageExporter.cleanOldSharedLooks(this@ChibiApplication)
        }
    }

    companion object {
        fun applyLocale(langCode: String) {
            val appLocales = when (langCode) {
                "vi" -> LocaleListCompat.forLanguageTags("vi")
                "en" -> LocaleListCompat.forLanguageTags("en")
                else -> LocaleListCompat.getEmptyLocaleList()
            }
            AppCompatDelegate.setApplicationLocales(appLocales)
        }
    }
}
