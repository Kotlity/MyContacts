package com.mycontacts.utils

import android.os.Parcelable
import androidx.navigation.NavController

fun <T: Parcelable> NavController.navigateWithArgument(key: String, argument: T, screenRoutes: ScreenRoutes) {
    currentBackStackEntry?.savedStateHandle?.set(key, argument)
    navigate(screenRoutes.route)
}

fun <T: Parcelable?> NavController.retrieveArgument(key: String): T? {
    return previousBackStackEntry?.savedStateHandle?.get(key)
}