import SwiftUI

/// App navigation destinations replicating Android `Screen.kt`.
public enum AppRoute: Hashable {
    case onboarding
    case characterSelect
    case dressUp
    case confirm
    case myCollection
    case settings
}

/// Root Navigation controller replicating Android `AppNavHost.kt`.
public struct AppNavigation: View {
    @State private var isSplashActive: Bool = true
    @State private var navigationPath = NavigationPath()
    @State private var dressUpViewModel = DressUpViewModel()

    public init() {}

    public var body: some View {
        ZStack {
            if isSplashActive {
                SplashScreen {
                    withAnimation(.easeInOut(duration: 0.35)) {
                        isSplashActive = false
                    }
                    if !UserPreferencesService.shared.onboardingCompleted {
                        navigationPath.append(AppRoute.onboarding)
                    }
                }
                .transition(.opacity)
            } else {
                NavigationStack(path: $navigationPath) {
                    HomeView(
                        onPlay: {
                            navigationPath.append(AppRoute.characterSelect)
                        },
                        onCollection: {
                            navigationPath.append(AppRoute.myCollection)
                        },
                        onSettings: {
                            navigationPath.append(AppRoute.settings)
                        }
                    )
                    .navigationDestination(for: AppRoute.self) { route in
                        switch route {
                        case .onboarding:
                            OnboardingView {
                                navigationPath.removeLast()
                            }
                            .navigationBarBackButtonHidden()

                        case .characterSelect:
                            CharacterSelectView(
                                onSelectPreset: { preset in
                                    dressUpViewModel.loadPreset(preset)
                                    navigationPath.append(AppRoute.dressUp)
                                },
                                onBack: {
                                    navigationPath.removeLast()
                                }
                            )
                            .navigationBarBackButtonHidden()

                        case .dressUp:
                            DressUpView(
                                viewModel: dressUpViewModel,
                                onNavigateBack: {
                                    navigationPath.removeLast()
                                },
                                onConfirmLook: { _, _ in
                                    navigationPath.append(AppRoute.confirm)
                                }
                            )
                            .navigationBarBackButtonHidden()

                        case .confirm:
                            ConfirmView(
                                look: dressUpViewModel.currentLook,
                                editingLookId: dressUpViewModel.editingLookId,
                                onBackToEdit: {
                                    navigationPath.removeLast()
                                },
                                onSaved: { savedId in
                                    dressUpViewModel.editingLookId = savedId
                                }
                            )
                            .navigationBarBackButtonHidden()

                        case .myCollection:
                            MyCollectionView(
                                onBack: {
                                    navigationPath.removeLast()
                                },
                                onCreateNew: {
                                    dressUpViewModel.loadPreset(CatalogService.shared.getPresetCharacters().first!)
                                    navigationPath.append(AppRoute.dressUp)
                                },
                                onEditLook: { look, lookId in
                                    dressUpViewModel.currentLook = look
                                    dressUpViewModel.editingLookId = lookId
                                    dressUpViewModel.refreshComposites()
                                    navigationPath.append(AppRoute.dressUp)
                                }
                            )
                            .navigationBarBackButtonHidden()

                        case .settings:
                            SettingsView(
                                onBack: {
                                    navigationPath.removeLast()
                                }
                            )
                            .navigationBarBackButtonHidden()
                        }
                    }
                }
            }
        }
    }
}

