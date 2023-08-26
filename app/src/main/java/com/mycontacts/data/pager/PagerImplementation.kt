package com.mycontacts.data.pager

import android.content.Context
import androidx.preference.PreferenceManager
import com.mycontacts.domain.pager.Pager
import com.mycontacts.utils.Constants.hasUserAlreadyEnteredTheApplicationKey
import dagger.hilt.android.qualifiers.ApplicationContext

class PagerImplementation(@ApplicationContext context: Context): Pager {

    private val preferenceManager = PreferenceManager.getDefaultSharedPreferences(context)

    override fun saveUserPressedStartButton() {
        preferenceManager.edit().putBoolean(hasUserAlreadyEnteredTheApplicationKey, true).apply()
    }

    override fun hasUserAlreadyPressedStartButton() = preferenceManager.getBoolean(hasUserAlreadyEnteredTheApplicationKey, false)
}