package com.mycontacts.presentation.pager.viewmodels

import androidx.lifecycle.ViewModel
import com.mycontacts.domain.pager.Pager
import com.mycontacts.presentation.pager.events.OnPagerEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject

@HiltViewModel
class PagerViewModel @Inject constructor(private val pager: Pager): ViewModel() {

    var hasAlreadyPressedButtonState = MutableStateFlow(false)
        private set

    init {
        updateHasAlreadyPressedButtonState()
    }

    fun onEvent(event: OnPagerEvent) {
        when(event) {
            OnPagerEvent.OnButtonClick -> {
                saveUserPressedStartButton()
                updateHasAlreadyPressedButtonState()
            }
        }
    }

    private fun saveUserPressedStartButton() {
        pager.saveUserPressedStartButton()
    }

    private fun hasUserAlreadyPressedStartButton() = pager.hasUserAlreadyPressedStartButton()

    private fun updateHasAlreadyPressedButtonState() {
        hasAlreadyPressedButtonState.value = hasUserAlreadyPressedStartButton()
    }
}