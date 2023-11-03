package com.mycontacts.presentation.main.events

import com.mycontacts.data.contacts.ContactInfo
import com.mycontacts.utils.ContactAction
import com.mycontacts.utils.ContactOrder
import com.mycontacts.utils.ContactsMethod
import com.mycontacts.utils.StickyHeaderAction

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
    data class DeleteContact(val contactsMethod: ContactsMethod, val index: Int, val contactInfo: ContactInfo): MainEvent()
    data class RestoreContact(val contactsMethod: ContactsMethod, val index: Int, val contactInfo: ContactInfo): MainEvent()
    data class UpdateSearchDropdownMenuState(val searchDropdownMenuVisibility: Boolean): MainEvent()
    data class OnSearchContactOrderClick(val searchQuery: String, val searchContactOrder: ContactOrder): MainEvent()
    data class UpdateSearchBarState(val isShouldShow: Boolean): MainEvent()
    data class UpdateContactOrderSectionVisibility(val isSectionVisible: Boolean): MainEvent()
    data class UpdateModalBottomSheetContactInfo(val contactsMethod: ContactsMethod?, val index: Int?, val contactInfo: ContactInfo?): MainEvent()
    data class UpdateWriteContactsPermissionResult(val isGranted: Boolean): MainEvent()
    data class UpdateDialAlertDialog(val contactInfo: ContactInfo?): MainEvent()
    data class UpdateSelectionGeneralMode(val selectionGeneralMode: Boolean): MainEvent()
    data class UpdateIsContactSelectedFieldByClickOnContactInfo(val header: Char, val index: Int): MainEvent()
    data class UpdateSelectedContactsByItsHeader(val header: Char, val stickyHeaderAction: StickyHeaderAction): MainEvent()
    object ClearSearchQuery: MainEvent()
    object UpdateModalBottomSheetVisibility: MainEvent()
}