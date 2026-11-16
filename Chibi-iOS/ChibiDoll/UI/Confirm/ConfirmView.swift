import SwiftUI
import UIKit

/// Confirm and export screen replicating Android `ConfirmScreen.kt`.
public struct ConfirmView: View {
    public let look: CharacterLook
    public let editingLookId: Int64?
    public let onBackToEdit: () -> Void
    public let onSaved: (Int64) -> Void

    @State private var baseImage: UIImage? = nil
    @State private var overlayImage: UIImage? = nil
    @State private var isSaving: Bool = false
    @State private var showSuccessBanner: Bool = false
    @State private var shareItem: UIImage? = nil
    @State private var isSharePresented: Bool = false

    public init(
        look: CharacterLook,
        editingLookId: Int64? = nil,
        onBackToEdit: @escaping () -> Void,
        onSaved: @escaping (Int64) -> Void
    ) {
        self.look = look
        self.editingLookId = editingLookId
        self.onBackToEdit = onBackToEdit
        self.onSaved = onSaved
    }

    public var body: some View {
        ZStack {
            Color(red: 0.99, green: 0.96, blue: 0.98).ignoresSafeArea()

            VStack(spacing: 0) {
                // Top Bar
                HStack {
                    Button(action: {
                        SoundManager.shared.playTap()
                        onBackToEdit()
                    }) {
                        HStack(spacing: 6) {
                            Image(systemName: "pencil")
                                .font(.system(size: 14, weight: .bold))
                            Text(String(localized: "btn_edit", defaultValue: "Edit"))
                                .font(.system(size: 15, weight: .bold, design: .rounded))
                        }
                        .foregroundColor(Color(red: 0.35, green: 0.25, blue: 0.45))
                        .padding(.horizontal, 14)
                        .padding(.vertical, 8)
                        .background(Capsule().fill(Color.white).shadow(color: Color.black.opacity(0.08), radius: 3, x: 0, y: 1))
                    }

                    Spacer()

                    Text(String(localized: "confirm_title", defaultValue: "My Chibi Doll"))
                        .font(.system(size: 18, weight: .heavy, design: .rounded))
                        .foregroundColor(Color(red: 0.35, green: 0.25, blue: 0.45))

                    Spacer()

                    // Share button
                    Button(action: {
                        SoundManager.shared.playTap()
                        prepareShare()
                    }) {
                        Image(systemName: "square.and.arrow.up")
                            .font(.system(size: 16, weight: .bold))
                            .foregroundColor(Color(red: 0.35, green: 0.25, blue: 0.45))
                            .frame(width: 40, height: 40)
                            .background(Circle().fill(Color.white).shadow(color: Color.black.opacity(0.08), radius: 3, x: 0, y: 1))
                    }
                }
                .padding(.horizontal, 16)
                .padding(.vertical, 10)

                // Master Canvas Frame
                Spacer()

                ZStack {
                    RoundedRectangle(cornerRadius: 24)
                        .fill(Color.white)
                        .shadow(color: Color.pink.opacity(0.12), radius: 14, x: 0, y: 8)

                    DollCanvasView(
                        look: look,
                        baseImage: baseImage,
                        overlayImage: overlayImage,
                        sparkleTrigger: 0
                    )
                    .clipShape(RoundedRectangle(cornerRadius: 22))
                    .padding(6)
                }
                .frame(maxWidth: 280, maxHeight: 420)
                .aspectRatio(2.0 / 3.0, contentMode: .fit)

                Spacer()

                // Save Action Button
                VStack(spacing: 12) {
                    if showSuccessBanner {
                        HStack(spacing: 8) {
                            Image(systemName: "checkmark.circle.fill")
                                .foregroundColor(.green)
                            Text(String(localized: "save_success", defaultValue: "Saved to Photos & Collection!"))
                                .font(.system(size: 14, weight: .bold, design: .rounded))
                                .foregroundColor(Color(red: 0.2, green: 0.5, blue: 0.3))
                        }
                        .padding(.horizontal, 16)
                        .padding(.vertical, 8)
                        .background(Capsule().fill(Color(red: 0.9, green: 0.98, blue: 0.92)))
                        .transition(.move(edge: .bottom).combined(with: .opacity))
                    }

                    Button(action: {
                        executeSave()
                    }) {
                        HStack(spacing: 8) {
                            if isSaving {
                                ProgressView()
                                    .tint(.white)
                            } else {
                                Image(systemName: "arrow.down.circle.fill")
                                    .font(.system(size: 20, weight: .bold))
                            }
                            Text(String(localized: "btn_save_photo", defaultValue: "Save Doll"))
                                .font(.system(size: 18, weight: .bold, design: .rounded))
                        }
                        .foregroundColor(.white)
                        .frame(maxWidth: .infinity)
                        .frame(height: 54)
                        .background(
                            LinearGradient(
                                colors: [Color(red: 1.0, green: 0.46, blue: 0.65), Color(red: 1.0, green: 0.55, blue: 0.75)],
                                startPoint: .leading,
                                endPoint: .trailing
                            )
                        )
                        .clipShape(Capsule())
                        .shadow(color: Color.pink.opacity(0.3), radius: 8, x: 0, y: 4)
                    }
                    .disabled(isSaving)
                }
                .padding(.horizontal, 28)
                .padding(.bottom, 24)
            }
        }
        .onAppear {
            renderPreview()
        }
        .sheet(isPresented: $isSharePresented) {
            if let img = shareItem {
                ActivityView(activityItems: [img])
            }
        }
    }

    private func renderPreview() {
        DispatchQueue.global(qos: .userInitiated).async {
            let base = DollCanvasCompositor.shared.compositeBaseLayers(look: look, targetWidth: 480, targetHeight: 720)
            let overlay = DollCanvasCompositor.shared.compositeOverlayLayers(look: look, targetWidth: 480, targetHeight: 720)
            DispatchQueue.main.async {
                self.baseImage = base
                self.overlayImage = overlay
            }
        }
    }

    private func executeSave() {
        guard !isSaving else { return }
        isSaving = true

        DispatchQueue.global(qos: .userInitiated).async {
            let master = DollImageExporter.shared.renderMasterImage(look: self.look)
            let thumbnail = DollImageExporter.shared.createThumbnail(masterImage: master)

            // Save to Photos
            Task {
                try? await DollImageExporter.shared.saveToPhotoLibrary(image: master)
            }

            // Save to Collection
            let savedEntity = try? SavedLooksRepository.shared.saveLook(
                look: self.look,
                thumbnailImage: thumbnail,
                existingId: self.editingLookId
            )

            DispatchQueue.main.async {
                self.isSaving = false
                SoundManager.shared.playSave()
                withAnimation {
                    self.showSuccessBanner = true
                }
                if let entity = savedEntity {
                    self.onSaved(entity.id)
                }
            }
        }
    }

    private func prepareShare() {
        DispatchQueue.global(qos: .userInitiated).async {
            let master = DollImageExporter.shared.renderMasterImage(look: self.look)
            DispatchQueue.main.async {
                self.shareItem = master
                self.isSharePresented = true
            }
        }
    }
}

/// SwiftUI wrapper for UIActivityViewController
private struct ActivityView: UIViewControllerRepresentable {
    let activityItems: [Any]

    func makeUIViewController(context: Context) -> UIActivityViewController {
        UIActivityViewController(activityItems: activityItems, applicationActivities: nil)
    }

    func updateUIViewController(_ uiViewController: UIActivityViewController, context: Context) {}
}

