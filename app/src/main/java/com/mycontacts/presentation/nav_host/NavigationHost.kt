package com.mycontacts.presentation.nav_host

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.mycontacts.presentation.general_content_screen.GeneralContentScreen
import com.mycontacts.presentation.pager.Pager
import com.mycontacts.presentation.pager.viewmodels.PagerViewModel
import com.mycontacts.utils.ScreenRoutes

@Composable
fun NavigationHost(
    startDestination: String
) {
    val navHostController = rememberNavController()
    val currentRoute = navHostController.currentBackStackEntryAsState().value?.destination?.route ?: startDestination

    GeneralContentScreen(
        currentRoute = currentRoute,
        navHostController = navHostController
    ) { modifier ->
        NavHost(
            modifier = modifier,
            navController = navHostController,
            startDestination = startDestination
        ) {
            composable(ScreenRoutes.Pager.route) {
                val pagerViewModel: PagerViewModel = hiltViewModel()
                Pager(onEvent = pagerViewModel::onEvent)
            }
            composable(ScreenRoutes.Main.route) {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(10.dp),
                    verticalArrangement = Arrangement.spacedBy(5.dp, Alignment.CenterVertically),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    items(100) { index ->
                        Text(text = "${index.plus(1)}.Testing in Main Screen")
                    }
                }
            }
            composable(ScreenRoutes.Settings.route) {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(10.dp),
                    verticalArrangement = Arrangement.spacedBy(5.dp, Alignment.CenterVertically),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    items(100) { index ->
                        Text(text = "${index.plus(1)}.Testing in Settings Screen")
                    }
                }
            }
        }
    }
}