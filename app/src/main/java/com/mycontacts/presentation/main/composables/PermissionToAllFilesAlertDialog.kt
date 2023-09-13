package com.mycontacts.presentation.main.composables

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.window.DialogProperties
import com.mycontacts.R
import com.mycontacts.utils.Constants._17sp
import com.mycontacts.utils.Constants._20sp
import com.mycontacts.utils.Constants._22sp

@Composable
fun PermissionToAllFilesAlertDialog(
    onConfirmButtonClick: () -> Unit,
    onDismissButtonClick: () -> Unit,
) {
    
    var isAlertDialogVisible by rememberSaveable {
        mutableStateOf(true)
    }
    
    val confirmButtonInteractionSource by remember {
        mutableStateOf(MutableInteractionSource())
    }

    val dismissButtonInteractionSource by remember {
        mutableStateOf(MutableInteractionSource())
    }
    
    val isConfirmButtonPressed = confirmButtonInteractionSource.collectIsPressedAsState().value

    val isDismissButtonPressed = dismissButtonInteractionSource.collectIsPressedAsState().value
    
    if (isAlertDialogVisible) {
        AlertDialog(
            onDismissRequest = { },
            confirmButton = {
                AlertDialogTextButton(
                    onTextButtonClick = {
                        onConfirmButtonClick()
                        isAlertDialogVisible = false
                    },
                    isTextButtonPressed = isConfirmButtonPressed,
                    pressedTextButtonColor = colorResource(id = R.color.confirmButton),
                    text = stringResource(id = R.string.permissionToAllFilesAlertDialogConfirmButton),
                    interactionSource = confirmButtonInteractionSource
                )
            },
            dismissButton = {
                AlertDialogTextButton(
                    onTextButtonClick = {
                        onDismissButtonClick()
                        isAlertDialogVisible = false
                    },
                    isTextButtonPressed = isDismissButtonPressed,
                    pressedTextButtonColor = colorResource(id = R.color.dismissButton),
                    text = stringResource(id = R.string.permissionToAllFilesAlertDialogDismissButton),
                    interactionSource = dismissButtonInteractionSource
                )
            },
            icon = {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = null
                )
            },
            title = {
                Text(
                    text = stringResource(id = R.string.permissionToAllFilesAlertDialogTitle),
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontSize = _20sp,
                        fontWeight = FontWeight.Bold
                    )
                )
            },
            text = {
                Text(
                    text = stringResource(id = R.string.permissionToAllFilesAlertDialogText),
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontSize = _17sp,
                        fontWeight = FontWeight.W600
                    )
                )
            },
            properties = DialogProperties(
                dismissOnBackPress = false,
                dismissOnClickOutside = false
            )
        )
    }
}