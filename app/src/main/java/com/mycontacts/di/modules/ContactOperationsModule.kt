package com.mycontacts.di.modules

import android.content.ContentResolver
import android.content.Context
import com.mycontacts.data.contactOperations.ContactOperationsImplementation
import com.mycontacts.domain.contactOperations.ContactOperationsInterface
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ViewModelScoped

@Module
@InstallIn(ViewModelComponent::class)
object ContactOperationsModule {

    @Provides
    @ViewModelScoped
    fun provideContentResolver(@ApplicationContext context: Context) = context.contentResolver

    @Provides
    @ViewModelScoped
    fun provideContactOperations(contentResolver: ContentResolver): ContactOperationsInterface = ContactOperationsImplementation(contentResolver)
}