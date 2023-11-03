@file:OptIn(ExperimentalMaterial3Api::class)

package com.mycontacts.utils

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SheetState
import androidx.core.content.ContextCompat
import com.mycontacts.presentation.main.events.MainEvent

fun isAppHasPermissionToWriteContacts(context: Context) = ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_CONTACTS) == PackageManager.PERMISSION_GRANTED

suspend fun hideBottomSheet(sheetState: SheetState, event: (MainEvent) -> Unit) {
    sheetState.hide()
    event(MainEvent.UpdateModalBottomSheetVisibility)
}