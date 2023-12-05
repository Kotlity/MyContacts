@file:OptIn(ExperimentalMaterial3Api::class)

package com.mycontacts.utils

import android.content.Context
import android.content.pm.PackageManager
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SheetState
import androidx.core.content.ContextCompat
import com.mycontacts.presentation.main.events.MainEvent

fun isAppHasPermission(context: Context, permission: String) = ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED

suspend fun hideBottomSheet(sheetState: SheetState, event: (MainEvent) -> Unit) {
    sheetState.hide()
    event(MainEvent.UpdateModalBottomSheetVisibility)
}