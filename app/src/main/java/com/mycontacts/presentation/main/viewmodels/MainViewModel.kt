package com.mycontacts.presentation.main.viewmodels

import android.content.ContentResolver
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mycontacts.data.contacts.ContactInfo
import com.mycontacts.domain.main.Main
import com.mycontacts.presentation.main.events.MainEvent
import com.mycontacts.presentation.main.states.MainState
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

    private var _contactsState = MutableStateFlow(MainState())
    val contactsState = _contactsState.asStateFlow()

    private var searchJob: Job? = null

    fun onEvent(contentResolver: ContentResolver, mainEvent: MainEvent) {
        when(mainEvent) {
            is MainEvent.Initial -> {
                getAllContacts(contentResolver)
            }
            is MainEvent.SearchContact -> {
                searchContact(mainEvent.searchQuery)
            }
            is MainEvent.OnContactClick -> {
                onContactClick(mainEvent.contact)
            }
        }
    }

    private fun getAllContacts(contentResolver: ContentResolver) {
        viewModelScope.launch {
            _contactsState.value = contactsState.value.copy(isLoading = true)
            main.getAllContacts(contentResolver).collect { contacts ->
                if (contacts.isEmpty()) _contactsState.value = contactsState.value.copy(isLoading = false, errorMessage = emptyContactsErrorMessage)
                else _contactsState.value = contactsState.value.copy(isLoading = false, contacts = contacts)
            }
        }
    }

    private fun searchContact(searchQuery: String) {
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            _contactsState.value = contactsState.value.copy(isLoading = true)
            delay(searchDelay)
            //TODO: CALL THE FUNCTION HERE TO SEARCH FOR CONTACTS AND UPDATE THE STATE
        }
    }

    private fun onContactClick(contactInfo: ContactInfo) {
        viewModelScope.launch {
            val contactId = main.getContactId(contactInfo)
            _contactsState.value = contactsState.value.copy(contactId = contactId)
        }
    }
}