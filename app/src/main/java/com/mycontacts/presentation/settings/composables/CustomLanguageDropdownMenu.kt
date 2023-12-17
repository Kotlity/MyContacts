package com.mycontacts.presentation.settings.composables

import androidx.annotation.DrawableRes
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomLanguageDropdownMenu(
    isExpanded: Boolean = false,
    onExpandedChange: (Boolean) -> Unit,
    onLanguageChange: (String) -> Unit,
    @DrawableRes languageIcon: Int
) {

    ExposedDropdownMenuBox(
        expanded = isExpanded,
        onExpandedChange = onExpandedChange
    ) {
        CustomOutlinedTextField(
            modifier = Modifier.menuAnchor(),
            isExpanded = isExpanded,
            languageIcon = languageIcon
        )
        ExposedDropdownMenu(
            expanded = isExpanded,
            onDismissRequest = { onExpandedChange(false) }
        ) {
            CustomLanguageDropdownMenuItem(
                languageIcon = languageIcon,
                onLanguageChange = onLanguageChange,
                onDropdownMenuItemClick = onExpandedChange
            )
        }
    }
}