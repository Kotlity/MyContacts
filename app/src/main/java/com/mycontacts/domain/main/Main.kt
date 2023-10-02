package com.mycontacts.domain.main

import android.content.ContentResolver
import com.mycontacts.data.contacts.ContactInfo
import com.mycontacts.utils.Resources
import kotlinx.coroutines.flow.Flow

interface Main {

    fun getAllContacts(contentResolver: ContentResolver): Flow<Resources<List<ContactInfo>>>

    fun searchContacts(contentResolver: ContentResolver, searchQuery: String): Flow<Resources<List<ContactInfo>>>

}