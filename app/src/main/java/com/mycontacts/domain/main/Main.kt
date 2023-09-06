package com.mycontacts.domain.main

import android.content.ContentResolver
import com.mycontacts.data.contacts.ContactInfo
import kotlinx.coroutines.flow.Flow

interface Main {

    fun getAllContacts(contentResolver: ContentResolver): Flow<List<ContactInfo>>

    fun searchContacts(contentResolver: ContentResolver, searchQuery: String): Flow<List<ContactInfo>>

    suspend fun getContactId(contactInfo: ContactInfo): Long
}