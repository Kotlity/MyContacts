package com.mycontacts.presentation.main.viewmodels

import android.content.ContentResolver
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mycontacts.data.contacts.ContactInfo
import com.mycontacts.domain.main.Main
import com.mycontacts.presentation.main.events.MainEvent
import com.mycontacts.presentation.main.states.ContactsSearchState
import com.mycontacts.presentation.main.states.ContactsState
import com.mycontacts.utils.Constants.contactsNotFound
import com.mycontacts.utils.Constants.emptyContactsErrorMessage
import com.mycontacts.utils.Constants.searchDelay
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(private val main: Main): ViewModel() {

    private var _contactsState = MutableStateFlow(ContactsState())
    val contactsState = _contactsState.asStateFlow()

    private var _contactsSearchState = MutableStateFlow(ContactsSearchState())
    val contactsSearchState = _contactsSearchState.asStateFlow()

    private var searchJob: Job? = null

    fun onEvent(contentResolver: ContentResolver, mainEvent: MainEvent) {
        when(mainEvent) {
            is MainEvent.Initial -> {
                getAllContacts(contentResolver)
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
            is MainEvent.ClearSearchQuery -> {
                clearSearchQuery()
            }
        }
    }

    private fun getAllContacts(contentResolver: ContentResolver) {
        viewModelScope.launch {
            _contactsState.value = contactsState.value.copy(isLoading = true)
            main.getAllContacts(contentResolver).collect { contacts ->
                if (contacts.isEmpty()) _contactsState.value = contactsState.value.copy(isLoading = false, errorMessage = emptyContactsErrorMessage, contacts = emptyList())
                else _contactsState.value = contactsState.value.copy(isLoading = false, errorMessage = "", contacts = contacts)
            }
        }
    }

    private fun searchContact(contentResolver: ContentResolver, searchQuery: String) {
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            _contactsSearchState.value = contactsSearchState.value.copy(isLoading = true, contacts = emptyList())
            delay(searchDelay)
            main.searchContacts(contentResolver, searchQuery).collect { searchContacts ->
                if (searchContacts.isEmpty()) _contactsSearchState.value = contactsSearchState.value.copy(isLoading = false, searchQuery = searchQuery, errorMessage = contactsNotFound)
                else _contactsSearchState.value = contactsSearchState.value.copy(isLoading = false, errorMessage = "", searchQuery = searchQuery, contacts = searchContacts)
            }
        }
    }

    private fun updateSearchBarState(searchBarState: Boolean) {
        _contactsSearchState.value = contactsSearchState.value.copy(isSearchBarActive = searchBarState)
    }

    private fun clearSearchQuery() {
        _contactsSearchState.value = _contactsSearchState.value.copy(searchQuery = "")
    }

    private fun onGeneralContactClick(contactInfo: ContactInfo) {
        viewModelScope.launch {
            val contactId = main.getContactId(contactInfo)
            _contactsState.value = contactsState.value.copy(contactId = contactId)
        }
    }

    private fun onSearchContactClick(contactInfo: ContactInfo) {
        viewModelScope.launch {
            val contactId = main.getContactId(contactInfo)
            _contactsSearchState.value = _contactsSearchState.value.copy(contactId = contactId)
        }
    }
}