package com.mycontacts.presentation.main.composables

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.mycontacts.data.contacts.ContactInfo

@Composable
fun ContactGeneralList(
    modifier: Modifier,
    lazyListState: LazyListState,
    contacts: List<ContactInfo>,
    onContactClick: (ContactInfo) -> Unit
) {
    LazyColumn(
        modifier = modifier,
        state = lazyListState
    ) {
        items(contacts) { contact ->
            ContactItem(
                contactInfo = contact,
                onContactClick = onContactClick
            )
        }
    }
}

@Composable
fun ContactSearchList(
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