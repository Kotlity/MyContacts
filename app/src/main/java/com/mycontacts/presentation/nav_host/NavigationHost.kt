package com.mycontacts.presentation.nav_host

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Environment
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.mycontacts.presentation.general_content_screen.GeneralContentScreen
import com.mycontacts.presentation.main.events.MainEvent
import com.mycontacts.presentation.main.screen.MainScreen
import com.mycontacts.presentation.main.viewmodels.MainViewModel
import com.mycontacts.presentation.pager.screen.PagerScreen
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
                PagerScreen(onEvent = pagerViewModel::onEvent)
            }
            composable(ScreenRoutes.Main.route) { backStackEntry ->
                val context = LocalContext.current
                val contentResolver = context.contentResolver
                val mainViewModel: MainViewModel = hiltViewModel()
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    mainViewModel.onEvent(contentResolver, MainEvent.OnMainViewModelInitializing(
                        Environment.isExternalStorageManager(),
                        ContextCompat.checkSelfPermission(context, Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED,
                        ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_CONTACTS) == PackageManager.PERMISSION_GRANTED)
                    )
                }

                MainScreen(
                    mainViewModel,
                    event = { mainEvent ->
                        mainViewModel.onEvent(contentResolver = contentResolver, mainEvent)
                    },
                    onContactInfoClicked = { contactInfo ->
//                        backStackEntry.savedStateHandle.set()
                    }
                )
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