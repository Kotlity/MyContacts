package com.mycontacts.domain.pager

import kotlinx.coroutines.flow.Flow

interface Pager {

    suspend fun saveUserPressedStartButton()

    fun hasUserAlreadyPressedStartButton(): Flow<Boolean>
}