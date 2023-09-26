package com.mycontacts.presentation.main.screen

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import com.mycontacts.R
import com.mycontacts.presentation.main.composables.ContactList
import com.mycontacts.presentation.main.events.MainEvent
import com.mycontacts.presentation.main.composables.CustomProgressBar
import com.mycontacts.presentation.main.composables.CustomSearchBar
import com.mycontacts.presentation.main.composables.EmptyContacts
import com.mycontacts.presentation.main.composables.PermissionToAllFilesAlertDialog
import com.mycontacts.presentation.main.viewmodels.MainViewModel
import com.mycontacts.utils.Constants.contactsNotFound
import com.mycontacts.utils.Constants.dismissSnackbarActionLabel
import com.mycontacts.utils.Constants.onDismissButtonClicked
import kotlinx.coroutines.launch

@Composable
fun MainScreen(
    mainViewModel: MainViewModel,
    event: (MainEvent) -> Unit
) {

    val isUserHasPermissionsForMainScreen = mainViewModel.isUserHasPermissionsForMainScreen
    val contactsState = mainViewModel.contactsState
    val contactsSearchState = mainViewModel.contactsSearchState

    val context = LocalContext.current

    val snackbarHostState = remember {
        SnackbarHostState()
    }

    val coroutineScope = rememberCoroutineScope()

    val permissionToAccessAllFilesLauncher = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            event(MainEvent.UpdateIsUserHasPermissionToAccessAllFiles(Environment.isExternalStorageManager()))
        }
    }

    val permissionToReadContactsLauncher = rememberLauncherForActivityResult(contract = ActivityResultContracts.RequestPermission()) { isGranted ->
        event(MainEvent.UpdateIsUserHasPermissionToReadContacts(isGranted))
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
        ) {
            if (!isUserHasPermissionsForMainScreen.isUserHasPermissionToAccessAllFiles) {
                PermissionToAllFilesAlertDialog(
                    onConfirmButtonClick = {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                            val permissionToAccessAllFilesIntent = Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION).apply {
                                addCategory(Intent.CATEGORY_DEFAULT)
                                data = Uri.parse(String.format("package:%s", context.applicationContext.packageName))
                            }
                            permissionToAccessAllFilesLauncher.launch(permissionToAccessAllFilesIntent)
                        }
                    },
                    onDismissButtonClick = {
                        coroutineScope.launch {
                            snackbarHostState.showSnackbar(onDismissButtonClicked, dismissSnackbarActionLabel, duration = SnackbarDuration.Long)
                        }
                    }
                )
            }

            LaunchedEffect(key1 = isUserHasPermissionsForMainScreen) {
                if (isUserHasPermissionsForMainScreen.isUserHasPermissionToAccessAllFiles && !isUserHasPermissionsForMainScreen.isUserHasPermissionToReadContacts) {
                    permissionToReadContactsLauncher.launch(Manifest.permission.READ_CONTACTS)
                }
            }

            if (isUserHasPermissionsForMainScreen.isUserHasPermissionToAccessAllFiles && isUserHasPermissionsForMainScreen.isUserHasPermissionToReadContacts) {
                event(MainEvent.GetAllContacts)
                CustomSearchBar(
                    contactsSearchState = contactsSearchState,
                    onQueryChangeEvent = { event(MainEvent.SearchContact(it)) },
                    onUpdateSearchBarEvent = { event(MainEvent.UpdateSearchBarState(it)) },
                    onClearSearchQueryEvent = { event(MainEvent.ClearSearchQuery) }
                ) {
                    if (contactsSearchState.isLoading) {
                        CustomProgressBar(modifier = Modifier.fillMaxSize())
                    }
                    if (contactsSearchState.contacts.isNotEmpty()) {
                        ContactList(
                            modifier = Modifier.fillMaxSize(),
                            contacts = contactsSearchState.contacts,
                            onContactClick = {
                                event(MainEvent.OnSearchContactClick(it))
                            }
                        )
                    }
                    if (contactsSearchState.contacts.isEmpty() && !contactsSearchState.isLoading) {
                        EmptyContacts(
                            modifier = Modifier.fillMaxSize(),
                            message = contactsSearchState.errorMessage ?: contactsNotFound,
                            imagePainter = painterResource(id = R.drawable.icon_not_found)
                        )
                    }
                }
                AnimatedVisibility(visible = contactsState.isLoading) {
                    CustomProgressBar(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                    )
                }
                if (contactsState.contacts.isNotEmpty()) {
                    ContactList(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        contacts = contactsState.contacts,
                        onContactClick = {
                            event(MainEvent.OnGeneralContactClick(it))
                        }
                    )
                }
                if (contactsState.contacts.isEmpty() && !contactsState.isLoading) {
                    EmptyContacts(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        message = contactsState.errorMessage,
                        imagePainter = painterResource(id = R.drawable.no_image_contact)
                    )
                }
            }
        }
    }
}