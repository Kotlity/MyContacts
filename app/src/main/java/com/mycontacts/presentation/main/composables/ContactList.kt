@file:OptIn(ExperimentalFoundationApi::class)

package com.mycontacts.presentation.main.composables

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import com.mycontacts.R
import com.mycontacts.data.contacts.ContactInfo

@Composable
fun ContactGeneralList(
    modifier: Modifier,
    lazyListState: LazyListState,
    contactsMap: Map<Char, List<ContactInfo>>,
    onContactClick: (ContactInfo) -> Unit,
    onLongContactClick: (Int, ContactInfo) -> Unit
) {
    LazyColumn(
        modifier = modifier,
        state = lazyListState
    ) {
        contactsMap.forEach { (header, contacts) ->
            stickyHeader {
                CustomStickyHeader(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.primaryContainer)
                        .padding(dimensionResource(id = R.dimen._10dp)),
                    header = header
                )
            }
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