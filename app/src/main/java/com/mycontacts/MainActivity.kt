package com.mycontacts

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.mycontacts.presentation.initial.InitialViewModel
import com.mycontacts.presentation.nav_host.NavigationHost
import com.mycontacts.presentation.ui.theme.MyContactsTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val initialViewModel by viewModels<InitialViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen().setKeepOnScreenCondition {
            initialViewModel.isShouldShowSplashScreen
        }

        setContent {
            MyContactsTheme {
                val startDestination = initialViewModel.startDestination
                NavigationHost(startDestination)
            }
        }
    }
}