package com.mycontacts.presentation.main.composables

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.SearchBar
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import com.mycontacts.R
import com.mycontacts.presentation.main.states.ContactsSearchState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomSearchBar(
    contactsSearchState: ContactsSearchState,
    onQueryChangeEvent: (String) -> Unit,
    onUpdateSearchBarEvent: (Boolean) -> Unit,
    onClearSearchQueryEvent: () -> Unit,
    content: @Composable () -> Unit
) {
    SearchBar(
        modifier = Modifier
            .fillMaxWidth()
            .padding(dimensionResource(id = R.dimen._10dp)),
        query = contactsSearchState.searchQuery,
        onQueryChange = { newSearch ->
            onQueryChangeEvent(newSearch)
        },
        onSearch = {
            onUpdateSearchBarEvent(false)
        },
        active = contactsSearchState.isSearchBarActive,
        onActiveChange = { activeState ->
            onUpdateSearchBarEvent(activeState)
        },
        placeholder = {
            Text(
                text = stringResource(id = R.string.searchBarPlaceholder),
                overflow = TextOverflow.Ellipsis
            )
        },
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = null
            )
        },
        trailingIcon = {
            Icon(
                modifier = Modifier.clickable {
                    if (contactsSearchState.searchQuery.isNotEmpty()) onClearSearchQueryEvent()
                    else onUpdateSearchBarEvent(false)
                },
                imageVector = Icons.Default.Clear,
                contentDescription = null
            )
        }
    ) {
        content()
    }
}