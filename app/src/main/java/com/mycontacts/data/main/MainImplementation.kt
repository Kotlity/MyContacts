package com.mycontacts.data.main

import android.content.ContentResolver
import android.provider.ContactsContract
import android.util.Log
import com.mycontacts.data.contacts.ContactInfo
import com.mycontacts.domain.main.Main
import com.mycontacts.utils.Constants.contactAddedTimeStampColumn
import com.mycontacts.utils.Constants.contactFirstNameColumn
import com.mycontacts.utils.Constants.contactIdColumn
import com.mycontacts.utils.Constants.contactLastNameColumn
import com.mycontacts.utils.Constants.contactPhoneNumberColumn
import com.mycontacts.utils.Constants.contactPhotoColumn
import com.mycontacts.utils.Constants.contactsNotFound
import com.mycontacts.utils.Constants.emptyContactsErrorMessage
import com.mycontacts.utils.Constants.orderByAdding
import com.mycontacts.utils.Constants.projection
import com.mycontacts.utils.Resources
import com.mycontacts.utils.getColumnIndex
import com.mycontacts.utils.retrieveBitmap
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainImplementation: Main {

    override fun getAllContacts(contentResolver: ContentResolver): Flow<Resources<List<ContactInfo>>> {
        return flow {

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
            emit(Resources.Loading())
            contentResolver.query(ContactsContract.Contacts.CONTENT_URI, projection, null, null, orderByAdding)?.use { cursor ->
                val contactsList = mutableListOf<ContactInfo>()
                val contactIdColumnIndex = getColumnIndex(cursor, contactIdColumn)
//                val contactPhotoColumnIndex = getColumnIndex(cursor, contactPhotoColumn)
//                val contactFirstNameColumnIndex = getColumnIndex(cursor, contactFirstNameColumn)
//                val contactLastNameColumnIndex = getColumnIndex(cursor, contactLastNameColumn)
//                val contactPhoneNumberColumnIndex = getColumnIndex(cursor, contactPhoneNumberColumn)
                val contactAddedTimeStampColumnIndex = getColumnIndex(cursor, contactAddedTimeStampColumn)
                while (cursor.moveToNext()) {
                    val id = cursor.getLong(contactIdColumnIndex)
//                    val photoByteArray = cursor.getBlob(contactPhotoColumnIndex)
//                    val photo = retrieveBitmap(photoByteArray)
//                    val firstName = cursor.getString(contactFirstNameColumnIndex)
//                    val lastName = cursor.getString(contactLastNameColumnIndex)
//                    val phoneNumber = cursor.getString(contactPhoneNumberColumnIndex)
                    val timeStamp = cursor.getLong(contactAddedTimeStampColumnIndex)

                    val time = SimpleDateFormat("dd:MM:yyyy HH:mm:ss", Locale.getDefault()).format(Date(timeStamp))

                    Log.d("MyTag", "id: $id")
//                    Log.d("MyTag", "photoByteArray: $photoByteArray")
//                    Log.d("MyTag", "photo: $photo")
//                    Log.d("MyTag", "firstName: $firstName")
//                    Log.d("MyTag", "lastName: $lastName")
//                    Log.d("MyTag", "phoneNumber: $phoneNumber")
                    Log.d("MyTag", "timeStamp: $timeStamp")
                    Log.d("MyTag", "time: $time")

                    contactsList.add(ContactInfo(id, firstName = "Test", phoneNumber = "Test phone number", timeStamp =  timeStamp))
                }
                if (contactsList.isEmpty()) emit(Resources.Error(emptyContactsErrorMessage))
                else emit(Resources.Success(contactsList))
            }
        }
    }

    override fun searchContacts(contentResolver: ContentResolver, searchQuery: String): Flow<Resources<List<ContactInfo>>> {
        return flow {

            emit(Resources.Loading())

            val selection = "$contactFirstNameColumn LIKE ? OR $contactLastNameColumn LIKE ? OR $contactPhoneNumberColumn LIKE ?"
            val selectionArgs = arrayOf("%$searchQuery%", "%$searchQuery%", "%$searchQuery%")

            contentResolver.query(ContactsContract.Contacts.CONTENT_URI, projection, selection, selectionArgs, orderByAdding)?.use { cursor ->
                val searchContactsList = mutableListOf<ContactInfo>()

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
                if (searchContactsList.isEmpty()) emit(Resources.Error(contactsNotFound))
                else emit(Resources.Success(searchContactsList))
            }
        }
    }

    override suspend fun getContactId(contactInfo: ContactInfo): Long = contactInfo.id

}