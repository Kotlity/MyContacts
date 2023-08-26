package com.mycontacts

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mycontacts.presentation.pager.Pager
import com.mycontacts.presentation.pager.viewmodels.PagerViewModel
import com.mycontacts.presentation.ui.theme.MyContactsTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val pagerViewModel by viewModels<PagerViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()

        setContent {
            MyContactsTheme {

                val hasAlreadyPressedButton by pagerViewModel.hasAlreadyPressedButtonState.collectAsStateWithLifecycle()

                if (!hasAlreadyPressedButton) {
                    Pager(onEvent = pagerViewModel::onEvent)
                } else {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "User has already pressed start button")
                    }
                }
            }
        }
    }
}