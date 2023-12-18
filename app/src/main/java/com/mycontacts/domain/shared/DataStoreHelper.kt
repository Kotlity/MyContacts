package com.mycontacts.domain.shared

import androidx.datastore.preferences.core.Preferences
import kotlinx.coroutines.flow.Flow

interface DataStoreHelper<T> {

    suspend fun editValue(key: Preferences.Key<T>, value: T)

    fun retrieveValue(key: Preferences.Key<T>, initialValue: T): Flow<T>
}