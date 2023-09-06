package com.mycontacts.di.modules

import com.mycontacts.data.main.MainImplementation
import com.mycontacts.domain.main.Main
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped

@Module
@InstallIn(ViewModelComponent::class)
object MainModule {

    @Provides
    @ViewModelScoped
    fun provideMain(): Main = MainImplementation()
}