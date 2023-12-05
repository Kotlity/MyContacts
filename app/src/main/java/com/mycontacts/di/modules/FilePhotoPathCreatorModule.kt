package com.mycontacts.di.modules

import android.content.Context
import com.mycontacts.data.contactOperations.FilePhotoPathCreatorImplementation
import com.mycontacts.domain.contactOperations.FilePhotoPathCreatorInterface
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ActivityScoped

@Module
@InstallIn(ActivityComponent::class)
object FilePhotoPathCreatorModule {

    @Provides
    @ActivityScoped
    fun bindFilePhotoPathCreator(@ApplicationContext context: Context): FilePhotoPathCreatorInterface = FilePhotoPathCreatorImplementation(context)
}