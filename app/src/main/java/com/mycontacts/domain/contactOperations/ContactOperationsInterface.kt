package com.mycontacts.domain.contactOperations

import android.graphics.Bitmap
import com.mycontacts.utils.ContactOperations

interface ContactOperationsInterface {

    suspend fun contactId(): String

    suspend fun contactPhotoOperations(photoBitmap: Bitmap, contactId: String, contactOperations: ContactOperations): Boolean

    suspend fun contactFirstNameOperations(firstName: String, contactId: String, contactOperations: ContactOperations): Boolean

    suspend fun contactLastNameOperations(lastName: String, contactId: String, contactOperations: ContactOperations): Boolean

    suspend fun contactPhoneNumberOperations(phoneNumber: String, contactId: String, contactOperations: ContactOperations): Boolean

    suspend fun contactTimeStampOperations(timeStamp: Long, contactId: String, contactOperations: ContactOperations): Boolean
}