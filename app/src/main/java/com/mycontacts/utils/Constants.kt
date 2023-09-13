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

    const val dataStoreName = "dataStore"
    private const val hasUserAlreadyClickedOnStartButtonName = "hasUserAlreadyClickedOnStartButton"
    const val onDismissButtonClicked = "You have denied permission to access all files.\nPlease restart the application and accept this permission"
    const val dismissSnackbarActionLabel = "Understood..."
    const val emptyContactsErrorMessage = "Nothing to show..."
    const val contactsNotFound = "Sorry, but there are no such contacts"

    val hasUserAlreadyClickedOnStartButton = booleanPreferencesKey(hasUserAlreadyClickedOnStartButtonName)

    const val contactIdColumn = ContactsContract.Contacts._ID
    const val contactPhotoColumn = ContactsContract.CommonDataKinds.Photo.PHOTO
    const val contactFirstNameColumn = ContactsContract.CommonDataKinds.StructuredName.GIVEN_NAME
    const val contactLastNameColumn = ContactsContract.CommonDataKinds.StructuredName.FAMILY_NAME
    const val contactPhoneNumberColumn = ContactsContract.CommonDataKinds.Phone.NUMBER
    const val contactAddedTimeStampColumn = ContactsContract.Contacts.CONTACT_LAST_UPDATED_TIMESTAMP
    const val orderByAdding = "$contactAddedTimeStampColumn DESC"

    val projection = arrayOf(
        contactIdColumn,
//        contactPhotoColumn,
//        contactFirstNameColumn,
//        contactLastNameColumn,
//        contactPhoneNumberColumn,
        contactAddedTimeStampColumn
    )
}