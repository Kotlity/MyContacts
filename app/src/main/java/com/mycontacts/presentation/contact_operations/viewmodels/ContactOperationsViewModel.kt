package com.mycontacts.presentation.contact_operations.viewmodels

import android.graphics.Bitmap
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
import com.mycontacts.presentation.contact_operations.states.ContactTextFieldsState
import com.mycontacts.presentation.contact_operations.states.DeleteIconsVisibilityState
import com.mycontacts.presentation.contact_operations.viewmodels.factory.ContactOperationsViewModelFactory
import com.mycontacts.utils.Constants.notSuccessfulAddingContactMessage
import com.mycontacts.utils.Constants.notSuccessfulUpdatingContactMessage
import com.mycontacts.utils.Constants.successfulAddingContactMessage
import com.mycontacts.utils.Constants.successfulUpdatingContactMessage
import com.mycontacts.utils.Constants.theSameInput
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

    var contactTextFields by mutableStateOf(ContactTextFieldsState())
        private set

    var contactOperationsButton by mutableStateOf(ContactOperationsButtonState())
        private set

    var deleteIconsVisibility by mutableStateOf(DeleteIconsVisibilityState(isDeleteContactInfoPhotoIcon = contactInfo != null, isDeleteContactInfoLastNameIcon = contactInfo != null))
        private set

    private val contactOperationsResultChannel = Channel<ContactOperationsResultEvent>()
    val contactOperationsResultFlow = contactOperationsResultChannel.receiveAsFlow()

    init {
        contactInfo?.let {
            onEvent(ContactOperationsEvent.InitialUpdate(it))
        }
    }

    fun onEvent(contactOperationsEvent: ContactOperationsEvent) {
        when(contactOperationsEvent) {
            is ContactOperationsEvent.InitialUpdate -> {
                initialUpdate(contactOperationsEvent.contactInfo)
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
            is ContactOperationsEvent.UpdateContactOperationsButton -> {
                updateContactOperationsButton()
            }
            is ContactOperationsEvent.UpdateOrInsertContactInfo -> {
                updateOrInsertContactInfo()
            }
            is ContactOperationsEvent.DeleteContactInfoPhoto -> {
                deleteContactInfoPhoto()
            }
            is ContactOperationsEvent.DeleteContactInfoLastName -> {
                deleteContactInfoLastName()
            }
        }
    }

    private fun initialUpdate(contactInfo: ContactInfo) {
        editableContactInfo = editableContactInfo.copy(
            id = contactInfo.id,
            photo = contactInfo.photo,
            firstName = contactInfo.firstName,
            lastName = contactInfo.lastName,
            phoneNumber = contactInfo.phoneNumber,
            timeStamp = contactInfo.timeStamp
        )
    }

    private fun updateFirstNameTextField(firstName: String) {
        val firstNameValidationStatus = firstName.firstNameValidation()
        editableContactInfo = editableContactInfo.copy(firstName = firstName)
        contactTextFields = contactTextFields.copy(firstNameStatus = firstNameValidationStatus)
    }

    private fun updateLastNameTextField(lastName: String) {
        val lastNameValidationStatus = lastName.lastNameValidation()
        editableContactInfo = editableContactInfo.copy(lastName = lastName)
        contactTextFields = contactTextFields.copy(lastNameStatus = lastNameValidationStatus)
    }

    private fun updatePhoneNumberTextField(phoneNumber: String) {
        val phoneNumberValidationStatus = phoneNumber.phoneNumberValidation()
        editableContactInfo = editableContactInfo.copy(phoneNumber = phoneNumber)
        contactTextFields = contactTextFields.copy(phoneNumberStatus = phoneNumberValidationStatus)
    }

    private fun updatePhoto(photoBitmap: Bitmap) {
        editableContactInfo = editableContactInfo.copy(photo = photoBitmap)
    }

    private fun isSucceedTextFieldsValidation() = if (editableContactInfo.lastName != null) {
        contactTextFields.firstNameStatus is ValidationStatus.Success &&
        contactTextFields.lastNameStatus is ValidationStatus.Success &&
        contactTextFields.phoneNumberStatus is ValidationStatus.Success
    } else {
        contactTextFields.firstNameStatus is ValidationStatus.Success &&
        contactTextFields.phoneNumberStatus is ValidationStatus.Success
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
                        else if (contactInfo.lastName == null && lastName != null) contactLastNameOperations(lastName, id, ContactOperations.ADD)
                        else true
                        val updatingPhoneNumberResult = contactPhoneNumberOperations(editableContactInfo.phoneNumber, id, ContactOperations.EDIT)
                        val updatingTimeStampResult = contactTimeStampOperations(timeStamp, id, ContactOperations.EDIT)

                        val isAllOperationsDoneSuccessful = photoOperationResult && updatingFirstNameResult && lastNameOperationResult && updatingPhoneNumberResult && updatingTimeStampResult

                        if (isAllOperationsDoneSuccessful) contactOperationsResultChannel.send(ContactOperationsResultEvent(message = successfulUpdatingContactMessage, isShouldNavigateBack = true))
                        else contactOperationsResultChannel.send(ContactOperationsResultEvent(message = notSuccessfulUpdatingContactMessage))
                    } else {
                        val id = contactId()
                        val addingPhotoResult = photo?.let { contactPhotoOperations(it, id, ContactOperations.ADD) } ?: true
                        val addingFirstNameResult = contactFirstNameOperations(firstName, id, ContactOperations.ADD)
                        val addingLastNameResult = editableContactInfo.lastName?.let { lastName -> contactLastNameOperations(lastName, id, ContactOperations.ADD) } ?: true
                        val addingPhoneNumberResult = contactPhoneNumberOperations(phoneNumber, id, ContactOperations.ADD)
                        val addingTimeStampResult = contactTimeStampOperations(timeStamp, id, ContactOperations.ADD)

                        val isSuccessfulAddingResult = addingPhotoResult && addingFirstNameResult && addingLastNameResult && addingPhoneNumberResult && addingTimeStampResult

                        if (isSuccessfulAddingResult) contactOperationsResultChannel.send(ContactOperationsResultEvent(message = successfulAddingContactMessage, isShouldNavigateBack = true))
                        else contactOperationsResultChannel.send(ContactOperationsResultEvent(message = notSuccessfulAddingContactMessage))
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
                    deleteIconsVisibility = deleteIconsVisibility.copy(isDeleteContactInfoPhotoIcon = false)
                }
            }
        }
    }

    private fun deleteContactInfoLastName() {
        viewModelScope.launch {
            editableContactInfo.lastName?.let { lastName ->
                val contactLastNameDeletingResult = contactOperations.contactLastNameOperations(lastName, editableContactInfo.id, ContactOperations.DELETE)
                if (contactLastNameDeletingResult) {
                    editableContactInfo = editableContactInfo.copy(lastName = null)
                    deleteIconsVisibility = deleteIconsVisibility.copy(isDeleteContactInfoLastNameIcon = false)
                }
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