package com.mycontacts.domain.pager

interface Pager {

    fun saveUserPressedStartButton()

    fun hasUserAlreadyPressedStartButton(): Boolean
}