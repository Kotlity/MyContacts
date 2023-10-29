package com.mycontacts.presentation.main.states

import com.mycontacts.data.contacts.ContactInfo

data class ModalBottomSheetState(val isShouldShow: Boolean = false, val index: Int? = null, val contactInfo: ContactInfo? = null)