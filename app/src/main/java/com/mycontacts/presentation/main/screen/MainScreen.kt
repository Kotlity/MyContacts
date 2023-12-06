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
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
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
import androidx.compose.ui.res.stringResource
import com.mycontacts.R
import com.mycontacts.data.contacts.ContactInfo
import com.mycontacts.presentation.main.composables.ContactActionsModalBottomSheet
import com.mycontacts.presentation.main.composables.ContactGeneralList
import com.mycontacts.presentation.main.composables.ContactSearchList
import com.mycontacts.presentation.main.composables.ContactsOrderSection
import com.mycontacts.presentation.main.composables.CustomExtendedFloatingActionButton
import com.mycontacts.presentation.main.events.MainEvent
import com.mycontacts.presentation.main.composables.CustomProgressBar
import com.mycontacts.presentation.main.composables.CustomSearchBar
import com.mycontacts.presentation.main.composables.DialAlertDialog
import com.mycontacts.presentation.main.composables.EmptyContacts
import com.mycontacts.presentation.main.composables.PermissionToAllFilesAlertDialog
import com.mycontacts.presentation.main.composables.RadioButtonsSection
import com.mycontacts.presentation.main.composables.SearchContactsFilteringSection
import com.mycontacts.presentation.main.composables.SelectedContactsInfoHeader
import com.mycontacts.presentation.main.composables.WriteContactsPermissionRationaleAlertDialog
import com.mycontacts.presentation.main.viewmodels.MainViewModel
import com.mycontacts.utils.Constants.contactsNotFound
import com.mycontacts.utils.Constants.deleteContactSuccessful
import com.mycontacts.utils.Constants.deleteContactUndo
import com.mycontacts.utils.Constants.deleteSelectedContactsSuccessful
import com.mycontacts.utils.Constants.dialPart
import com.mycontacts.utils.Constants.dismissSnackbarActionLabel
import com.mycontacts.utils.Constants.onDismissButtonClicked
import com.mycontacts.utils.Constants.writeContactsPermissionNotGranted
import com.mycontacts.utils.Constants.writeContactsPermissionGranted
import com.mycontacts.utils.ContactAction
import com.mycontacts.utils.ContactsMethod
import com.mycontacts.utils.order.ContactOrder
import com.mycontacts.utils.order.ContactOrderType
import com.mycontacts.utils.StickyHeaderAction
import com.mycontacts.utils.hideBottomSheet
import com.mycontacts.utils.isAppHasPermission
import com.mycontacts.utils.showSnackbarWithAction
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    mainViewModel: MainViewModel,
    event: (MainEvent) -> Unit,
    editContactInfo: (ContactInfo) -> Unit,
    addContactInfo: () -> Unit
) {
    val isUserHasPermissionsForMainScreenState = mainViewModel.mainScreenPermissionsState
    val contactsState = mainViewModel.contactsState
    val contactsSearchState = mainViewModel.contactsSearchState
    val contactsOrderSectionVisibleState = mainViewModel.contactsOrderSectionVisibleState
    val contactActionsModalBottomSheetState = mainViewModel.modalBottomSheetState
    val writeContactsPermissionRationaleAlertDialogState = mainViewModel.writeContactsPermissionRationaleAlertDialog
    val dialAlertDialog = mainViewModel.dialAlertDialog
    val isSelectionGeneralModeActiveState = mainViewModel.isSelectionGeneralModeActive
    val isSelectionSearchModeActiveState = mainViewModel.isSelectionSearchModeActive
    val selectedGeneralContacts = contactsState.contacts.values.flatten().filter { contactInfo -> contactInfo.isSelected }
    val selectedSearchContacts = contactsSearchState.contacts.filter { searchContactInfo -> searchContactInfo.isSelected }

    val writeContactsPermissionResult = mainViewModel.writeContactsPermissionResult.receiveAsFlow()
    val deleteContactResult = mainViewModel.deleteContactResult.receiveAsFlow()
    val deleteSelectedContactsResult = mainViewModel.deleteSelectedContactsResult.receiveAsFlow()
    
    val isExpandedFloatingActionButtonState = mainViewModel.isExpandedFloatingActionButtonState
    
    val mutableInteractionSource = remember {
        MutableInteractionSource()
    }

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

    LaunchedEffect(key1 = isUserHasPermissionsForMainScreenState) {
        if (isUserHasPermissionsForMainScreenState.isUserHasPermissionToAccessAllFiles && !isUserHasPermissionsForMainScreenState.isUserHasPermissionToReadContacts) {
            permissionToReadContactsLauncher.launch(Manifest.permission.READ_CONTACTS)
        }
        if (isUserHasPermissionsForMainScreenState.isUserHasPermissionToAccessAllFiles && isUserHasPermissionsForMainScreenState.isUserHasPermissionToReadContacts && contactsState.contacts.isEmpty()) {
            event(MainEvent.GetAllContacts(ContactOrder.TimeStamp(ContactOrderType.Descending)))
        }
    }

    if (!isSelectionGeneralModeActiveState) {
        LaunchedEffect(key1 = lazyListState) {
            snapshotFlow { lazyListState.firstVisibleItemIndex }
                .map { firstVisibleItemIndex -> firstVisibleItemIndex > 0 }
                .distinctUntilChanged()
                .collect { isScrolling ->
                    event(MainEvent.UpdateContactOrderSectionVisibility(!isScrolling))
                }
        }
    }
    
    LaunchedEffect(key1 = mutableInteractionSource) {
        mutableInteractionSource.interactions
            .filterIsInstance<PressInteraction>()
            .distinctUntilChanged()
            .collect { pressInteraction ->
                if (pressInteraction is PressInteraction.Press) event(MainEvent.ChangeIsExpandedFloatingActionButtonState(true))
                else event(MainEvent.ChangeIsExpandedFloatingActionButtonState(false))
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
        deleteContactResult.collect { deleteContactResult ->
            snackbarHostState.showSnackbarWithAction(
                message = deleteContactResult.result,
                undo = if (deleteContactResult.result == deleteContactSuccessful) deleteContactUndo else null,
                onUndoClick = {
                    deleteContactResult.apply {
                        contactsMethod?.let { contactsMethod ->
                            contactInfoIndex?.let { index ->
                                contactInfo?.let { contactInfo ->
                                    event(MainEvent.RestoreSingleContactInfo(contactsMethod, index, contactInfo))
                                }
                            }
                        }
                    }
                }
            )
        }
    }

    LaunchedEffect(key1 = Unit) {
        deleteSelectedContactsResult.collect { deleteContactsResult ->
            snackbarHostState.showSnackbarWithAction(
                message = deleteContactsResult.message,
                undo = if (deleteContactsResult.message == deleteSelectedContactsSuccessful) deleteContactUndo else null,
                onUndoClick = {
                    val selectedContacts = deleteContactsResult.selectedContacts
                    val contactsMethod = deleteContactsResult.contactsMethod!!
                    event(MainEvent.RestoreSelectedContacts(selectedContacts, contactsMethod))
                }
            )
        }
    }

    LaunchedEffect(key1 = selectedGeneralContacts.size) {
        if (selectedGeneralContacts.isEmpty()) event(MainEvent.UpdateSelectionGeneralMode(false))
        else event(MainEvent.UpdateSelectionGeneralMode(true))
    }

    LaunchedEffect(key1 = selectedSearchContacts.size) {
        if (selectedSearchContacts.isEmpty()) event(MainEvent.UpdateSelectionSearchMode(false))
        else event(MainEvent.UpdateSelectionSearchMode(true))
    }

    if (!isUserHasPermissionsForMainScreenState.isUserHasPermissionToAccessAllFiles) {
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
                event(MainEvent.UpdateModalBottomSheetContactInfo(null, null, null))
            },
            sheetState = modalBottomSheetState,
            content = {
                ContactActionsModalBottomSheet(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = dimensionResource(id = R.dimen._20dp)),
                    onEditContactClick = {
                        if (isAppHasPermission(context, Manifest.permission.WRITE_CONTACTS)) {
                            coroutineScope.launch { hideBottomSheet(modalBottomSheetState, event) }
                            contactActionsModalBottomSheetState.contactInfo?.let { contactInfo -> editContactInfo(contactInfo) }
                        } else {
                            event(MainEvent.Permissions.UpdateWriteContactsPermissionRationaleAlertDialog(ContactAction.EDIT))
                            coroutineScope.launch { hideBottomSheet(modalBottomSheetState, event) }
                        }
                    },
                    onDeleteContactClick = {
                        if (isAppHasPermission(context, Manifest.permission.WRITE_CONTACTS)) {
                            coroutineScope.launch { hideBottomSheet(modalBottomSheetState, event) }
                            contactActionsModalBottomSheetState.apply {
                                index?.let { index ->
                                    contactInfo?.let { contactInfo ->
                                        contactsMethod?.let { contactsMethod ->
                                            event(MainEvent.DeleteSingleContactInfo(contactsMethod, contactInfo, index))
                                        }
                                    }
                                }
                            }
                        } else {
                            event(MainEvent.Permissions.UpdateWriteContactsPermissionRationaleAlertDialog(ContactAction.DELETE))
                            coroutineScope.launch { hideBottomSheet(modalBottomSheetState, event) }
                        }
                    },
                    onSelectedModeClick = {
                        coroutineScope.launch { hideBottomSheet(modalBottomSheetState, event) }
                        contactActionsModalBottomSheetState.apply {
                            index?.let { index ->
                                contactInfo?.let { contactInfo ->
                                    contactsMethod?.let { contactsMethod ->
                                        when(contactsMethod) {
                                            ContactsMethod.GENERAL -> event(MainEvent.UpdateIsContactSelectedFieldByClickOnContactInfo(contactInfo.firstName.first(), index))
                                            ContactsMethod.SEARCH -> event(MainEvent.UpdateIsSearchContactSelectedFieldByClickOnContactInfo(index))
                                        }
                                    }
                                }
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

    dialAlertDialog.apply {
        if (isShouldShow) {
            dialContactInfo?.let { dialContactInfo ->
                DialAlertDialog(
                    contactInfo = dialContactInfo,
                    onConfirmClick = {
                        event(MainEvent.UpdateDialAlertDialog(null))
                        Intent(Intent.ACTION_DIAL).apply {
                            data = Uri.fromParts(dialPart, dialContactInfo.phoneNumber, null)
                            flags = Intent.FLAG_ACTIVITY_NEW_TASK
                            context.startActivity(this)
                        }
                    },
                    onDismissClick = { event(MainEvent.UpdateDialAlertDialog(null)) }
                )
            }
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        floatingActionButton = {
            CustomExtendedFloatingActionButton(
                modifier = Modifier
                    .padding(dimensionResource(id = R.dimen._5dp)),
                text = stringResource(id = R.string.addContact),
                icon = Icons.Default.Add,
                onClick = addContactInfo,
                isExpanded = isExpandedFloatingActionButtonState,
                mutableInteractionSource = mutableInteractionSource
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
        ) {
            AnimatedVisibility(
                visible = !isSelectionGeneralModeActiveState
            ) {
                CustomSearchBar(
                    contactsSearchState = contactsSearchState,
                    isSearchBarEnabled = !isSelectionSearchModeActiveState,
                    onQueryChangeEvent = { event(MainEvent.SearchContact(it, contactsSearchState.searchContactOrder)) },
                    onUpdateSearchBarEvent = { event(MainEvent.UpdateSearchBarState(it)) },
                    onClearSearchQueryEvent = { event(MainEvent.ClearSearchQuery) }
                ) {
                    AnimatedVisibility(visible = contactsSearchState.isLoading) {
                        CustomProgressBar(modifier = Modifier.fillMaxSize())
                    }
                    AnimatedVisibility(
                        visible = isSelectionSearchModeActiveState,
                        enter = slideInHorizontally(),
                        exit = slideOutHorizontally(targetOffsetX = { offsetX -> -offsetX })
                    ) {
                        SelectedContactsInfoHeader(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = dimensionResource(id = R.dimen._10dp)),
                            selectedContactsInfoCount = selectedSearchContacts.size,
                            onDeleteIconClick = {
                                if (isAppHasPermission(context, Manifest.permission.WRITE_CONTACTS)) {
                                    event(MainEvent.DeleteSelectedContacts(selectedSearchContacts, ContactsMethod.SEARCH))
                                } else event(MainEvent.Permissions.UpdateWriteContactsPermissionRationaleAlertDialog(ContactAction.DELETE_MULTIPLE))
                            }
                        )
                    }
                    AnimatedVisibility(visible = !isSelectionSearchModeActiveState) {
                        SearchContactsFilteringSection(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = dimensionResource(id = R.dimen._5dp)),
                            isExpanded = contactsSearchState.isSearchDropdownMenuExpanded,
                            currentSearchContactOrder = contactsSearchState.searchContactOrder,
                            onSearchContactOrderClick = { event(MainEvent.OnSearchContactOrderClick(contactsSearchState.searchQuery, it)) } ,
                            onUpdateDropdownMenuVisibility = { event(MainEvent.UpdateSearchDropdownMenuState(it)) }
                        )
                    }
                    if (contactsSearchState.contacts.isNotEmpty()) {
                        ContactSearchList(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f),
                            contacts = contactsSearchState.contacts,
                            onContactClick = { index, contactInfo ->
                                if (!isSelectionSearchModeActiveState) event(MainEvent.UpdateDialAlertDialog(contactInfo))
                                else event(MainEvent.UpdateIsSearchContactSelectedFieldByClickOnContactInfo(index))
                            },
                            onLongContactClick = { index, contactInfo ->
                                if (!isSelectionSearchModeActiveState) {
                                    event(MainEvent.UpdateModalBottomSheetVisibility)
                                    event(MainEvent.UpdateModalBottomSheetContactInfo(ContactsMethod.SEARCH, index, contactInfo))
                                }
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
                    if (!isSelectionGeneralModeActiveState) {
                        event(MainEvent.UpdateContactOrderSectionVisibility(!contactsOrderSectionVisibleState))
                    }
                }
            )
            AnimatedVisibility(
                visible = isSelectionGeneralModeActiveState,
                enter = slideInHorizontally(),
                exit = slideOutHorizontally(targetOffsetX = { offsetX -> -offsetX })
            ) {
                SelectedContactsInfoHeader(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = dimensionResource(id = R.dimen._10dp)),
                    selectedContactsInfoCount = selectedGeneralContacts.size,
                    onDeleteIconClick = {
                        if (isAppHasPermission(context, Manifest.permission.WRITE_CONTACTS)) {
                            event(MainEvent.DeleteSelectedContacts(selectedGeneralContacts, ContactsMethod.GENERAL))
                        } else event(MainEvent.Permissions.UpdateWriteContactsPermissionRationaleAlertDialog(ContactAction.DELETE_MULTIPLE))
                    }
                )
            }
            AnimatedVisibility(visible = contactsOrderSectionVisibleState && !isSelectionGeneralModeActiveState) {
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
                    isAtLeastOneContactInfoSelected = { header ->
                        selectedGeneralContacts.any { selectedContactInfo -> selectedContactInfo.firstName.first() == header }
                    },
                    contactsMap = contactsState.contacts,
                    onContactClick = { index, contactInfo ->
                        if (!isSelectionGeneralModeActiveState) event(MainEvent.UpdateDialAlertDialog(contactInfo = contactInfo))
                        else event(MainEvent.UpdateIsContactSelectedFieldByClickOnContactInfo(contactInfo.firstName.first(), index))
                    },
                    onLongContactClick = { index, contactInfo ->
                        if (!isSelectionGeneralModeActiveState) {
                            event(MainEvent.UpdateModalBottomSheetVisibility)
                            event(MainEvent.UpdateModalBottomSheetContactInfo(ContactsMethod.GENERAL, index, contactInfo))
                        }
                    },
                    onStickyHeaderClick = { header ->
                         if (selectedGeneralContacts.any { selectedContactInfo -> selectedContactInfo.firstName.first() == header }) {
                             event(MainEvent.UpdateSelectedContactsByItsHeader(header, StickyHeaderAction.UNSELECT_ALL))
                         }
                         else event(MainEvent.UpdateSelectedContactsByItsHeader(header, StickyHeaderAction.SELECT_ALL))
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