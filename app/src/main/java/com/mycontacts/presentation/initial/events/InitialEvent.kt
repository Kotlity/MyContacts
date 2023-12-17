package com.mycontacts.presentation.initial.events

sealed interface InitialEvent {

    data object UpdateStartDestination: InitialEvent
    data class UpdateIsDarkUiModePreferences(val isDarkUiMode: Boolean): InitialEvent
    data object RetrieveIsDarkUiModePreferences: InitialEvent
    data object RetrieveCurrentLanguageCode: InitialEvent
    data class ChangeAppLanguage(val languageCode: String): InitialEvent
}