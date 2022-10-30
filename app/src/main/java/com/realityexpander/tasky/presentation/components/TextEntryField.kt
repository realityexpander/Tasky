package com.realityexpander.tasky.presentation.components

import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Error
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import com.realityexpander.tasky.R
import com.realityexpander.tasky.presentation.ui.theme.textEntryFieldTextStyle
import com.realityexpander.tasky.presentation.common.util.UiText

@Composable
fun TextEntryField(
    modifier: Modifier = Modifier,
    value: String,
    textStyle: TextStyle = textEntryFieldTextStyle(),
    label: String? = UiText.Res(R.string.textEntryField_label).get, // if this is null, label is not shown
    labelComponent: @Composable (() -> Unit)? =
        { Text(text = label ?: UiText.Res(R.string.textEntryField_label).get) },
    placeholder: String = UiText.Res(R.string.textEntryField_placeholder).get,
    isError: Boolean,
    onValueChange: (String) -> Unit,
    keyboardOptions: KeyboardOptions =
        KeyboardOptions(
            keyboardType = KeyboardType.Text,
            imeAction = ImeAction.Next
        ),
    keyboardActions: KeyboardActions? = null,
    leadingIcon: @Composable (() -> Unit)? = null,
    @Suppress("UNUSED_PARAMETER") // validInputDescription is used in the preview
    validInputDescription: String =
        UiText.Res(R.string.textEntryField_description_isValid).get,
    @Suppress("UNUSED_PARAMETER") // invalidInputDescription is used in the preview
    invalidInputDescription: String =
        UiText.Res(R.string.textEntryField_description_isInvalid).get,
) {
    val focusManager = LocalFocusManager.current
    val keyboardActionsLocal: KeyboardActions = keyboardActions
            ?: KeyboardActions(
            onNext = {
                focusManager.moveFocus(FocusDirection.Down)
            }
    )

    OutlinedTextField(
        modifier = modifier,
        value = value,
        singleLine = true,
        onValueChange = onValueChange,
        isError = isError,
        label = if (label != null) labelComponent else null,
        placeholder = { Text(text = placeholder) },
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActionsLocal,
        leadingIcon = leadingIcon,
        textStyle = textStyle,
        trailingIcon = {
            if (value.isNotBlank()) {
                val isNameValid = !isError

                val image =
                    if (isNameValid)
                        Icons.Filled.Check
                    else
                        Icons.Filled.Error

                // localized description for accessibility services
                val description =
                    if (isNameValid)
                        UiText.Res(R.string.textEntryField_description_isValid).get
                    else
                        UiText.Res(R.string.textEntryField_description_isInvalid).get

                Icon(imageVector = image, description)
            }
        }
    )
}
