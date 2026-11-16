package com.chibidoll.dressup.ui.sound

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import android.media.SoundPool
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder
import kotlin.math.PI
import kotlin.math.exp
import kotlin.math.sin

object SoundManager {

    private const val SAMPLE_RATE = 22050

    var isSoundEnabled: Boolean = true

    // SoundPool handles rapid multi-stream repeated playback seamlessly without clipping
    private var soundPool: SoundPool? = null
    private var tapSoundId: Int = 0
    private var equipSoundId: Int = 0
    private var saveSoundId: Int = 0

    // AudioTrack fallbacks with synchronized pause + flush + playback head reset
    private var tapTrack: AudioTrack? = null
    private var equipTrack: AudioTrack? = null
    private var saveTrack: AudioTrack? = null

    private val scope = CoroutineScope(Dispatchers.Default)
    private var isInitialized: Boolean = false

    fun initialize(context: Context? = null) {
        if (isInitialized) return
        isInitialized = true

        scope.launch {
            try {
                // 1. Synthesize Tap Pop Sound (45ms, 700Hz -> 400Hz sweep with quick decay)
                val tapSamples = generateSweepPcm(
                    startFreq = 700.0,
                    endFreq = 400.0,
                    durationMs = 45,
                    decayRate = 30.0
                )

                // 2. Synthesize Equip Chime (120ms, 880Hz + 1320Hz double chime)
                val equipSamples = generateChimePcm(
                    freqs = listOf(880.0, 1320.0),
                    durationMs = 120,
                    decayRate = 18.0
                )

                // 3. Synthesize Save Sparkle Arpeggio (320ms, C5 -> E5 -> G5 -> C6)
                val saveSamples = generateArpeggioPcm(
                    freqs = listOf(523.25, 659.25, 783.99, 1046.50),
                    totalDurationMs = 320
                )

                // Initialize AudioTrack fallback with immediate playback head reset
                try {
                    tapTrack = buildStaticTrack(tapSamples)
                    equipTrack = buildStaticTrack(equipSamples)
                    saveTrack = buildStaticTrack(saveSamples)
                } catch (_: Throwable) {
                    // Headless / non-audio environment
                }

                // If Context is provided, initialize SoundPool with temporary synthesized WAV files
                if (context != null) {
                    try {
                        val soundDir = File(context.cacheDir, "sounds").apply { mkdirs() }
                        val tapFile = File(soundDir, "tap.wav")
                        val equipFile = File(soundDir, "equip.wav")
                        val saveFile = File(soundDir, "save.wav")

                        writeWavFile(tapFile, tapSamples, SAMPLE_RATE)
                        writeWavFile(equipFile, equipSamples, SAMPLE_RATE)
                        writeWavFile(saveFile, saveSamples, SAMPLE_RATE)

                        val attributes = AudioAttributes.Builder()
                            .setUsage(AudioAttributes.USAGE_GAME)
                            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                            .build()

                        val pool = SoundPool.Builder()
                            .setMaxStreams(8)
                            .setAudioAttributes(attributes)
                            .build()

                        tapSoundId = pool.load(tapFile.absolutePath, 1)
                        equipSoundId = pool.load(equipFile.absolutePath, 1)
                        saveSoundId = pool.load(saveFile.absolutePath, 1)

                        soundPool = pool
                    } catch (_: Throwable) {
                        // SoundPool initialization error, will use AudioTrack fallback
                    }
                }
            } catch (_: Throwable) {
                // Audio synthesis error fallback
            }
        }
    }

    fun playTap() {
        if (!isSoundEnabled) return
        val pool = soundPool
        if (pool != null && tapSoundId != 0) {
            try {
                pool.play(tapSoundId, 1.0f, 1.0f, 1, 0, 1.0f)
                return
            } catch (_: Throwable) {}
        }
        playAudioTrackHeadReset(tapTrack)
    }

    fun playEquip() {
        if (!isSoundEnabled) return
        val pool = soundPool
        if (pool != null && equipSoundId != 0) {
            try {
                pool.play(equipSoundId, 1.0f, 1.0f, 1, 0, 1.0f)
                return
            } catch (_: Throwable) {}
        }
        playAudioTrackHeadReset(equipTrack)
    }

    fun playSparkle() {
        playEquip()
    }

    fun playSave() {
        if (!isSoundEnabled) return
        val pool = soundPool
        if (pool != null && saveSoundId != 0) {
            try {
                pool.play(saveSoundId, 1.0f, 1.0f, 1, 0, 1.0f)
                return
            } catch (_: Throwable) {}
        }
        playAudioTrackHeadReset(saveTrack)
    }

    /**
     * Stop and reset playback head to 0 before every play.
     * Uses pause() + flush() + setPlaybackHeadPosition(0) to avoid blocking drain.
     */
    private fun playAudioTrackHeadReset(track: AudioTrack?) {
        if (track == null) return
        try {
            synchronized(track) {
                if (track.playState == AudioTrack.PLAYSTATE_PLAYING) {
                    track.pause()
                    track.flush()
                }
                track.setPlaybackHeadPosition(0)
                track.play()
            }
        } catch (_: Throwable) {
            // Audio playback error fallback
        }
    }

    /**
     * Releases SoundPool and AudioTrack resources.
     * Call in Activity.onDestroy().
     */
    fun release() {
        try {
            soundPool?.release()
            soundPool = null
            tapSoundId = 0
            equipSoundId = 0
            saveSoundId = 0

            tapTrack?.release()
            tapTrack = null
            equipTrack?.release()
            equipTrack = null
            saveTrack?.release()
            saveTrack = null

            isInitialized = false
        } catch (_: Throwable) {
            // Resource release cleanup
        }
    }

