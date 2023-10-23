package com.mycontacts.presentation.main.composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import com.mycontacts.R
import com.mycontacts.utils.Constants._18sp
import com.mycontacts.utils.Constants._20sp

@Composable
fun ContactActionsModalBottomSheet(
    modifier: Modifier = Modifier,
    onEditContactClick: () -> Unit,
    onDeleteContactClick: () -> Unit
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen._3dp))
    ) {
        Text(
            text = stringResource(id = R.string.contactActionsBottomSheetTitle),
            style = MaterialTheme.typography.titleMedium.copy(
                fontSize = _18sp,
                fontWeight = FontWeight.W600
            )
        )
        ContactActionButton(
            onClick = onEditContactClick,
            imageVector = Icons.Default.Edit,
            text = stringResource(id = R.string.contactActionsBottomSheetEditContact)
        )
        ContactActionButton(
            onClick = onDeleteContactClick,
            imageVector = Icons.Default.Delete,
            text = stringResource(id = R.string.contactActionsBottomSheetDeleteContact)
        )
    }
}

@Composable
private fun ContactActionButton(
    onClick: () -> Unit,
    imageVector: ImageVector,
    text: String
) {
    Button(
        modifier = Modifier.fillMaxWidth(0.6f),
        onClick = onClick,
        shape = RoundedCornerShape(dimensionResource(id = R.dimen._13dp))
    ) {
        Icon(
            imageVector = imageVector,
            contentDescription = null
        )
        Text(
            text = text,
            style = MaterialTheme.typography.titleLarge.copy(
                fontSize = _20sp,
                fontWeight = FontWeight.Bold
            )
        )
    }
}