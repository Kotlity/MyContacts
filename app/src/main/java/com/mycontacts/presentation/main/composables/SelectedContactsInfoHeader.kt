package com.mycontacts.presentation.main.composables

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import com.mycontacts.R
import com.mycontacts.utils.Constants._22sp
import com.mycontacts.utils.Constants._24sp

@Composable
fun SelectedContactsInfoHeader(
    modifier: Modifier = Modifier,
    selectedContactsInfoCount: Int,
    onDeleteIconClick: () -> Unit
) {

    var previousCount = rememberSaveable { mutableIntStateOf(selectedContactsInfoCount) }.intValue

    SideEffect {
        previousCount = selectedContactsInfoCount
    }

    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = stringResource(id = R.string.selectedContactsInfoHeader),
            style = textStyleForHeader(textSize = _22sp)
        )

        Spacer(modifier = Modifier.width(dimensionResource(id = R.dimen._3dp)))

        val currentCountString = selectedContactsInfoCount.toString()
        val previousCountString = previousCount.toString()
        for (index in currentCountString.indices) {
            val oldChar = previousCountString.getOrNull(index)
            val newChar = currentCountString[index]
            val char = if (oldChar == newChar) previousCountString[index] else currentCountString[index]

            AnimatedContent(
                targetState = char,
                transitionSpec = {
                    if (selectedContactsInfoCount > previousCount) {
                        slideInVertically { initialOffsetY -> initialOffsetY } togetherWith slideOutVertically { targetOffsetY -> -targetOffsetY }
                    }
                    else {
                        slideInVertically { initialOffsetY -> -initialOffsetY } togetherWith slideOutVertically { targetOffsetY -> targetOffsetY }
                    }
                }, label = ""
            ) { animatedChar ->
                Text(
                    text = animatedChar.toString(),
                    softWrap = false,
                    style = textStyleForHeader(textSize = _24sp)
                )
            }
        }
        Spacer(modifier = Modifier.weight(1f))

        IconButton(
            onClick = onDeleteIconClick
        ) {
            Icon(
                modifier = Modifier.size(dimensionResource(id = R.dimen._30dp)),
                imageVector = Icons.Default.Delete,
                contentDescription = null
            )
        }
    }

}

@Composable
private fun textStyleForHeader(textSize: TextUnit) = MaterialTheme.typography.titleLarge.copy(fontSize = textSize, fontWeight = FontWeight.W600)