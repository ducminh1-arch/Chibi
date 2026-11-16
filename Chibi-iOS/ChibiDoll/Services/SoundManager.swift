import Foundation
import AVFoundation

/// Synthesizes and manages royalty-free UI sound effects matching Android `SoundManager.kt`.
/// Requires zero external audio files by generating mathematical PCM waveforms in memory.
public final class SoundManager: @unchecked Sendable {
    public static let shared = SoundManager()

    public var isSoundEnabled: Bool = true

    private var tapPlayer: AVAudioPlayer?
    private var equipPlayer: AVAudioPlayer?
    private var savePlayer: AVAudioPlayer?
    private let sampleRate: Double = 22050

    private init() {
        prepareSynthesizedSounds()
    }

    public func playTap() {
        guard isSoundEnabled, let player = tapPlayer else { return }
        player.currentTime = 0
        player.play()
    }

    public func playEquip() {
        guard isSoundEnabled, let player = equipPlayer else { return }
        player.currentTime = 0
        player.play()
    }

    public func playSave() {
        guard isSoundEnabled, let player = savePlayer else { return }
        player.currentTime = 0
        player.play()
    }

    private func prepareSynthesizedSounds() {
        DispatchQueue.global(qos: .userInitiated).async { [weak self] in
            guard let self = self else { return }

            // 1. Synthesize Tap Pop Sound (45ms, 700Hz -> 400Hz frequency sweep)
            let tapData = self.generateSweepWav(startFreq: 700.0, endFreq: 400.0, durationMs: 45, decayRate: 30.0)
            let tapP = try? AVAudioPlayer(data: tapData)
            tapP?.prepareToPlay()

            // 2. Synthesize Equip Chime (120ms, 880Hz + 1320Hz double chime)
            let equipData = self.generateChimeWav(freqs: [880.0, 1320.0], durationMs: 120, decayRate: 18.0)
            let equipP = try? AVAudioPlayer(data: equipData)
            equipP?.prepareToPlay()

            // 3. Synthesize Save Sparkle Arpeggio (320ms, C5 -> E5 -> G5 -> C6)
            let saveData = self.generateArpeggioWav(freqs: [523.25, 659.25, 783.99, 1046.50], totalDurationMs: 320)
            let saveP = try? AVAudioPlayer(data: saveData)
            saveP?.prepareToPlay()

            DispatchQueue.main.async {
                self.tapPlayer = tapP
                self.equipPlayer = equipP
                self.savePlayer = saveP
            }
        }
    }

    // MARK: - Waveform Synthesizers

    private func generateSweepWav(startFreq: Double, endFreq: Double, durationMs: Int, decayRate: Double) -> Data {
        let totalSamples = Int((Double(durationMs) / 1000.0) * sampleRate)
        var pcm = [Int16](repeating: 0, count: totalSamples)
        var phase: Double = 0.0

        for i in 0..<totalSamples {
            let t = Double(i) / sampleRate
            let progress = Double(i) / Double(totalSamples)
            let currentFreq = startFreq + (endFreq - startFreq) * progress
            phase += 2.0 * .pi * currentFreq / sampleRate

            let envelope = exp(-decayRate * t)
            let sample = sin(phase) * envelope * 0.85
            pcm[i] = Int16(clamping: Int(sample * 32767.0))
        }

        return createWavFile(pcmSamples: pcm, sampleRate: Int(sampleRate))
    }

    private func generateChimeWav(freqs: [Double], durationMs: Int, decayRate: Double) -> Data {
        let totalSamples = Int((Double(durationMs) / 1000.0) * sampleRate)
        var pcm = [Int16](repeating: 0, count: totalSamples)

        for i in 0..<totalSamples {
            let t = Double(i) / sampleRate
            var sample: Double = 0.0
            for freq in freqs {
                sample += sin(2.0 * .pi * freq * t)
            }
            sample /= Double(freqs.count)
            let envelope = exp(-decayRate * t)
            pcm[i] = Int16(clamping: Int(sample * envelope * 0.85 * 32767.0))
        }

        return createWavFile(pcmSamples: pcm, sampleRate: Int(sampleRate))
    }

    private func generateArpeggioWav(freqs: [Double], totalDurationMs: Int) -> Data {
        let totalSamples = Int((Double(totalDurationMs) / 1000.0) * sampleRate)
        let samplesPerNote = totalSamples / freqs.count
        var pcm = [Int16](repeating: 0, count: totalSamples)

        for (idx, freq) in freqs.enumerated() {
            let start = idx * samplesPerNote
            let end = min(start + samplesPerNote, totalSamples)
            for i in start..<end {
                let noteT = Double(i - start) / sampleRate
                let decay = exp(-12.0 * noteT)
                let sample = sin(2.0 * .pi * freq * noteT) * decay * 0.8
                pcm[i] = Int16(clamping: Int(sample * 32767.0))
            }
        }

        return createWavFile(pcmSamples: pcm, sampleRate: Int(sampleRate))
    }

    private func createWavFile(pcmSamples: [Int16], sampleRate: Int) -> Data {
        var data = Data()
        let numChannels: Int16 = 1
        let bitsPerSample: Int16 = 16
        let byteRate = Int32(sampleRate * Int(numChannels) * Int(bitsPerSample / 8))
        let blockAlign = Int16(numChannels * (bitsPerSample / 8))
        let subChunk2Size = Int32(pcmSamples.count * 2)
        let chunkSize = 36 + subChunk2Size

        // RIFF header
        data.append("RIFF".data(using: .ascii)!)
        data.append(withUnsafeBytes(of: chunkSize.littleEndian) { Data($0) })
        data.append("WAVE".data(using: .ascii)!)

        // fmt subchunk
        data.append("fmt ".data(using: .ascii)!)
        data.append(withUnsafeBytes(of: Int32(16).littleEndian) { Data($0) }) // subChunk1Size (PCM = 16)
        data.append(withUnsafeBytes(of: Int16(1).littleEndian) { Data($0) })  // audioFormat (1 = PCM)
        data.append(withUnsafeBytes(of: numChannels.littleEndian) { Data($0) })
        data.append(withUnsafeBytes(of: Int32(sampleRate).littleEndian) { Data($0) })
        data.append(withUnsafeBytes(of: byteRate.littleEndian) { Data($0) })
        data.append(withUnsafeBytes(of: blockAlign.littleEndian) { Data($0) })
        data.append(withUnsafeBytes(of: bitsPerSample.littleEndian) { Data($0) })

        // data subchunk
        data.append("data".data(using: .ascii)!)
        data.append(withUnsafeBytes(of: subChunk2Size.littleEndian) { Data($0) })
        pcmSamples.withUnsafeBytes { data.append(contentsOf: $0) }

        return data
    }
}

