package com.mycontacts.presentation.main.composables

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.text.font.FontWeight
import com.mycontacts.R
import com.mycontacts.utils.Constants._15sp

@Composable
fun CustomExtendedFloatingActionButton(
    modifier: Modifier = Modifier,
    text: String,
    icon: ImageVector,
    onClick: () -> Unit,
    isExpanded: Boolean,
    mutableInteractionSource: MutableInteractionSource
) {

    ExtendedFloatingActionButton(
        modifier = modifier,
        text = {
            Text(
                text = text,
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontSize = _15sp,
                    fontWeight = FontWeight.Bold
                )
            )
        },
        icon = {
            Icon(
                imageVector = icon,
                contentDescription = null
            )
        },
        onClick = onClick,
        expanded = isExpanded,
        elevation = FloatingActionButtonDefaults.loweredElevation(
            defaultElevation = dimensionResource(id = R.dimen._0dp),
            pressedElevation = dimensionResource(id = R.dimen._10dp)
        ),
        interactionSource = mutableInteractionSource
    )
}