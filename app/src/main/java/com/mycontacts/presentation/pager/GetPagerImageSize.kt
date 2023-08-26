package com.mycontacts.presentation.pager

import android.content.res.Resources
import android.graphics.BitmapFactory
import androidx.compose.runtime.Composable

@Composable
fun getPagerImageSize(resources: Resources, drawableResId: Int): Pair<Int, Int> {
    val bitmapFactoryOptions = BitmapFactory.Options().apply {
        inJustDecodeBounds = true
    }

    BitmapFactory.decodeResource(resources, drawableResId, bitmapFactoryOptions)

    val bitmapWidth = bitmapFactoryOptions.outWidth
    val bitmapHeight = bitmapFactoryOptions.outHeight

    return bitmapWidth to bitmapHeight
}