package com.mycontacts.presentation.main.composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Mouse
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import com.mycontacts.R
import com.mycontacts.utils.Constants._18sp
import com.mycontacts.utils.ModalBottomSheetActionButton

@Composable
fun ContactActionsModalBottomSheet(
    modifier: Modifier = Modifier,
    onEditContactClick: () -> Unit,
    onDeleteContactClick: () -> Unit,
    onSelectedModeClick: () -> Unit,
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
        ModalBottomSheetActionButton(
            onClick = onEditContactClick,
            imageVector = Icons.Default.Edit,
            text = stringResource(id = R.string.contactActionsBottomSheetEditContact)
        )
        ModalBottomSheetActionButton(
            onClick = onDeleteContactClick,
            imageVector = Icons.Default.Delete,
            text = stringResource(id = R.string.contactActionsBottomSheetDeleteContact)
        )
        ModalBottomSheetActionButton(
            backgroundColor = MaterialTheme.colorScheme.error,
            onClick = onSelectedModeClick,
            imageVector = Icons.Default.Mouse,
            text = stringResource(id = R.string.contactActionsBottomSheetSelectionMode)
        )
    }
}