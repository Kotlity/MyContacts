package com.mycontacts.presentation.main.states

import com.mycontacts.data.contacts.ContactInfo

data class DialAlertDialogState(val isShouldShow: Boolean = false, val dialContactInfo: ContactInfo? = null)
