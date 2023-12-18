package com.mycontacts.utils

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalConfiguration

@Composable
fun currentLanguageCode(): String {
    val localConfiguration = LocalConfiguration.current
    return localConfiguration.locales[0].language
}