package com.mycontacts.utils.order

sealed class ContactOrderType {
    object Ascending: ContactOrderType()
    object Descending: ContactOrderType()
}
