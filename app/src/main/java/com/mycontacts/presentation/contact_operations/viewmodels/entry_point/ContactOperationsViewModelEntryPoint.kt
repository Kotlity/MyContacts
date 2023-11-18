package com.mycontacts.presentation.contact_operations.viewmodels.entry_point

import com.mycontacts.presentation.contact_operations.viewmodels.factory.ContactOperationsViewModelFactory
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent

@EntryPoint
@InstallIn(ActivityComponent::class)
interface ContactOperationsViewModelEntryPoint {

    fun contactOperationsViewModelFactory(): ContactOperationsViewModelFactory
}