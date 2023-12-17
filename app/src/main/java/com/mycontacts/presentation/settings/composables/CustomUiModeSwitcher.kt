package com.mycontacts.presentation.settings.composables

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material3.Icon
import androidx.compose.material3.Switch
import androidx.compose.runtime.Composable

@Composable
fun CustomUiModeSwitcher(
    isSystemInDarkUiMode: Boolean = false,
    onToggleSwitch: (Boolean) -> Unit
) {
    Switch(
        checked = isSystemInDarkUiMode,
        onCheckedChange = onToggleSwitch,
        thumbContent = {
            Icon(
                imageVector = if (isSystemInDarkUiMode) Icons.Default.DarkMode else Icons.Default.LightMode,
                contentDescription = null
            )
        }
    )
}