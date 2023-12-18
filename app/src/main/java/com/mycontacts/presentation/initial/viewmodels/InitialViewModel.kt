package com.mycontacts.presentation.initial.viewmodels

import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mycontacts.domain.pager.Pager
import com.mycontacts.domain.settings.LanguageSettings
import com.mycontacts.domain.shared.DataStoreHelper
import com.mycontacts.presentation.initial.events.InitialEvent
import com.mycontacts.utils.Constants._500L
import com.mycontacts.utils.Constants.isDarkUiModePreferences
import com.mycontacts.utils.ScreenRoutes
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class InitialViewModel @Inject constructor(
    private val pager: Pager,
    private val booleanDataStoreHelper: DataStoreHelper<Boolean>,
    private val languageSettings: LanguageSettings
): ViewModel() {

    var isShouldShowSplashScreen by mutableStateOf(true)
        private set

    var startDestination by mutableStateOf(ScreenRoutes.Pager.route)
        private set

    var isDarkUiMode by derivedStateOf {
        mutableStateOf(false)
    }.value
        private set

    var currentLanguageCode by derivedStateOf {
        mutableStateOf("")
    }.value
        private set

    var isExpandedLanguageDropdownMenu by mutableStateOf(false)
        private set

    init {
        onEvent(InitialEvent.UpdateStartDestination)
    }

    fun onEvent(initialEvent: InitialEvent) {
        when(initialEvent) {
            InitialEvent.UpdateStartDestination -> {
                updateStartDestination()
            }
            is InitialEvent.RetrieveIsDarkUiModePreferences -> {
                retrieveIsDarkUiModePreferences(initialEvent.initialValue)
            }
            is InitialEvent.UpdateIsDarkUiModePreferences -> {
                updateIsDarkUiModePreferences(initialEvent.isDarkUiMode)
            }
            is InitialEvent.RetrieveCurrentLanguageCode -> {
                retrieveCurrentLanguageCode(initialEvent.languageCode)
            }
            is InitialEvent.ChangeAppLanguage -> {
                changeAppLanguage(initialEvent.languageCode)
            }
            is InitialEvent.ChangeLanguageDropdownMenuExpandedState -> {
                changeLanguageDropdownMenuExpandedState(initialEvent.isExpanded)
            }
        }
    }

    private fun updateStartDestination() {
        pager.hasUserAlreadyPressedStartButton().onEach { hasUserAlreadyPressedStartButton ->
            startDestination = if (hasUserAlreadyPressedStartButton) ScreenRoutes.Main.route
            else ScreenRoutes.Pager.route
            delay(_500L)
            isShouldShowSplashScreen = false
        }.launchIn(viewModelScope)
    }

    private fun updateIsDarkUiModePreferences(isDarkUiMode: Boolean) {
        viewModelScope.launch {
            booleanDataStoreHelper.editValue(key = isDarkUiModePreferences, value = isDarkUiMode)
        }
    }

    private fun retrieveIsDarkUiModePreferences(initialValue: Boolean) {
        viewModelScope.launch {
            booleanDataStoreHelper.retrieveValue(key = isDarkUiModePreferences, initialValue = initialValue).collect { isInDarkUiMode ->
                isDarkUiMode = isInDarkUiMode
            }
        }
    }

    private fun retrieveCurrentLanguageCode(languageCode: String) {
        currentLanguageCode = languageCode
    }

    private fun changeAppLanguage(languageCode: String) {
        languageSettings.changeAppLanguage(languageCode)
    }

    private fun changeLanguageDropdownMenuExpandedState(isExpanded: Boolean) {
        isExpandedLanguageDropdownMenu = isExpanded
    }
}