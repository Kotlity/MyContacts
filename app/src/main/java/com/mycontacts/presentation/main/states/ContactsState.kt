package com.mycontacts.presentation.main.states

import com.mycontacts.data.contacts.ContactInfo

data class ContactsState(
    val isLoading: Boolean = false,
    val errorMessage: String = "",
    val contactId: Long? = null,
    val contacts: List<ContactInfo> = emptyList()
)