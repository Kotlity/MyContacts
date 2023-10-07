package com.mycontacts.presentation.main.composables

import androidx.compose.foundation.layout.Row
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.res.colorResource
import com.mycontacts.R

@Composable
fun CustomRadioButtonSection(
    orderTitle: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(
            selected = isSelected,
            onClick = { onClick() },
            colors = RadioButtonDefaults.colors(
                selectedColor = colorResource(id = R.color.radioButtonSelected),
                unselectedColor = colorResource(id = R.color.radioButtonUnselected)
            )
        )
        Text(
            text = orderTitle,
            style = MaterialTheme.typography.labelLarge
        )
    }
}