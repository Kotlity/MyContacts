package com.mycontacts.presentation.contact_operations.composables

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import com.mycontacts.R
import com.mycontacts.utils.Constants._18sp

@Composable
fun ContactInfoButtonOperations(
    modifier: Modifier = Modifier,
    onButtonClick: () -> Unit,
    text: String = "",
    supportingText: String = "",
    isEnabled: Boolean = false
) {
    Column {
        OutlinedButton(
            modifier = modifier,
            onClick = onButtonClick,
            enabled = isEnabled,
            shape = RoundedCornerShape(dimensionResource(id = R.dimen._15dp)),
            colors = ButtonDefaults.outlinedButtonColors(
                containerColor = Color.Green,
                disabledContainerColor = Color.Red
            )
        ) {
            Text(
                text = text
            )
        }
        Text(
            text = supportingText,
            style = MaterialTheme.typography.bodyMedium.copy(fontSize = _18sp)
        )
    }
}