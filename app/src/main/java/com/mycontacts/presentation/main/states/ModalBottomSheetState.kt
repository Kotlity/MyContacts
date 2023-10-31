package com.mycontacts.presentation.main.states

import com.mycontacts.data.contacts.ContactInfo
import com.mycontacts.utils.ContactsMethod

data class ModalBottomSheetState(val isShouldShow: Boolean = false, val contactsMethod: ContactsMethod? = null, val index: Int? = null, val contactInfo: ContactInfo? = null)