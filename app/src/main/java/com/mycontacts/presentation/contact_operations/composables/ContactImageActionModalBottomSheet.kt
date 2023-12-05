package com.mycontacts.presentation.contact_operations.composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Camera
import androidx.compose.material.icons.filled.Image
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import com.mycontacts.R
import com.mycontacts.utils.ModalBottomSheetActionButton

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContactImageActionModalBottomSheet(
    sheetState: SheetState,
    onDismiss: () -> Unit,
    onTakeAPhoto: () -> Unit,
    onSelectAPhoto: () -> Unit
) {

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen._5dp))
        ) {
            ModalBottomSheetActionButton(
                imageVector = Icons.Default.Camera,
                text = stringResource(id = R.string.takeAPhotoTitle),
                onClick = onTakeAPhoto
            )
            ModalBottomSheetActionButton(
                imageVector = Icons.Default.Image,
                text = stringResource(id = R.string.selectAPhotoTitle),
                onClick = onSelectAPhoto
            )
        }
    }
}
