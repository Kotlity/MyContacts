package com.mycontacts.presentation.main.composables

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.dimensionResource
import com.mycontacts.R
import com.mycontacts.utils.Constants._22sp

@Composable
fun EmptyContacts(
    modifier: Modifier,
    message: String,
    imagePainter: Painter
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen._5dp), Alignment.CenterVertically)
    ) {
        Image(
            modifier = Modifier
                .size(dimensionResource(id = R.dimen._200dp))
                .clip(CircleShape),
            painter = imagePainter,
            contentDescription = null,
            contentScale = ContentScale.Crop
        )
        Text(
            text = message,
            style = MaterialTheme.typography.titleMedium.copy(fontSize = _22sp)
        )
    }
}