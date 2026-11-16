package com.chibidoll.dressup

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.chibidoll.dressup.domain.export.DollImageExporter
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import java.io.File

@RunWith(RobolectricTestRunner::class)
class CacheCleanupTest {

    @Test
    fun cleanOldSharedLooks_deletesOldFilesAndPreservesRecentFiles() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val cacheDir = File(context.cacheDir, "shared_looks").apply {
            if (!exists()) mkdirs()
        }

        val now = System.currentTimeMillis()
        val oneHourMillis = 3600 * 1000L
        val twentyFourHoursMillis = 24 * 3600 * 1000L

        // File 1: 48 hours old (> 24h threshold)
        val oldFile = File(cacheDir, "chibi_look_old.png").apply {
            writeBytes(byteArrayOf(1, 2, 3))
            setLastModified(now - (48 * oneHourMillis))
        }

        // File 2: 2 hours old (< 24h threshold)
        val recentFile = File(cacheDir, "chibi_look_recent.png").apply {
            writeBytes(byteArrayOf(4, 5, 6))
            setLastModified(now - (2 * oneHourMillis))
        }

        assertTrue(oldFile.exists())
        assertTrue(recentFile.exists())

        // Run cleanup with 24h threshold
        DollImageExporter.cleanOldSharedLooks(context, twentyFourHoursMillis)

        // Old file must be purged; recent file must be preserved
        assertFalse("Old file (>24h) should be deleted", oldFile.exists())
        assertTrue("Recent file (<24h) should be retained", recentFile.exists())

        // Cleanup after test
        recentFile.delete()
    }
}

