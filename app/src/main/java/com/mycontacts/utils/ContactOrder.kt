package com.mycontacts.utils

sealed class ContactOrder(val contactOrderType: ContactOrderType) {
    class FirstName(contactOrderType: ContactOrderType): ContactOrder(contactOrderType)
    class LastName(contactOrderType: ContactOrderType): ContactOrder(contactOrderType)
    class TimeStamp(contactOrderType: ContactOrderType): ContactOrder(contactOrderType)
}
