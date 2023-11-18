package com.mycontacts.domain.contactOperations

import android.graphics.Bitmap
import com.mycontacts.utils.ContactOperations

interface ContactOperationsInterface {

    suspend fun contactId(): Long

    suspend fun contactPhotoOperations(photoBitmap: Bitmap, contactId: Long, contactOperations: ContactOperations): Boolean

    suspend fun contactFirstNameOperations(firstName: String, contactId: Long, contactOperations: ContactOperations): Boolean

    suspend fun contactLastNameOperations(lastName: String, contactId: Long, contactOperations: ContactOperations): Boolean

    suspend fun contactPhoneNumberOperations(phoneNumber: String, contactId: Long, contactOperations: ContactOperations): Boolean

    suspend fun contactTimeStampOperations(timeStamp: Long, contactId: Long, contactOperations: ContactOperations): Boolean
}