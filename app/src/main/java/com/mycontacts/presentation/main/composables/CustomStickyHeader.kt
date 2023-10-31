package com.mycontacts.presentation.main.composables

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import com.mycontacts.utils.Constants._20sp

@Composable
fun CustomStickyHeader(
    modifier: Modifier = Modifier,
    header: Char
) {
    Text(
        modifier = modifier,
        text = header.toString(),
        style = MaterialTheme.typography.titleMedium.copy(
            fontSize = _20sp,
            fontWeight = FontWeight.Bold
        )
    )
}