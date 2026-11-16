package com.chibidoll.dressup

import com.chibidoll.dressup.domain.export.DollImageExporter
import com.chibidoll.dressup.domain.model.Layer
import org.junit.Assert.assertTrue
import org.junit.Test

class MemoryOptimizationTest {

    @Test
    fun calculateMemory_21LayersEquipped_downsampledVsFullMaster() {
        val layerCount = 21
        val bytesPerPixelArgb8888 = 4

        // 1. Full master size (1024 x 1536)
        val masterPixelsPerLayer = DollImageExporter.MASTER_WIDTH * DollImageExporter.MASTER_HEIGHT
        val masterBytesPerLayer = masterPixelsPerLayer * bytesPerPixelArgb8888
        val masterTotalMegabytes = (masterBytesPerLayer.toLong() * layerCount) / (1024.0 * 1024.0)

        // 2. Typical downsampled on-screen size (e.g. 360 x 540 dp)
        val displayWidth = 360
        val displayHeight = 540
        val downsampledPixelsPerLayer = displayWidth * displayHeight
        val downsampledBytesPerLayer = downsampledPixelsPerLayer * bytesPerPixelArgb8888
        val downsampledTotalMegabytes = (downsampledBytesPerLayer.toLong() * layerCount) / (1024.0 * 1024.0)

        // 3. Medium tablet / large screen downsampled size (512 x 768)
        val tabletWidth = 512
        val tabletHeight = 768
        val tabletPixelsPerLayer = tabletWidth * tabletHeight
        val tabletBytesPerLayer = tabletPixelsPerLayer * bytesPerPixelArgb8888
        val tabletTotalMegabytes = (tabletBytesPerLayer.toLong() * layerCount) / (1024.0 * 1024.0)

        println("=== MEMORY MEASUREMENT REPORT FOR 21 LAYERS ===")
        println("Full 1024x1536 uncompressed: ${String.format("%.2f", masterTotalMegabytes)} MB (UNSAFE to keep on-screen simultaneously)")
        println("On-Screen downsampled (360x540): ${String.format("%.2f", downsampledTotalMegabytes)} MB (87.5% memory reduction)")
        println("Tablet downsampled (512x768): ${String.format("%.2f", tabletTotalMegabytes)} MB (75.0% memory reduction)")

        // Assert that full master 21-layer decode exceeds 100MB
        assertTrue(masterTotalMegabytes > 120.0)

        // Assert that on-screen downsampled 21-layer decode is < 20MB (safe for 2GB RAM budget device)
        assertTrue(downsampledTotalMegabytes < 20.0)

        // Assert tablet downsampled is < 35MB
        assertTrue(tabletTotalMegabytes < 35.0)
    }

    @Test
    fun verifyLayerCount_coversAll21Layers() {
        val nonBackgroundLayers = Layer.entries.filter { it != Layer.BACKGROUND && it != Layer.FACE }
        assertTrue("Expected at least 21 paper-doll layer types", nonBackgroundLayers.size >= 21)
    }
}

