package com.mycontacts.presentation.contact_operations.composables

import android.graphics.Bitmap
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import com.mycontacts.R
import com.mycontacts.utils.Constants._500

@Composable
fun ContactInfoPhoto(
    photoBitmap: Bitmap? = null,
    onPhotoClick: () -> Unit
) {
    
    val photoBorderColor = animateColorAsState(
        targetValue = photoBitmap?.let { Color.Green } ?: Color.Red,
        animationSpec = tween(durationMillis = _500)
    ).value
    
    if (photoBitmap != null) {
        Image(
            modifier = Modifier
                .size(dimensionResource(id = R.dimen._200dp))
                .clip(CircleShape)
                .border(
                    border = BorderStroke(
                        width = dimensionResource(id = R.dimen._1dp),
                        color = photoBorderColor
                    ),
                    shape = CircleShape
                )
                .clickable { onPhotoClick() },
            bitmap = photoBitmap.asImageBitmap(),
            contentScale = ContentScale.Crop,
            contentDescription = null
        )
    } else {
        Image(
            modifier = Modifier
                .size(dimensionResource(id = R.dimen._200dp))
                .clip(CircleShape)
                .border(
                    border = BorderStroke(
                        width = dimensionResource(id = R.dimen._1dp),
                        color = photoBorderColor
                    ),
                    shape = CircleShape
                )
                .clickable { onPhotoClick() },
            painter = painterResource(id = R.drawable.no_image_contact),
            contentScale = ContentScale.Crop,
            contentDescription = null
        )
    }
}