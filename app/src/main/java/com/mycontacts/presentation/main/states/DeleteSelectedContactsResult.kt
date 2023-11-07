package com.mycontacts.presentation.main.states

import com.mycontacts.data.contacts.ContactInfo
import com.mycontacts.utils.ContactsMethod

data class DeleteSelectedContactsResult(val message: String = "", val contactsMethod: ContactsMethod? = null, val selectedContacts: List<ContactInfo> = emptyList())
