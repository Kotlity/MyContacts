package com.mycontacts.presentation.main.states

import com.mycontacts.data.contacts.ContactInfo
import com.mycontacts.utils.order.ContactOrder
import com.mycontacts.utils.order.ContactOrderType

data class ContactsSearchState(
    val isLoading: Boolean = false,
    val searchQuery: String = "",
    val isSearchBarActive: Boolean = false,
    val errorMessage: String? = null,
    val contacts: List<ContactInfo> = emptyList(),
    val isSearchDropdownMenuExpanded: Boolean = false,
    val searchContactOrder: ContactOrder = ContactOrder.TimeStamp(ContactOrderType.Descending)
)
