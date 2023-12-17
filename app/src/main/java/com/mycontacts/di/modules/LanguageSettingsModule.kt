package com.mycontacts.di.modules

import android.content.Context
import com.mycontacts.data.settings.AppLanguageSettings
import com.mycontacts.domain.settings.LanguageSettings
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object LanguageSettingsModule {

    @Provides
    @Singleton
    fun provideLanguageSettings(@ApplicationContext context: Context): LanguageSettings = AppLanguageSettings(context)
}