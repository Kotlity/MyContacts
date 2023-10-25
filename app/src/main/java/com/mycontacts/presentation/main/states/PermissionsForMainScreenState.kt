package com.mycontacts.presentation.main.states

data class PermissionsForMainScreenState(
    val isUserHasPermissionToAccessAllFiles: Boolean = false,
    val isUserHasPermissionToReadContacts: Boolean = false,
    val isUserHasPermissionToWriteContacts: Boolean = false
)
