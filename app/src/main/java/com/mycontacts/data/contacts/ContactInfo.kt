package com.mycontacts.data.contacts

import android.graphics.Bitmap
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ContactInfo(
    val id: Long,
    val photo: Bitmap? = null,
    val firstName: String,
    val lastName: String? = null,
    val phoneNumber: String,
    val timeStamp: Long
): Parcelable