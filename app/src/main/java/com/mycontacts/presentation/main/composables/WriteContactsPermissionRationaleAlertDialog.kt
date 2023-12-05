package com.mycontacts.presentation.main.composables

import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Start
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.window.DialogProperties
import com.mycontacts.R
import com.mycontacts.utils.CustomAlertDialogButton
import com.mycontacts.utils.Constants._16sp
import com.mycontacts.utils.ContactAction

@Composable
fun WriteContactsPermissionRationaleAlertDialog(
    contactAction: ContactAction,
    onConfirmButtonClick: () -> Unit,
    onDismissButtonClick: () -> Unit
) {
    AlertDialog(
        onDismissRequest = {},
        confirmButton = {
            CustomAlertDialogButton(
                text = stringResource(id = R.string.confirm),
                onClick = onConfirmButtonClick
            )
        },
        dismissButton = {
            CustomAlertDialogButton(
                text = stringResource(id = R.string.dismiss),
                onClick = onDismissButtonClick
            )
        },
        icon = {
            Icon(
                modifier = Modifier.size(dimensionResource(id = R.dimen._30dp)),
                imageVector = when(contactAction) {
                    ContactAction.INITIAL -> Icons.Default.Start
                    ContactAction.EDIT -> Icons.Default.Edit
                    ContactAction.DELETE -> Icons.Default.Delete
                    ContactAction.DELETE_MULTIPLE -> Icons.Default.DeleteForever
                },
                contentDescription = null
            )
        },
        text = {
            Text(
                text = when (contactAction) {
                    ContactAction.INITIAL -> ""
                    ContactAction.EDIT -> stringResource(id = R.string.writeContactsPermissionRationaleAlertDialogEdit)
                    ContactAction.DELETE -> stringResource(id = R.string.writeContactsPermissionRationaleAlertDialogDelete)
                    ContactAction.DELETE_MULTIPLE -> stringResource(id = R.string.writeContactsPermissionRationaleAlertDialogDeleteMultiple)
                },
                style = MaterialTheme.typography.titleSmall.copy(
                    fontSize = _16sp,
                    fontStyle = FontStyle.Italic,
                    fontWeight = FontWeight.W600
                )
            )
        },
        properties = DialogProperties(dismissOnBackPress = false, dismissOnClickOutside = false)
    )
}