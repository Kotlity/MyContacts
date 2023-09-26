package com.mycontacts.presentation.main.viewmodels

import android.content.ContentResolver
import android.os.Build
import android.os.Environment
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
import com.mycontacts.presentation.main.states.PermissionsForMainScreenState
import com.mycontacts.utils.Constants.contactsNotFound
import com.mycontacts.utils.Constants.searchDelay
import com.mycontacts.utils.Resources
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
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
            MainEvent.GetAllContacts -> {
                getAllContacts(contentResolver)
            }
            MainEvent.ClearSearchQuery -> {
                clearSearchQuery()
            }
            is MainEvent.SearchContact -> {
                searchContact(contentResolver, mainEvent.searchQuery)
            }
            is MainEvent.OnGeneralContactClick -> {
                onGeneralContactClick(mainEvent.contact)
            }
            is MainEvent.OnSearchContactClick -> {
                onSearchContactClick(mainEvent.contact)
            }
            is MainEvent.UpdateSearchBarState -> {
                updateSearchBarState(mainEvent.isShouldShow)
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

    private fun getAllContacts(contentResolver: ContentResolver) {
        contactsJob?.cancel()
        contactsJob = main.getAllContacts(contentResolver).onEach { result ->
            when (result) {
                is Resources.Success -> {
                    contactsState = contactsState.copy(isLoading = false, errorMessage = "", contacts = result.data ?: emptyList())
                }
                is Resources.Error -> {
                    contactsState = contactsState.copy(isLoading = false, errorMessage = result.errorMessage ?: "", contacts = emptyList())
                }
                is Resources.Loading -> {
                    contactsState = contactsState.copy(isLoading = true, errorMessage = "", contacts = emptyList())
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

    private fun onGeneralContactClick(contactInfo: ContactInfo) {
        viewModelScope.launch {
            val contactId = main.getContactId(contactInfo)
            contactsState = contactsState.copy(contactId = contactId)
        }
    }

    private fun onSearchContactClick(contactInfo: ContactInfo) {
        viewModelScope.launch {
            val contactId = main.getContactId(contactInfo)
            contactsSearchState = contactsSearchState.copy(contactId = contactId)
        }
    }
}