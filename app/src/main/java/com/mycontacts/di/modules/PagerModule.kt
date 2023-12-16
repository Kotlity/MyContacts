package com.mycontacts.di.modules

import com.mycontacts.data.pager.PagerImplementation
import com.mycontacts.domain.pager.Pager
import com.mycontacts.domain.shared.DataStoreHelper
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped

@Module
@InstallIn(ViewModelComponent::class)
object PagerModule {
    @Provides
    @ViewModelScoped
    fun providePager(booleanDataStoreHelper: DataStoreHelper<Boolean>): Pager = PagerImplementation(booleanDataStoreHelper)
}