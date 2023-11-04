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
import com.mycontacts.presentation.main.states.DeleteContactResult
import com.mycontacts.presentation.main.states.DialAlertDialogState
import com.mycontacts.presentation.main.states.ModalBottomSheetState
import com.mycontacts.presentation.main.states.PermissionsForMainScreenState
import com.mycontacts.presentation.main.states.WriteContactsAlertDialogState
import com.mycontacts.utils.Constants.contactsNotFound
import com.mycontacts.utils.Constants.deleteContactNotSuccessful
import com.mycontacts.utils.Constants.deleteContactSuccessful
import com.mycontacts.utils.ContactAction
import com.mycontacts.utils.ContactListAction
import com.mycontacts.utils.ContactOrder
import com.mycontacts.utils.ContactsMethod
import com.mycontacts.utils.Resources
import com.mycontacts.utils.StickyHeaderAction
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

    var dialAlertDialog by mutableStateOf(DialAlertDialogState())
        private set

    var writeContactsPermissionResult = Channel<Boolean>()
        private set

    var deleteContactResult = Channel<DeleteContactResult>()
        private set

    var contactsOrderSectionVisibleState by derivedStateOf { mutableStateOf(true) }.value
        private set

    var isSelectionGeneralModeActive by derivedStateOf { mutableStateOf(false) }.value
        private set

    var isSelectionSearchModeActive by derivedStateOf { mutableStateOf(false) }.value

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
                deleteContact(contentResolver, mainEvent.contactsMethod, mainEvent.index, mainEvent.contactInfo)
            }
            is MainEvent.RestoreContact -> {
                restoreContact(contentResolver, mainEvent.contactsMethod, mainEvent.index, mainEvent.contactInfo)
            }
            is MainEvent.UpdateModalBottomSheetContactInfo -> {
                updateModalBottomSheetContactInfo(mainEvent.contactsMethod, mainEvent.index, mainEvent.contactInfo)
            }
            is MainEvent.UpdateWriteContactsPermissionResult -> {
                updateWriteContactsPermissionResult(mainEvent.isGranted)
            }
            is MainEvent.UpdateDialAlertDialog -> {
                updateDialAlertDialogState(mainEvent.contactInfo)
            }
            is MainEvent.UpdateSelectionGeneralMode -> {
                updateSelectionGeneralMode(mainEvent.selectionGeneralMode)
            }
            is MainEvent.UpdateSelectionSearchMode -> {
                updateSelectionSearchMode(mainEvent.selectionSearchMode)
            }
            is MainEvent.UpdateIsContactSelectedFieldByClickOnContactInfo -> {
                updateIsContactSelectedFieldByClickOnContactInfo(mainEvent.header, mainEvent.index)
            }
            is MainEvent.UpdateIsSearchContactsSelectedFieldByClickOnContactInfo -> {
                updateIsSearchContactsSelectedFieldByClickOnContactInfo(mainEvent.index)
            }
            is MainEvent.UpdateSelectedContactsByItsHeader -> {
                updateSelectedContactsByItsHeader(mainEvent.header, mainEvent.stickyHeaderAction)
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
                    contactsState.copy(isLoading = false, errorMessage = "", contacts = result.data?.groupBy { contactInfo -> contactInfo.firstName.first() } ?: emptyMap(), contactOrder = contactOrder)
                }

                is Resources.Error -> {
                    contactsState.copy(isLoading = false, errorMessage = result.errorMessage ?: "", contacts = emptyMap(), contactOrder = contactOrder)
                }

                is Resources.Loading -> {
                    contactsState.copy(isLoading = true, errorMessage = "", contacts = emptyMap(), contactOrder = contactOrder)
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

    private fun deleteContact(contentResolver: ContentResolver, contactsMethod: ContactsMethod, index: Int, contactInfo: ContactInfo) {
        viewModelScope.launch {
            val isSuccessful = main.deleteContact(contentResolver, contactInfo)
            if (isSuccessful){
                deleteContactResult.send(DeleteContactResult(deleteContactSuccessful, contactsMethod, index, contactInfo))
                contactListAction(contactsMethod, ContactListAction.REMOVE, contactInfo)
            } else deleteContactResult.send(DeleteContactResult(deleteContactNotSuccessful, null, null, null))
        }
    }

    private fun restoreContact(contentResolver: ContentResolver, contactsMethod: ContactsMethod, index: Int, contactInfo: ContactInfo) {
        viewModelScope.launch {
            main.restoreContact(contentResolver, contactInfo)?.let { restoredContact ->
                contactListAction(contactsMethod, ContactListAction.RESTORE, restoredContact, index)
            }
        }
    }

    private fun contactListAction(contactsMethod: ContactsMethod, contactListAction: ContactListAction, contactInfo: ContactInfo, index: Int? = null) {
        when(contactsMethod) {
            ContactsMethod.GENERAL -> {
                val key = contactInfo.firstName.first()
                val mutableContactsMap = contactsState.contacts.mapValues { map -> map.value.toMutableList() }.toMutableMap()

                when(contactListAction) {
                    ContactListAction.REMOVE -> {
                        val updatedContactsByKey = mutableContactsMap.getOrDefault(key, mutableListOf()).apply { remove(contactInfo) }
                        mutableContactsMap[key] = updatedContactsByKey
                        if (updatedContactsByKey.isEmpty()) mutableContactsMap.remove(key)
                    }
                    ContactListAction.RESTORE -> {
                        if (mutableContactsMap.containsKey(key)) {
                            val updatedContactsByKey = mutableContactsMap.getOrDefault(key, mutableListOf()).apply { add(index!!, contactInfo) }
                            mutableContactsMap[key] = updatedContactsByKey
                        } else mutableContactsMap[key] = mutableListOf(contactInfo)
                    }
                }

                val updatedContactsMap = mutableContactsMap.mapValues { map -> map.value.toList() }
                contactsState = contactsState.copy(contacts = updatedContactsMap)
            }
            ContactsMethod.SEARCH -> {
                val mutableSearchContactsList = contactsSearchState.contacts.toMutableList()

                when(contactListAction) {
                    ContactListAction.REMOVE -> mutableSearchContactsList.remove(contactInfo)
                    ContactListAction.RESTORE -> mutableSearchContactsList.add(index!!, contactInfo)
                }

                val updatedSearchContactsList = mutableSearchContactsList.toList()
                contactsSearchState = contactsSearchState.copy(contacts = updatedSearchContactsList)
            }
        }
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

    private fun updateModalBottomSheetContactInfo(contactsMethod: ContactsMethod?, index: Int?, contactInfo: ContactInfo?) {
        modalBottomSheetState = modalBottomSheetState.copy(contactsMethod = contactsMethod, index = index, contactInfo = contactInfo)
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

    private fun updateDialAlertDialogState(dialContactInfo: ContactInfo?) {
        dialAlertDialog = dialAlertDialog.copy(isShouldShow = !dialAlertDialog.isShouldShow, dialContactInfo = dialContactInfo)
    }

    private fun updateSelectionGeneralMode(selectionGeneralMode: Boolean) {
        isSelectionGeneralModeActive = selectionGeneralMode
    }

    private fun updateSelectionSearchMode(selectionSearchMode: Boolean) {
        isSelectionSearchModeActive = selectionSearchMode
    }

    private fun updateIsContactSelectedFieldByClickOnContactInfo(header: Char, index: Int) {
        val mutableContactsMap = contactsState.contacts.mapValues { map -> map.value.toMutableList() }.toMutableMap()

        mutableContactsMap[header] = contactsState.contacts[header]?.mapIndexed { i, contactInfo ->
            if (index == i) contactInfo.copy(isSelected = !contactInfo.isSelected)
            else contactInfo
        }?.toMutableList() ?: mutableListOf()
        val updatedContactsMap = mutableContactsMap.mapValues { map -> map.value.toList() }

        contactsState = contactsState.copy(contacts = updatedContactsMap)
    }

    private fun updateIsSearchContactsSelectedFieldByClickOnContactInfo(index: Int) {
        contactsSearchState.contacts.mapIndexed { i, searchContactInfo ->
            if (index == i) searchContactInfo.copy(isSelected = !searchContactInfo.isSelected)
            else searchContactInfo
        }.also { updatedSearchContactsList ->
            contactsSearchState = contactsSearchState.copy(contacts = updatedSearchContactsList)
        }
    }

    private fun updateSelectedContactsByItsHeader(header: Char, stickyHeaderAction: StickyHeaderAction) {
        val mutableContactsMap = contactsState.contacts.mapValues { map -> map.value.toMutableList() }.toMutableMap()

        when(stickyHeaderAction) {
            StickyHeaderAction.SELECT_ALL -> {
                mutableContactsMap[header] = mutableContactsMap[header]?.map { contactInfo ->
                    contactInfo.copy(isSelected = true)
                }?.toMutableList() ?: mutableListOf()
            }
            StickyHeaderAction.UNSELECT_ALL -> {
                mutableContactsMap[header] = mutableContactsMap[header]?.map { contactInfo ->
                    contactInfo.copy(isSelected = false)
                }?.toMutableList() ?: mutableListOf()
            }
        }
        val updatedContactsMap = mutableContactsMap.mapValues { map -> map.value.toList() }

        contactsState = contactsState.copy(contacts = updatedContactsMap)
    }
}