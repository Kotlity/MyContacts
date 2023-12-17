package com.mycontacts.presentation.settings.composables

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource

@Composable
fun CustomOutlinedTextField(
    modifier: Modifier = Modifier,
    isExpanded: Boolean = false,
    isReadOnly: Boolean = true,
    @DrawableRes languageIcon: Int
) {
    OutlinedTextField(
        modifier = modifier,
        value = "",
        onValueChange = {},
        readOnly = isReadOnly,
        leadingIcon = {
            Image(
                painter = painterResource(id = languageIcon),
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