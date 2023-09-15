package com.mycontacts.data.main

import android.content.ContentResolver
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.provider.ContactsContract
import android.util.Log
import com.mycontacts.data.contacts.ContactInfo
import com.mycontacts.domain.main.Main
import com.mycontacts.utils.Constants.contactsNotFound
import com.mycontacts.utils.Constants.emptyContactsErrorMessage
import com.mycontacts.utils.Constants.myTag
import com.mycontacts.utils.Constants.photoBitmapError
import com.mycontacts.utils.Resources
import com.mycontacts.utils.getColumnIndex
import com.mycontacts.utils.retrieveBitmap
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

private const val contactIdColumn = ContactsContract.Contacts._ID
private const val contactPhotoColumn = ContactsContract.CommonDataKinds.Photo.PHOTO
private const val contactFirstNameColumn = ContactsContract.CommonDataKinds.StructuredName.GIVEN_NAME
private const val contactLastNameColumn = ContactsContract.CommonDataKinds.StructuredName.FAMILY_NAME
private const val contactPhoneNumberColumn = ContactsContract.CommonDataKinds.Phone.NUMBER
private const val contactAddedTimeStampColumn = ContactsContract.Contacts.CONTACT_LAST_UPDATED_TIMESTAMP

private const val orderByAdding = "$contactAddedTimeStampColumn DESC"

private val projection = arrayOf(
    contactIdColumn,
//        contactPhotoColumn,
//        contactFirstNameColumn,
//        contactLastNameColumn,
//        contactPhoneNumberColumn,
    contactAddedTimeStampColumn
)

class MainImplementation: Main {

