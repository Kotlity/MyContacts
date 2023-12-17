package com.mycontacts.domain.settings

interface LanguageSettings {

    val currentLanguageCode: String

    fun changeAppLanguage(languageCode: String)
}