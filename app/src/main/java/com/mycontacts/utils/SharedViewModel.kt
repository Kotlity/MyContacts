package com.mycontacts.utils

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel

@Composable
inline fun <reified T: ViewModel> Context.sharedViewModel(): T {
    return hiltViewModel(viewModelStoreOwner = this as AppCompatActivity)
}