package com.mycontacts.data.pager

import com.mycontacts.domain.pager.Pager
import com.mycontacts.domain.shared.DataStoreHelper
import com.mycontacts.utils.Constants.hasUserAlreadyClickedOnStartButtonPreferences
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class PagerImplementation @Inject constructor(private val dataStoreHelper: DataStoreHelper<Boolean>): Pager {

    override suspend fun saveUserPressedStartButton() {
        dataStoreHelper.editValue(hasUserAlreadyClickedOnStartButtonPreferences, true)
    }

    override fun hasUserAlreadyPressedStartButton(): Flow<Boolean> {
        return dataStoreHelper.retrieveValue(hasUserAlreadyClickedOnStartButtonPreferences)
    }
}