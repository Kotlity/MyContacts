package com.mycontacts.data.main

import android.content.ContentResolver
import android.provider.ContactsContract
import com.mycontacts.data.contacts.ContactInfo
import com.mycontacts.domain.main.Main
import com.mycontacts.utils.Constants.contactAddedTimeStampColumn
import com.mycontacts.utils.Constants.contactFirstNameColumn
import com.mycontacts.utils.Constants.contactIdColumn
import com.mycontacts.utils.Constants.contactLastNameColumn
import com.mycontacts.utils.Constants.contactPhoneNumberColumn
import com.mycontacts.utils.Constants.contactPhotoColumn
import com.mycontacts.utils.Constants.orderByAdding
import com.mycontacts.utils.Constants.projection
import com.mycontacts.utils.getColumnIndex
import com.mycontacts.utils.retrieveBitmap
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class MainImplementation: Main {

    override fun getAllContacts(contentResolver: ContentResolver): Flow<List<ContactInfo>> {
        return flow {
            val contactsList = mutableListOf<ContactInfo>()

//            val contactIdColumn = ContactsContract.Contacts._ID
//            val contactPhotoColumn = ContactsContract.CommonDataKinds.Photo.PHOTO
//            val contactFirstNameColumn = ContactsContract.CommonDataKinds.StructuredName.GIVEN_NAME
//            val contactLastNameColumn = ContactsContract.CommonDataKinds.StructuredName.FAMILY_NAME
//            val contactPhoneNumberColumn = ContactsContract.CommonDataKinds.Phone.NUMBER
//            val contactAddedTimeStampColumn = ContactsContract.Contacts.CONTACT_LAST_UPDATED_TIMESTAMP
//
//            val projection = arrayOf(
//                contactIdColumn,
//                contactPhotoColumn,
//                contactFirstNameColumn,
//                contactLastNameColumn,
//                contactPhoneNumberColumn,
//                contactAddedTimeStampColumn
//            )

//            val orderByAdding = "${ContactsContract.Contacts.CONTACT_LAST_UPDATED_TIMESTAMP} DESC"
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

    override fun searchContacts(contentResolver: ContentResolver, searchQuery: String): Flow<List<ContactInfo>> {
        return flow {
            val searchContactsList = mutableListOf<ContactInfo>()

            val selection = "$contactFirstNameColumn LIKE ? OR $contactLastNameColumn LIKE ? OR $contactPhoneNumberColumn LIKE ?"
            val selectionArgs = arrayOf("%$searchQuery%", "%$searchQuery%", "%$searchQuery%")

            contentResolver.query(ContactsContract.Contacts.CONTENT_URI, projection, selection, selectionArgs, orderByAdding)?.use { cursor ->
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

                    searchContactsList.add(ContactInfo(id, photo, firstName, lastName, phoneNumber, timeStamp))
                }
            }
            emit(searchContactsList)
        }
    }

    override suspend fun getContactId(contactInfo: ContactInfo): Long = contactInfo.id

}