package com.mycontacts.presentation.main.events

import com.mycontacts.data.contacts.ContactInfo
import com.mycontacts.utils.ContactOrder

sealed class MainEvent {

    sealed class Permissions: MainEvent() {
        data class UpdateIsUserHasPermissionToAccessAllFiles(val isUserHasPermissionToAccessAllFiles: Boolean): Permissions()
        data class UpdateIsUserHasPermissionToReadContacts(val isUserHasPermissionToReadContacts: Boolean): Permissions()
    }
    data class OnMainViewModelInitializing(val permissionToAccessAllFiles: Boolean, val permissionToReadContacts: Boolean): MainEvent()
    data class GetAllContacts(val contactOrder: ContactOrder): MainEvent()
    data class SearchContact(val searchQuery: String, val searchContactOrder: ContactOrder): MainEvent()
    data class UpdateSearchDropdownMenuState(val searchDropdownMenuVisibility: Boolean): MainEvent()
    data class OnSearchContactOrderClick(val searchQuery: String, val searchContactOrder: ContactOrder): MainEvent()
    data class UpdateSearchBarState(val isShouldShow: Boolean): MainEvent()
    data class UpdateContactOrderSectionVisibility(val isSectionVisible: Boolean): MainEvent()
    data class DeleteContact(val contactId: Long): MainEvent()
    data class UpdateModalBottomSheetContactInfo(val contactInfo: ContactInfo? = null): MainEvent()
    object ClearSearchQuery: MainEvent()
    object UpdateModalBottomSheetVisibility: MainEvent()
}