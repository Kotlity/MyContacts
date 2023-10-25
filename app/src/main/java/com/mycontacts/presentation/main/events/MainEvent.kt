package com.mycontacts.presentation.main.events

import com.mycontacts.data.contacts.ContactInfo
import com.mycontacts.utils.ContactAction
import com.mycontacts.utils.ContactOrder

sealed class MainEvent {

    sealed class Permissions: MainEvent() {
        data class UpdateIsUserHasPermissionToAccessAllFiles(val isUserHasPermissionToAccessAllFiles: Boolean): Permissions()
        data class UpdateIsUserHasPermissionToReadContacts(val isUserHasPermissionToReadContacts: Boolean): Permissions()
        data class UpdateIsUserHasPermissionToWriteContacts(val isUserHasPermissionToWriteContacts: Boolean): Permissions()
        data class UpdateWriteContactsPermissionRationaleAlertDialog(val contactAction: ContactAction): Permissions()
        object ClearWriteContactsPermissionRationaleAlertDialog: Permissions()
    }
    data class OnMainViewModelInitializing(val permissionToAccessAllFiles: Boolean, val permissionToReadContacts: Boolean, val permissionToWriteContacts: Boolean): MainEvent()
    data class GetAllContacts(val contactOrder: ContactOrder): MainEvent()
    data class SearchContact(val searchQuery: String, val searchContactOrder: ContactOrder): MainEvent()
    data class DeleteContact(val contactInfo: ContactInfo): MainEvent()
    data class UpdateSearchDropdownMenuState(val searchDropdownMenuVisibility: Boolean): MainEvent()
    data class OnSearchContactOrderClick(val searchQuery: String, val searchContactOrder: ContactOrder): MainEvent()
    data class UpdateSearchBarState(val isShouldShow: Boolean): MainEvent()
    data class UpdateContactOrderSectionVisibility(val isSectionVisible: Boolean): MainEvent()
    data class UpdateModalBottomSheetContactInfo(val contactInfo: ContactInfo): MainEvent()
    data class UpdateWriteContactsPermissionResult(val isGranted: Boolean): MainEvent()
    object ClearSearchQuery: MainEvent()
    object UpdateModalBottomSheetVisibility: MainEvent()
}