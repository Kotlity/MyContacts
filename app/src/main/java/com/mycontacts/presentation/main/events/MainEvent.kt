package com.mycontacts.presentation.main.events

import com.mycontacts.data.contacts.ContactInfo

sealed class MainEvent {
    object Initial: MainEvent()
    data class SearchContact(val searchQuery: String): MainEvent()
    data class UpdateSearchBarState(val isShouldShow: Boolean): MainEvent()
    object ClearSearchQuery: MainEvent()
    data class OnGeneralContactClick(val contact: ContactInfo): MainEvent()
    data class OnSearchContactClick(val contact: ContactInfo): MainEvent()
}
