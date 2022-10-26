package com.realityexpander.tasky.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Icon
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Person
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import com.realityexpander.tasky.R
import com.realityexpander.tasky.ui.util.UiText

@Composable
fun NameField(
    modifier: Modifier = Modifier.fillMaxWidth(),
    value: String,
    label: String? = UiText.Res(R.string.nameField_label).get(), // if this is null, label is not shown
    labelComponent: @Composable (() -> Unit)? =
        { Text(text = label ?: UiText.Res(R.string.nameField_label).get()) },
    placeholder: String = UiText.Res(R.string.nameField_placeholder).get(),
    isError: Boolean,
    onValueChange: (String) -> Unit,
    keyboardOptions: KeyboardOptions =
        KeyboardOptions(
            keyboardType = KeyboardType.Text,
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
            Icon(imageVector = Icons.Filled.Person,
                UiText.Res(R.string.nameField_description_name).get())
        },
        validInputDescription = UiText.Res(R.string.nameField_description_isValid).get(),
        invalidInputDescription = UiText.Res(R.string.nameField_description_isInvalid).get(),
    )
}

@Preview(showBackground = true)
@Composable
fun NameFieldPreview() {
    NameField(
        value = "John Doe",
        isError = false,
        onValueChange = {}
    )
}

@Preview(showBackground = true)
@Composable
fun NameFieldPreviewError() {
    NameField(
        value = "Chris Athanas",
        isError = true,
        onValueChange = {}
    )
}

@Preview(showBackground = true)
@Composable
fun NameFieldPreviewNoLabel() {
    NameField(
        value = "",
        label = null,
        isError = false,
        onValueChange = {}
    )
}