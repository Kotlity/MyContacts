package com.mycontacts.presentation.contact_operations.events

import android.graphics.Bitmap

sealed class ContactOperationsEvent {
    data object InitialUpdate: ContactOperationsEvent()
    data class UpdateCameraPermissionResult(val permissionResult: String): ContactOperationsEvent()
    data class UpdateFirstNameTextField(val firstName: String): ContactOperationsEvent()
    data class UpdateLastNameTextField(val lastName: String): ContactOperationsEvent()
    data class UpdatePhoneNumberTextField(val phoneNumber: String): ContactOperationsEvent()
    data class UpdatePhoto(val bitmap: Bitmap): ContactOperationsEvent()
    data object UpdateModalBottomSheetActiveState: ContactOperationsEvent()
    data object UpdateCameraPermissionRationaleAlertDialogState: ContactOperationsEvent()
    data object ClearFirstNameTextField: ContactOperationsEvent()
    data object ClearLastNameTextField: ContactOperationsEvent()
    data object ClearPhoneNumberTextField: ContactOperationsEvent()
    data object UpdateContactOperationsButton: ContactOperationsEvent()
    data object UpdateOrInsertContactInfo: ContactOperationsEvent()
    data object DeleteContactInfoPhoto: ContactOperationsEvent()
    data object DeleteContactInfoLastName: ContactOperationsEvent()
}