    override fun getAllContacts(contentResolver: ContentResolver): Flow<Resources<List<ContactInfo>>> {
        return flow {
            val contactList = mutableListOf<ContactInfo>()

            emit(Resources.Loading())

            contentResolver.query(ContactsContract.Contacts.CONTENT_URI, arrayOf(ContactsContract.Contacts._ID, ContactsContract.Contacts.CONTACT_LAST_UPDATED_TIMESTAMP), null, null, null)?.use { generalCursor ->

                while (generalCursor.moveToNext()) {
                    val id = generalCursor.getString(getColumnIndex(generalCursor, ContactsContract.Contacts._ID))
                    val timeStamp = generalCursor.getLong(getColumnIndex(generalCursor, ContactsContract.Contacts.CONTACT_LAST_UPDATED_TIMESTAMP))

                    var phoneNumber = ""
                    contentResolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, arrayOf(ContactsContract.CommonDataKinds.Phone.NUMBER) , "${ContactsContract.CommonDataKinds.Phone.CONTACT_ID} = ?", arrayOf(id), null)?.use { phoneCursor ->
                        if (phoneCursor.moveToFirst()) {
                            phoneNumber = phoneCursor.getString(getColumnIndex(phoneCursor, ContactsContract.CommonDataKinds.Phone.NUMBER))
                        }
                    }

                    var firstName = ""
                    var lastName: String? = null
                    contentResolver.query(ContactsContract.Data.CONTENT_URI, null, "${ContactsContract.Data.MIMETYPE} = ? AND ${ContactsContract.CommonDataKinds.StructuredName.CONTACT_ID} = ?", arrayOf(ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE, id), null)?.use { firstLastNameCursor ->
                        if (firstLastNameCursor.moveToFirst()) {
                            firstName = firstLastNameCursor.getString(getColumnIndex(firstLastNameCursor, ContactsContract.CommonDataKinds.StructuredName.GIVEN_NAME))
                            lastName = firstLastNameCursor.getString(getColumnIndex(firstLastNameCursor, ContactsContract.CommonDataKinds.StructuredName.FAMILY_NAME))
                        }
                    }

                    var photo: Bitmap? = null

                    try {
                        val photoUri = Uri.withAppendedPath(ContactsContract.Contacts.CONTENT_URI, id)
                        val photoInputStream = ContactsContract.Contacts.openContactPhotoInputStream(contentResolver, photoUri)
                        photo = BitmapFactory.decodeStream(photoInputStream)
                    } catch (_: Exception) { Log.d(myTag, photoBitmapError) }

                    val contactInfo = ContactInfo(id = id.toLong(), photo = photo, firstName = firstName, lastName = lastName, phoneNumber = phoneNumber, timeStamp = timeStamp)
                    contactList.add(contactInfo)
                }

                if (contactList.isEmpty()) emit(Resources.Error(emptyContactsErrorMessage))
                else {
                    contactList.sortByDescending { it.timeStamp }
                    emit(Resources.Success(contactList))
                }
            }

//            contentResolver.query(ContactsContract.Data.CONTENT_URI, firstLastNameProjection, ContactsContract.Data.MIMETYPE + " = ?", arrayOf(ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE), null)?.use { cursor ->
//                while (cursor.moveToNext()) {
//                    val contactId = cursor.getLong(cursor.getColumnIndex(ContactsContract.Data.CONTACT_ID))
//                    val givenName = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.StructuredName.GIVEN_NAME))
//                    val familyName = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.StructuredName.FAMILY_NAME))
//
//                    val photoByteArray: ByteArray? = contentResolver.query(ContactsContract.Data.CONTENT_URI, arrayOf(ContactsContract.Data.CONTACT_ID, ContactsContract.CommonDataKinds.Photo.PHOTO), ContactsContract.Data.CONTACT_ID + " = ? AND " + ContactsContract.Data.MIMETYPE + " = ?", arrayOf(contactId.toString(), ContactsContract.CommonDataKinds.Photo.CONTENT_ITEM_TYPE), null)?.use { photoCursor ->
//                        while (photoCursor.moveToFirst()) {
//                            photoCursor.getBlob()
//                        }
//                        if (photoCursor.moveToFirst()) {
//                            photoCursor.getBlob(photoCursor.getColumnIndex(ContactsContract.CommonDataKinds.Photo.PHOTO))
//                        } else {
//                            null
//                        }
//                    }
//
//                    val phoneNumber: String? = contentResolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, arrayOf(ContactsContract.CommonDataKinds.Phone.NUMBER), ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?", arrayOf(contactId.toString()), null)?.use { phoneNumberCursor ->
//                        if (phoneNumberCursor.moveToFirst()) {
//                            phoneNumberCursor.getString(phoneNumberCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))
//                        } else {
//                            null
//                        }
//                    }
//
//                    val photoBitmap = retrieveBitmap(photoByteArray)
//                    val contact = ContactInfo(id = contactId, photo = photoBitmap, firstName = givenName, lastName = familyName, phoneNumber = phoneNumber!!, timeStamp = )
//                    contactList.add(contact)
//                }
//            }
//            if (contactList.isEmpty()) emit(Resources.Error(emptyContactsErrorMessage))
//            else {
//                emit(Resources.Success(contactList))
//            }

//            contentResolver.query(ContactsContract.Contacts.CONTENT_URI, projection, null, null, orderByAdding)?.use { cursor ->
//                val contactsList = mutableListOf<ContactInfo>()
//                val contactIdColumnIndex = getColumnIndex(cursor, contactIdColumn)
////                val contactPhotoColumnIndex = getColumnIndex(cursor, contactPhotoColumn)
////                val contactFirstNameColumnIndex = getColumnIndex(cursor, contactFirstNameColumn)
////                val contactLastNameColumnIndex = getColumnIndex(cursor, contactLastNameColumn)
////                val contactPhoneNumberColumnIndex = getColumnIndex(cursor, contactPhoneNumberColumn)
//                val contactAddedTimeStampColumnIndex = getColumnIndex(cursor, contactAddedTimeStampColumn)
//                while (cursor.moveToNext()) {
//                    val id = cursor.getLong(contactIdColumnIndex)
////                    val photoByteArray = cursor.getBlob(contactPhotoColumnIndex)
////                    val photo = retrieveBitmap(photoByteArray)
////                    val firstName = cursor.getString(contactFirstNameColumnIndex)
////                    val lastName = cursor.getString(contactLastNameColumnIndex)
////                    val phoneNumber = cursor.getString(contactPhoneNumberColumnIndex)
//                    val timeStamp = cursor.getLong(contactAddedTimeStampColumnIndex)
//
//                    val time = SimpleDateFormat("dd:MM:yyyy HH:mm:ss", Locale.getDefault()).format(Date(timeStamp))
//
//                    Log.d("MyTag", "id: $id")
////                    Log.d("MyTag", "photoByteArray: $photoByteArray")
////                    Log.d("MyTag", "photo: $photo")
////                    Log.d("MyTag", "firstName: $firstName")
////                    Log.d("MyTag", "lastName: $lastName")
////                    Log.d("MyTag", "phoneNumber: $phoneNumber")
//                    Log.d("MyTag", "timeStamp: $timeStamp")
//                    Log.d("MyTag", "time: $time")
//
//                    contactsList.add(ContactInfo(id, firstName = "Test", phoneNumber = "Test phone number", timeStamp =  timeStamp))
//                }
//                if (contactsList.isEmpty()) emit(Resources.Error(emptyContactsErrorMessage))
//                else emit(Resources.Success(contactsList))
//            }


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