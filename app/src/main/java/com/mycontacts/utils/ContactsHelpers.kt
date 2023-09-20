package com.mycontacts.utils

import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.mycontacts.utils.Constants.datePattern
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun getColumnIndex(cursor: Cursor, column: String) = cursor.getColumnIndex(column)

fun retrieveBitmap(byteArray: ByteArray?): Bitmap? = if (byteArray == null) null else BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)

fun convertTimestamp(timeStamp: Long): String = SimpleDateFormat(datePattern, Locale.getDefault()).format(Date(timeStamp))