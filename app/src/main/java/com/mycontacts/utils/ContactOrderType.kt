package com.mycontacts.utils

sealed class ContactOrderType {
    object Ascending: ContactOrderType()
    object Descending: ContactOrderType()
}
