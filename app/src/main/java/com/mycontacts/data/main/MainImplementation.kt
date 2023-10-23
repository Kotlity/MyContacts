package com.mycontacts.data.main

import android.content.ContentResolver
import android.content.ContentUris
import android.provider.ContactsContract
import androidx.core.database.getLongOrNull
import androidx.core.database.getStringOrNull
import com.mycontacts.data.contacts.ContactInfo
import com.mycontacts.domain.main.Main
import com.mycontacts.utils.Constants.contactsNotFound
import com.mycontacts.utils.Constants.deleteDelay
import com.mycontacts.utils.Constants.emptyContactsErrorMessage
import com.mycontacts.utils.Constants.searchDelay
import com.mycontacts.utils.ContactOrder
import com.mycontacts.utils.ContactOrderType
import com.mycontacts.utils.Resources
import com.mycontacts.utils.getColumnIndex
import com.mycontacts.utils.retrieveBitmap
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class MainImplementation: Main {

    override fun getAllContacts(contentResolver: ContentResolver, contactOrder: ContactOrder): Flow<Resources<List<ContactInfo>>> {
        return flow {
            emit(Resources.Loading())

            val result = retrieveContacts(contentResolver, RetrieveContactsMethod.GENERAL, contactOrder)

            if (result.isEmpty()) emit(Resources.Error(emptyContactsErrorMessage))
            else emit(Resources.Success(result))
        }
    }

    override fun searchContacts(contentResolver: ContentResolver, searchQuery: String, searchContactOrder: ContactOrder): Flow<Resources<List<ContactInfo>>> {
        return flow {
            emit(Resources.Loading())

            delay(searchDelay)

            val searchResult = retrieveContacts(contentResolver, RetrieveContactsMethod.SEARCH, searchContactOrder, searchQuery)

            if (searchResult.isEmpty()) emit(Resources.Error(contactsNotFound))
            else emit(Resources.Success(searchResult))
        }
    }

    override fun deleteContact(contentResolver: ContentResolver, contactId: Long): Flow<Resources<Boolean>> {
        return flow {
            emit(Resources.Loading())

            delay(deleteDelay)

            val rawContactId = getRawContactId(contentResolver, contactId)
            if (rawContactId == null) {
                emit(Resources.Success(false))
                return@flow
            }
            val rawContactUri = ContentUris.withAppendedId(ContactsContract.RawContacts.CONTENT_URI, rawContactId)

            contentResolver.delete(rawContactUri, null, null)
            emit(Resources.Success(true))
        }
    }

    private fun retrieveContacts(contentResolver: ContentResolver, retrieveContactsMethod: RetrieveContactsMethod, contactOrder: ContactOrder, searchQuery: String? = null): List<ContactInfo> {

        return when(retrieveContactsMethod) {
            RetrieveContactsMethod.GENERAL -> {
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
            RetrieveContactsMethod.SEARCH -> {
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

    private fun getRawContactId(contentResolver: ContentResolver, contactId: Long): Long? {
        var rawContactId: Long? = null

        contentResolver.query(
            ContactsContract.RawContacts.CONTENT_URI,
            arrayOf(ContactsContract.RawContacts._ID),
            ContactsContract.RawContacts.CONTACT_ID + " = ?",
            arrayOf(contactId.toString()),
            null
        )?.use { rawContactCursor ->
            if (rawContactCursor.moveToFirst()) {
                rawContactId = rawContactCursor.getLongOrNull(getColumnIndex(rawContactCursor, ContactsContract.RawContacts._ID))
            }
        }
        return rawContactId
    }

}

private enum class RetrieveContactsMethod {
    GENERAL, SEARCH
}