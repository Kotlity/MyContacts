package com.mycontacts.presentation.main.composables

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import com.mycontacts.R

@Composable
fun CustomRadioButtonCard(
    modifier: Modifier,
    content: @Composable () -> Unit
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(dimensionResource(id = R.dimen._10dp)),
        elevation = CardDefaults.cardElevation(defaultElevation = dimensionResource(id = R.dimen._3dp)),
        border = BorderStroke(
            dimensionResource(id = R.dimen._2dp),
            MaterialTheme.colorScheme.primary
        )
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            content()
        }
    }
}