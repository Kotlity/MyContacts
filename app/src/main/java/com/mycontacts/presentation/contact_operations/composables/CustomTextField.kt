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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import com.mycontacts.utils.Constants._15sp
import com.mycontacts.utils.Constants._18sp
import com.mycontacts.utils.Constants._22sp

@Composable
fun CustomTextField(
    modifier: Modifier = Modifier,
    inputText: String = "",
    onInputChange: (String) -> Unit,
    isError: Boolean = false,
    textStyle: TextStyle = MaterialTheme.typography.displaySmall.copy(fontSize = _22sp),
    label: String = "",
    trailingIcon: ImageVector = Icons.Default.Person,
    supportingText: String? = null,
    keyboardType: KeyboardType = KeyboardType.Text,
    imeAction: ImeAction = ImeAction.Next,
    focusRequester: FocusRequester = FocusRequester.Default,
    onTrailingIconClick: () -> Unit,
    onButtonClick: (KeyboardActionScope.() -> Unit),
    singleLine: Boolean = true
) {

    OutlinedTextField(
        modifier = modifier
            .focusRequester(focusRequester),
        value = inputText,
        onValueChange = onInputChange,
        isError = isError,
        textStyle = textStyle,
        label = {
            Text(
                text = label,
                style = MaterialTheme.typography.headlineSmall.copy(fontSize = _18sp)
            )
        },
        trailingIcon = {
              UpdateTrailingIcon(
                  isInputTextEmpty = inputText.isBlank(),
                  icon = trailingIcon,
                  onTrailingIconClick = onTrailingIconClick
              )
        },
        supportingText = {
            supportingText?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = _15sp)
                )
            }
        },
        keyboardOptions = KeyboardOptions(
            keyboardType = keyboardType,
            imeAction = imeAction
        ),
        keyboardActions = KeyboardActions(
            onNext = if (imeAction == ImeAction.Next) onButtonClick else null,
            onDone = if (imeAction == ImeAction.Done) onButtonClick else null
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