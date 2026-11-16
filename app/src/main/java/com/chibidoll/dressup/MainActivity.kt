package com.chibidoll.dressup

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.chibidoll.dressup.ui.dressup.DressUpViewModel
import com.chibidoll.dressup.ui.navigation.AppNavHost
import com.chibidoll.dressup.ui.sound.SoundManager
import com.chibidoll.dressup.ui.theme.ChibiDollTheme

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val app = application as ChibiApplication
        val catalogRepository = app.catalogRepository
        val savedLooksRepository = app.savedLooksRepository
        val userPreferencesRepository = app.userPreferencesRepository

        setContent {
            ChibiDollTheme {
                val navController = rememberNavController()
                val viewModel: DressUpViewModel = viewModel(
                    factory = object : ViewModelProvider.Factory {
                        @Suppress("UNCHECKED_CAST")
                        override fun <T : ViewModel> create(modelClass: Class<T>): T {
                            return DressUpViewModel(catalogRepository) as T
                        }
                    }
                )

                AppNavHost(
                    navController = navController,
                    viewModel = viewModel,
                    catalogRepository = catalogRepository,
                    savedLooksRepository = savedLooksRepository,
                    userPreferencesRepository = userPreferencesRepository
                )
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (isFinishing) {
            SoundManager.release()
        }
    }
}
