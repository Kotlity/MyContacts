package com.mycontacts.presentation.contact_operations.states

import com.mycontacts.utils.Constants.addContactButtonText

data class ContactOperationsButtonState(val isEnabled: Boolean = false, val buttonText: String = addContactButtonText, val supportingText: String = "")
