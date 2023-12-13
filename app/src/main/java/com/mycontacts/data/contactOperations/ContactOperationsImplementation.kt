package com.mycontacts.data.contactOperations

import android.content.ContentResolver
import android.content.ContentValues
import android.graphics.Bitmap
import android.provider.ContactsContract
import android.util.Log
import com.mycontacts.domain.contactOperations.ContactOperationsInterface
import com.mycontacts.utils.ContactOperations
import com.mycontacts.utils.addContactField
import com.mycontacts.utils.contactId
import com.mycontacts.utils.deleteContactField
import com.mycontacts.utils.editContactField
import com.mycontacts.utils.retrieveByteArray
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.withContext
import javax.inject.Inject

class ContactOperationsImplementation @Inject constructor(private val contentResolver: ContentResolver): ContactOperationsInterface {

    override suspend fun contactId(): Long {
        return withContext(Dispatchers.IO) {
            ensureActive()
            contentResolver.contactId().toLong()
        }
    }

    override suspend fun contactPhotoOperations(photoBitmap: Bitmap, contactId: Long, contactOperations: ContactOperations): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                val photoContentValues = ContentValues()
                val photoByteArray = photoBitmap.retrieveByteArray()

                when(contactOperations) {
                    ContactOperations.EDIT -> {
                        photoContentValues.put(ContactsContract.CommonDataKinds.Photo.PHOTO, photoByteArray)
                        val selection = "${ContactsContract.Data.CONTACT_ID} = ? AND ${ContactsContract.Data.MIMETYPE} = ?"
                        val selectionArgs = arrayOf(contactId.toString(), ContactsContract.CommonDataKinds.Photo.CONTENT_ITEM_TYPE)

                        ensureActive()
                        contentResolver.update(
                            ContactsContract.Data.CONTENT_URI,
                            photoContentValues,
                            selection,
                            selectionArgs
                        )
                    }
                    ContactOperations.ADD -> {
                        photoContentValues.apply {
                            put(ContactsContract.Data.RAW_CONTACT_ID, contactId)
                            put(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Photo.CONTENT_ITEM_TYPE)
                            put(ContactsContract.CommonDataKinds.Photo.PHOTO, photoByteArray)
                        }

                        ensureActive()
                        contentResolver.insert(
                            ContactsContract.Data.CONTENT_URI,
                            photoContentValues
                        )
                    }
                    ContactOperations.DELETE -> {
                        ensureActive()
                        contentResolver.deleteContactField(
                            ContactsContract.CommonDataKinds.Photo.CONTENT_ITEM_TYPE,
                            contactId
                        )
                    }
                }
                true
            } catch (e: Exception) {
                Log.e("MyTag", "contact photo operations error: ${e.message.toString()}")
                false
            }
        }
    }

    override suspend fun contactFirstNameOperations(firstName: String, contactId: Long, contactOperations: ContactOperations): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                when(contactOperations) {
                    ContactOperations.EDIT -> {
                        ensureActive()
                        contentResolver.editContactField(
                            ContactsContract.CommonDataKinds.StructuredName.GIVEN_NAME,
                            ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE,
                            firstName,
                            contactId
                        )
                    }
                    ContactOperations.ADD -> {
                        ensureActive()
                        contentResolver.addContactField(
                            ContactsContract.CommonDataKinds.StructuredName.GIVEN_NAME,
                            ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE,
                            firstName,
                            contactId
                        )
                    }
                    else -> Unit
                }

