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
import com.mycontacts.presentation.main.states.DeleteSelectedContactsResult
import com.mycontacts.presentation.main.states.DialAlertDialogState
import com.mycontacts.presentation.main.states.ModalBottomSheetState
import com.mycontacts.presentation.main.states.PermissionsForMainScreenState
import com.mycontacts.presentation.main.states.WriteContactsAlertDialogState
import com.mycontacts.utils.Constants.contactsNotFound
import com.mycontacts.utils.Constants.deleteContactNotSuccessful
import com.mycontacts.utils.Constants.deleteContactSuccessful
import com.mycontacts.utils.Constants.deleteSelectedContactsSuccessful
import com.mycontacts.utils.ContactAction
import com.mycontacts.utils.ContactsMethod
import com.mycontacts.utils.order.ContactOrder
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

    var deleteSelectedContactsResult = Channel<DeleteSelectedContactsResult>()
        private set

    var contactsOrderSectionVisibleState by derivedStateOf { mutableStateOf(true) }.value
        private set

    var isSelectionGeneralModeActive by derivedStateOf { mutableStateOf(false) }.value
        private set

    var isSelectionSearchModeActive by derivedStateOf { mutableStateOf(false) }.value

    var isExpandedFloatingActionButtonState by derivedStateOf { mutableStateOf(false) }.value

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
            is MainEvent.DeleteSingleContactInfo -> {
                deleteSingleContactInfo(contentResolver, mainEvent.contactsMethod, mainEvent.contactInfo, mainEvent.index)
            }
            is MainEvent.DeleteSelectedContacts -> {
                deleteSelectedContacts(contentResolver, mainEvent.contactsMethod, mainEvent.selectedContacts)
            }
            is MainEvent.RestoreSingleContactInfo -> {
                restoreSingleContactInfo(contentResolver, mainEvent.contactsMethod, mainEvent.contactInfo, mainEvent.index)
            }
            is MainEvent.RestoreSelectedContacts -> {
                restoreSelectedContacts(contentResolver, mainEvent.contactsMethod, mainEvent.selectedContacts)
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
            is MainEvent.UpdateIsSearchContactSelectedFieldByClickOnContactInfo -> {
                updateIsSearchContactSelectedFieldByClickOnContactInfo(mainEvent.index)
            }
            is MainEvent.UpdateSelectedContactsByItsHeader -> {
                updateSelectedContactsByItsHeader(mainEvent.header, mainEvent.stickyHeaderAction)
            }
            is MainEvent.ChangeIsExpandedFloatingActionButtonState -> {
                changeIsExpandedFloatingActionButtonState(mainEvent.isExpanded)
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

    private fun deleteSingleContactInfo(contentResolver: ContentResolver, contactsMethod: ContactsMethod, contactInfo: ContactInfo, index: Int) {
        viewModelScope.launch {
            val isSuccessful = main.deleteContact(contentResolver, contactInfo)
            if (isSuccessful) {
                deleteContactResult.send(DeleteContactResult(deleteContactSuccessful, contactsMethod, index, contactInfo))

                when (contactsMethod) {
                    ContactsMethod.GENERAL -> removeSingleContactInfoInGeneralList(contactInfo)
                    ContactsMethod.SEARCH -> {
                        removeSingleContactInfoInSearchingList(contactInfo)
                        removeSingleContactInfoInGeneralList(contactInfo)
                    }
                }

            } else deleteContactResult.send(DeleteContactResult(deleteContactNotSuccessful, null, null, null))
        }
    }

    private fun deleteSelectedContacts(contentResolver: ContentResolver, contactsMethod: ContactsMethod, selectedContacts: List<ContactInfo>) {
        main.deleteSelectedContacts(contentResolver, selectedContacts).onEach { deleteResult ->
            when(deleteResult) {
                is Resources.Success -> {
                    deleteSelectedContactsResult.send(
                        DeleteSelectedContactsResult(deleteSelectedContactsSuccessful, contactsMethod, deleteResult.data ?: emptyList())
                    )
                    when(contactsMethod) {
                        ContactsMethod.GENERAL -> removeSelectedContactsInGeneralList(selectedContacts)
                        ContactsMethod.SEARCH -> {
                            removeSelectedContactsInSearchList(selectedContacts)
                            removeSelectedContactsInGeneralList(selectedContacts)
                        }
                    }
                }
                is Resources.Error -> {
                    deleteSelectedContactsResult.send(
                        DeleteSelectedContactsResult(deleteResult.errorMessage ?: "")
                    )
                    when(contactsMethod) {
                        ContactsMethod.GENERAL -> unselectAllSelectedGeneralContacts()
                        ContactsMethod.SEARCH -> unselectAllSelectedSearchContacts()
                    }
                }
                else -> Unit
            }
        }.launchIn(viewModelScope)
    }

    private fun restoreSingleContactInfo(contentResolver: ContentResolver, contactsMethod: ContactsMethod, contactInfo: ContactInfo, index: Int) {
        viewModelScope.launch {
            main.restoreContact(contentResolver, contactInfo)?.let { restoredContact ->
                when(contactsMethod) {
                    ContactsMethod.GENERAL -> restoreSingleContactInfoInGeneralList(restoredContact, index)
                    ContactsMethod.SEARCH -> {
                        restoreSingleContactInfoInSearchingList(restoredContact, index)
                        restoreSingleContactInfoInGeneralList(restoredContact, index)
                    }
                }
            }
        }
    }

    private fun restoreSelectedContacts(contentResolver: ContentResolver, contactsMethod: ContactsMethod, selectedContacts: List<ContactInfo>) {
        viewModelScope.launch {
            val restoredSelectedContacts = main.restoreSelectedContacts(contentResolver, selectedContacts)
            when(contactsMethod) {
                ContactsMethod.GENERAL -> {
                    restoreSelectedContactsInGeneralList(restoredSelectedContacts)
                }
                ContactsMethod.SEARCH -> {
                    restoreSelectedContactsInSearchList(restoredSelectedContacts)
                    restoreSelectedContactsInGeneralList(restoredSelectedContacts)
                }
            }
        }
    }

    private fun removeSelectedContactsInGeneralList(selectedGeneralContacts: List<ContactInfo>) {
        val mutableContactsMap = contactsState.contacts.mapValues { contactsMap -> contactsMap.value.toMutableList() }.toMutableMap()

        selectedGeneralContacts.forEach { selectedGeneralContact ->
            val header = selectedGeneralContact.firstName.first()
            val generalContactsByHeader = mutableContactsMap[header]
            generalContactsByHeader?.remove(selectedGeneralContact)

            if (generalContactsByHeader.isNullOrEmpty()) mutableContactsMap.remove(header)
        }

        val updatedContactsMap = mutableContactsMap.mapValues { updatedMap -> updatedMap.value.toList() }

        contactsState = contactsState.copy(contacts = updatedContactsMap)
    }

    private fun removeSelectedContactsInSearchList(selectedSearchContacts: List<ContactInfo>) {
        val mutableSearchContacts = contactsSearchState.contacts.toMutableList().apply { removeAll(selectedSearchContacts) }

        val updatedSearchContacts = mutableSearchContacts.toList()

        contactsSearchState = contactsSearchState.copy(contacts = updatedSearchContacts)
    }

    private fun removeSingleContactInfoInGeneralList(contactInfo: ContactInfo) {
        val mutableContactsMap = contactsState.contacts.mapValues { contactInfoMap -> contactInfoMap.value.toMutableList() }.toMutableMap()
        val key = contactInfo.firstName.first()

        val updatedContactsByKey = mutableContactsMap.getOrDefault(key, mutableListOf()).apply { remove(contactInfo) }
        mutableContactsMap[key] = updatedContactsByKey
        if (updatedContactsByKey.isEmpty()) mutableContactsMap.remove(key)

        val updatedContactsMap = mutableContactsMap.mapValues { updatedContactInfoMap -> updatedContactInfoMap.value.toList() }

        contactsState = contactsState.copy(contacts = updatedContactsMap)
    }

    private fun removeSingleContactInfoInSearchingList(contactInfo: ContactInfo) {
        val mutableSearchContactsList = contactsSearchState.contacts.toMutableList().apply { remove(contactInfo) }
        val updatedSearchContactsList = mutableSearchContactsList.toList()

        contactsSearchState = contactsSearchState.copy(contacts = updatedSearchContactsList)
    }

    private fun restoreSelectedContactsInGeneralList(restoredSelectedContacts: List<ContactInfo>) {
        val mutableContactsMap = contactsState.contacts.mapValues { contactsMap -> contactsMap.value.toMutableList() }.toMutableMap()

        restoredSelectedContacts.forEach { restoredSelectedContact ->
            val header = restoredSelectedContact.firstName.first()
            val updatedContactsByHeader = mutableContactsMap[header]?.toMutableList() ?: mutableListOf()

            updatedContactsByHeader.add(restoredSelectedContact)

            mutableContactsMap[header] = updatedContactsByHeader

        }

        val updatedContactsMap = mutableContactsMap.mapValues { updatedContactMap -> updatedContactMap.value.toList() }

        contactsState = contactsState.copy(contacts = updatedContactsMap)
    }

    private fun restoreSelectedContactsInSearchList(restoredSelectedContacts: List<ContactInfo>) {
        val mutableSearchContacts = contactsSearchState.contacts.toMutableList().apply { addAll(restoredSelectedContacts) }
        val updatedSearchContacts = mutableSearchContacts.toList()

        contactsSearchState = contactsSearchState.copy(contacts = updatedSearchContacts)
    }

    private fun restoreSingleContactInfoInGeneralList(contactInfo: ContactInfo, index: Int) {
        val mutableContactsMap = contactsState.contacts.mapValues { contactInfoMap -> contactInfoMap.value.toMutableList() }.toMutableMap()
        val key = contactInfo.firstName.first()

        if (mutableContactsMap.containsKey(key)) {
            val updatedContactsByKey = mutableContactsMap.getOrDefault(key, mutableListOf()).apply { add(index, contactInfo) }
            mutableContactsMap[key] = updatedContactsByKey
        } else mutableContactsMap[key] = mutableListOf(contactInfo)

        val updatedContactsMap = mutableContactsMap.mapValues { updatedContactInfoMap -> updatedContactInfoMap.value.toList() }

        contactsState = contactsState.copy(contacts = updatedContactsMap)
    }

    private fun restoreSingleContactInfoInSearchingList(contactInfo: ContactInfo, index: Int) {
        val mutableSearchContactsList = contactsSearchState.contacts.toMutableList().apply { add(index, contactInfo) }
        val updatedSearchContactsList = mutableSearchContactsList.toList()

        contactsSearchState = contactsSearchState.copy(contacts = updatedSearchContactsList)
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

    private fun changeIsExpandedFloatingActionButtonState(isExpanded: Boolean) {
        isExpandedFloatingActionButtonState = isExpanded
    }

    private fun updateIsContactSelectedFieldByClickOnContactInfo(header: Char, index: Int) {
        val mutableContactsMap = contactsState.contacts.mapValues { map -> map.value.toMutableList() }.toMutableMap()

        mutableContactsMap[header] = mutableContactsMap[header]?.mapIndexed { i, contactInfo ->
            if (index == i) contactInfo.copy(isSelected = !contactInfo.isSelected)
            else contactInfo
        }?.toMutableList() ?: mutableListOf()
        val updatedContactsMap = mutableContactsMap.mapValues { map -> map.value.toList() }

        contactsState = contactsState.copy(contacts = updatedContactsMap)
    }

    private fun updateIsSearchContactSelectedFieldByClickOnContactInfo(index: Int) {
        contactsSearchState.contacts.mapIndexed { i, searchContactInfo ->
            if (index == i) searchContactInfo.copy(isSelected = !searchContactInfo.isSelected)
            else searchContactInfo
        }.also { updatedSearchContactsList ->
            contactsSearchState = contactsSearchState.copy(contacts = updatedSearchContactsList)
        }
    }

    private fun unselectAllSelectedGeneralContacts() {
        val mutableContactsMap = contactsState.contacts.mapValues { map -> map.value.toMutableList() }.toMutableMap()

        mutableContactsMap.keys.forEach { header ->
            mutableContactsMap[header] = mutableContactsMap[header]?.map { contactInfo ->
                contactInfo.copy(isSelected = false)
            }?.toMutableList() ?: mutableListOf()
        }

        val updatedContactsMap = mutableContactsMap.mapValues { updatedContactMap -> updatedContactMap.value.toList() }

        contactsState = contactsState.copy(contacts = updatedContactsMap)
    }

    private fun unselectAllSelectedSearchContacts() {
        var mutableSearchContacts = contactsSearchState.contacts.toMutableList()

        mutableSearchContacts = mutableSearchContacts.map { searchContact ->
            searchContact.copy(isSelected = false)
        }.toMutableList()

        val updatedSearchContacts = mutableSearchContacts.toList()

        contactsSearchState = contactsSearchState.copy(contacts = updatedSearchContacts)
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