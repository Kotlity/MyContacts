package com.mycontacts.data.settings

import android.app.LocaleManager
import android.content.Context
import android.os.Build
import android.os.LocaleList
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import com.mycontacts.domain.settings.LanguageSettings
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.Locale
import javax.inject.Inject

class AppLanguageSettings @Inject constructor(@ApplicationContext private val context: Context): LanguageSettings {

    override fun changeAppLanguage(languageCode: String) {
        if (isGreaterOrEqualTiramisu()) {
            val localeManager = context.getSystemService(LocaleManager::class.java)
            val locale = Locale.forLanguageTag(languageCode)
            val localeList = LocaleList(locale)
            localeManager.applicationLocales = localeList
        } else {
            val localeListCompat = LocaleListCompat.forLanguageTags(languageCode)
            AppCompatDelegate.setApplicationLocales(localeListCompat)
        }
    }

    private fun isGreaterOrEqualTiramisu() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU
}