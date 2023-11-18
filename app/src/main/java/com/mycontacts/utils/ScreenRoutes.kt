package com.mycontacts.utils

sealed class ScreenRoutes(val route: String) {
    object Pager: ScreenRoutes("Pager")
    object Main: ScreenRoutes("Main")
    object Settings: ScreenRoutes("Settings")
    object ContactOperations: ScreenRoutes("Contact editor")
}
