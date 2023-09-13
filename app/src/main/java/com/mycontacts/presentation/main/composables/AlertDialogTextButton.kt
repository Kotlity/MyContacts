package com.mycontacts.presentation.main.composables

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.text.font.FontWeight
import com.mycontacts.R
import com.mycontacts.utils.Constants

@Composable
fun AlertDialogTextButton(
    onTextButtonClick: () -> Unit,
    isTextButtonPressed: Boolean,
    pressedTextButtonColor: Color,
    text: String,
    interactionSource: MutableInteractionSource
) {
    TextButton(
        onClick = {
            onTextButtonClick()
        },
        interactionSource = interactionSource,
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = dimensionResource(id = R.dimen._5dp),
            pressedElevation = dimensionResource(id = R.dimen._0dp)
        ),
        colors = ButtonDefaults.textButtonColors(containerColor = if (isTextButtonPressed) pressedTextButtonColor else Color.Transparent)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelLarge.copy(
                fontSize = Constants._22sp,
                fontWeight = FontWeight.Bold
            )
        )
    }
}