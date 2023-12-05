package com.mycontacts.presentation.contact_operations.events

data class ContactOperationsResultEvent(val message: String = "", val isShouldNavigateBack: Boolean = false)