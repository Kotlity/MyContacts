package com.mycontacts.presentation.contact_operations.viewmodels.factory

import com.mycontacts.data.contacts.ContactInfo
import com.mycontacts.presentation.contact_operations.viewmodels.ContactOperationsViewModel
import dagger.assisted.AssistedFactory

@AssistedFactory
interface ContactOperationsViewModelFactory {

    fun createContactOperationsViewModel(contactInfo: ContactInfo?): ContactOperationsViewModel
}