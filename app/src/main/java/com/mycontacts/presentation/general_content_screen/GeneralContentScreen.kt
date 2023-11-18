package com.mycontacts.presentation.general_content_screen

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.navigation.NavHostController
import com.mycontacts.presentation.navigation_bar.CustomNavigationBar
import com.mycontacts.presentation.top_app_bar.CustomTopAppBar
import com.mycontacts.utils.ScreenRoutes

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GeneralContentScreen(
    currentRoute: String,
    navHostController: NavHostController,
    content: @Composable (Modifier) -> Unit
) {
    val topAppBarScrollBehaviour = TopAppBarDefaults.enterAlwaysScrollBehavior()

    val isShowTopAppBar = currentRoute != ScreenRoutes.Pager.route

    val isShowNavigationBar = when (currentRoute) {
        ScreenRoutes.Pager.route -> false
        ScreenRoutes.ContactOperations.route -> false
        else -> true
    }

    Scaffold(
        modifier = if (currentRoute == ScreenRoutes.Main.route) Modifier
            .fillMaxSize()
            .nestedScroll(topAppBarScrollBehaviour.nestedScrollConnection)
        else Modifier.fillMaxSize(),
        topBar = {
            if (isShowTopAppBar) {
                CustomTopAppBar(
                    route = currentRoute,
                    scrollBehavior = topAppBarScrollBehaviour,
                    onNavigationIconClick = {
                        navHostController.popBackStack()
                    }
                )
            }
        },
        bottomBar = {
            if (isShowNavigationBar) {
                CustomNavigationBar(
                    route = currentRoute
                ) { sectionRoute ->
                    navHostController.navigate(sectionRoute) {
                        popUpTo(sectionRoute) {
                            inclusive = true
                        }
                        launchSingleTop = true
                    }
                }
            }
        }
    ) { paddingValues ->
        content(
            Modifier
                .fillMaxSize()
                .padding(paddingValues))
    }
}