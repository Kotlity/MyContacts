package com.mycontacts.presentation.pager.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mycontacts.domain.pager.Pager
import com.mycontacts.presentation.pager.events.OnPagerEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PagerViewModel @Inject constructor(private val pager: Pager): ViewModel() {

    fun onEvent(event: OnPagerEvent) {
        when(event) {
            OnPagerEvent.OnButtonClick -> {
                saveUserPressedStartButton()
            }
        }
    }

    private fun saveUserPressedStartButton() {
        viewModelScope.launch {
            pager.saveUserPressedStartButton()
        }
    }
}