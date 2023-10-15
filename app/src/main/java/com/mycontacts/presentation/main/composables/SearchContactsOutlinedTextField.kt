package com.mycontacts.presentation.main.composables

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Person2
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.mycontacts.R
import com.mycontacts.utils.ContactOrder
import com.mycontacts.utils.ContactOrderType

@Composable
fun SearchContactsOutlinedTextField(
    modifier: Modifier = Modifier,
    isExpanded: Boolean = false,
    currentSearchContactOrder: ContactOrder = ContactOrder.TimeStamp(ContactOrderType.Descending)
) {
    OutlinedTextField(
        modifier = modifier,
        value = when(currentSearchContactOrder) {
            is ContactOrder.TimeStamp -> stringResource(id = R.string.contactOrderTimestamp)
            is ContactOrder.FirstName -> stringResource(id = R.string.contactOrderFirstName)
            is ContactOrder.LastName -> stringResource(id = R.string.contactOrderLastName) },
        onValueChange = {},
        readOnly = true,
        leadingIcon = {
            Icon(
                imageVector = when(currentSearchContactOrder) {
                    is ContactOrder.TimeStamp -> Icons.Default.Timer
                    is ContactOrder.FirstName -> Icons.Default.Person
                    is ContactOrder.LastName -> Icons.Default.Person2 },
                contentDescription = null
            )
        },
        trailingIcon = {
            Icon(
                imageVector = if (isExpanded) Icons.Default.ArrowDropUp else Icons.Default.ArrowDropDown,
                contentDescription = null
            )
        }
    )
}