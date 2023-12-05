package com.mycontacts.presentation.contact_operations.composables

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.text.KeyboardActionScope
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType

@Composable
fun CustomTextField(
    modifier: Modifier = Modifier,
    inputText: String = "",
    onInputChange: (String) -> Unit,
    isError: Boolean = false,
    label: String = "",
    trailingIcon: ImageVector = Icons.Default.Person,
    supportingText: String = "",
    keyboardType: KeyboardType = KeyboardType.Text,
    imeAction: ImeAction = ImeAction.Next,
    focusRequester: FocusRequester = FocusRequester.Default,
    onTrailingIconClick: () -> Unit,
    onShowKeyboard: (() -> Unit)? = null,
    onButtonClick: (KeyboardActionScope.() -> Unit),
    singleLine: Boolean = true
) {

    OutlinedTextField(
        modifier = modifier
            .focusRequester(focusRequester)
            .onFocusChanged { focusState ->
                if (focusState.isFocused) if (onShowKeyboard != null) {
                    onShowKeyboard()
                }
            },
        value = inputText,
        onValueChange = onInputChange,
        isError = isError,
        label = {
            Text(text = label)
        },
        trailingIcon = {
              UpdateTrailingIcon(
                  isInputTextEmpty = inputText.isEmpty(),
                  icon = trailingIcon,
                  onTrailingIconClick = onTrailingIconClick
              )
//            Icon(
//                imageVector = trailingIcon,
//                contentDescription = null
//            )
        },
        supportingText = {
            Text(
                text = supportingText
            )
        },
        keyboardOptions = KeyboardOptions(
            keyboardType = keyboardType,
            imeAction = imeAction
        ),
        keyboardActions = KeyboardActions(
            onNext = if (imeAction == ImeAction.Next) onButtonClick else return,
            onDone = if (imeAction == ImeAction.Done) onButtonClick else return
        ),
        singleLine = singleLine
    )
}

@Composable
private fun UpdateTrailingIcon(
    isInputTextEmpty: Boolean,
    icon: ImageVector,
    onTrailingIconClick: () -> Unit
) {
    AnimatedContent(targetState = isInputTextEmpty) { isEmpty ->
        if (isEmpty) {
            Icon(
                imageVector = icon,
                contentDescription = null
            )
        } else {
            Icon(
                modifier = Modifier.clickable { onTrailingIconClick() },
                imageVector = Icons.Default.Delete,
                contentDescription = null
            )
        }
    }
}