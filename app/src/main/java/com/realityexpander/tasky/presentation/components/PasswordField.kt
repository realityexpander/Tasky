package com.realityexpander.tasky.presentation.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import com.realityexpander.tasky.R
import com.realityexpander.tasky.common.UiText

@Composable
fun PasswordField(
    value: String,
    label: String = UiText.Res(R.string.passwordField_label).get(),
    placeholder: String = UiText.Res(R.string.passwordField_placeholder).get(),
    isError: Boolean,
    isPasswordVisible: Boolean = false,
    clickTogglePasswordVisibility: () -> Unit,
    onValueChange: (String) -> Unit,
    imeAction: ImeAction = ImeAction.Done,
    keyboardOptions: KeyboardOptions =
        KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = imeAction),
    keyboardActions: KeyboardActions? = null,
    doneAction : () -> Unit = {},
) {
    val focusManager = LocalFocusManager.current
    val keyboardActionsLocal: KeyboardActions = keyboardActions ?: KeyboardActions(
        onNext = {
            focusManager.moveFocus(FocusDirection.Down)
        },
        onDone = {
            focusManager.clearFocus()
            doneAction()
        }
    )

    OutlinedTextField(
        value = value,
        singleLine = true,
        onValueChange = onValueChange,
        isError = isError,
        label = { Text(text = label) },
        placeholder = { Text(text = placeholder) },
        modifier = Modifier.fillMaxWidth(1f),
        visualTransformation =
        if (isPasswordVisible)
            VisualTransformation.None
        else
            PasswordVisualTransformation(),
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActionsLocal,
        trailingIcon = {
            val image = if (isPasswordVisible)
                Icons.Default.VisibilityOff
            else
                Icons.Default.Visibility

            // Please provide localized description for accessibility services
            val description = if (isPasswordVisible)
                    UiText.Res(R.string.passwordField_description_hide).get()
                else
                    UiText.Res(R.string.passwordField_description_show).get()

            IconButton(onClick = clickTogglePasswordVisibility){
                Icon(imageVector  = image, description)
            }
        }
    )
}
