package com.mycontacts.presentation.initial.viewmodels

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mycontacts.domain.pager.Pager
import com.mycontacts.domain.shared.DataStoreHelper
import com.mycontacts.presentation.initial.events.InitialEvent
import com.mycontacts.utils.Constants._500L
import com.mycontacts.utils.Constants.isDarkUiModePreferences
import com.mycontacts.utils.ScreenRoutes
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class InitialViewModel @Inject constructor(
    private val pager: Pager,
    private val booleanDataStoreHelper: DataStoreHelper<Boolean>
): ViewModel() {

    var isShouldShowSplashScreen by mutableStateOf(true)
        private set

    var startDestination by mutableStateOf(ScreenRoutes.Pager.route)
        private set

    var isDarkUiMode by mutableStateOf(false)
        private set

    private var updatingIsDarkUiModeJob: Job? = null

    init {
        onEvent(InitialEvent.UpdateStartDestination)
    }

    fun onEvent(initialEvent: InitialEvent) {
        when(initialEvent) {
            InitialEvent.UpdateStartDestination -> {
                updateStartDestination()
            }
            is InitialEvent.UpdateIsDarkUiModePreferences -> {
                updateIsDarkUiModePreferences(initialEvent.isDarkUiMode)
            }
            InitialEvent.RetrieveIsDarkUiModePreferences -> {
                retrieveIsDarkUiModePreferences()
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
        updatingIsDarkUiModeJob?.cancel()
        updatingIsDarkUiModeJob = viewModelScope.launch {
            booleanDataStoreHelper.editValue(key = isDarkUiModePreferences, value = isDarkUiMode)
            Log.e("MyTag", "updateIsDarkUiModePreferences: $isDarkUiMode")
        }
    }

    private fun retrieveIsDarkUiModePreferences() {
        updatingIsDarkUiModeJob?.invokeOnCompletion {
            viewModelScope.launch {
                booleanDataStoreHelper.retrieveValue(key = isDarkUiModePreferences).collect { isInDarkUiMode ->
                    Log.e("MyTag", "isInDarkUiMode: $isInDarkUiMode")
                    isDarkUiMode = isInDarkUiMode
                }
            }
        }
    }
}