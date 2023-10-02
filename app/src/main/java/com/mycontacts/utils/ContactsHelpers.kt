package com.mycontacts.utils

import android.content.ContentResolver
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.provider.ContactsContract
import com.mycontacts.utils.Constants.datePattern
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun getColumnIndex(cursor: Cursor, column: String) = cursor.getColumnIndex(column)

fun retrieveBitmap(contactId: String, contentResolver: ContentResolver): Bitmap? {
    return try {
        val uri = Uri.withAppendedPath(ContactsContract.Contacts.CONTENT_URI, contactId)
        val inputSteam = ContactsContract.Contacts.openContactPhotoInputStream(contentResolver, uri)
        BitmapFactory.decodeStream(inputSteam)
    } catch (e: IllegalArgumentException) {
        e.printStackTrace()
        null
    }
}

fun convertTimestamp(timeStamp: Long): String = SimpleDateFormat(datePattern, Locale.getDefault()).format(Date(timeStamp))