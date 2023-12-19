package com.mycontacts.di.modules

import android.content.ContentResolver
import android.content.Context
import com.mycontacts.data.contactOperations.ContactOperationsImplementation
import com.mycontacts.domain.contactOperations.ContactOperationsInterface
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ActivityScoped

@Module
@InstallIn(ActivityComponent::class)
object ContactOperationsModule {

    @Provides
    @ActivityScoped
    fun provideContentResolver(@ApplicationContext context: Context): ContentResolver = context.contentResolver

    @Provides
    @ActivityScoped
    fun provideContactOperations(contentResolver: ContentResolver): ContactOperationsInterface = ContactOperationsImplementation(contentResolver)
}