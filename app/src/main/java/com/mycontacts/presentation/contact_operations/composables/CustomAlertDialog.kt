package com.mycontacts.presentation.contact_operations.composables

import androidx.annotation.StringRes
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.window.DialogProperties
import com.mycontacts.R
import com.mycontacts.utils.CustomAlertDialogButton

@Composable
fun CustomAlertDialog(
    icon: ImageVector,
    @StringRes title: Int,
    fontSizes: List<TextUnit>,
    fontWeights: List<FontWeight>,
    @StringRes text: Int,
    onConfirmButtonClick: () -> Unit,
    onDismissButtonClick: () -> Unit
) {
    AlertDialog(
        onDismissRequest = {},
        confirmButton = {
            CustomAlertDialogButton(
                text = stringResource(id = R.string.ok),
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
                imageVector = icon,
                contentDescription = null
            )
        },
        title = {
            Text(
                text = stringResource(id = title),
                style = MaterialTheme.typography.titleMedium.copy(
                    fontSize = fontSizes[0],
                    fontWeight = fontWeights[0]
                )
            )
        },
        text = {
            Text(
                text = stringResource(id = text),
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontSize = fontSizes[1],
                    fontWeight = fontWeights[1],
                )
            )
        },
        properties = DialogProperties(
            dismissOnBackPress = false,
            dismissOnClickOutside = false
        )
    )
}