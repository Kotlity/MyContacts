package com.mycontacts.presentation.main.composables

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import com.mycontacts.R
import com.mycontacts.data.contacts.ContactInfo
import com.mycontacts.utils.Constants._1000
import com.mycontacts.utils.Constants._18sp
import com.mycontacts.utils.Constants._22sp

@Composable
fun DialAlertDialog(
    contactInfo: ContactInfo,
    onConfirmClick: () -> Unit,
    onDismissClick: () -> Unit,
) {

    var startAnimation by rememberSaveable {
        mutableStateOf(false)
    }

    val animateIconSize by animateDpAsState(
        targetValue = if (startAnimation) dimensionResource(id = R.dimen._30dp) else dimensionResource(id = R.dimen._0dp),
        animationSpec = tween(durationMillis = _1000)
    )

    if (!startAnimation) { LaunchedEffect(key1 = Unit) { startAnimation = true } }

    AlertDialog(
        onDismissRequest = onDismissClick,
        confirmButton = {
            AlertDialogButton(
                text = stringResource(id = R.string.ok),
                onClick = onConfirmClick
            )
        },
        dismissButton = {
            AlertDialogButton(
                text = stringResource(id = R.string.dismiss),
                onClick = onDismissClick
            )
        },
        icon = {
            Icon(
                modifier = Modifier.size(animateIconSize),
                imageVector = Icons.Default.Call,
                contentDescription = null
            )
        },
        title = {
            Text(
                text = stringResource(id = R.string.dialAlertDialogTitle),
                style = MaterialTheme.typography.titleLarge.copy(
                    fontSize = _22sp,
                    fontWeight = FontWeight.Bold
                )
            )
        },
        text = {
            Text(text = buildAnnotatedString {
                withStyle(style = SpanStyle(
                    fontSize = _18sp,
                    fontWeight = FontWeight.W600
                )) {
                    append(stringResource(id = R.string.dialAlertDialogText))
                }
                append(" ")
                withStyle(style = SpanStyle(
                    fontSize = _18sp,
                    textDecoration = TextDecoration.Underline
                )) {
                    append(contactInfo.phoneNumber)
                }
            })
        }
    )
}