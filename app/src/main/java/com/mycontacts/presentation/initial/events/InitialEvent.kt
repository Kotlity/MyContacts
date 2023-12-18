package com.mycontacts.presentation.initial.events

sealed interface InitialEvent {

    data object UpdateStartDestination: InitialEvent
    data class UpdateIsDarkUiModePreferences(val isDarkUiMode: Boolean): InitialEvent
    data class RetrieveIsDarkUiModePreferences(val initialValue: Boolean): InitialEvent
    data class RetrieveCurrentLanguageCode(val languageCode: String): InitialEvent
    data class ChangeAppLanguage(val languageCode: String): InitialEvent
    data class ChangeLanguageDropdownMenuExpandedState(val isExpanded: Boolean): InitialEvent
}