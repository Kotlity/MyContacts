package com.mycontacts.presentation.main.composables

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector

@Composable
fun SearchContactsOutlinedTextField(
    modifier: Modifier = Modifier,
    title: String,
    isExpanded: Boolean = false,
    icon: ImageVector
) {
    OutlinedTextField(
        modifier = modifier,
        value = title,
        onValueChange = {},
        readOnly = true,
        leadingIcon = {
            Icon(
                imageVector = icon,
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