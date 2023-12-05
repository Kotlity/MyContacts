package com.mycontacts.presentation.nav_host

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Environment
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.mycontacts.data.contacts.ContactInfo
import com.mycontacts.presentation.contact_operations.screen.ContactOperationsScreen
import com.mycontacts.presentation.general_content_screen.GeneralContentScreen
import com.mycontacts.presentation.main.events.MainEvent
import com.mycontacts.presentation.main.screen.MainScreen
import com.mycontacts.presentation.main.viewmodels.MainViewModel
import com.mycontacts.presentation.pager.screen.PagerScreen
import com.mycontacts.presentation.pager.viewmodels.PagerViewModel
import com.mycontacts.utils.Constants.contactInfoKey
import com.mycontacts.utils.ScreenRoutes
import com.mycontacts.utils.contactOperationsViewModelCreator
import com.mycontacts.utils.navigateWithArgument
import com.mycontacts.utils.retrieveArgument

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
            composable(ScreenRoutes.Main.route) {
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
                    editContactInfo = { contactInfo ->
                        navHostController.navigateWithArgument(contactInfoKey, contactInfo, ScreenRoutes.ContactOperations)
                    }
                )
            }
            composable(ScreenRoutes.ContactOperations.route) { navBackStackEntry ->
                val contactInfo = navHostController.retrieveArgument<ContactInfo>(contactInfoKey)
                val context = LocalContext.current
                val contactOperationsViewModel = contactOperationsViewModelCreator(
                    context = context,
                    viewModelStoreOwner = navBackStackEntry,
                    contactInfo = contactInfo
                )
                ContactOperationsScreen(
                    contactOperationsViewModel = contactOperationsViewModel,
                    event = contactOperationsViewModel::onEvent,
                    onNavigateUp = { navHostController.navigateUp() }
                )
            }
            composable(ScreenRoutes.Settings.route) {

            }
        }
    }
}