package com.mycontacts.domain.main

import android.content.ContentResolver
import com.mycontacts.data.contacts.ContactInfo
import com.mycontacts.utils.order.ContactOrder
import com.mycontacts.utils.Resources
import kotlinx.coroutines.flow.Flow

interface Main {

    fun getAllContacts(contentResolver: ContentResolver, contactOrder: ContactOrder): Flow<Resources<List<ContactInfo>>>

    fun searchContacts(contentResolver: ContentResolver, searchQuery: String, searchContactOrder: ContactOrder): Flow<Resources<List<ContactInfo>>>

    suspend fun deleteContact(contentResolver: ContentResolver, contactInfo: ContactInfo): Boolean

    fun deleteSelectedContacts(contentResolver: ContentResolver, selectedContacts: List<ContactInfo>): Flow<Resources<List<ContactInfo>>>

    suspend fun restoreSelectedContacts(contentResolver: ContentResolver, selectedContacts: List<ContactInfo>): List<ContactInfo>

    suspend fun restoreContact(contentResolver: ContentResolver, contactInfo: ContactInfo): ContactInfo?

}