package com.mycontacts.presentation.initial

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mycontacts.domain.pager.Pager
import com.mycontacts.utils.Constants._500L
import com.mycontacts.utils.ScreenRoutes
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class InitialViewModel @Inject constructor(private val pager: Pager): ViewModel() {

    var isShouldShowSplashScreen by mutableStateOf(true)
        private set

    var startDestination by mutableStateOf(ScreenRoutes.Pager.route)
        private set

    init {
        updateStartDestination()
    }

    private fun updateStartDestination() {
        pager.hasUserAlreadyPressedStartButton().onEach { hasUserAlreadyPressedStartButton ->
            startDestination = if (hasUserAlreadyPressedStartButton) ScreenRoutes.Main.route
            else ScreenRoutes.Pager.route
            delay(_500L)
            isShouldShowSplashScreen = false
        }.launchIn(viewModelScope)
    }
}