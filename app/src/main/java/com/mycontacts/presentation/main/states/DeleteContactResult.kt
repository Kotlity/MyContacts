package com.mycontacts.presentation.main.states

import com.mycontacts.data.contacts.ContactInfo
import com.mycontacts.utils.ContactsMethod

data class DeleteContactResult(val result: String = "", val contactsMethod: ContactsMethod? = null, val contactInfoIndex: Int? = null, val contactInfo: ContactInfo? = null)
