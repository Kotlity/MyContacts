package com.mycontacts.utils

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.text.font.FontWeight
import com.mycontacts.R

@Composable
fun ModalBottomSheetActionButton(
    backgroundColor: Color = MaterialTheme.colorScheme.primary,
    imageVector: ImageVector,
    text: String,
    onClick: () -> Unit,
) {
    Button(
        modifier = Modifier.fillMaxWidth(0.6f),
        onClick = onClick,
        shape = RoundedCornerShape(dimensionResource(id = R.dimen._13dp)),
        colors = ButtonDefaults.buttonColors(containerColor = backgroundColor)
    ) {
        Icon(
            imageVector = imageVector,
            contentDescription = null
        )
        Text(
            text = text,
            style = MaterialTheme.typography.titleLarge.copy(
                fontSize = Constants._20sp,
                fontWeight = FontWeight.Bold
            )
        )
    }
}