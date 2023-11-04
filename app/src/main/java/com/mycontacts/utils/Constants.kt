package com.mycontacts.utils

import androidx.compose.ui.unit.sp
import androidx.datastore.preferences.core.booleanPreferencesKey

object Constants {

    const val zero = 0
    const val second = 2
    const val third = 3
    const val pagerImageSmallSizeInPixels = 512
    const val _1000 = 1000

    const val searchDelay = 300L
    const val splashDelay = 500L
    const val deleteSelectedContactsDelay = 5000L

    const val _06Float = 0.6f
    const val _04Float = 0.4f
    const val _05Float = 0.5f

    val _15sp = 15.sp
    val _16sp = 16.sp
    val _17sp = 17.sp
    val _18sp = 18.sp
    val _20sp = 20.sp
    val _22sp = 22.sp
    val _24sp = 24.sp

    const val dataStoreName = "dataStore"
    private const val hasUserAlreadyClickedOnStartButtonName = "hasUserAlreadyClickedOnStartButton"
    const val onDismissButtonClicked = "You have denied permission to access all files.\nPlease restart the application and accept this permission"
    const val dismissSnackbarActionLabel = "Understood..."
    const val emptyContactsErrorMessage = "Nothing to show..."
    const val contactsNotFound = "Sorry, but there are no such contacts"
    const val writeContactsPermissionGranted = "Permission granted, you can now edit/delete contacts"
    const val writeContactsPermissionNotGranted = "Permission not granted, repeat the action again"
    const val deleteContactSuccessful = "The contact was successfully deleted"
    const val deleteContactNotSuccessful = "Sorry, an unknown error occurred"
    const val deleteSelectedContactsSuccessful = "The selected contacts were successfully deleted"
    const val deleteContactUndo = "Undo"
    const val dialPart = "tel"

    const val datePattern = "dd:MM:yyyy HH:mm:ss"

    val hasUserAlreadyClickedOnStartButton = booleanPreferencesKey(hasUserAlreadyClickedOnStartButtonName)
}