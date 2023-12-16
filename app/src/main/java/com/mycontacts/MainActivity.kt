package com.mycontacts

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.mycontacts.presentation.initial.events.InitialEvent
import com.mycontacts.presentation.initial.viewmodels.InitialViewModel
import com.mycontacts.presentation.nav_host.NavigationHost
import com.mycontacts.presentation.ui.theme.MyContactsTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val initialViewModel by viewModels<InitialViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen().setKeepOnScreenCondition {
            initialViewModel.isShouldShowSplashScreen
        }

        setContent {
            initialViewModel.apply {
                onEvent(InitialEvent.UpdateIsDarkUiModePreferences(isSystemInDarkTheme()))
                onEvent(InitialEvent.RetrieveIsDarkUiModePreferences)
            }

            val isDarkUiMode = initialViewModel.isDarkUiMode
            MyContactsTheme(darkTheme = isDarkUiMode) {
                val startDestination = initialViewModel.startDestination
                NavigationHost(startDestination)
            }
        }
    }
}