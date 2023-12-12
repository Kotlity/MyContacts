package com.mycontacts.presentation.contact_operations.viewmodels

import android.graphics.Bitmap
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.mycontacts.data.contacts.ContactInfo
import com.mycontacts.domain.contactOperations.ContactOperationsInterface
import com.mycontacts.presentation.contact_operations.events.ContactOperationsEvent
import com.mycontacts.presentation.contact_operations.events.ContactOperationsResultEvent
import com.mycontacts.presentation.contact_operations.states.ContactOperationsButtonState
import com.mycontacts.presentation.contact_operations.states.DeleteIconsVisibilityState
import com.mycontacts.presentation.contact_operations.viewmodels.factory.ContactOperationsViewModelFactory
import com.mycontacts.utils.Constants.unsuccessfulAddingContactMessage
import com.mycontacts.utils.Constants.unsuccessfulUpdatingContactMessage
import com.mycontacts.utils.Constants.successfulAddingContactMessage
import com.mycontacts.utils.Constants.successfulLastNameDeletion
import com.mycontacts.utils.Constants.successfulPhotoDeletion
import com.mycontacts.utils.Constants.successfulUpdatingContactMessage
import com.mycontacts.utils.Constants.theSameInput
import com.mycontacts.utils.Constants.unsuccessfulLastNameDeletion
import com.mycontacts.utils.Constants.unsuccessfulPhotoDeletion
import com.mycontacts.utils.Constants.updateContactButtonText
import com.mycontacts.utils.Constants.wrongInput
import com.mycontacts.utils.ContactOperations
import com.mycontacts.utils.validation.ValidationStatus
import com.mycontacts.utils.validation.firstNameValidation
import com.mycontacts.utils.validation.lastNameValidation
import com.mycontacts.utils.validation.phoneNumberValidation
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class ContactOperationsViewModel @AssistedInject constructor(
    private val contactOperations: ContactOperationsInterface,
    @Assisted
    private val contactInfo: ContactInfo?
): ViewModel() {

    var editableContactInfo by mutableStateOf(ContactInfo())
        private set

    var isModalBottomSheetActive by mutableStateOf(false)
        private set

    var firstNameValidationStatus by derivedStateOf {
        mutableStateOf<ValidationStatus>(ValidationStatus.Unspecified)
    }.value
        private set

    var lastNameValidationStatus by derivedStateOf {
        mutableStateOf<ValidationStatus>(ValidationStatus.Unspecified)
    }.value
        private set

    var phoneNumberValidationStatus by derivedStateOf {
        mutableStateOf<ValidationStatus>(ValidationStatus.Unspecified)
    }.value
        private set

    var contactOperationsButton by mutableStateOf(ContactOperationsButtonState())
        private set

    var deleteIconsVisibility by mutableStateOf(DeleteIconsVisibilityState(isDeleteContactInfoPhotoIconVisible = contactInfo != null, isDeleteContactInfoLastNameIconVisible = contactInfo != null))
        private set

    var cameraPermissionRationaleAlertDialog by mutableStateOf(false)
        private set

    private val cameraPermissionResultChannel = Channel<String>()
    val cameraPermissionResultFlow = cameraPermissionResultChannel.receiveAsFlow()

    private val deleteIconsResultChannel = Channel<String>()
    val deleteIconsResultFlow = deleteIconsResultChannel.receiveAsFlow()

    private val contactOperationsResultChannel = Channel<ContactOperationsResultEvent>()
    val contactOperationsResultFlow = contactOperationsResultChannel.receiveAsFlow()

    init {
        onEvent(ContactOperationsEvent.InitialUpdate)
    }

    fun onEvent(contactOperationsEvent: ContactOperationsEvent) {
        when(contactOperationsEvent) {
            ContactOperationsEvent.InitialUpdate -> {
                initialUpdate()
            }
            is ContactOperationsEvent.UpdateCameraPermissionResult -> {
                updateCameraPermissionResult(contactOperationsEvent.permissionResult)
            }
            is ContactOperationsEvent.UpdateFirstNameTextField -> {
                updateFirstNameTextField(contactOperationsEvent.firstName)
            }
            is ContactOperationsEvent.UpdateLastNameTextField -> {
                updateLastNameTextField(contactOperationsEvent.lastName)
            }
            is ContactOperationsEvent.UpdatePhoneNumberTextField -> {
                updatePhoneNumberTextField(contactOperationsEvent.phoneNumber)
            }
            is ContactOperationsEvent.UpdatePhoto -> {
                updatePhoto(contactOperationsEvent.bitmap)
            }
            ContactOperationsEvent.UpdateModalBottomSheetActiveState -> {
                updateModalBottomSheetActiveState()
            }
            ContactOperationsEvent.UpdateCameraPermissionRationaleAlertDialogState -> {
                updateCameraPermissionRationaleAlertDialogState()
            }
            ContactOperationsEvent.ClearFirstNameTextField -> {
                clearFirstNameTextField()
            }
            ContactOperationsEvent.ClearLastNameTextField -> {
                clearLastNameTextField()
            }
            ContactOperationsEvent.ClearPhoneNumberTextField -> {
                clearPhoneNumberTextField()
            }
            ContactOperationsEvent.UpdateContactOperationsButton -> {
                updateContactOperationsButton()
            }
            ContactOperationsEvent.UpdateOrInsertContactInfo -> {
                updateOrInsertContactInfo()
            }
            ContactOperationsEvent.DeleteContactInfoPhoto -> {
                deleteContactInfoPhoto()
            }
            ContactOperationsEvent.DeleteContactInfoLastName -> {
                deleteContactInfoLastName()
            }
        }
    }

    private fun initialUpdate() {
        if (contactInfo != null) {
            editableContactInfo = editableContactInfo.copy(
                id = contactInfo.id,
                photo = contactInfo.photo,
                firstName = contactInfo.firstName,
                lastName = contactInfo.lastName,
                phoneNumber = contactInfo.phoneNumber,
                timeStamp = contactInfo.timeStamp
            )
            editableContactInfo.apply {
                firstNameValidationStatus = firstName.firstNameValidation()
                phoneNumberValidationStatus = phoneNumber.phoneNumberValidation()
                if (lastName != null) lastNameValidationStatus = lastName.lastNameValidation()
            }
            contactOperationsButton = contactOperationsButton.copy(buttonText = updateContactButtonText)
        } else {
            editableContactInfo.apply {
                firstNameValidationStatus = firstName.firstNameValidation()
                phoneNumberValidationStatus = phoneNumber.phoneNumberValidation()
            }
        }
    }

    private fun updateModalBottomSheetActiveState() {
        isModalBottomSheetActive = !isModalBottomSheetActive
    }

    private fun updateCameraPermissionRationaleAlertDialogState() {
        cameraPermissionRationaleAlertDialog = !cameraPermissionRationaleAlertDialog
    }

    private fun updateCameraPermissionResult(permissionResult: String) {
        viewModelScope.launch {
            cameraPermissionResultChannel.send(permissionResult)
        }
    }

    private fun updateFirstNameTextField(firstName: String) {
        val firstNameValidation = firstName.firstNameValidation()
        editableContactInfo = editableContactInfo.copy(firstName = firstName)
        firstNameValidationStatus = firstNameValidation
    }

    private fun clearFirstNameTextField() {
        editableContactInfo = editableContactInfo.copy(firstName = "")
        val firstNameValidation = editableContactInfo.firstName.firstNameValidation()
        firstNameValidationStatus = firstNameValidation
    }

    private fun updateLastNameTextField(lastName: String) {
        if (lastName.isNotEmpty()) {
            val lastNameValidation = lastName.lastNameValidation()
            editableContactInfo = editableContactInfo.copy(lastName = lastName)
            lastNameValidationStatus = lastNameValidation
        } else clearLastNameTextField()
    }

    private fun clearLastNameTextField() {
        editableContactInfo = editableContactInfo.copy(lastName = "")
        lastNameValidationStatus = ValidationStatus.Success
    }

    private fun updatePhoneNumberTextField(phoneNumber: String) {
        val phoneNumberValidation = phoneNumber.phoneNumberValidation()
        editableContactInfo = editableContactInfo.copy(phoneNumber = phoneNumber)
        phoneNumberValidationStatus = phoneNumberValidation
    }

    private fun clearPhoneNumberTextField() {
        editableContactInfo = editableContactInfo.copy(phoneNumber = "")
        val phoneNumberValidation = editableContactInfo.phoneNumber.phoneNumberValidation()
        phoneNumberValidationStatus = phoneNumberValidation
    }

    private fun updatePhoto(photoBitmap: Bitmap) {
        editableContactInfo = editableContactInfo.copy(photo = photoBitmap)
    }

    private fun isSucceedTextFieldsValidation() = if (editableContactInfo.lastName != null || editableContactInfo.lastName?.isNotEmpty() == true) {
        firstNameValidationStatus is ValidationStatus.Success &&
        lastNameValidationStatus is ValidationStatus.Success &&
        phoneNumberValidationStatus is ValidationStatus.Success
    } else {
        firstNameValidationStatus is ValidationStatus.Success &&
        phoneNumberValidationStatus is ValidationStatus.Success
    }

    private fun isTheSameInput() = if (contactInfo == null) false
        else {
            if (editableContactInfo.lastName != null) {
                editableContactInfo.firstName == contactInfo.firstName &&
                editableContactInfo.lastName == contactInfo.lastName &&
                editableContactInfo.phoneNumber == contactInfo.phoneNumber
            } else {
                editableContactInfo.firstName == contactInfo.firstName &&
                editableContactInfo.phoneNumber == contactInfo.phoneNumber
            }
        }

    private fun updateContactOperationsButton() {
        val isButtonEnabled = isSucceedTextFieldsValidation() && !isTheSameInput()
        val supportingText = if (!isSucceedTextFieldsValidation()) wrongInput
            else if (isTheSameInput()) theSameInput else ""
        contactOperationsButton = contactOperationsButton.copy(isEnabled = isButtonEnabled, supportingText = supportingText)
    }

    private fun updateOrInsertContactInfo() {
        viewModelScope.launch {
            editableContactInfo.apply {
                contactOperations.apply {
                    val timeStamp = System.currentTimeMillis()
                    if (contactInfo != null) {
                        val id = contactInfo.id
                        val photoOperationResult = if (contactInfo.photo != null && photo != null) contactPhotoOperations(photo, id, ContactOperations.EDIT)
                            else if (contactInfo.photo == null && photo != null) contactPhotoOperations(photo, id, ContactOperations.ADD)
                            else true
                        val updatingFirstNameResult = contactFirstNameOperations(editableContactInfo.firstName, id, ContactOperations.EDIT)
                        val lastNameOperationResult = if (contactInfo.lastName != null && lastName != null) contactLastNameOperations(lastName, id, ContactOperations.EDIT)
                        else if (contactInfo.lastName == null && lastName != null) contactLastNameOperations(lastName, id, ContactOperations.ADD) else true
                        val updatingPhoneNumberResult = contactPhoneNumberOperations(editableContactInfo.phoneNumber, id, ContactOperations.EDIT)
                        val updatingTimeStampResult = contactTimeStampOperations(timeStamp, id, ContactOperations.EDIT)

                        val isAllOperationsDoneSuccessful = photoOperationResult && updatingFirstNameResult && lastNameOperationResult && updatingPhoneNumberResult && updatingTimeStampResult

                        if (isAllOperationsDoneSuccessful) contactOperationsResultChannel.send(ContactOperationsResultEvent(message = successfulUpdatingContactMessage, isShouldNavigateBack = true))
                        else contactOperationsResultChannel.send(ContactOperationsResultEvent(message = unsuccessfulUpdatingContactMessage))
                    } else {
                        val id = contactId()
                        val addingPhotoResult = photo?.let { contactPhotoOperations(it, id, ContactOperations.ADD) } ?: true
                        val addingFirstNameResult = contactFirstNameOperations(firstName, id, ContactOperations.ADD)
                        val addingLastNameResult = editableContactInfo.lastName?.let { lastName -> contactLastNameOperations(lastName, id, ContactOperations.ADD) } ?: true
                        val addingPhoneNumberResult = contactPhoneNumberOperations(phoneNumber, id, ContactOperations.ADD)
                        val addingTimeStampResult = contactTimeStampOperations(timeStamp, id, ContactOperations.ADD)

                        val isSuccessfulAddingResult = addingPhotoResult && addingFirstNameResult && addingLastNameResult && addingPhoneNumberResult && addingTimeStampResult

                        if (isSuccessfulAddingResult) contactOperationsResultChannel.send(ContactOperationsResultEvent(message = successfulAddingContactMessage, isShouldNavigateBack = true))
                        else contactOperationsResultChannel.send(ContactOperationsResultEvent(message = unsuccessfulAddingContactMessage))
                    }
                }
            }
        }
    }

    private fun deleteContactInfoPhoto() {
        viewModelScope.launch {
            editableContactInfo.photo?.let { photo ->
                val contactPhotoDeletingResult = contactOperations.contactPhotoOperations(photo, editableContactInfo.id, ContactOperations.DELETE)
                if (contactPhotoDeletingResult) {
                    editableContactInfo = editableContactInfo.copy(photo = null)
                    deleteIconsVisibility = deleteIconsVisibility.copy(isDeleteContactInfoPhotoIconVisible = false)
                    deleteIconsResultChannel.send(successfulPhotoDeletion)
                } else deleteIconsResultChannel.send(unsuccessfulPhotoDeletion)
            }
        }
    }

    private fun deleteContactInfoLastName() {
        viewModelScope.launch {
            editableContactInfo.lastName?.let { lastName ->
                val contactLastNameDeletingResult = contactOperations.contactLastNameOperations(lastName, editableContactInfo.id, ContactOperations.DELETE)
                if (contactLastNameDeletingResult) {
                    editableContactInfo = editableContactInfo.copy(lastName = null)
                    deleteIconsVisibility = deleteIconsVisibility.copy(isDeleteContactInfoLastNameIconVisible = false)
                    deleteIconsResultChannel.send(successfulLastNameDeletion)
                } else deleteIconsResultChannel.send(unsuccessfulLastNameDeletion)
            }
        }
    }

    companion object {

        fun provideContactOperationsViewModelFactory(contactOperationsViewModelFactory: ContactOperationsViewModelFactory, contactInfo: ContactInfo?): ViewModelProvider.Factory {
            return object: ViewModelProvider.Factory {
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return contactOperationsViewModelFactory.createContactOperationsViewModel(contactInfo = contactInfo) as T
                }
            }
        }
    }
}