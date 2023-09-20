package com.mycontacts.utils

import android.provider.ContactsContract
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
    val _20sp = 20.sp
    val _18sp = 18.sp
    val _17sp = 17.sp
    val _15sp = 15.sp

    const val myTag = "MyTag"
    const val dataStoreName = "dataStore"
    private const val hasUserAlreadyClickedOnStartButtonName = "hasUserAlreadyClickedOnStartButton"
    const val onDismissButtonClicked = "You have denied permission to access all files.\nPlease restart the application and accept this permission"
    const val dismissSnackbarActionLabel = "Understood..."
    const val emptyContactsErrorMessage = "Nothing to show..."
    const val contactsNotFound = "Sorry, but there are no such contacts"
    const val photoBitmapError = "Something went wrong while retrieving photo bitmap"

    const val datePattern = "dd:MM:yyyy HH:mm:ss"

    val hasUserAlreadyClickedOnStartButton = booleanPreferencesKey(hasUserAlreadyClickedOnStartButtonName)
}