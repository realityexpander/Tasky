package com.realityexpander.tasky.ui.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
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
    @SuppressWarnings("unused_parameter")
    label: String? = UiText.Res(R.string.passwordField_label).get(),
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
        //label = { if(isLabelVisible) Text(text = label) },
        placeholder = { Text(text = placeholder) },
        modifier = Modifier.fillMaxWidth(1f),
        visualTransformation =
            if (isPasswordVisible)
                VisualTransformation.None
            else
                PasswordVisualTransformation(),
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActionsLocal,
        leadingIcon = {
            Icon(imageVector = Icons.Filled.Lock,
            UiText.Res(R.string.passwordField_description_lock).get())
        },
        trailingIcon = {
            val isPasswordValid = !isError

            val validImage =
                if (isPasswordValid)
                    Icons.Filled.Check
                else
                    Icons.Filled.Error

            // localized description for accessibility services
            val validDescription =
                if (isPasswordValid)
                    UiText.Res(R.string.emailField_description_isValid).get()
                else
                    UiText.Res(R.string.emailField_description_isInvalid).get()

            val passwordVisibleImage = if (isPasswordVisible)
                Icons.Default.Visibility
            else
                Icons.Default.VisibilityOff

            // localized description for accessibility services
            val passwordVisibleDescription = if (isPasswordVisible)
                    UiText.Res(R.string.passwordField_description_hide).get()
                else
                    UiText.Res(R.string.passwordField_description_show).get()

            Row {
                if(value.isNotBlank()) {
                    Icon(
                        imageVector = validImage,
                        contentDescription = validDescription,
                        modifier = Modifier
                            .align(Alignment.CenterVertically)
                    )
                }

                IconButton(onClick = clickTogglePasswordVisibility) {
                    Icon(imageVector = passwordVisibleImage, passwordVisibleDescription)
                }
            }
        }
    )
}
