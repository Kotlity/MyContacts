package com.mycontacts.presentation.main.events

import com.mycontacts.data.contacts.ContactInfo

sealed class MainEvent {
    object Initial: MainEvent()
    data class SearchContact(val searchQuery: String): MainEvent()
    data class OnContactClick(val contact: ContactInfo): MainEvent()
}
