import SwiftUI

/// Main Dress-Up studio screen matching the reference app UI and Android implementation.
public struct DressUpView: View {
    @Bindable var viewModel: DressUpViewModel
    public var onNavigateBack: () -> Void
    public var onConfirmLook: (CharacterLook, Int64?) -> Void

    public init(
        viewModel: DressUpViewModel,
        onNavigateBack: @escaping () -> Void = {},
        onConfirmLook: @escaping (CharacterLook, Int64?) -> Void = { _, _ in }
    ) {
        self.viewModel = viewModel
        self.onNavigateBack = onNavigateBack
        self.onConfirmLook = onConfirmLook
    }

    public var body: some View {
        ZStack {
            // Background wallpaper or soft pastel gradient
            LinearGradient(
                colors: [Color(red: 1.0, green: 0.94, blue: 0.96), Color(red: 0.93, green: 0.95, blue: 0.98)],
                startPoint: .top,
                endPoint: .bottom
            )
            .ignoresSafeArea()

            VStack(spacing: 0) {
                // Top section: Canvas + Side button columns
                ZStack {
                    DollCanvasView(
                        look: viewModel.currentLook,
                        baseImage: viewModel.baseCompositeImage,
                        overlayImage: viewModel.overlayCompositeImage,
                        sparkleTrigger: viewModel.sparkleTrigger
                    )
                    .padding(.vertical, 8)

                    // Left buttons
                    HStack {
                        LeftSideButtonColumn(
                            onBack: {
                                SoundManager.shared.playTap()
                                onNavigateBack()
                            },
                            onReset: {
                                SoundManager.shared.playTap()
                                viewModel.showResetAlert()
                            },
                            onRandomize: {
                                SoundManager.shared.playTap()
                                viewModel.randomizeOutfit()
                            }
                        )
                        .padding(.leading, 12)
                        Spacer()
                    }

                    // Right buttons
                    HStack {
                        Spacer()
                        RightSideButtonColumn(
                            onConfirm: {
                                SoundManager.shared.playTap()
                                onConfirmLook(viewModel.currentLook, viewModel.editingLookId)
                            },
                            onPaletteToggle: {
                                SoundManager.shared.playTap()
                                viewModel.togglePalette()
                            },
                            onBackgroundPicker: {
                                SoundManager.shared.playTap()
                                viewModel.isBackgroundPickerVisible = true
                            }
                        )
                        .padding(.trailing, 12)
                    }

                    // Floating color palette panel
                    if viewModel.isPaletteOpen, let pal = viewModel.activePalette {
                        HStack {
                            Spacer()
                            PalettePickerView(
                                palette: pal,
                                onSelectSwatch: { swatch in
                                    SoundManager.shared.playTap()
                                    viewModel.selectColorSwatch(swatch)
                                },
                                onClose: {
                                    SoundManager.shared.playTap()
                                    viewModel.closePalette()
                                }
                            )
                            .padding(.trailing, 64)
                        }
                        .transition(.scale.combined(with: .opacity))
                    }
                }
                .frame(maxHeight: UIScreen.main.bounds.height * 0.48)

                // Divider / Card container for categories and grid
                VStack(spacing: 0) {
                    // Two-level category tab bar
                    CategoryTabsView(
                        groups: viewModel.categoryGroups,
                        selectedGroup: viewModel.selectedGroup,
                        selectedSubcategory: viewModel.selectedSubcategory,
                        onGroupSelected: { group in
                            SoundManager.shared.playTap()
                            viewModel.selectCategoryGroup(group)
                        },
                        onSubcategorySelected: { subcat in
                            SoundManager.shared.playTap()
                            viewModel.selectSubcategory(subcat)
                        }
                    )
                    .padding(.top, 4)

                    // Multi-row items grid
                    ItemGridView(
                        items: viewModel.currentSubcategoryItems,
                        currentLook: viewModel.currentLook,
                        onItemTap: { item in
                            SoundManager.shared.playEquip()
                            viewModel.selectItem(item)
                        }
                    )
                }
                .background(
                    RoundedRectangle(cornerRadius: 24)
                        .fill(Color(red: 0.98, green: 0.97, blue: 1.0).opacity(0.95))
                        .shadow(color: Color.black.opacity(0.06), radius: 8, x: 0, y: -4)
                )
            }

            // Loading overlay
            if viewModel.isLoading {
                ProgressView()
                    .scaleEffect(1.4)
            }
        }
        .alert("Reset Outfit?", isPresented: $viewModel.isResetDialogVisible) {
            Button("Reset", role: .destructive) {
                SoundManager.shared.playTap()
                viewModel.confirmResetOutfit()
            }
            Button("Cancel", role: .cancel) {
                SoundManager.shared.playTap()
                viewModel.dismissResetAlert()
            }
        } message: {
            Text("Are you sure you want to reset clothes to default?")
        }
        .sheet(isPresented: $viewModel.isBackgroundPickerVisible) {
            BackgroundPickerSheet(viewModel: viewModel)
        }
    }
}

/// Simple modal sheet to pick backgrounds
private struct BackgroundPickerSheet: View {
    @Bindable var viewModel: DressUpViewModel
    @Environment(\.dismiss) private var dismiss

    var backgrounds: [CatalogItem] {
        viewModel.catalog.filter { $0.categoryEnum == .background }
    }

    var body: some View {
        NavigationStack {
            ScrollView {
                LazyVGrid(columns: [GridItem(.adaptive(minimum: 100, maximum: 140), spacing: 12)], spacing: 12) {
                    // "None" option
                    Button(action: {
                        SoundManager.shared.playTap()
                        viewModel.selectBackground(nil)
                        dismiss()
                    }) {
                        VStack {
                            ZStack {
                                RoundedRectangle(cornerRadius: 12)
                                    .fill(Color.gray.opacity(0.15))
                                Image(systemName: "circle.slash")
                                    .font(.system(size: 24))
                                    .foregroundColor(.gray)
                            }
                            .frame(height: 100)
                            Text("None")
                                .font(.caption)
                                .foregroundColor(.primary)
                        }
                    }

                    ForEach(backgrounds) { bg in
                        Button(action: {
                            SoundManager.shared.playTap()
                            viewModel.selectBackground(bg)
                            dismiss()
                        }) {
                            VStack {
                                if let img = ImageCache.shared.loadDownsampledImage(relativePath: bg.thumbnail.isEmpty ? bg.file : bg.thumbnail, maxPixelSize: 150) {
                                    Image(uiImage: img)
                                        .resizable()
                                        .aspectRatio(contentMode: .fill)
                                        .frame(height: 100)
                                        .clipped()
                                        .clipShape(RoundedRectangle(cornerRadius: 12))
                                }
                                Text(bg.nameEn)
                                    .font(.caption)
                                    .lineLimit(1)
                                    .foregroundColor(.primary)
                            }
                        }
                    }
                }
                .padding()
            }
            .navigationTitle("Choose Wallpaper")
            .navigationBarTitleDisplayMode(.inline)
            .toolbar {
                ToolbarItem(placement: .topBarTrailing) {
                    Button("Done") { dismiss() }
                }
            }
        }
    }
}
