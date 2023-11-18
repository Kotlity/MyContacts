package com.mycontacts.presentation.contact_operations.events

data class ContactOperationsResultEvent(val message: String? = null, val isShouldNavigateBack: Boolean = false)