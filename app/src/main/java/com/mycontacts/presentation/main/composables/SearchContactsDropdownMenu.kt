package com.mycontacts.presentation.main.composables

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Person2
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Person2
import androidx.compose.material.icons.outlined.Timer
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.mycontacts.R
import com.mycontacts.presentation.ui.theme.MyContactsTheme
import com.mycontacts.utils.Constants._17sp
import com.mycontacts.utils.ContactOrder
import com.mycontacts.utils.ContactOrderType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchContactsDropdownMenu(
    modifier: Modifier = Modifier,
    isExpanded: Boolean = false,
    currentSearchContactOrder: ContactOrder = ContactOrder.TimeStamp(ContactOrderType.Descending),
    onUpdateDropdownMenuVisibility: (Boolean) -> Unit,
    onSearchContactOrderClick: (ContactOrder) -> Unit,
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.TopEnd
    ) {
        ExposedDropdownMenuBox(
            expanded = isExpanded,
            onExpandedChange = onUpdateDropdownMenuVisibility
        ) {
            SearchContactsOutlinedTextField(
                modifier = Modifier.menuAnchor(),
                isExpanded = isExpanded,
                currentSearchContactOrder = currentSearchContactOrder
            )
            ExposedDropdownMenu(
                expanded = isExpanded,
                onDismissRequest = {
                    onUpdateDropdownMenuVisibility(false)
                }
            ) {
                searchContactsItemsList.forEachIndexed { index, searchContactItem ->
                    DropdownMenuItem(
                        modifier = Modifier.background(color = if (searchContactItem.getTitle() == currentSearchContactOrder.javaClass.simpleName) colorResource(id = R.color.green)
                        else colorResource(id = R.color.white)),
                        text = {
                            Text(
                                text = searchContactItem.getTitle(),
                                style = MaterialTheme.typography.titleMedium.copy(fontSize = _17sp)
                            )
                        },
                        onClick = {
                            when(index) {
                                0 -> onSearchContactOrderClick(ContactOrder.TimeStamp(currentSearchContactOrder.contactOrderType))
                                1 -> onSearchContactOrderClick(ContactOrder.FirstName(currentSearchContactOrder.contactOrderType))
                                2 -> onSearchContactOrderClick(ContactOrder.LastName(currentSearchContactOrder.contactOrderType))
                            }
                            onUpdateDropdownMenuVisibility(false)

                        },
                        leadingIcon = {
                            Icon(
                                imageVector = if (searchContactItem.getTitle() == currentSearchContactOrder.javaClass.simpleName) searchContactItem.selectedIcon else searchContactItem.unselectedIcon,
                                contentDescription = null
                            )
                        }
                    )
                }
            }
        }
    }
}

@Preview(showSystemUi = true, showBackground = true, uiMode = UI_MODE_NIGHT_YES)
@Preview(showSystemUi = true, showBackground = true)
@Composable
fun SearchContactsDropdownMenuPreview() {
    MyContactsTheme {

        var isExpanded by rememberSaveable {
            mutableStateOf(false)
        }

        var currentContactOrder by remember {
            derivedStateOf {
                mutableStateOf<ContactOrder>(ContactOrder.TimeStamp(ContactOrderType.Descending))
            }
        }.value

        SearchContactsDropdownMenu(
            modifier = Modifier.fillMaxWidth(),
            isExpanded = isExpanded,
            currentSearchContactOrder = currentContactOrder,
            onUpdateDropdownMenuVisibility = {
                isExpanded = it
            },
            onSearchContactOrderClick = { contactOrder ->
                currentContactOrder = contactOrder
            }
        )
    }
}

private data class SearchContactsItem(val selectedIcon: ImageVector, val unselectedIcon: ImageVector, @StringRes val title: Int) {

    @Composable
    fun getTitle() = stringResource(id = title)
}

private val searchContactsItemsList = listOf(
    SearchContactsItem(Icons.Default.Timer, Icons.Outlined.Timer, R.string.contactOrderTimestamp),
    SearchContactsItem(Icons.Default.Person, Icons.Outlined.Person, R.string.contactOrderFirstName),
    SearchContactsItem(Icons.Default.Person2, Icons.Outlined.Person2, R.string.contactOrderLastName)
)