//                val firstNameContentValues = ContentValues().apply {
//                    put(ContactsContract.CommonDataKinds.StructuredName.GIVEN_NAME, firstName)
//                }
//                val selection = "${ContactsContract.Data.CONTACT_ID} = ? AND ${ContactsContract.Data.MIMETYPE} = ?"
//                val selectionArgs = arrayOf(contactId.toString(), ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE)
//
//                contentResolver.update(
//                    ContactsContract.Data.CONTENT_URI,
//                    firstNameContentValues,
//                    selection,
//                    selectionArgs
//                )

                true
            } catch (e: Exception) {
                Log.e("MyTag", "edit contact first name error: ${e.message.toString()}")
                false
            }
        }
    }

    override suspend fun contactLastNameOperations(lastName: String, contactId: Long, contactOperations: ContactOperations): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                when(contactOperations) {
                    ContactOperations.EDIT -> {
                        ensureActive()
                        contentResolver.editContactField(
                            ContactsContract.CommonDataKinds.StructuredName.FAMILY_NAME,
                            ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE,
                            lastName,
                            contactId
                        )
//                        lastNameContentValues.put(ContactsContract.CommonDataKinds.StructuredName.FAMILY_NAME, lastName)
//                        val selection = "${ContactsContract.Data.CONTACT_ID} = ? AND ${ContactsContract.Data.MIMETYPE} = ?"
//                        val selectionArgs = arrayOf(contactId.toString(), ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE)
//
//                        contentResolver.update(
//                            ContactsContract.Data.CONTENT_URI,
//                            lastNameContentValues,
//                            selection,
//                            selectionArgs
//                        )
                    }
                    ContactOperations.ADD -> {
                        ensureActive()
                        contentResolver.addContactField(
                            ContactsContract.CommonDataKinds.StructuredName.FAMILY_NAME,
                            ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE,
                            lastName,
                            contactId
                        )
//                        lastNameContentValues.apply {
//                            put(ContactsContract.Data.RAW_CONTACT_ID, contactId)
//                            put(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE)
//                            put(ContactsContract.CommonDataKinds.StructuredName.FAMILY_NAME, lastName)
//                        }
//
//                        contentResolver.insert(
//                            ContactsContract.Data.CONTENT_URI,
//                            lastNameContentValues
//                        )
                    }
                    ContactOperations.DELETE -> {
                        val lastNameContentValues = ContentValues()
                        lastNameContentValues.putNull(ContactsContract.CommonDataKinds.StructuredName.FAMILY_NAME)

                        val selection = "${ContactsContract.Data.CONTACT_ID} = ? AND ${ContactsContract.Data.MIMETYPE} = ?"
                        val selectionArgs = arrayOf(contactId.toString(), ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE)

                        ensureActive()
                        contentResolver.update(
                            ContactsContract.Data.CONTENT_URI,
                            lastNameContentValues,
                            selection,
                            selectionArgs
                        )
                    }
                }

                true
            } catch (e: Exception) {
                Log.e("MyTag", "contact last name operations error: ${e.message.toString()}")
                false
            }
        }
    }

    override suspend fun contactPhoneNumberOperations(phoneNumber: String, contactId: Long, contactOperations: ContactOperations): Boolean {
        return withContext(Dispatchers.IO) {
            try {

                when(contactOperations) {
                    ContactOperations.EDIT -> {
                        ensureActive()
                        contentResolver.editContactField(
                            ContactsContract.CommonDataKinds.Phone.NUMBER,
                            ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE,
                            phoneNumber,
                            contactId
                        )
                    }
                    ContactOperations.ADD -> {
                        ensureActive()
                        contentResolver.addContactField(
                            ContactsContract.CommonDataKinds.Phone.NUMBER,
                            ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE,
                            phoneNumber,
                            contactId
                        )
                    }
                    else -> Unit
                }

//                val phoneNumberContentValues = ContentValues().apply {
//                    put(ContactsContract.CommonDataKinds.Phone.NUMBER, phoneNumber)
//                }
//                val selection = "${ContactsContract.Data.CONTACT_ID} = ? AND ${ContactsContract.Data.MIMETYPE} = ?"
//                val selectionArgs = arrayOf(contactId.toString(), ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
//
//                contentResolver.update(
//                    ContactsContract.Data.CONTENT_URI,
//                    phoneNumberContentValues,
//                    selection,
//                    selectionArgs
//                )

                true
            } catch (e: Exception) {
                Log.e("MyTag", "edit contact phone number error: ${e.message.toString()}")
                false
            }
        }
    }

    override suspend fun contactTimeStampOperations(timeStamp: Long, contactId: Long, contactOperations: ContactOperations): Boolean {
        return withContext(Dispatchers.IO) {
            try {

                when(contactOperations) {
                    ContactOperations.EDIT -> {
                        val contactTimeStampContentValues = ContentValues().apply {
                            put(ContactsContract.Contacts.CONTACT_LAST_UPDATED_TIMESTAMP, timeStamp)
                        }
                        val selection = "${ContactsContract.Data.CONTACT_ID} = ? AND ${ContactsContract.Data.MIMETYPE} = ?"
                        val selectionArgs = arrayOf(contactId.toString(), ContactsContract.Contacts.CONTENT_ITEM_TYPE)

                        ensureActive()
                        contentResolver.update(
                            ContactsContract.Data.CONTENT_URI,
                            contactTimeStampContentValues,
                            selection,
                            selectionArgs
                        )
                    }
                    ContactOperations.ADD -> {
                        val timeStampContentValues = ContentValues().apply {
                            put(ContactsContract.Data.RAW_CONTACT_ID, contactId)
                            put(ContactsContract.Data.MIMETYPE, ContactsContract.Contacts.CONTENT_ITEM_TYPE)
                            put(ContactsContract.Contacts.CONTACT_LAST_UPDATED_TIMESTAMP, timeStamp)
                        }

                        ensureActive()
                        contentResolver.insert(
                            ContactsContract.Data.CONTENT_URI,
                            timeStampContentValues
                        )
                    }
                    else -> Unit
                }

                true
            } catch (e: Exception) {
                Log.e("MyTag", "edit contact time stamp error: ${e.message.toString()}")
                false
            }
        }
    }
}