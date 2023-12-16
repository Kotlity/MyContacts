package com.mycontacts.utils

import android.content.ContentResolver
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.provider.ContactsContract
import androidx.core.content.FileProvider
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.mycontacts.utils.Constants.datePattern
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.InputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(Constants.dataStoreName)

fun getColumnIndex(cursor: Cursor, column: String) = cursor.getColumnIndex(column)

fun retrieveBitmap(contactId: String, contentResolver: ContentResolver): Bitmap? {
    var inputStream: InputStream? = null
    return try {
        val uri = Uri.withAppendedPath(ContactsContract.Contacts.CONTENT_URI, contactId)
        inputStream = ContactsContract.Contacts.openContactPhotoInputStream(contentResolver, uri)
        BitmapFactory.decodeStream(inputStream)
    } catch (e: IllegalArgumentException) {
        e.printStackTrace()
        null
    }
    finally {
        inputStream?.close()
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

fun createTempFilePhotoPath(context: Context): Uri {
    val currentTime = System.currentTimeMillis()
    val photoName = "picture_$currentTime"
    val photoFormat = ".png"
    val authority = "com.mycontacts.provider"
    val tempFile =
        File.createTempFile(photoName, photoFormat, context.cacheDir).apply { createNewFile() }
    return FileProvider.getUriForFile(context.applicationContext, authority, tempFile)
}

fun Uri.uriToBitmap(contentResolver: ContentResolver): Bitmap? {
    var inputStream: InputStream? = null
    return try {
        inputStream = contentResolver.openInputStream(this)
        BitmapFactory.decodeStream(inputStream)
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
    finally {
        inputStream?.close()
    }
}

fun convertTimestamp(timeStamp: Long): String = SimpleDateFormat(datePattern, Locale.ROOT).format(Date(timeStamp))

fun ContentResolver.editContactField(rawToEdit: String, contentType: String, contactField: String, contactId: String) {
    val fieldContentValues = ContentValues().apply {
        put(rawToEdit, contactField)
    }
    val selection = "${ContactsContract.Data.CONTACT_ID} = ? AND ${ContactsContract.Data.MIMETYPE} = ?"
    val selectionArgs = arrayOf(contactId, contentType)

    update(
        ContactsContract.Data.CONTENT_URI,
        fieldContentValues,
        selection,
        selectionArgs
    )
}

fun ContentResolver.addContactField(rawToAdd: String, contentType: String, contactField: String, contactId: String) {
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

fun ContentResolver.deleteContactField(contentType: String, contactId: String) {
    val selection = "${ContactsContract.Data.CONTACT_ID} = ? AND ${ContactsContract.Data.MIMETYPE} = ?"
    val selectionArgs = arrayOf(contactId, contentType)

    delete(
        ContactsContract.Data.CONTENT_URI,
        selection,
        selectionArgs
    )
}