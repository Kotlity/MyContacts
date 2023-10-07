package com.mycontacts.presentation.main.viewmodels

import android.content.ContentResolver
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mycontacts.domain.main.Main
import com.mycontacts.presentation.main.events.MainEvent
import com.mycontacts.presentation.main.states.ContactsSearchState
import com.mycontacts.presentation.main.states.ContactsState
import com.mycontacts.presentation.main.states.PermissionsForMainScreenState
import com.mycontacts.utils.Constants.contactsNotFound
import com.mycontacts.utils.Constants.searchDelay
import com.mycontacts.utils.ContactOrder
import com.mycontacts.utils.Resources
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(private val main: Main): ViewModel() {

    var isUserHasPermissionsForMainScreen by mutableStateOf(PermissionsForMainScreenState())
        private set

    var contactsState by mutableStateOf(ContactsState())
        private set

    var contactsSearchState by mutableStateOf(ContactsSearchState())
        private set

    var contactsOrderSectionVisibleState by derivedStateOf { mutableStateOf(true) }.value
        private set

    private var contactsJob: Job? = null

    private var searchJob: Job? = null

    fun onEvent(contentResolver: ContentResolver, mainEvent: MainEvent) {
        when(mainEvent) {
            is MainEvent.OnMainViewModelInitializing -> {
                onMainViewModelInitializing(mainEvent.permissionToAccessAllFiles, mainEvent.permissionToReadContacts)
            }
            is MainEvent.UpdateIsUserHasPermissionToAccessAllFiles -> {
                updateIsUserHasPermissionToAccessAllFiles(mainEvent.isUserHasPermissionToAccessAllFiles)
            }
            is MainEvent.UpdateIsUserHasPermissionToReadContacts -> {
                updateIsUserHasPermissionToReadContacts(mainEvent.isUserHasPermissionToReadContacts)
            }
            is MainEvent.GetAllContacts -> {
                if (checkIfTheSameContactOrderClicked(mainEvent.contactOrder)) return
                getAllContacts(contentResolver, mainEvent.contactOrder)
            }
            is MainEvent.SearchContact -> {
                searchContact(contentResolver, mainEvent.searchQuery)
            }
            is MainEvent.UpdateSearchBarState -> {
                updateSearchBarState(mainEvent.isShouldShow)
            }
            MainEvent.ClearSearchQuery -> {
                clearSearchQuery()
            }
            is MainEvent.UpdateContactOrderSectionVisibility -> {
                updateContactOrderSectionVisibility(mainEvent.isSectionVisible)
            }
        }
    }

    private fun onMainViewModelInitializing(isUserHasPermissionToAccessAllFiles: Boolean, isUserHasPermissionToReadContacts: Boolean) {
        isUserHasPermissionsForMainScreen = isUserHasPermissionsForMainScreen.copy(
            isUserHasPermissionToAccessAllFiles = isUserHasPermissionToAccessAllFiles,
            isUserHasPermissionToReadContacts = isUserHasPermissionToReadContacts
        )
    }

    private fun updateIsUserHasPermissionToAccessAllFiles(isUserHasPermissionToAccessAllFiles: Boolean) {
        isUserHasPermissionsForMainScreen = isUserHasPermissionsForMainScreen.copy(isUserHasPermissionToAccessAllFiles = isUserHasPermissionToAccessAllFiles)
    }

    private fun updateIsUserHasPermissionToReadContacts(isUserHasPermissionToReadContacts: Boolean) {
        isUserHasPermissionsForMainScreen = isUserHasPermissionsForMainScreen.copy(isUserHasPermissionToReadContacts = isUserHasPermissionToReadContacts)
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

    private fun searchContact(contentResolver: ContentResolver, searchQuery: String) {
        contactsSearchState = contactsSearchState.copy(searchQuery = searchQuery)
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            delay(searchDelay)
            main.searchContacts(contentResolver, searchQuery).collect { searchResult ->
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

    private fun updateSearchBarState(searchBarState: Boolean) {
        contactsSearchState = ContactsSearchState(isSearchBarActive = searchBarState)
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