package com.mycontacts.presentation.main.viewmodels

import android.content.ContentResolver
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mycontacts.data.contacts.ContactInfo
import com.mycontacts.domain.main.Main
import com.mycontacts.presentation.main.events.MainEvent
import com.mycontacts.presentation.main.states.ContactsSearchState
import com.mycontacts.presentation.main.states.ContactsState
import com.mycontacts.presentation.main.states.ModalBottomSheetState
import com.mycontacts.presentation.main.states.PermissionsForMainScreenState
import com.mycontacts.presentation.main.states.WriteContactsAlertDialogState
import com.mycontacts.utils.Constants.contactsNotFound
import com.mycontacts.utils.Constants.deleteContactNotSuccessful
import com.mycontacts.utils.Constants.deleteContactSuccessful
import com.mycontacts.utils.ContactAction
import com.mycontacts.utils.ContactOrder
import com.mycontacts.utils.Resources
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(private val main: Main): ViewModel() {

    var mainScreenPermissionsState by mutableStateOf(PermissionsForMainScreenState())
        private set

    var contactsState by mutableStateOf(ContactsState())
        private set

    var contactsSearchState by mutableStateOf(ContactsSearchState())
        private set

    var modalBottomSheetState by mutableStateOf(ModalBottomSheetState())
        private set

    var writeContactsPermissionRationaleAlertDialog by mutableStateOf(WriteContactsAlertDialogState())
        private set

    var writeContactsPermissionResult = Channel<Boolean>()
        private set

    var deleteContactResult = Channel<String>()
        private set

    var contactsOrderSectionVisibleState by derivedStateOf { mutableStateOf(true) }.value
        private set

    private var contactsJob: Job? = null

    private var searchJob: Job? = null

    fun onEvent(contentResolver: ContentResolver, mainEvent: MainEvent) {
        when(mainEvent) {
            is MainEvent.OnMainViewModelInitializing -> {
                onMainViewModelInitializing(mainEvent.permissionToAccessAllFiles, mainEvent.permissionToReadContacts, mainEvent.permissionToWriteContacts)
            }
            is MainEvent.Permissions.UpdateIsUserHasPermissionToAccessAllFiles -> {
                updateIsUserHasPermissionToAccessAllFiles(mainEvent.isUserHasPermissionToAccessAllFiles)
            }
            is MainEvent.Permissions.UpdateIsUserHasPermissionToReadContacts -> {
                updateIsUserHasPermissionToReadContacts(mainEvent.isUserHasPermissionToReadContacts)
            }
            is MainEvent.Permissions.UpdateIsUserHasPermissionToWriteContacts -> {
                updateIsUserHasPermissionToWriteContacts(mainEvent.isUserHasPermissionToWriteContacts)
            }
            is MainEvent.Permissions.UpdateWriteContactsPermissionRationaleAlertDialog -> {
                updateWriteContactsPermissionRationaleAlertDialog(mainEvent.contactAction)
            }
            MainEvent.Permissions.ClearWriteContactsPermissionRationaleAlertDialog -> {
                clearWriteContactsPermissionRationaleAlertDialog()
            }
            is MainEvent.GetAllContacts -> {
                if (checkIfTheSameContactOrderClicked(mainEvent.contactOrder)) return
                getAllContacts(contentResolver, mainEvent.contactOrder)
            }
            is MainEvent.SearchContact -> {
                searchContact(contentResolver, mainEvent.searchQuery, mainEvent.searchContactOrder)
            }
            is MainEvent.UpdateSearchDropdownMenuState -> {
                updateSearchDropdownMenuState(mainEvent.searchDropdownMenuVisibility)
            }
            is MainEvent.OnSearchContactOrderClick -> {
                if (checkIfTheSameContactOrderClicked(mainEvent.searchContactOrder) || mainEvent.searchQuery.isEmpty()) return
                onSearchContactOrderClick(contentResolver, mainEvent.searchQuery, mainEvent.searchContactOrder)
            }
            is MainEvent.UpdateSearchBarState -> {
                updateSearchBarState(mainEvent.isShouldShow)
            }
            is MainEvent.UpdateContactOrderSectionVisibility -> {
                updateContactOrderSectionVisibility(mainEvent.isSectionVisible)
            }
            is MainEvent.DeleteContact -> {
                deleteContact(contentResolver, mainEvent.contactInfo)
            }
            is MainEvent.UpdateModalBottomSheetContactInfo -> {
                updateModalBottomSheetContactInfo(mainEvent.contactInfo)
            }
            is MainEvent.UpdateWriteContactsPermissionResult -> {
                updateWriteContactsPermissionResult(mainEvent.isGranted)
            }
            MainEvent.ClearSearchQuery -> {
                clearSearchQuery()
            }
            MainEvent.UpdateModalBottomSheetVisibility -> {
                updateModalBottomSheetVisibility()
            }
        }
    }

    private fun onMainViewModelInitializing(isUserHasPermissionToAccessAllFiles: Boolean, isUserHasPermissionToReadContacts: Boolean, isUserHasPermissionToWriteContacts: Boolean) {
        mainScreenPermissionsState = mainScreenPermissionsState.copy(
            isUserHasPermissionToAccessAllFiles = isUserHasPermissionToAccessAllFiles,
            isUserHasPermissionToReadContacts = isUserHasPermissionToReadContacts,
            isUserHasPermissionToWriteContacts = isUserHasPermissionToWriteContacts
        )
    }

    private fun getAllContacts(contentResolver: ContentResolver, contactOrder: ContactOrder) {
        contactsJob?.cancel()
        contactsJob = main.getAllContacts(contentResolver, contactOrder).onEach { result ->
            contactsState = when (result) {
                is Resources.Success -> {
                    contactsState.copy(isLoading = false, errorMessage = "", contacts = result.data ?: emptyList(), contactOrder = contactOrder)
                }

                is Resources.Error -> {
                    contactsState.copy(isLoading = false, errorMessage = result.errorMessage ?: "", contacts = emptyList(), contactOrder = contactOrder)
                }

                is Resources.Loading -> {
                    contactsState.copy(isLoading = true, errorMessage = "", contacts = emptyList(), contactOrder = contactOrder)
                }
            }
        }.launchIn(viewModelScope)
    }

    private fun searchContact(contentResolver: ContentResolver, searchQuery: String, searchContactOrder: ContactOrder) {
        contactsSearchState = contactsSearchState.copy(searchQuery = searchQuery)
        searchJob?.cancel()
        searchJob = viewModelScope.launch(Dispatchers.IO) {
            main.searchContacts(contentResolver, searchQuery, searchContactOrder).collect { searchResult ->
                contactsSearchState = when (searchResult) {
                    is Resources.Success -> {
                        contactsSearchState.copy(isLoading = false, errorMessage = "", contacts = searchResult.data ?: emptyList())
                    }

                    is Resources.Error -> {
                        contactsSearchState.copy(isLoading = false, errorMessage = searchResult.errorMessage ?: "", contacts = emptyList())
                    }

                    is Resources.Loading -> {
                        contactsSearchState.copy(isLoading = true, errorMessage = "", contacts = emptyList())
                    }
                }
            }
        }
    }

    private fun deleteContact(contentResolver: ContentResolver, contactInfo: ContactInfo) {
        viewModelScope.launch {
            val result = main.deleteContact(contentResolver, contactInfo)
            if (result){
                deleteContactResult.send(deleteContactSuccessful)
                removeContactFromList(contactInfo)
            }
            else {
                deleteContactResult.send(deleteContactNotSuccessful)
            }
        }
    }

    private fun removeContactFromList(contactInfo: ContactInfo) {
        val updatedContactsList = contactsState.contacts.toMutableList()
        updatedContactsList.remove(contactInfo)
        contactsState = contactsState.copy(contacts = updatedContactsList.toList())
    }

    private fun updateIsUserHasPermissionToAccessAllFiles(isUserHasPermissionToAccessAllFiles: Boolean) {
        mainScreenPermissionsState = mainScreenPermissionsState.copy(isUserHasPermissionToAccessAllFiles = isUserHasPermissionToAccessAllFiles)
    }

    private fun updateIsUserHasPermissionToReadContacts(isUserHasPermissionToReadContacts: Boolean) {
        mainScreenPermissionsState = mainScreenPermissionsState.copy(isUserHasPermissionToReadContacts = isUserHasPermissionToReadContacts)
    }

    private fun updateIsUserHasPermissionToWriteContacts(isUserHasPermissionToWriteContacts: Boolean) {
        mainScreenPermissionsState = mainScreenPermissionsState.copy(isUserHasPermissionToWriteContacts = isUserHasPermissionToWriteContacts)
    }

    private fun updateWriteContactsPermissionRationaleAlertDialog(contactAction: ContactAction) {
        writeContactsPermissionRationaleAlertDialog = writeContactsPermissionRationaleAlertDialog.copy(isShouldShow = !writeContactsPermissionRationaleAlertDialog.isShouldShow, contactAction = contactAction)
    }

    private fun clearWriteContactsPermissionRationaleAlertDialog() {
        writeContactsPermissionRationaleAlertDialog = WriteContactsAlertDialogState()
    }

    private fun updateModalBottomSheetVisibility() {
        modalBottomSheetState = modalBottomSheetState.copy(isShouldShow = !modalBottomSheetState.isShouldShow)
    }

    private fun updateModalBottomSheetContactInfo(contactInfo: ContactInfo?) {
        modalBottomSheetState = modalBottomSheetState.copy(contactInfo = contactInfo)
    }

    private fun updateWriteContactsPermissionResult(isGranted: Boolean) {
        viewModelScope.launch {
            writeContactsPermissionResult.send(isGranted)
        }
    }

    private fun updateSearchDropdownMenuState(searchDropdownMenu: Boolean) {
        contactsSearchState = contactsSearchState.copy(isSearchDropdownMenuExpanded = searchDropdownMenu)
    }

    private fun onSearchContactOrderClick(contentResolver: ContentResolver, searchQuery: String, searchContactOrder: ContactOrder) {
        contactsSearchState = contactsSearchState.copy(searchContactOrder = searchContactOrder)
        searchContact(contentResolver, searchQuery, searchContactOrder)
    }

    private fun updateSearchBarState(searchBarState: Boolean) {
        contactsSearchState = contactsSearchState.copy(isLoading = false, searchQuery = "", isSearchBarActive = searchBarState, errorMessage = null, contacts = emptyList(), isSearchDropdownMenuExpanded = false, searchContactOrder = contactsSearchState.searchContactOrder)
    }

    private fun clearSearchQuery() {
        contactsSearchState = contactsSearchState.copy(searchQuery = "", contacts = emptyList(), errorMessage = contactsNotFound)
    }

    private fun updateContactOrderSectionVisibility(isSectionVisible: Boolean) {
        contactsOrderSectionVisibleState = isSectionVisible
    }

    private fun checkIfTheSameContactOrderClicked(changingContactOrder: ContactOrder) = contactsState.contactOrder::class == changingContactOrder &&
            contactsState.contactOrder.contactOrderType == changingContactOrder.contactOrderType
}