package com.mycontacts.data.main

import android.content.ContentResolver
import android.content.ContentValues
import android.graphics.Bitmap
import android.net.Uri
import android.provider.ContactsContract
import androidx.core.database.getStringOrNull
import com.mycontacts.data.contacts.ContactInfo
import com.mycontacts.domain.main.Main
import com.mycontacts.utils.Constants.contactsNotFound
import com.mycontacts.utils.Constants.emptyContactsErrorMessage
import com.mycontacts.utils.Constants.searchDelay
import com.mycontacts.utils.ContactOrder
import com.mycontacts.utils.ContactOrderType
import com.mycontacts.utils.ContactsMethod
import com.mycontacts.utils.Resources
import com.mycontacts.utils.getColumnIndex
import com.mycontacts.utils.retrieveBitmap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream

class MainImplementation: Main {

    override fun getAllContacts(contentResolver: ContentResolver, contactOrder: ContactOrder): Flow<Resources<List<ContactInfo>>> {
        return flow {
            emit(Resources.Loading())

            val result = retrieveContacts(contentResolver, ContactsMethod.GENERAL, contactOrder)

            if (result.isEmpty()) emit(Resources.Error(emptyContactsErrorMessage))
            else emit(Resources.Success(result))
        }.flowOn(Dispatchers.IO)
    }

    override fun searchContacts(contentResolver: ContentResolver, searchQuery: String, searchContactOrder: ContactOrder): Flow<Resources<List<ContactInfo>>> {
        return flow {
            emit(Resources.Loading())

            delay(searchDelay)

            val searchResult = retrieveContacts(contentResolver, ContactsMethod.SEARCH, searchContactOrder, searchQuery)

            if (searchResult.isEmpty()) emit(Resources.Error(contactsNotFound))
            else emit(Resources.Success(searchResult))
        }.flowOn(Dispatchers.IO)
    }

