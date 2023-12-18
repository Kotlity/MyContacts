package com.mycontacts.presentation.settings.composables

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource
import com.mycontacts.R

@Composable
fun CustomLanguageDropdownMenuItem(
    @DrawableRes languageIcon: Int,
    onLanguageChange: (String) -> Unit,
    onDropdownMenuItemClick: (Boolean) -> Unit,
) {
    val isEnglishLanguage = languageIcon == R.drawable.usa

    DropdownMenuItem(
        text = { },
        onClick = {
            if (isEnglishLanguage) onLanguageChange("uk") else onLanguageChange("en")
            onDropdownMenuItemClick(false)
        },
        leadingIcon = {
            Image(
                painter = if (isEnglishLanguage) painterResource(id = R.drawable.ukraine) else painterResource(id = R.drawable.usa),
                contentDescription = null
            )
        }
    )
}