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
import com.realityexpander.tasky.presentation.register_screen.RegisterEvent
import kotlinx.coroutines.launch

@Composable
fun EmailField(
    email: String,
    label: String = "Email",
    placeholder: String = "Enter your Email",
    isError: Boolean,
    onEmailChange: (String) -> Unit,
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
        value = email,
        singleLine = true,
        onValueChange = onEmailChange,
        isError = isError,
        label = { Text(text = label) },
        placeholder = { Text(text = placeholder) },
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActionsLocal,
        modifier = Modifier.fillMaxWidth()
    )
}



















