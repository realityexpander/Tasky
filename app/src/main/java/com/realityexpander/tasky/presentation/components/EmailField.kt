package com.realityexpander.tasky.presentation.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import com.realityexpander.tasky.R
import com.realityexpander.tasky.presentation.util.UiText

@Composable
fun EmailField(
    modifier: Modifier = Modifier.fillMaxWidth(),
    value: String,
    label: String? = UiText.Res(R.string.emailField_label).get, // if this is null, label is not shown.
    @Suppress("UNUSED_PARAMETER") // left for future use
    labelComponent: @Composable (() -> Unit)? =
        { Text(text = label ?: UiText.Res(R.string.emailField_label).get) },
    placeholder: String = UiText.Res(R.string.emailField_placeholder).get,
    isError: Boolean,
    onValueChange: (String) -> Unit,
    keyboardOptions: KeyboardOptions =
        KeyboardOptions(
            keyboardType = KeyboardType.Email,
            imeAction = ImeAction.Next
        ),
    keyboardActions: KeyboardActions? = null,
) {
    val focusManager = LocalFocusManager.current
    val keyboardActionsLocal: KeyboardActions = keyboardActions
        ?: KeyboardActions(
            onNext = {
                focusManager.moveFocus(FocusDirection.Down)
            }
    )

    TextEntryField(
        modifier = modifier,
        value = value,
        onValueChange = onValueChange,
        isError = isError,
        label = label,
        placeholder = placeholder,
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActionsLocal,
        leadingIcon = {
            Icon(imageVector = Icons.Filled.Email,
                UiText.Res(R.string.emailField_description_email).get)
        },
        validInputDescription = UiText.Res(R.string.emailField_description_isValid).get,
        invalidInputDescription = UiText.Res(R.string.emailField_description_isInvalid).get
    )
}

@Preview(showBackground = true)
@Composable
fun EmailFieldPreview() {
    EmailField(
        value = "",
        label = null,
        isError = false,
        onValueChange = {}
    )
}

@Preview(showBackground = true)
@Composable
fun EmailFieldPreviewError() {
    EmailField(
        value = "Bad.Email",
        isError = true,
        onValueChange = {}
    )
}

@Preview(showBackground = true)
@Composable
fun EmailFieldPreviewValid() {
    EmailField(
        value = "chris@demo.com",
        isError = false,
        onValueChange = {}
    )
}















