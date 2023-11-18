package com.mycontacts.utils

import android.content.ContentResolver
import android.content.ContentValues
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.provider.ContactsContract
import com.mycontacts.utils.Constants.datePattern
import java.io.ByteArrayOutputStream
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

fun Bitmap.retrieveByteArray(): ByteArray {
    val byteArrayOutputStream = ByteArrayOutputStream()
    compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream)
    return byteArrayOutputStream.toByteArray()
}

fun ContentResolver.contactId(): String {
    val contentValues = ContentValues().apply {
        putNull(ContactsContract.RawContacts.ACCOUNT_TYPE)
        putNull(ContactsContract.RawContacts.ACCOUNT_NAME)
    }
    val contactUri = insert(ContactsContract.RawContacts.CONTENT_URI, contentValues)
    return contactUri!!.lastPathSegment!!
}

fun Uri.uriToBitmap(contentResolver: ContentResolver): Bitmap? {
    return try {
        val inputStream = contentResolver.openInputStream(this)
        BitmapFactory.decodeStream(inputStream)
    } catch (_: Exception) { null }
}

fun convertTimestamp(timeStamp: Long): String = SimpleDateFormat(datePattern, Locale.ROOT).format(Date(timeStamp))

fun ContentResolver.editContactField(rawToEdit: String, contentType: String, contactField: String, contactId: Long) {
    val fieldContentValues = ContentValues().apply {
        put(rawToEdit, contactField)
    }
    val selection = "${ContactsContract.Data.CONTACT_ID} = ? AND ${ContactsContract.Data.MIMETYPE} = ?"
    val selectionArgs = arrayOf(contactId.toString(), contentType)

    update(
        ContactsContract.Data.CONTENT_URI,
        fieldContentValues,
        selection,
        selectionArgs
    )
}

fun ContentResolver.addContactField(rawToAdd: String, contentType: String, contactField: String, contactId: Long) {
    val fieldContentValues = ContentValues().apply {
        put(ContactsContract.Data.RAW_CONTACT_ID, contactId)
        put(ContactsContract.Data.MIMETYPE, contentType)
        put(rawToAdd, contactField)
    }

    insert(
        ContactsContract.Data.CONTENT_URI,
        fieldContentValues
    )
}

fun ContentResolver.deleteContactField(contentType: String, contactId: Long) {
    val selection = "${ContactsContract.Data.CONTACT_ID} = ? AND ${ContactsContract.Data.MIMETYPE} = ?"
    val selectionArgs = arrayOf(contactId.toString(), contentType)

    delete(
        ContactsContract.Data.CONTENT_URI,
        selection,
        selectionArgs
    )
}