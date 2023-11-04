package com.mycontacts.utils.order

sealed class ContactOrder(val contactOrderType: ContactOrderType) {
    class FirstName(contactOrderType: ContactOrderType): ContactOrder(contactOrderType)
    class LastName(contactOrderType: ContactOrderType): ContactOrder(contactOrderType)
    class TimeStamp(contactOrderType: ContactOrderType): ContactOrder(contactOrderType)

    fun updateContactOrderWithContactOrderType(contactOrderType: ContactOrderType): ContactOrder {
        return when (this) {
            is FirstName -> FirstName(contactOrderType)
            is LastName -> LastName(contactOrderType)
            is TimeStamp -> TimeStamp(contactOrderType)
        }
    }
}
