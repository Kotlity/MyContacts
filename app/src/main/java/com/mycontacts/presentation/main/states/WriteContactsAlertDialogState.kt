package com.mycontacts.presentation.main.states

import com.mycontacts.utils.ContactAction

data class WriteContactsAlertDialogState(val isShouldShow: Boolean = false, val contactAction: ContactAction = ContactAction.INITIAL)
