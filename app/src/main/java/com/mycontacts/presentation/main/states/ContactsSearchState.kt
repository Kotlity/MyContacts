package com.mycontacts.presentation.main.states

import com.mycontacts.data.contacts.ContactInfo

data class ContactsSearchState(
    val isLoading: Boolean = false,
    val searchQuery: String = "",
    val isSearchBarActive: Boolean = false,
    val errorMessage: String? = null,
    val contactId: Long? = null,
    val contacts: List<ContactInfo> = emptyList()
)
