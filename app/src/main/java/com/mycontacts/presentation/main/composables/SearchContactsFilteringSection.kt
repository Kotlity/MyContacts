package com.mycontacts.presentation.main.composables

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Person2
import androidx.compose.material.icons.filled.Timer
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import com.mycontacts.R
import com.mycontacts.utils.order.ContactOrder
import com.mycontacts.utils.order.ContactOrderType

@Composable
fun SearchContactsFilteringSection(
    modifier: Modifier,
    isExpanded: Boolean,
    currentSearchContactOrder: ContactOrder,
    onSearchContactOrderClick: (ContactOrder) -> Unit,
    onUpdateDropdownMenuVisibility: (Boolean) -> Unit,

    ) {
    Column(
        modifier = modifier
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = dimensionResource(id = R.dimen._5dp)),
            verticalAlignment = Alignment.CenterVertically
        ) {
            CustomRadioButtonSection(
                orderTitle = stringResource(id = R.string.contactOrderAscending),
                isSelected = currentSearchContactOrder.contactOrderType == ContactOrderType.Ascending,
                onClick = {
                    onSearchContactOrderClick(currentSearchContactOrder.updateContactOrderWithContactOrderType(
                        ContactOrderType.Ascending))
                }
            )
            CustomRadioButtonSection(
                orderTitle = stringResource(id = R.string.contactOrderDescending),
                isSelected = currentSearchContactOrder.contactOrderType == ContactOrderType.Descending,
                onClick = {
                    onSearchContactOrderClick(currentSearchContactOrder.updateContactOrderWithContactOrderType(
                        ContactOrderType.Descending))
                }
            )
            SearchContactsDropdownMenu(
                isExpanded = isExpanded,
                title = when(currentSearchContactOrder) {
                    is ContactOrder.TimeStamp -> stringResource(id = R.string.contactOrderTimestamp)
                    is ContactOrder.FirstName -> stringResource(id = R.string.contactOrderFirstName)
                    is ContactOrder.LastName -> stringResource(id = R.string.contactOrderLastName) },
                icon = when(currentSearchContactOrder) {
                    is ContactOrder.TimeStamp -> Icons.Default.Timer
                    is ContactOrder.FirstName -> Icons.Default.Person
                    is ContactOrder.LastName -> Icons.Default.Person2 },
                currentSearchContactOrder = currentSearchContactOrder,
                onUpdateDropdownMenuVisibility = onUpdateDropdownMenuVisibility,
                onSearchContactOrderClick = onSearchContactOrderClick
            )
        }
    }
}