package com.mycontacts.domain.main

import android.content.ContentResolver
import com.mycontacts.data.contacts.ContactInfo
import kotlinx.coroutines.flow.Flow

interface Main {

    fun getAllContacts(contentResolver: ContentResolver): Flow<List<ContactInfo>>

    suspend fun getContactId(contactInfo: ContactInfo): Long
}