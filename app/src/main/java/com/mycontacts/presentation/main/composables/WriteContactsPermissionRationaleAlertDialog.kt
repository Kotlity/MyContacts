package com.mycontacts.presentation.main.composables

import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Start
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.window.DialogProperties
import com.mycontacts.R
import com.mycontacts.utils.Constants._16sp
import com.mycontacts.utils.Constants._18sp
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
            WriteContactsButton(
                text = stringResource(id = R.string.confirm),
                onClick = onConfirmButtonClick
            )
        },
        dismissButton = {
            WriteContactsButton(
                text = stringResource(id = R.string.dismiss),
                onClick = onDismissButtonClick
            )
        },
        icon = {
            Icon(
                modifier = Modifier.size(dimensionResource(id = R.dimen._30dp)),
                imageVector = when(contactAction) {
                    ContactAction.EDIT -> Icons.Default.Edit
                    ContactAction.DELETE -> Icons.Default.Delete
                    ContactAction.INITIAL -> Icons.Default.Start
                },
                contentDescription = null
            )
        },
        text = {
            Text(
                text = when (contactAction) {
                    ContactAction.EDIT -> stringResource(id = R.string.writeContactsPermissionRationaleAlertDialogEdit)
                    ContactAction.DELETE -> stringResource(id = R.string.writeContactsPermissionRationaleAlertDialogDelete)
                    ContactAction.INITIAL -> ""
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

@Composable
private fun WriteContactsButton(
    text: String,
    onClick: () -> Unit
) {
    TextButton(onClick = onClick) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelMedium.copy(fontSize = _18sp)
        )
    }
}