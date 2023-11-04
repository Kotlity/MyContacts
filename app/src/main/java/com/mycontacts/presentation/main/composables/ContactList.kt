@file:OptIn(ExperimentalFoundationApi::class)

package com.mycontacts.presentation.main.composables

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import com.mycontacts.R
import com.mycontacts.data.contacts.ContactInfo

@Composable
fun ContactGeneralList(
    modifier: Modifier,
    lazyListState: LazyListState,
    isAtLeastOneContactInfoSelected: (Char) -> Boolean,
    contactsMap: Map<Char, List<ContactInfo>>,
    onContactClick: (Int, ContactInfo) -> Unit,
    onLongContactClick: (Int, ContactInfo) -> Unit,
    onStickyHeaderClick: (Char) -> Unit
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
                        .background(
                            if (isAtLeastOneContactInfoSelected(header)) colorResource(id = R.color.dismissButton)
                            else colorResource(id = R.color.teal_700)
                        )
                        .padding(dimensionResource(id = R.dimen._10dp))
                        .clickable {
                            onStickyHeaderClick(header)
                        },
                    header = header
                )
            }
            itemsIndexed(contacts) { index, contact ->
                ContactItem(
                    contactInfo = contact,
                    onContactClick = {
                        onContactClick(index, contact)
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
    onContactClick: (Int, ContactInfo) -> Unit,
    onLongContactClick: (Int, ContactInfo) -> Unit
) {
    LazyColumn(modifier = modifier) {
        itemsIndexed(contacts) { index, contact ->
            ContactItem(
                contactInfo = contact,
                onContactClick = {
                    onContactClick(index, contact)
                },
                onLongContactClick = {
                    onLongContactClick(index, contact)
                }
            )
        }
    }
}