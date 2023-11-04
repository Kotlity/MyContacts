package com.mycontacts.presentation.main.composables

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import com.mycontacts.R
import com.mycontacts.utils.order.ContactOrder
import com.mycontacts.utils.order.ContactOrderType

@Composable
fun RadioButtonsSection(
    modifier: Modifier,
    currentContactOrder: ContactOrder = ContactOrder.FirstName(ContactOrderType.Descending),
    onOrderClick: (ContactOrder) -> Unit
) {
    Row(
        modifier = modifier
    ) {
        CustomRadioButtonCard(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            CustomRadioButtonSection(
                orderTitle = stringResource(id = R.string.contactOrderFirstName),
                isSelected = currentContactOrder is ContactOrder.FirstName,
                onClick = {
                    onOrderClick(ContactOrder.FirstName(currentContactOrder.contactOrderType))
                }
            )
            CustomRadioButtonSection(
                orderTitle = stringResource(id = R.string.contactOrderLastName),
                isSelected = currentContactOrder is ContactOrder.LastName,
                onClick = {
                    onOrderClick(ContactOrder.LastName(currentContactOrder.contactOrderType))
                }
            )
            CustomRadioButtonSection(
                orderTitle = stringResource(id = R.string.contactOrderTimestamp),
                isSelected = currentContactOrder is ContactOrder.TimeStamp,
                onClick = {
                    onOrderClick(ContactOrder.TimeStamp(currentContactOrder.contactOrderType))
                }
            )
        }
        Spacer(modifier = Modifier.width(dimensionResource(id = R.dimen._5dp)))
        CustomRadioButtonCard(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            CustomRadioButtonSection(
                orderTitle = stringResource(id = R.string.contactOrderAscending),
                isSelected = currentContactOrder.contactOrderType is ContactOrderType.Ascending,
                onClick = {
                    onOrderClick(currentContactOrder.updateContactOrderWithContactOrderType(
                        ContactOrderType.Ascending))
                }
            )
            CustomRadioButtonSection(
                orderTitle = stringResource(id = R.string.contactOrderDescending),
                isSelected = currentContactOrder.contactOrderType is ContactOrderType.Descending,
                onClick = {
                    onOrderClick(currentContactOrder.updateContactOrderWithContactOrderType(
                        ContactOrderType.Descending))
                }
            )
        }
    }
}