    override suspend fun deleteContact(contentResolver: ContentResolver, contactInfo: ContactInfo): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                contentResolver.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null)?.use { generalCursor ->
                    if (generalCursor.moveToFirst()) {
                        do {
                            if (generalCursor.getString(getColumnIndex(generalCursor, ContactsContract.PhoneLookup._ID)) == contactInfo.id.toString()) {
                                val lookup = generalCursor.getString(getColumnIndex(generalCursor, ContactsContract.Contacts.LOOKUP_KEY))
                                val lookupUri = Uri.withAppendedPath(ContactsContract.Contacts.CONTENT_LOOKUP_URI, lookup)
                                contentResolver.delete(lookupUri, null, null)
                                break
                            }
                        } while (generalCursor.moveToNext())
                    }
                }
            } catch (_: Exception) {
                return@withContext false
            }
            true
        }
    }

    override suspend fun restoreContact(contentResolver: ContentResolver, contactInfo: ContactInfo): ContactInfo? {
        return withContext(Dispatchers.IO) {
            try {
                val rawContactsContentValues = ContentValues().apply {
                    putNull(ContactsContract.RawContacts.ACCOUNT_TYPE)
                    putNull(ContactsContract.RawContacts.ACCOUNT_NAME)
                }
                val rawContactUri = contentResolver.insert(ContactsContract.RawContacts.CONTENT_URI, rawContactsContentValues)
                val rawContactId = rawContactUri!!.lastPathSegment!!

                val firstLastNameContentValues = ContentValues().apply {
                    put(ContactsContract.Data.RAW_CONTACT_ID, rawContactId)
                    put(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE)
                    put(ContactsContract.CommonDataKinds.StructuredName.GIVEN_NAME, contactInfo.firstName)
                    contactInfo.lastName?.let { lastName -> put(ContactsContract.CommonDataKinds.StructuredName.FAMILY_NAME, lastName) }
                }
                contentResolver.insert(ContactsContract.Data.CONTENT_URI, firstLastNameContentValues)

                val phoneNumberContentValues = ContentValues().apply {
                    put(ContactsContract.Data.RAW_CONTACT_ID, rawContactId)
                    put(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
                    put(ContactsContract.CommonDataKinds.Phone.NUMBER, contactInfo.phoneNumber)
                    put(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
                    put(ContactsContract.CommonDataKinds.Phone.TYPE, ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE)
                }
                contentResolver.insert(ContactsContract.Data.CONTENT_URI, phoneNumberContentValues)

                val timeStampContentValues = ContentValues().apply {
                    put(ContactsContract.Data.RAW_CONTACT_ID, rawContactId)
                    put(ContactsContract.Data.MIMETYPE, ContactsContract.Contacts.CONTENT_ITEM_TYPE)
                    put(ContactsContract.Contacts.CONTACT_LAST_UPDATED_TIMESTAMP, contactInfo.timeStamp)
                }
                contentResolver.insert(ContactsContract.Data.CONTENT_URI, timeStampContentValues)

                contactInfo.photo?.let { bitmap ->
                    val outputStream = ByteArrayOutputStream()
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
                    val photoByteArray = outputStream.toByteArray()

                    val photoContentValues = ContentValues().apply {
                        put(ContactsContract.Data.RAW_CONTACT_ID, rawContactId)
                        put(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Photo.CONTENT_ITEM_TYPE)
                        put(ContactsContract.CommonDataKinds.Photo.PHOTO, photoByteArray)
                    }
                    contentResolver.insert(ContactsContract.Data.CONTENT_URI, photoContentValues)
                }
                ContactInfo(
                    id = rawContactId.toLong(),
                    photo = contactInfo.photo,
                    firstName = contactInfo.firstName,
                    lastName = contactInfo.lastName,
                    phoneNumber = contactInfo.phoneNumber,
                    timeStamp = contactInfo.timeStamp
                )
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }
    }

    private fun retrieveContacts(contentResolver: ContentResolver, contactsMethod: ContactsMethod, contactOrder: ContactOrder, searchQuery: String? = null): List<ContactInfo> {
        return when(contactsMethod) {
            ContactsMethod.GENERAL -> {
                var generalContactInfoList = mutableListOf<ContactInfo>()

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
                        val photoBitmap = retrieveBitmap(id, contentResolver)

                        generalContactInfoList.add(ContactInfo(id.toLong(), photoBitmap, firstName, lastName, phoneNumber, timeStamp))
                    }
                }
                if (generalContactInfoList.isNotEmpty()) {
                    generalContactInfoList = sortFinalList(generalContactInfoList, contactOrder)
                }
                generalContactInfoList
            }
            ContactsMethod.SEARCH -> {
                var searchContactInfoList = mutableListOf<ContactInfo>()

                contentResolver.query(
                    ContactsContract.Contacts.CONTENT_URI,
                    null,
                    ContactsContract.Contacts.DISPLAY_NAME + " LIKE ?",
                    arrayOf("%$searchQuery%"),
                    null
                )?.use { searchCursor ->
                    while (searchCursor.moveToNext()) {
                        val id = searchCursor.getString(getColumnIndex(searchCursor, ContactsContract.Contacts._ID))
                        val timeStamp = searchCursor.getLong(getColumnIndex(searchCursor, ContactsContract.Contacts.CONTACT_LAST_UPDATED_TIMESTAMP))
                        val displayName = searchCursor.getString(getColumnIndex(searchCursor, ContactsContract.Contacts.DISPLAY_NAME))
                        val fullName = displayName.split(" ")
                        val firstName = fullName[0]
                        val lastName = if (fullName.size > 1) fullName[1] else ""
                        var phoneNumber = ""
                        contentResolver.query(
                            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                            arrayOf(ContactsContract.CommonDataKinds.Phone.NUMBER),
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                            arrayOf(id),
                            null
                        )?.use { phoneNumberCursor ->
                            if (phoneNumberCursor.moveToFirst()) {
                                phoneNumber = phoneNumberCursor.getString(getColumnIndex(phoneNumberCursor, ContactsContract.CommonDataKinds.Phone.NUMBER))
                            }
                        }
                        val photoBitmap = retrieveBitmap(id, contentResolver)

                        val searchContact = ContactInfo(id.toLong(), photoBitmap, firstName, lastName, phoneNumber, timeStamp)
                        searchContactInfoList.add(searchContact)
                    }
                }

                if (searchContactInfoList.isNotEmpty()) {
                    searchContactInfoList = sortFinalList(searchContactInfoList, contactOrder)
                }
                searchContactInfoList
            }
        }
    }

    private fun sortFinalList(finalList: MutableList<ContactInfo>, contactOrder: ContactOrder): MutableList<ContactInfo> {
        when (contactOrder.contactOrderType) {
            is ContactOrderType.Ascending -> {
                when (contactOrder) {
                    is ContactOrder.FirstName -> finalList.sortBy { it.firstName.lowercase() }
                    is ContactOrder.LastName -> finalList.sortWith(compareBy { it.lastName })
                    is ContactOrder.TimeStamp -> finalList.sortBy { it.timeStamp }
                }
            }
            is ContactOrderType.Descending -> {
                when (contactOrder) {
                    is ContactOrder.FirstName -> finalList.sortByDescending { it.firstName.lowercase() }
                    is ContactOrder.LastName -> finalList.sortWith(compareByDescending { it.lastName })
                    is ContactOrder.TimeStamp -> finalList.sortByDescending { it.timeStamp }
                }
            }
        }
        return finalList
    }

}