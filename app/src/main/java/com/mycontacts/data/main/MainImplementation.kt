package com.mycontacts.data.main

import android.content.ContentResolver
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.provider.ContactsContract
import android.util.Log
import androidx.core.database.getBlobOrNull
import androidx.core.database.getStringOrNull
import com.mycontacts.data.contacts.ContactInfo
import com.mycontacts.domain.main.Main
import com.mycontacts.utils.Constants.emptyContactsErrorMessage
import com.mycontacts.utils.Constants.myTag
import com.mycontacts.utils.Constants.photoBitmapError
import com.mycontacts.utils.Resources
import com.mycontacts.utils.getColumnIndex
import com.mycontacts.utils.retrieveBitmap
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class MainImplementation: Main {

    override fun getAllContacts(contentResolver: ContentResolver): Flow<Resources<List<ContactInfo>>> {
        return flow {
            emit(Resources.Loading())

            val result = retrieveContacts(contentResolver, RetrieveContactsMethod.GENERAL)

            if (result.isEmpty()) emit(Resources.Error(emptyContactsErrorMessage))
            else emit(Resources.Success(result))
        }
    }

    override fun searchContacts(contentResolver: ContentResolver, searchQuery: String): Flow<Resources<List<ContactInfo>>> {
        return flow {

            emit(Resources.Loading())


        }
    }

    private fun retrieveContacts(contentResolver: ContentResolver, retrieveContactsMethod: RetrieveContactsMethod, searchQuery: String? = null): List<ContactInfo> {

        return when(retrieveContactsMethod) {
            RetrieveContactsMethod.GENERAL -> {
                val generalContactInfoList = mutableListOf<ContactInfo>()
                contentResolver.query(
                    ContactsContract.Contacts.CONTENT_URI,
                    arrayOf(ContactsContract.Contacts._ID,
                        ContactsContract.Contacts.CONTACT_LAST_UPDATED_TIMESTAMP),
                    null,
                    null,
                    null
                )?.use { generalCursor ->
                    while (generalCursor.moveToNext()) {
                        val id = generalCursor.getString(getColumnIndex(generalCursor, ContactsContract.Contacts._ID))
                        val timeStamp = generalCursor.getLong(getColumnIndex(generalCursor, ContactsContract.Contacts.CONTACT_LAST_UPDATED_TIMESTAMP))

                        var phoneNumber = ""
                        contentResolver.query(
                            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                            arrayOf(ContactsContract.CommonDataKinds.Phone.NUMBER),
                            "${ContactsContract.CommonDataKinds.Phone.CONTACT_ID} = ?",
                            arrayOf(id),
                            null
                        )?.use { phoneNumberCursor ->
                            if (phoneNumberCursor.moveToFirst()) {
                                phoneNumber = phoneNumberCursor.getString(getColumnIndex(phoneNumberCursor, ContactsContract.CommonDataKinds.Phone.NUMBER))
                            }
                        }

                        var firstName = ""
                        var lastName: String? = null
                        contentResolver.query(
                            ContactsContract.Data.CONTENT_URI,
                            null,
                            "${ContactsContract.Data.MIMETYPE} = ? AND ${ContactsContract.CommonDataKinds.StructuredName.CONTACT_ID} = ?",
                            arrayOf(ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE, id),
                            null
                        )?.use { firstLastNameCursor ->
                            if (firstLastNameCursor.moveToFirst()) {
                                firstName = firstLastNameCursor.getString(getColumnIndex(firstLastNameCursor, ContactsContract.CommonDataKinds.StructuredName.GIVEN_NAME))
                                lastName = firstLastNameCursor.getStringOrNull(getColumnIndex(firstLastNameCursor, ContactsContract.CommonDataKinds.StructuredName.FAMILY_NAME))
                            }
                        }

                        var photo: Bitmap? = null

                        try {
                            val photoUri = Uri.withAppendedPath(ContactsContract.Contacts.CONTENT_URI, id)
                            val photoInputStream = ContactsContract.Contacts.openContactPhotoInputStream(contentResolver, photoUri)
                            photo = BitmapFactory.decodeStream(photoInputStream)
                        } catch (_: Exception) { Log.d(myTag, photoBitmapError) }

                        generalContactInfoList.add(ContactInfo(id.toLong(), photo, firstName, lastName, phoneNumber, timeStamp))
                    }
                }
                generalContactInfoList.sortedByDescending { contactInfo -> contactInfo.timeStamp }
            }
            RetrieveContactsMethod.FIRST_LAST_NAME -> {
                val searchContactInfoList = mutableListOf<ContactInfo>()

                contentResolver.query(
                    ContactsContract.Data.CONTENT_URI,
                    null,
                    ContactsContract.Data.MIMETYPE + " = ? AND (" +
                    ContactsContract.CommonDataKinds.StructuredName.GIVEN_NAME + " LIKE ? OR " +
                    ContactsContract.CommonDataKinds.StructuredName.FAMILY_NAME + " LIKE ?) OR " +
                    ContactsContract.Data.MIMETYPE + " = ? AND " +
                    ContactsContract.CommonDataKinds.Phone.NUMBER + " LIKE ?",
                    arrayOf(ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE, "%$searchQuery%", "%$searchQuery%", ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE, "%$searchQuery%"),
                    null
                )?.use { searchQueryCursor ->
                    while (searchQueryCursor.moveToNext()) {
                        val id = searchQueryCursor.getString(getColumnIndex(searchQueryCursor, ContactsContract.Data._ID))
                        val firstName = searchQueryCursor.getString(getColumnIndex(searchQueryCursor, ContactsContract.CommonDataKinds.StructuredName.GIVEN_NAME))
                        val lastName = searchQueryCursor.getStringOrNull(getColumnIndex(searchQueryCursor, ContactsContract.CommonDataKinds.StructuredName.FAMILY_NAME))
                        val timeStamp = searchQueryCursor.getLong(getColumnIndex(searchQueryCursor, ContactsContract.Data.CONTACT_LAST_UPDATED_TIMESTAMP))

                        var phoneNumber = ""
                        contentResolver.query(
                            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                            arrayOf(ContactsContract.CommonDataKinds.Phone.NUMBER),
                            "${ContactsContract.CommonDataKinds.Phone.CONTACT_ID} = ?",
                            arrayOf(id),
                            null
                        )?.use { phoneNumberCursor ->
                            phoneNumber = phoneNumberCursor.getString(getColumnIndex(phoneNumberCursor, ContactsContract.CommonDataKinds.Phone.NUMBER))
                        }

                        var photo: Bitmap? = null
                        contentResolver.query(
                            ContactsContract.Data.CONTENT_URI,
                            null,
                            "${ContactsContract.Data.MIMETYPE} = ? AND ${ContactsContract.CommonDataKinds.Photo.CONTACT_ID} = ?",
                            arrayOf(ContactsContract.CommonDataKinds.Photo.CONTENT_ITEM_TYPE, id),
                            null
                        )?.use { photoCursor ->
                            val photoByteArray = photoCursor.getBlobOrNull(getColumnIndex(photoCursor, ContactsContract.CommonDataKinds.Photo.PHOTO))
                            photo = retrieveBitmap(photoByteArray)
                        }

                        val searchContactInfo = ContactInfo(id.toLong(), photo, firstName, lastName, phoneNumber, timeStamp)
                        searchContactInfoList.add(searchContactInfo)
                    }
                }
                searchContactInfoList.sortedBy { contactInfo -> contactInfo.firstName }
            }
        }
    }

    override suspend fun getContactId(contactInfo: ContactInfo): Long = contactInfo.id

}

private enum class RetrieveContactsMethod {
    GENERAL, FIRST_LAST_NAME
}