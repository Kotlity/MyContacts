package com.mycontacts.data.contacts

import android.graphics.Bitmap

data class ContactInfo(
    val id: Long,
    val photo: Bitmap? = null,
    val firstName: String,
    val lastName: String? = null,
    val phoneNumber: String,
    val timeStamp: Long
)
