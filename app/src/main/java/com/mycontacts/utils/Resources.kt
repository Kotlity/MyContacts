package com.mycontacts.utils

sealed class Resources<T>(val data: T? = null, val errorMessage: String? = null) {
    class Success<T>(data: T): Resources<T>(data)
    class Error<T>(errorMessage: String): Resources<T>(errorMessage = errorMessage)
    class Loading<T>: Resources<T>()
}
