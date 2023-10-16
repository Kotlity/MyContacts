package com.mycontacts.presentation.main.events

import com.mycontacts.utils.ContactOrder

sealed class MainEvent {
    data class OnMainViewModelInitializing(val permissionToAccessAllFiles: Boolean, val permissionToReadContacts: Boolean): MainEvent()
    data class UpdateIsUserHasPermissionToAccessAllFiles(val isUserHasPermissionToAccessAllFiles: Boolean): MainEvent()
    data class UpdateIsUserHasPermissionToReadContacts(val isUserHasPermissionToReadContacts: Boolean): MainEvent()
    data class GetAllContacts(val contactOrder: ContactOrder): MainEvent()
    data class SearchContact(val searchQuery: String, val searchContactOrder: ContactOrder): MainEvent()
    data class UpdateSearchDropdownMenuState(val searchDropdownMenuVisibility: Boolean): MainEvent()
    data class OnSearchContactOrderClick(val searchQuery: String, val searchContactOrder: ContactOrder): MainEvent()
    data class UpdateSearchBarState(val isShouldShow: Boolean): MainEvent()
    object ClearSearchQuery: MainEvent()
    data class UpdateContactOrderSectionVisibility(val isSectionVisible: Boolean): MainEvent()
}
