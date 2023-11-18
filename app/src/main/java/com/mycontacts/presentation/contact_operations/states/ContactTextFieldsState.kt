package com.mycontacts.presentation.contact_operations.states

import com.mycontacts.utils.validation.ValidationStatus

data class ContactTextFieldsState(
    val firstNameStatus: ValidationStatus = ValidationStatus.Unspecified,
    val lastNameStatus: ValidationStatus = ValidationStatus.Unspecified,
    val phoneNumberStatus: ValidationStatus = ValidationStatus.Unspecified
)