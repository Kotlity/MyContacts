package com.mycontacts.presentation.main.composables

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.mycontacts.data.contacts.ContactInfo

@Composable
fun ContactList(
    modifier: Modifier,
    contacts: List<ContactInfo>,
    onContactClick: (ContactInfo) -> Unit
) {
    LazyColumn(modifier = modifier) {
        items(contacts) { contact ->
            ContactItem(
                contactInfo = contact,
                onContactClick = onContactClick
            )
        }
    }
}