package com.mycontacts.presentation.contact_operations.events

import android.graphics.Bitmap
import android.net.Uri
import com.mycontacts.data.contacts.ContactInfo

sealed class ContactOperationsEvent {
    object InitialUpdate: ContactOperationsEvent()
    data class UpdateCameraPermissionResult(val permissionResult: String): ContactOperationsEvent()
    data class UpdateFirstNameTextField(val firstName: String): ContactOperationsEvent()
    data class UpdateLastNameTextField(val lastName: String): ContactOperationsEvent()
    data class UpdatePhoneNumberTextField(val phoneNumber: String): ContactOperationsEvent()
    data class UpdatePhoto(val bitmap: Bitmap): ContactOperationsEvent()
    object UpdateModalBottomSheetActiveState: ContactOperationsEvent()
    object UpdateCameraPermissionRationaleAlertDialogState: ContactOperationsEvent()
    object ClearFirstNameTextField: ContactOperationsEvent()
    object ClearLastNameTextField: ContactOperationsEvent()
    object ClearPhoneNumberTextField: ContactOperationsEvent()
    object UpdateContactOperationsButton: ContactOperationsEvent()
    object UpdateOrInsertContactInfo: ContactOperationsEvent()
    object DeleteContactInfoPhoto: ContactOperationsEvent()
    object DeleteContactInfoLastName: ContactOperationsEvent()
}
