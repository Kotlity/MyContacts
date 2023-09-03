package com.mycontacts.presentation.main.states

import com.mycontacts.data.contacts.ContactInfo

data class MainState(
    val isLoading: Boolean = false,
    val searchQuery: String = "",
    val errorMessage: String? = null,
    val contactId: Long? = null,
    val contacts: List<ContactInfo> = emptyList()
)
