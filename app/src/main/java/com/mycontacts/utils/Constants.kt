package com.mycontacts.utils

import androidx.compose.ui.unit.sp
import androidx.datastore.preferences.core.booleanPreferencesKey

object Constants {

    const val third = 3
    const val second = 2
    const val pagerImageSmallSizeInPixels = 512
    const val zero = 0
    const val splashDelay = 500L
    const val searchDelay = 300L

    const val _06Float = 0.6f
    const val _04Float = 0.4f
    const val _05Float = 0.5f

    val _22sp = 22.sp
    val _18sp = 18.sp

    const val dataStoreName = "dataStore"
    private const val hasUserAlreadyClickedOnStartButtonName = "hasUserAlreadyClickedOnStartButton"
    const val emptyContactsErrorMessage = "Nothing to show..."

    val hasUserAlreadyClickedOnStartButton = booleanPreferencesKey(hasUserAlreadyClickedOnStartButtonName)
}