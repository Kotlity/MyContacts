package com.mycontacts.data.shared

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import com.mycontacts.domain.shared.DataStoreHelper
import com.mycontacts.utils.dataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class BooleanDataStoreHelper(@ApplicationContext private val context: Context): DataStoreHelper<Boolean> {

    override suspend fun editValue(key: Preferences.Key<Boolean>, value: Boolean) {
        withContext(Dispatchers.IO) {
            context.dataStore.edit { preferences ->
                preferences[key] = value
            }
        }
    }

    override fun retrieveValue(key: Preferences.Key<Boolean>): Flow<Boolean> {
        return context.dataStore.data.map { preferences ->
            preferences[key] ?: false
        }.flowOn(Dispatchers.IO)
    }
}