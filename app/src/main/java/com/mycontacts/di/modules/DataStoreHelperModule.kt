package com.mycontacts.di.modules

import android.content.Context
import com.mycontacts.data.shared.BooleanDataStoreHelper
import com.mycontacts.domain.shared.DataStoreHelper
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataStoreHelperModule {

    @Provides
    @Singleton
    fun provideBooleanDataStoreHelper(@ApplicationContext context: Context): DataStoreHelper<Boolean> = BooleanDataStoreHelper(context)
}