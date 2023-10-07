package com.mycontacts.presentation.main.composables

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Sort
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import com.mycontacts.R
import com.mycontacts.utils.Constants._18sp

@Composable
fun ContactsOrderSection(
    modifier: Modifier,
    onIconClick: () -> Unit
) {

    val interactionSource = remember {
        MutableInteractionSource()
    }

    val isIconPressed by interactionSource.collectIsPressedAsState()

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = stringResource(id = R.string.contactOrderSortingTitle),
            style = MaterialTheme.typography.bodyLarge.copy(fontSize = _18sp)
        )
        IconButton(
            onClick = {
                onIconClick()
            },
            colors = IconButtonDefaults.iconButtonColors(
                containerColor = if (isIconPressed) colorResource(id = R.color.contactOrderIconPressed) else colorResource(id = R.color.contactOrderIconNotPressed)
            ),
            interactionSource = interactionSource
        ) {
            Icon(
                imageVector = Icons.Default.Sort,
                contentDescription = null
            )
        }
    }
}