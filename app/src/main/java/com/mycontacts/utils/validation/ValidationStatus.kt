package com.mycontacts.utils.validation

sealed class ValidationStatus {
    object Success: ValidationStatus()
    data class Error(val errorMessage: String): ValidationStatus()
    object Unspecified: ValidationStatus()
}
