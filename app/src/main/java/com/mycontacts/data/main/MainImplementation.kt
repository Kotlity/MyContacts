package com.mycontacts.data.main

import android.content.ContentResolver
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.provider.ContactsContract
import com.mycontacts.data.contacts.ContactInfo
import com.mycontacts.domain.main.Main
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class MainImplementation: Main {

    override fun getAllContacts(contentResolver: ContentResolver): Flow<List<ContactInfo>> {
        return flow {
            val contactsList = mutableListOf<ContactInfo>()

            val contactIdColumn = ContactsContract.Contacts._ID
            val contactPhotoColumn = ContactsContract.CommonDataKinds.Photo.PHOTO
            val contactFirstNameColumn = ContactsContract.CommonDataKinds.StructuredName.GIVEN_NAME
            val contactLastNameColumn = ContactsContract.CommonDataKinds.StructuredName.FAMILY_NAME
            val contactPhoneNumberColumn = ContactsContract.CommonDataKinds.Phone.NUMBER
            val contactAddedTimeStampColumn = ContactsContract.Contacts.CONTACT_LAST_UPDATED_TIMESTAMP

            val projection = arrayOf(
                contactIdColumn,
                contactPhotoColumn,
                contactFirstNameColumn,
                contactLastNameColumn,
                contactPhoneNumberColumn,
                contactAddedTimeStampColumn
            )

            val orderByAdding = "${ContactsContract.Contacts.CONTACT_LAST_UPDATED_TIMESTAMP} DESC"
            contentResolver.query(ContactsContract.Contacts.CONTENT_URI, projection, null, null, orderByAdding)?.use { cursor ->
                val contactIdColumnIndex = getColumnIndex(cursor, contactIdColumn)
                val contactPhotoColumnIndex = getColumnIndex(cursor, contactPhotoColumn)
                val contactFirstNameColumnIndex = getColumnIndex(cursor, contactFirstNameColumn)
                val contactLastNameColumnIndex = getColumnIndex(cursor, contactLastNameColumn)
                val contactPhoneNumberColumnIndex = getColumnIndex(cursor, contactPhoneNumberColumn)
                val contactAddedTimeStampColumnIndex = getColumnIndex(cursor, contactAddedTimeStampColumn)
                while (cursor.moveToNext()) {
                    val id = cursor.getLong(contactIdColumnIndex)
                    val photoByteArray = cursor.getBlob(contactPhotoColumnIndex)
                    val photo = retrieveBitmap(photoByteArray)
                    val firstName = cursor.getString(contactFirstNameColumnIndex)
                    val lastName = cursor.getString(contactLastNameColumnIndex)
                    val phoneNumber = cursor.getString(contactPhoneNumberColumnIndex)
                    val timeStamp = cursor.getLong(contactAddedTimeStampColumnIndex)

                    contactsList.add(ContactInfo(id, photo, firstName, lastName, phoneNumber, timeStamp))
                }
            }
            emit(contactsList)
        }
    }

    override suspend fun getContactId(contactInfo: ContactInfo): Long = contactInfo.id

    private fun getColumnIndex(cursor: Cursor, column: String) = cursor.getColumnIndex(column)

    private fun retrieveBitmap(byteArray: ByteArray?): Bitmap? = if (byteArray == null) null else BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)

}