    private fun buildStaticTrack(pcmData: ShortArray): AudioTrack {
        val audioAttributes = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_GAME)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .build()

        val audioFormat = AudioFormat.Builder()
            .setSampleRate(SAMPLE_RATE)
            .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
            .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
            .build()

        val track = AudioTrack.Builder()
            .setAudioAttributes(audioAttributes)
            .setAudioFormat(audioFormat)
            .setBufferSizeInBytes(pcmData.size * 2)
            .setTransferMode(AudioTrack.MODE_STATIC)
            .build()

        track.write(pcmData, 0, pcmData.size)
        return track
    }

    private fun writeWavFile(file: File, pcmData: ShortArray, sampleRate: Int) {
        val totalAudioLen = pcmData.size * 2
        val totalDataLen = totalAudioLen + 36
        val byteRate = sampleRate * 2

        FileOutputStream(file).use { out ->
            val header = ByteArray(44)
            header[0] = 'R'.code.toByte()
            header[1] = 'I'.code.toByte()
            header[2] = 'F'.code.toByte()
            header[3] = 'F'.code.toByte()
            header[4] = (totalDataLen and 0xff).toByte()
            header[5] = ((totalDataLen shr 8) and 0xff).toByte()
            header[6] = ((totalDataLen shr 16) and 0xff).toByte()
            header[7] = ((totalDataLen shr 24) and 0xff).toByte()
            header[8] = 'W'.code.toByte()
            header[9] = 'A'.code.toByte()
            header[10] = 'V'.code.toByte()
            header[11] = 'E'.code.toByte()
            header[12] = 'f'.code.toByte()
            header[13] = 'm'.code.toByte()
            header[14] = 't'.code.toByte()
            header[15] = ' '.code.toByte()
            header[16] = 16
            header[17] = 0
            header[18] = 0
            header[19] = 0
            header[20] = 1
            header[21] = 0
            header[22] = 1
            header[23] = 0
            header[24] = (sampleRate and 0xff).toByte()
            header[25] = ((sampleRate shr 8) and 0xff).toByte()
            header[26] = ((sampleRate shr 16) and 0xff).toByte()
            header[27] = ((sampleRate shr 24) and 0xff).toByte()
            header[28] = (byteRate and 0xff).toByte()
            header[29] = ((byteRate shr 8) and 0xff).toByte()
            header[30] = ((byteRate shr 16) and 0xff).toByte()
            header[31] = ((byteRate shr 24) and 0xff).toByte()
            header[32] = 2
            header[33] = 0
            header[34] = 16
            header[35] = 0
            header[36] = 'd'.code.toByte()
            header[37] = 'a'.code.toByte()
            header[38] = 't'.code.toByte()
            header[39] = 'a'.code.toByte()
            header[40] = (totalAudioLen and 0xff).toByte()
            header[41] = ((totalAudioLen shr 8) and 0xff).toByte()
            header[42] = ((totalAudioLen shr 16) and 0xff).toByte()
            header[43] = ((totalAudioLen shr 24) and 0xff).toByte()
            out.write(header)

            val byteBuffer = ByteBuffer.allocate(totalAudioLen).order(ByteOrder.LITTLE_ENDIAN)
            for (sample in pcmData) {
                byteBuffer.putShort(sample)
            }
            out.write(byteBuffer.array())
        }
    }

    private fun generateSweepPcm(startFreq: Double, endFreq: Double, durationMs: Int, decayRate: Double): ShortArray {
        val count = (SAMPLE_RATE * durationMs / 1000.0).toInt()
        val pcm = ShortArray(count)
        var phase = 0.0

        for (i in 0 until count) {
            val progress = i.toDouble() / count
            val freq = startFreq + (endFreq - startFreq) * progress
            phase += 2.0 * PI * freq / SAMPLE_RATE
            val envelope = exp(-progress * decayRate)
            val sample = (sin(phase) * envelope * 0.75 * Short.MAX_VALUE).toInt()
            pcm[i] = sample.coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
        }
        return pcm
    }

    private fun generateChimePcm(freqs: List<Double>, durationMs: Int, decayRate: Double): ShortArray {
        val count = (SAMPLE_RATE * durationMs / 1000.0).toInt()
        val pcm = ShortArray(count)

        for (i in 0 until count) {
            val progress = i.toDouble() / count
            val envelope = exp(-progress * decayRate)
            var sum = 0.0
            for (freq in freqs) {
                sum += sin(2.0 * PI * freq * i / SAMPLE_RATE)
            }
            sum /= freqs.size
            val sample = (sum * envelope * 0.7 * Short.MAX_VALUE).toInt()
            pcm[i] = sample.coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
        }
        return pcm
    }

    private fun generateArpeggioPcm(freqs: List<Double>, totalDurationMs: Int): ShortArray {
        val count = (SAMPLE_RATE * totalDurationMs / 1000.0).toInt()
        val pcm = ShortArray(count)
        val noteCount = freqs.size
        val noteSamples = count / noteCount

        for (n in 0 until noteCount) {
            val freq = freqs[n]
            val startIdx = n * noteSamples
            val endIdx = minOf((n + 1) * noteSamples, count)
            for (i in startIdx until endIdx) {
                val noteProgress = (i - startIdx).toDouble() / (count - startIdx)
                val envelope = exp(-noteProgress * 8.0)
                val sample = (sin(2.0 * PI * freq * i / SAMPLE_RATE) * envelope * 0.75 * Short.MAX_VALUE).toInt()
                pcm[i] = sample.coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
            }
        }
        return pcm
    }
}
