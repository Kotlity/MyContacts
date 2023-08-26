package com.mycontacts.di.modules

import android.content.Context
import com.mycontacts.data.pager.PagerImplementation
import com.mycontacts.domain.pager.Pager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ViewModelScoped

@Module
@InstallIn(ViewModelComponent::class)
object PagerModule {

    @Provides
    @ViewModelScoped
    fun providePager(@ApplicationContext context: Context): Pager = PagerImplementation(context)
}