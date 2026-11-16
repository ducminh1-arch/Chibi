package com.chibidoll.dressup.ui.navigation

sealed class Screen(val route: String) {
    data object Splash : Screen("splash")
    data object Onboarding : Screen("onboarding")
    data object Home : Screen("home")
    data object Settings : Screen("settings")
    data object CharacterSelect : Screen("character_select")
    data object DressUp : Screen("dress_up")
    data object Confirm : Screen("confirm")
    data object MyCollection : Screen("my_collection")
}
