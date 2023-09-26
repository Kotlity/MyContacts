package com.mycontacts.presentation.main.events

import com.mycontacts.data.contacts.ContactInfo

sealed class MainEvent {
    data class OnMainViewModelInitializing(val permissionToAccessAllFiles: Boolean, val permissionToReadContacts: Boolean): MainEvent()
    data class UpdateIsUserHasPermissionToAccessAllFiles(val isUserHasPermissionToAccessAllFiles: Boolean): MainEvent()
    data class UpdateIsUserHasPermissionToReadContacts(val isUserHasPermissionToReadContacts: Boolean): MainEvent()
    object GetAllContacts: MainEvent()
    data class SearchContact(val searchQuery: String): MainEvent()
    data class UpdateSearchBarState(val isShouldShow: Boolean): MainEvent()
    object ClearSearchQuery: MainEvent()
    data class OnGeneralContactClick(val contact: ContactInfo): MainEvent()
    data class OnSearchContactClick(val contact: ContactInfo): MainEvent()
}
