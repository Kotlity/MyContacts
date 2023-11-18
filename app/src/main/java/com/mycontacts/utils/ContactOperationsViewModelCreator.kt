package com.mycontacts.utils

import android.app.Activity
import android.content.Context
import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mycontacts.data.contacts.ContactInfo
import com.mycontacts.presentation.contact_operations.viewmodels.ContactOperationsViewModel
import com.mycontacts.presentation.contact_operations.viewmodels.entry_point.ContactOperationsViewModelEntryPoint
import dagger.hilt.android.EntryPointAccessors

@Composable
fun contactOperationsViewModelCreator(
    context: Context,
    viewModelStoreOwner: ViewModelStoreOwner,
    contactInfo: ContactInfo?
): ContactOperationsViewModel {

    val contactOperationsViewModelFactory = EntryPointAccessors.fromActivity(
        context as Activity,
        ContactOperationsViewModelEntryPoint::class.java
    ).contactOperationsViewModelFactory()
    
    return viewModel(
        viewModelStoreOwner = viewModelStoreOwner,
        factory = ContactOperationsViewModel.provideContactOperationsViewModelFactory(
            contactOperationsViewModelFactory,
            contactInfo = contactInfo
        )
    )
}