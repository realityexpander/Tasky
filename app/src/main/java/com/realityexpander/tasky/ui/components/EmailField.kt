package com.realityexpander.tasky.ui.components

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
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import com.realityexpander.tasky.R
import com.realityexpander.tasky.common.UiText

@Composable
fun EmailField(
    value: String,
    label: String = UiText.Res(R.string.emailField_label).get(),
    placeholder: String = UiText.Res(R.string.emailField_placeholder).get(),
    isError: Boolean,
    onValueChange: (String) -> Unit,
    keyboardOptions: KeyboardOptions =
        KeyboardOptions(keyboardType = KeyboardType.Email, imeAction = ImeAction.Next),
    keyboardActions: KeyboardActions? = null,
) {
    val focusManager = LocalFocusManager.current
    val keyboardActionsLocal: KeyboardActions = keyboardActions ?: KeyboardActions(
        onNext = {
            focusManager.moveFocus(FocusDirection.Down)
        }
    )

    OutlinedTextField(
        value = value,
        singleLine = true,
        onValueChange = onValueChange,
        isError = isError,
        //label = { Text(text = label) },
        placeholder = { Text(text = placeholder) },
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActionsLocal,
        modifier = Modifier.fillMaxWidth(),
        leadingIcon = { Icon(imageVector = Icons.Filled.Email, "Email") },
        trailingIcon = {
            if(value.isNotBlank() && value.length >= 5) {
                val isEmailValid = !isError

                val image =
                    if (isEmailValid)
                        Icons.Filled.Check
                    else
                        Icons.Filled.Error

                // localized description for accessibility services
                val description =
                    if (isEmailValid)
                        UiText.Res(R.string.emailField_description_isValid).get()
                    else
                        UiText.Res(R.string.emailField_description_isInvalid).get()

                Icon(imageVector = image, description)
            }
        }
    )
}

















