package com.mycontacts.presentation.settings

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import com.mycontacts.presentation.initial.viewmodels.InitialViewModel
import com.mycontacts.utils.sharedViewModel

@Composable
fun SettingsScreen() {
    val context = LocalContext.current
    val initialViewModel: InitialViewModel = context.sharedViewModel()
}