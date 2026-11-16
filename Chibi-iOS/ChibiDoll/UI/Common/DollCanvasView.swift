import SwiftUI

/// On-screen doll canvas view displaying cached base layers, dynamic eyes with idle blink animation,
/// cached overlay layers, and equip sparkle particle bursts.
public struct DollCanvasView: View {
    public let look: CharacterLook
    public let baseImage: UIImage?
    public let overlayImage: UIImage?
    public let sparkleTrigger: Int

    @State private var isBlinking: Bool = false
    @State private var blinkTimer: Timer? = nil

    public init(
        look: CharacterLook,
        baseImage: UIImage?,
        overlayImage: UIImage?,
        sparkleTrigger: Int = 0
    ) {
        self.look = look
        self.baseImage = baseImage
        self.overlayImage = overlayImage
        self.sparkleTrigger = sparkleTrigger
    }

    public var body: some View {
        GeometryReader { geometry in
            let canvasAspect: CGFloat = 1024.0 / 1536.0
            let containerAspect = geometry.size.width / geometry.size.height

            let drawWidth = containerAspect > canvasAspect
                ? geometry.size.height * canvasAspect
                : geometry.size.width
            let drawHeight = drawWidth / canvasAspect

            ZStack {
                // 1. Cached Base Layers (background, body, clothes, mouth, blush)
                if let base = baseImage {
                    Image(uiImage: base)
                        .resizable()
                        .interpolation(.high)
                        .aspectRatio(contentMode: .fit)
                }

                // 2. Dynamic Eye Layers with Idle Blinking (~120ms every ~3.8s)
                renderEyeLayers(width: drawWidth, height: drawHeight)

                // 3. Cached Overlay Layers (eyebrows, glasses, hair front, hat, accessories)
                if let overlay = overlayImage {
                    Image(uiImage: overlay)
                        .resizable()
                        .interpolation(.high)
                        .aspectRatio(contentMode: .fit)
                }

                // 4. Sparkle Particle Burst
                SparkleView(trigger: sparkleTrigger)
            }
            .frame(width: drawWidth, height: drawHeight)
            .frame(maxWidth: .infinity, maxHeight: .infinity, alignment: .center)
            .onAppear {
                startBlinkLoop()
            }
            .onDisappear {
                stopBlinkLoop()
            }
        }
    }

    @ViewBuilder
    private func renderEyeLayers(width: CGFloat, height: CGFloat) -> some View {
        let eyeLayers = look.getDetailedRenderLayers().filter { $0.zIndex >= 79 && $0.zIndex <= 82 }

        ForEach(eyeLayers, id: \.filePath) { layerInfo in
            let path = isBlinking ? (layerInfo.altFilePath ?? layerInfo.filePath) : layerInfo.filePath
            if !path.isEmpty, let img = ImageCache.shared.loadDownsampledImage(relativePath: path, maxPixelSize: Int(max(width, height))) {
                if layerInfo.isColorable, let tint = layerInfo.colorTint {
                    let tinted = DollCanvasCompositor.tintImage(img, tintColor: tint)
                    Image(uiImage: tinted)
                        .resizable()
                        .interpolation(.high)
                        .aspectRatio(contentMode: .fit)
                } else {
                    Image(uiImage: img)
                        .resizable()
                        .interpolation(.high)
                        .aspectRatio(contentMode: .fit)
                }
            }
        }
    }

    private func startBlinkLoop() {
        stopBlinkLoop()
        blinkTimer = Timer.scheduledTimer(withTimeInterval: 3.8, repeats: true) { _ in
            triggerBlink()
        }
    }

    private func triggerBlink() {
        isBlinking = true
        DispatchQueue.main.asyncAfter(deadline: .now() + 0.12) {
            self.isBlinking = false
        }
    }

    private func stopBlinkLoop() {
        blinkTimer?.invalidate()
        blinkTimer = nil
    }
}

