package com.mycontacts.presentation.main.screen

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
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
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.core.content.ContextCompat
import com.mycontacts.R
import com.mycontacts.data.contacts.ContactInfo
import com.mycontacts.presentation.main.composables.ContactActionsModalBottomSheet
import com.mycontacts.presentation.main.composables.ContactGeneralList
import com.mycontacts.presentation.main.composables.ContactSearchList
import com.mycontacts.presentation.main.composables.ContactsOrderSection
import com.mycontacts.presentation.main.events.MainEvent
import com.mycontacts.presentation.main.composables.CustomProgressBar
import com.mycontacts.presentation.main.composables.CustomSearchBar
import com.mycontacts.presentation.main.composables.EmptyContacts
import com.mycontacts.presentation.main.composables.PermissionToAllFilesAlertDialog
import com.mycontacts.presentation.main.composables.RadioButtonsSection
import com.mycontacts.presentation.main.composables.SearchContactsFilteringSection
import com.mycontacts.presentation.main.composables.WriteContactsPermissionRationaleAlertDialog
import com.mycontacts.presentation.main.viewmodels.MainViewModel
import com.mycontacts.utils.Constants.contactsNotFound
import com.mycontacts.utils.Constants.deleteContactSuccessful
import com.mycontacts.utils.Constants.deleteContactUndo
import com.mycontacts.utils.Constants.dismissSnackbarActionLabel
import com.mycontacts.utils.Constants.onDismissButtonClicked
import com.mycontacts.utils.Constants.writeContactsPermissionNotGranted
import com.mycontacts.utils.Constants.writeContactsPermissionGranted
import com.mycontacts.utils.ContactAction
import com.mycontacts.utils.ContactOrder
import com.mycontacts.utils.ContactOrderType
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    mainViewModel: MainViewModel,
    event: (MainEvent) -> Unit,
    onContactInfoClicked: (ContactInfo) -> Unit
) {
    val isUserHasPermissionsForMainScreen = mainViewModel.mainScreenPermissionsState
    val contactsState = mainViewModel.contactsState
    val contactsSearchState = mainViewModel.contactsSearchState
    val contactsOrderSectionVisibleState = mainViewModel.contactsOrderSectionVisibleState
    val contactActionsModalBottomSheetState = mainViewModel.modalBottomSheetState
    val writeContactsPermissionRationaleAlertDialogState = mainViewModel.writeContactsPermissionRationaleAlertDialog
    val writeContactsPermissionResult = mainViewModel.writeContactsPermissionResult.receiveAsFlow()
    val deleteContactResult = mainViewModel.deleteContactResult.receiveAsFlow()

    val context = LocalContext.current

    val snackbarHostState = remember {
        SnackbarHostState()
    }

    val modalBottomSheetState = rememberModalBottomSheetState()

    val coroutineScope = rememberCoroutineScope()

    val lazyListState = rememberLazyListState()

    val permissionToAccessAllFilesLauncher = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            event(MainEvent.Permissions.UpdateIsUserHasPermissionToAccessAllFiles(Environment.isExternalStorageManager()))
        }
    }

    val permissionToReadContactsLauncher = rememberLauncherForActivityResult(contract = ActivityResultContracts.RequestPermission()) { isGranted ->
        event(MainEvent.Permissions.UpdateIsUserHasPermissionToReadContacts(isGranted))
    }

    val permissionToWriteContactsLauncher = rememberLauncherForActivityResult(contract = ActivityResultContracts.RequestPermission()) { isGranted ->
        event(MainEvent.UpdateWriteContactsPermissionResult(isGranted))
    }

    LaunchedEffect(key1 = isUserHasPermissionsForMainScreen) {
        if (isUserHasPermissionsForMainScreen.isUserHasPermissionToAccessAllFiles && !isUserHasPermissionsForMainScreen.isUserHasPermissionToReadContacts) {
            permissionToReadContactsLauncher.launch(Manifest.permission.READ_CONTACTS)
        }
        if (isUserHasPermissionsForMainScreen.isUserHasPermissionToAccessAllFiles && isUserHasPermissionsForMainScreen.isUserHasPermissionToReadContacts && contactsState.contacts.isEmpty()) {
            event(MainEvent.GetAllContacts(ContactOrder.TimeStamp(ContactOrderType.Descending)))
        }
    }

    LaunchedEffect(key1 = lazyListState) {
        snapshotFlow { lazyListState.firstVisibleItemIndex }
            .map { firstVisibleItemIndex -> firstVisibleItemIndex > 0 }
            .distinctUntilChanged()
            .collect { isScrolling ->
                event(MainEvent.UpdateContactOrderSectionVisibility(!isScrolling))
            }
    }

    LaunchedEffect(key1 = Unit) {
        writeContactsPermissionResult.collect { isGranted ->
            snackbarHostState.showSnackbar(
                message = if (isGranted) writeContactsPermissionGranted else writeContactsPermissionNotGranted,
                duration = SnackbarDuration.Short
            )
        }
    }

    LaunchedEffect(key1 = Unit) {
        deleteContactResult.collect { triple ->
            val snackbarResult = snackbarHostState.showSnackbar(
                message = triple.first,
                actionLabel = if (triple.first == deleteContactSuccessful) deleteContactUndo else null,
                duration = SnackbarDuration.Long
            )
            if (snackbarResult == SnackbarResult.ActionPerformed) {
                triple.apply {
                    second?.let { index ->
                        third?.let { contactInfo ->
                            event(MainEvent.RestoreContact(index, contactInfo))
                        }
                    }
                }
            }
        }
    }

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

    if (contactActionsModalBottomSheetState.isShouldShow) {
        ModalBottomSheet(
            onDismissRequest = {
                event(MainEvent.UpdateModalBottomSheetVisibility)
                event(MainEvent.UpdateModalBottomSheetContactInfo(null, null))
            },
            sheetState = modalBottomSheetState,
            content = {
                ContactActionsModalBottomSheet(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = dimensionResource(id = R.dimen._20dp)),
                    onEditContactClick = {
                        if (ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
                            coroutineScope.launch {
                                modalBottomSheetState.hide()
                                event(MainEvent.UpdateModalBottomSheetVisibility)
                            }
                        } else {
                            event(MainEvent.Permissions.UpdateWriteContactsPermissionRationaleAlertDialog(ContactAction.EDIT))
                            coroutineScope.launch {
                                modalBottomSheetState.hide()
                                event(MainEvent.UpdateModalBottomSheetVisibility)
                            }
                        }
                    },
                    onDeleteContactClick = {
                        if (ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
                            coroutineScope.launch {
                                modalBottomSheetState.hide()
                                event(MainEvent.UpdateModalBottomSheetVisibility)
                            }
                            contactActionsModalBottomSheetState.apply {
                                index?.let { index ->
                                    contactInfo?.let { contactInfo ->
                                        event(MainEvent.DeleteContact(index, contactInfo))
                                    }
                                }
                            }
                        } else {
                            event(MainEvent.Permissions.UpdateWriteContactsPermissionRationaleAlertDialog(ContactAction.DELETE))
                            coroutineScope.launch {
                                modalBottomSheetState.hide()
                                event(MainEvent.UpdateModalBottomSheetVisibility)
                            }
                        }
                    }
                )
            }
        )
    }

    if (writeContactsPermissionRationaleAlertDialogState.isShouldShow) {
        WriteContactsPermissionRationaleAlertDialog(
            contactAction = writeContactsPermissionRationaleAlertDialogState.contactAction,
            onConfirmButtonClick = {
                event(MainEvent.Permissions.ClearWriteContactsPermissionRationaleAlertDialog)
                permissionToWriteContactsLauncher.launch(Manifest.permission.WRITE_CONTACTS)
            },
            onDismissButtonClick = {
                event(MainEvent.Permissions.ClearWriteContactsPermissionRationaleAlertDialog)
            }
        )
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
            CustomSearchBar(
                contactsSearchState = contactsSearchState,
                onQueryChangeEvent = { event(MainEvent.SearchContact(it, contactsSearchState.searchContactOrder)) },
                onUpdateSearchBarEvent = { event(MainEvent.UpdateSearchBarState(it)) },
                onClearSearchQueryEvent = { event(MainEvent.ClearSearchQuery) }
            ) {
                AnimatedVisibility(visible = contactsSearchState.isLoading) {
                    CustomProgressBar(modifier = Modifier.fillMaxSize())
                }
                SearchContactsFilteringSection(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = dimensionResource(id = R.dimen._5dp)),
                    isExpanded = contactsSearchState.isSearchDropdownMenuExpanded,
                    currentSearchContactOrder = contactsSearchState.searchContactOrder,
                    onSearchContactOrderClick = { event(MainEvent.OnSearchContactOrderClick(contactsSearchState.searchQuery, it)) } ,
                    onUpdateDropdownMenuVisibility = { event(MainEvent.UpdateSearchDropdownMenuState(it)) }
                )
                if (contactsSearchState.contacts.isNotEmpty()) {
                    ContactSearchList(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        contacts = contactsSearchState.contacts,
                        onContactClick = { contactInfo ->
                            onContactInfoClicked(contactInfo)
                        },
                        onLongContactClick = { index, contactInfo ->

                        }
                    )
                }
                if (contactsSearchState.contacts.isEmpty() && !contactsSearchState.isLoading) {
                    EmptyContacts(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        message = contactsSearchState.errorMessage ?: contactsNotFound,
                        imagePainter = painterResource(id = R.drawable.icon_not_found)
                    )
                }
            }
            ContactsOrderSection(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        start = dimensionResource(id = R.dimen._10dp),
                        end = dimensionResource(id = R.dimen._10dp),
                        bottom = dimensionResource(id = R.dimen._5dp)
                    ),
                onIconClick = {
                    event(MainEvent.UpdateContactOrderSectionVisibility(!contactsOrderSectionVisibleState))
                }
            )
            AnimatedVisibility(visible = contactsOrderSectionVisibleState) {
                RadioButtonsSection(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(dimensionResource(id = R.dimen._10dp)),
                    currentContactOrder = contactsState.contactOrder,
                    onOrderClick = { contactOrder ->
                        event(MainEvent.GetAllContacts(contactOrder))
                    }
                )
            }
            AnimatedVisibility(visible = contactsState.isLoading) {
                CustomProgressBar(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                )
            }
            if (contactsState.contacts.isNotEmpty()) {
                ContactGeneralList(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    lazyListState = lazyListState,
                    contacts = contactsState.contacts,
                    onContactClick = { contactInfo ->
                        onContactInfoClicked(contactInfo)
                    },
                    onLongContactClick = { index, contactInfo ->
                        event(MainEvent.UpdateModalBottomSheetVisibility)
                        event(MainEvent.UpdateModalBottomSheetContactInfo(index, contactInfo))
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