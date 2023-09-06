package com.mycontacts.presentation.main.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import com.mycontacts.R
import com.mycontacts.presentation.main.composables.ContactList
import com.mycontacts.presentation.main.events.MainEvent
import com.mycontacts.presentation.main.composables.CustomProgressBar
import com.mycontacts.presentation.main.composables.CustomSearchBar
import com.mycontacts.presentation.main.composables.EmptyContacts
import com.mycontacts.presentation.main.states.ContactsSearchState
import com.mycontacts.presentation.main.states.ContactsState

@Composable
fun MainScreen(
    contactsState: ContactsState,
    contactsSearchState: ContactsSearchState,
    event: (MainEvent) -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        CustomSearchBar(
            contactsSearchState = contactsSearchState,
            onQueryChangeEvent = { event(MainEvent.SearchContact(it)) },
            onUpdateSearchBarEvent = { event(MainEvent.UpdateSearchBarState(it)) },
            onClearSearchQueryEvent = { event(MainEvent.ClearSearchQuery) }
        ) {
            if (contactsSearchState.isLoading) {
                CustomProgressBar(modifier = Modifier.fillMaxSize())
            }
            if (contactsSearchState.contacts.isNotEmpty()) {
                ContactList(
                    modifier = Modifier.fillMaxSize(),
                    contacts = contactsState.contacts,
                    onContactClick = {
                        event(MainEvent.OnSearchContactClick(it))
                    }
                )
            } else {
                EmptyContacts(
                    modifier = Modifier.fillMaxSize(),
                    message = contactsSearchState.errorMessage!!,
                    imagePainter = painterResource(id = R.drawable.icon_not_found)
                )
            }
        }
        if (contactsState.isLoading) {
            CustomProgressBar(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            )
        }
        if (contactsState.contacts.isNotEmpty()) {
            ContactList(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contacts = contactsState.contacts,
                onContactClick = {
                    event(MainEvent.OnGeneralContactClick(it))
                }
            )
        } else {
            EmptyContacts(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                message = contactsState.errorMessage!!,
                imagePainter = painterResource(id = R.drawable.no_image_contact)
            )
        }
    }
}