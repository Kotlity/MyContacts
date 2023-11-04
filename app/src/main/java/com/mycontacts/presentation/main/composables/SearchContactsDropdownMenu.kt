package com.mycontacts.presentation.main.composables

import androidx.annotation.StringRes
import androidx.compose.foundation.background
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import com.mycontacts.R
import com.mycontacts.utils.Constants._17sp
import com.mycontacts.utils.order.ContactOrder

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchContactsDropdownMenu(
    isExpanded: Boolean,
    title: String,
    icon: ImageVector,
    currentSearchContactOrder: ContactOrder,
    onUpdateDropdownMenuVisibility: (Boolean) -> Unit,
    onSearchContactOrderClick: (ContactOrder) -> Unit,
) {
    ExposedDropdownMenuBox(
        expanded = isExpanded,
        onExpandedChange = onUpdateDropdownMenuVisibility
    ) {
        SearchContactsOutlinedTextField(
            modifier = Modifier.menuAnchor(),
            title = title,
            isExpanded = isExpanded,
            icon = icon
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

private data class SearchContactsItem(val selectedIcon: ImageVector, val unselectedIcon: ImageVector, @StringRes val title: Int) {

    @Composable
    fun getTitle() = stringResource(id = title)
}

private val searchContactsItemsList = listOf(
    SearchContactsItem(Icons.Default.Timer, Icons.Outlined.Timer, R.string.contactOrderTimestamp),
    SearchContactsItem(Icons.Default.Person, Icons.Outlined.Person, R.string.contactOrderFirstName),
    SearchContactsItem(Icons.Default.Person2, Icons.Outlined.Person2, R.string.contactOrderLastName)
)