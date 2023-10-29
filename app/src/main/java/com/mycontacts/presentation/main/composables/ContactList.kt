package com.mycontacts.presentation.main.composables

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.mycontacts.data.contacts.ContactInfo

@Composable
fun ContactGeneralList(
    modifier: Modifier,
    lazyListState: LazyListState,
    contacts: List<ContactInfo>,
    onContactClick: (ContactInfo) -> Unit,
    onLongContactClick: (Int, ContactInfo) -> Unit
) {
    LazyColumn(
        modifier = modifier,
        state = lazyListState
    ) {
        itemsIndexed(contacts) { index, contact ->
            ContactItem(
                contactInfo = contact,
                onContactClick = {
                    onContactClick(contact)
                },
                onLongContactClick = {
                    onLongContactClick(index, contact)
                }
            )
        }
    }
}

@Composable
fun ContactSearchList(
    modifier: Modifier,
    contacts: List<ContactInfo>,
    onContactClick: (ContactInfo) -> Unit,
    onLongContactClick: (Int, ContactInfo) -> Unit
) {
    LazyColumn(modifier = modifier) {
        itemsIndexed(contacts) { index, contact ->
            ContactItem(
                contactInfo = contact,
                onContactClick = {
                    onContactClick(contact)
                },
                onLongContactClick = {
                    onLongContactClick(index, contact)
                }
            )
        }
    }
}