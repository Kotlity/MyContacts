package com.mycontacts.utils

import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory

fun getColumnIndex(cursor: Cursor, column: String) = cursor.getColumnIndex(column)

fun retrieveBitmap(byteArray: ByteArray?): Bitmap? = if (byteArray == null) null else BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)