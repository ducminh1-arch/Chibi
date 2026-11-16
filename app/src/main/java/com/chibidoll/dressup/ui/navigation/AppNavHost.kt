package com.chibidoll.dressup.ui.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.chibidoll.dressup.data.local.UserPreferencesRepository
import com.chibidoll.dressup.data.repository.CatalogRepository
import com.chibidoll.dressup.data.repository.SavedLooksRepository
import com.chibidoll.dressup.ui.collection.MyCollectionScreen
import com.chibidoll.dressup.ui.confirm.ConfirmScreen
import com.chibidoll.dressup.ui.dressup.DressUpScreen
import com.chibidoll.dressup.ui.dressup.DressUpViewModel
import com.chibidoll.dressup.ui.home.HomeScreen
import com.chibidoll.dressup.ui.onboarding.OnboardingScreen
import com.chibidoll.dressup.ui.select.CharacterSelectScreen
import com.chibidoll.dressup.ui.settings.SettingsScreen
import com.chibidoll.dressup.ui.splash.SplashScreen
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

@Composable
fun AppNavHost(
    navController: NavHostController,
    viewModel: DressUpViewModel,
    catalogRepository: CatalogRepository,
    savedLooksRepository: SavedLooksRepository,
    userPreferencesRepository: UserPreferencesRepository,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val coroutineScope = rememberCoroutineScope()

    NavHost(
        navController = navController,
        startDestination = Screen.Splash.route,
        modifier = modifier,
        enterTransition = {
            slideIntoContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.Start,
                animationSpec = tween(350)
            ) + fadeIn(animationSpec = tween(350))
        },
        exitTransition = {
            slideOutOfContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.Start,
                animationSpec = tween(350)
            ) + fadeOut(animationSpec = tween(350))
        },
        popEnterTransition = {
            slideIntoContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.End,
                animationSpec = tween(350)
            ) + fadeIn(animationSpec = tween(350))
        },
        popExitTransition = {
            slideOutOfContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.End,
                animationSpec = tween(350)
            ) + fadeOut(animationSpec = tween(350))
        }
    ) {
        // 1. Splash Screen
        composable(
            route = Screen.Splash.route,
            enterTransition = { fadeIn(animationSpec = tween(400)) },
            exitTransition = { fadeOut(animationSpec = tween(400)) }
        ) {
            SplashScreen(
                onSplashFinished = {
                    coroutineScope.launch {
                        val onboardingDone = userPreferencesRepository.onboardingCompleted.first()
                        if (onboardingDone) {
                            navController.navigate(Screen.Home.route) {
                                popUpTo(Screen.Splash.route) { inclusive = true }
                            }
                        } else {
                            navController.navigate(Screen.Onboarding.route) {
                                popUpTo(Screen.Splash.route) { inclusive = true }
                            }
                        }
                    }
                }
            )
        }

        // 2. Onboarding Screen
        composable(
            route = Screen.Onboarding.route,
            enterTransition = { fadeIn(animationSpec = tween(400)) },
            exitTransition = { fadeOut(animationSpec = tween(400)) }
        ) {
            OnboardingScreen(
                onFinishOnboarding = {
                    coroutineScope.launch {
                        userPreferencesRepository.setOnboardingCompleted(true)
                        navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.Onboarding.route) { inclusive = true }
                        }
                    }
                }
            )
        }

        // 3. Home Screen
        composable(Screen.Home.route) {
            HomeScreen(
                currentLook = uiState.currentLook,
                onPlayClick = {
                    navController.navigate(Screen.CharacterSelect.route)
                },
                onCollectionClick = {
                    navController.navigate(Screen.MyCollection.route)
                },
                onSettingsClick = {
                    navController.navigate(Screen.Settings.route)
                }
            )
        }

        // 4. Character Select Screen
        composable(Screen.CharacterSelect.route) {
            CharacterSelectScreen(
                catalogRepository = catalogRepository,
                onPresetSelected = { preset ->
                    viewModel.loadPreset(preset)
                    navController.navigate(Screen.DressUp.route)
                },
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }

        // 4. Settings Screen
        composable(Screen.Settings.route) {
            SettingsScreen(
                userPreferencesRepository = userPreferencesRepository,
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }

        // 5. Dress-Up Screen
        composable(Screen.DressUp.route) {
            DressUpScreen(
                viewModel = viewModel,
                onNavigateBack = {
                    navController.popBackStack()
                },
                onConfirmLook = {
                    navController.navigate(Screen.Confirm.route)
                }
            )
        }

        // 6. Confirm Screen
        composable(Screen.Confirm.route) {
            ConfirmScreen(
                currentLook = uiState.currentLook,
                editingLookId = uiState.editingLookId,
                savedLooksRepository = savedLooksRepository,
                onBackToEdit = {
                    navController.popBackStack()
                },
                onLookSaved = { savedId ->
                    viewModel.updateSavedLookId(savedId)
                }
            )
        }

        // 7. My Collection Screen
        composable(Screen.MyCollection.route) {
            MyCollectionScreen(
                savedLooksRepository = savedLooksRepository,
                catalog = uiState.catalog,
                onBackClick = {
                    navController.popBackStack()
                },
                onCreateLookClick = {
                    viewModel.startNewLook()
                    navController.navigate(Screen.DressUp.route)
                },
                onEditLook = { restoredLook, lookId ->
                    viewModel.loadSavedLook(restoredLook, lookId)
                    navController.navigate(Screen.DressUp.route)
                }
            )
        }
    }
}
