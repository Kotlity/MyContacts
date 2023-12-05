package com.mycontacts.presentation.contact_operations.composables

import androidx.compose.animation.animateColor
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.PointerInputScope
import androidx.compose.ui.input.pointer.pointerInput
import com.mycontacts.utils.Constants._0f
import com.mycontacts.utils.Constants._180f
import com.mycontacts.utils.Constants._500

@Composable
fun DeletePhotoOrLastNameIcon(
    onIconClick: () -> Unit
) {

    var isIconPressed by remember {
        derivedStateOf {
            mutableStateOf(false)
        }
    }.value

    val iconTransition = updateTransition(targetState = isIconPressed, label = null)

    val rotationIconAnimation by iconTransition.animateFloat(
        label = "",
        transitionSpec = {
            when {
                true isTransitioningTo false -> tween(durationMillis = _500)
                false isTransitioningTo true -> tween(durationMillis = _500)
                else -> spring()
            }
        }
    ) { isPressed ->
        if (isPressed) _180f else _0f
    }

    val colorIconAnimation by iconTransition.animateColor(
        label = "",
        transitionSpec = {
            when {
                true isTransitioningTo false -> tween(durationMillis = _500)
                false isTransitioningTo true -> tween(durationMillis = _500)
                else -> spring()
            }
        }
    ) { isPressed ->
        if (isPressed) Color.Green else Color.Red
    }

    Icon(
        modifier = Modifier
            .pointerInput(Unit) {
                onIconTap { onIconClick() }
            }
            .pointerInput(Unit) {
                awaitEachGesture {
                    awaitFirstDown()
                    isIconPressed = true
                    waitForUpOrCancellation()
                    isIconPressed = false
                }
            }
            .graphicsLayer {
                rotationZ = rotationIconAnimation
            }
            .background(color = colorIconAnimation, shape = CircleShape)
        ,
        imageVector = Icons.Default.Cancel,
        contentDescription = null
    )
}

private suspend inline fun PointerInputScope.onIconTap(crossinline onTap: () -> Unit) {
    detectTapGestures(onTap = { onTap() })
}