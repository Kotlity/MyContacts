package com.mycontacts.data.pager

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import com.mycontacts.domain.pager.Pager
import com.mycontacts.utils.Constants.dataStoreName
import com.mycontacts.utils.Constants.hasUserAlreadyClickedOnStartButton
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class PagerImplementation(@ApplicationContext private val context: Context): Pager {

    override suspend fun saveUserPressedStartButton() {
        context.dataStore.edit { mutablePreferences ->
            mutablePreferences[hasUserAlreadyClickedOnStartButton] = true
        }
    }

    override fun hasUserAlreadyPressedStartButton(): Flow<Boolean> {
        return context.dataStore.data.map { preferences ->
            preferences[hasUserAlreadyClickedOnStartButton] ?: false
        }
    }
}

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(dataStoreName)