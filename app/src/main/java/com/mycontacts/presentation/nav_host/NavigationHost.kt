package com.mycontacts.presentation.nav_host

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.mycontacts.presentation.pager.Pager
import com.mycontacts.presentation.pager.viewmodels.PagerViewModel
import com.mycontacts.utils.ScreenRoutes

@Composable
fun NavigationHost(
    pagerViewModel: PagerViewModel = hiltViewModel()
) {

    val navHostController = rememberNavController()

    val hasAlreadyPressedButton by pagerViewModel.hasAlreadyPressedButtonState.collectAsStateWithLifecycle()

    NavHost(
        navController = navHostController,
        startDestination = if (hasAlreadyPressedButton) ScreenRoutes.Main.route else ScreenRoutes.Pager.route
    ) {
        composable(ScreenRoutes.Pager.route) {
            Pager(
                onEvent = pagerViewModel::onEvent,
                navHostController
            )
        }
        composable(ScreenRoutes.Main.route) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "Main Screen")
            }
        }
    }
}