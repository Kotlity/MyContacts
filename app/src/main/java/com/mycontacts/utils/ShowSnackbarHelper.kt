package com.mycontacts.utils

import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult

suspend fun SnackbarHostState.showSnackbarWithAction(message: String, undo: String?, onUndoClick: () -> Unit) {
    val snackbarResult = showSnackbar(message = message, actionLabel = undo, duration = SnackbarDuration.Long)

    if (snackbarResult == SnackbarResult.ActionPerformed) onUndoClick()
}