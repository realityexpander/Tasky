package com.realityexpander.tasky.presentation.register_screen

import android.content.res.Configuration
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.SavedStateHandle
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.navigation.EmptyDestinationsNavigator
import com.realityexpander.tasky.R
import com.realityexpander.tasky.common.UiText
import com.realityexpander.tasky.data.repository.AuthRepositoryFakeImpl
import com.realityexpander.tasky.data.repository.local.AuthDaoFakeImpl
import com.realityexpander.tasky.data.repository.remote.AuthApiFakeImpl
import com.realityexpander.tasky.domain.validation.ValidateEmailImpl
import com.realityexpander.tasky.domain.validation.ValidatePassword
import com.realityexpander.tasky.presentation.destinations.LoginScreenDestination
import com.realityexpander.tasky.ui.components.EmailField
import com.realityexpander.tasky.ui.components.NameField
import com.realityexpander.tasky.ui.components.PasswordField
import com.realityexpander.tasky.ui.theme.TaskyTheme
import com.realityexpander.tasky.ui.theme.modifiers.*

@Composable
@Destination
fun RegisterScreen(
    @Suppress("UNUSED_PARAMETER")  // extracted from navArgs in the viewModel
    email: String? = null,
    @Suppress("UNUSED_PARAMETER")  // extracted from navArgs in the viewModel
    password: String? = null,
    @Suppress("UNUSED_PARAMETER")  // extracted from navArgs in the viewModel
    confirmPassword: String? = null,
    navigator: DestinationsNavigator,
    viewModel: RegisterViewModel = hiltViewModel(),
) {

    val registerState by viewModel.registerState.collectAsState()
    val focusManager = LocalFocusManager.current

    fun performRegister() {
        viewModel.sendEvent(RegisterEvent.Register(
            registerState.email,
            registerState.password,
            registerState.confirmPassword
        ))

        focusManager.clearFocus()
    }

    fun navigateToLogin() {
        navigator.navigate(
            LoginScreenDestination(
                email = registerState.email,
                password = registerState.password,
                confirmPassword = registerState.confirmPassword
            )
        ) {
            popUpTo("RegisterScreen") {
                inclusive = true
            }
        }
    }

    BackHandler(true) {
        navigateToLogin()
    }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        if(registerState.isLoading) {
            Spacer(modifier = Modifier.height(8.dp))
            CircularProgressIndicator(
                modifier = Modifier.align(alignment = Alignment.Center)
            )
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = MaterialTheme.colors.onSurface)
    ) {
        Spacer(modifier = Modifier.mediumHeight())
        Text(
            text = UiText.Res(R.string.register_title).get(),
            style = MaterialTheme.typography.h5,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colors.surface,
            modifier = Modifier
                .align(alignment = Alignment.CenterHorizontally)
        )
        Spacer(modifier = Modifier.mediumHeight())

        Column(
            modifier = Modifier
                .taskyScreenTopCorners(color = MaterialTheme.colors.surface)
                .weight(1f)
        ) {
//            // • USERNAME
//            NameField(
//                value = registerState.username,
//                label = null,
//                isError = registerState.isInvalidUsername,
//                onValueChange = {
//                    viewModel.sendEvent(RegisterEvent.UpdateUsername(it))
//                }
//            )
//            if (registerState.isInvalidUsername && registerState.isShowInvalidUsernameMessage) {
//                Text(text = UiText.Res(R.string.error_invalid_username).get(), color = Color.Red)
//            }
//            Spacer(modifier = Modifier.smallHeight())

            // • EMAIL
            EmailField(
                value = registerState.email,
                label = null,
                isError = registerState.isInvalidEmail,
                onValueChange = {
                    viewModel.sendEvent(RegisterEvent.UpdateEmail(it))
                }
            )
            if (registerState.isInvalidEmail && registerState.isShowInvalidEmailMessage) {
                Text(text = UiText.Res(R.string.error_invalid_email).get(), color = Color.Red)
            }
            Spacer(modifier = Modifier.smallHeight())

            // • PASSWORD
            PasswordField(
                value = registerState.password,
                label = null,
                isError = registerState.isInvalidPassword,
                onValueChange = {
                    viewModel.sendEvent(RegisterEvent.UpdatePassword(it))
                },
                isPasswordVisible = registerState.isPasswordVisible,
                clickTogglePasswordVisibility = {
                    viewModel.sendEvent(RegisterEvent.TogglePasswordVisibility(registerState.isPasswordVisible))
                },
                imeAction = ImeAction.Next,
            )
            if (registerState.isInvalidPassword && registerState.isShowInvalidPasswordMessage) {
                Text(text = UiText.Res(R.string.error_invalid_password).get(), color = Color.Red)
            }
            Spacer(modifier = Modifier.smallHeight())

            // • CONFIRM PASSWORD
            PasswordField(
                label = null, //UiText.Res(R.string.register_label_confirm_password).get(),
                placeholder = UiText.Res(R.string.register_placeholder_confirm_password).get(),
                value = registerState.confirmPassword,
                isError = registerState.isInvalidConfirmPassword,
                onValueChange = {
                    viewModel.sendEvent(RegisterEvent.UpdateConfirmPassword(it))
                },
                isPasswordVisible = registerState.isPasswordVisible,
                clickTogglePasswordVisibility = {
                    viewModel.sendEvent(RegisterEvent.TogglePasswordVisibility(registerState.isPasswordVisible))
                },
                imeAction = ImeAction.Done,
                doneAction = {
                    performRegister()
                },
            )
            if (registerState.isInvalidConfirmPassword && registerState.isShowInvalidConfirmPasswordMessage) {
                Text(
                    text = UiText.Res(R.string.error_invalid_confirm_password).get(),
                    color = Color.Red
                )
                Spacer(modifier = Modifier.extraSmallHeight())
            }
            // • SHOW IF MATCHING PASSWORDS
            if (!registerState.isPasswordsMatch) {
                Text(
                    text = UiText.Res(R.string.register_error_passwords_do_not_match).get(),
                    color = Color.Red
                )
                Spacer(modifier = Modifier.extraSmallHeight())
            }
            // • SHOW PASSWORD REQUIREMENTS
            if (registerState.isShowInvalidPasswordMessage || registerState.isShowInvalidConfirmPasswordMessage) {
                Text(
                    text = UiText.Res(R.string.register_password_requirements).get(),
                    color = Color.Red
                )
                Spacer(modifier = Modifier.extraSmallHeight())
            }
            Spacer(modifier = Modifier.mediumHeight())

            // • REGISTER BUTTON
            Button(
                onClick = {
                    performRegister()
                },
                enabled = !registerState.isLoading,
                modifier = Modifier
                    .taskyWideButton(color = MaterialTheme.colors.primary)
                    .align(alignment = Alignment.CenterHorizontally)
            ) {
                Text(
                    text = UiText.Res(R.string.register_button).get(),
                    fontSize = MaterialTheme.typography.button.fontSize,
                )
                if (registerState.isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .padding(start = DP.small)
                            .size(16.dp)
                            .align(alignment = Alignment.CenterVertically)
                    )
                }
            }
            Spacer(modifier = Modifier.mediumHeight())

            // STATUS //////////////////////////////////////////

            registerState.errorMessage.getOrNull()?.let { errorMessage ->
                Text(
                    text = "Error: $errorMessage",
                    color = Color.Red,
                )
                Spacer(modifier = Modifier.extraSmallHeight())
            }
            if (registerState.isLoggedIn) {
                Text(text = UiText.Res(R.string.register_registered).get())
                Spacer(modifier = Modifier.extraSmallHeight())
            }
            registerState.statusMessage.asStrOrNull()?.let { message ->
                Text(text = message)
                Spacer(modifier = Modifier.extraSmallHeight())
            }
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(color = MaterialTheme.colors.surface)
        ) {
            // • BACK TO SIGN IN BUTTON
//            Text(
//                text = UiText.Res(R.string.register_already_a_member_sign_in).get(),
//                style = MaterialTheme.typography.body2,
//                color = MaterialTheme.colors.primaryVariant,
//                modifier = Modifier
//                    .align(alignment = Alignment.CenterHorizontally)
//                    .clickable(onClick = {
//                        navigateToLogin()
//                    })
//            )
            Button(
                onClick = {
                    navigateToLogin()
                },
                modifier = Modifier
                    .align(alignment = Alignment.Start)
                    .padding(start = DP.small)
            ) {
//                Text(text = UiText.Str("<").get())
                Icon(
                    imageVector = Icons.Filled.ChevronLeft,
                    contentDescription = UiText.Res(R.string.register_description_back).get(),
                    modifier = Modifier
                        .size(DP.small)
                        .align(alignment = Alignment.CenterVertically)
                )
            }
            Spacer(modifier = Modifier.extraLargeHeight())
        }

    }
}


@Composable
@Preview(
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
)
fun RegisterScreenPreview() {
    TaskyTheme {
        Surface {
            RegisterScreen(
                navigator = EmptyDestinationsNavigator,
                viewModel = RegisterViewModel(
                    authRepository = AuthRepositoryFakeImpl(
                        authApi = AuthApiFakeImpl(),
                        authDao = AuthDaoFakeImpl(),
                        validateEmail = ValidateEmailImpl(),
                    ),
                    validateEmail = ValidateEmailImpl(),
                    validatePassword = ValidatePassword(),
                    savedStateHandle = SavedStateHandle().apply {
                        // For Live Preview
                        set("email", "chris@demo.com")
                        set("password", "123456Aa")
                        set("confirmPassword", "123456Aa")
                    }
                )
            )
        }
    }
}

@Composable
@Preview(
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_NO,
)
fun RegisterScreenPreview_NightMode() {
    RegisterScreenPreview()
}















