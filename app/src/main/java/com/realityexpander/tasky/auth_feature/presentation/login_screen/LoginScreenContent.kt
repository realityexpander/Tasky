package com.realityexpander.tasky.auth_feature.presentation.login_screen

import android.content.res.Configuration
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.datastore.core.DataStore
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.navigation.EmptyDestinationsNavigator
import com.realityexpander.tasky.MainActivity
import com.realityexpander.tasky.R
import com.realityexpander.tasky.auth_feature.domain.AuthInfo
import com.realityexpander.tasky.auth_feature.presentation.components.EmailField
import com.realityexpander.tasky.auth_feature.presentation.components.PasswordField
import com.realityexpander.tasky.core.data.settings.AppSettings
import com.realityexpander.tasky.core.domain.IAppSettingsRepository
import com.realityexpander.tasky.core.presentation.common.modifiers.DP
import com.realityexpander.tasky.core.presentation.common.modifiers.extraSmallHeight
import com.realityexpander.tasky.core.presentation.common.modifiers.largeHeight
import com.realityexpander.tasky.core.presentation.common.modifiers.mediumHeight
import com.realityexpander.tasky.core.presentation.common.modifiers.smallHeight
import com.realityexpander.tasky.core.presentation.common.modifiers.taskyScreenTopCorners
import com.realityexpander.tasky.core.presentation.common.modifiers.taskyWideButton
import com.realityexpander.tasky.core.presentation.theme.TaskyTheme
import com.realityexpander.tasky.core.presentation.util.keyboardVisibilityObserver
import com.realityexpander.tasky.destinations.AgendaScreenDestination
import com.realityexpander.tasky.destinations.RegisterScreenDestination
import kotlinx.coroutines.launch

@Composable
fun LoginScreenContent(
    username: String? = null,
    confirmPassword: String? = null,
    state: LoginState,
    onAction: (LoginEvent) -> Unit,
    navigator: DestinationsNavigator,
    appSettingsRepository: IAppSettingsRepository
) {
    val focusManager = LocalFocusManager.current
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val isKeyboardOpen by keyboardVisibilityObserver()

    fun performLogin() {
        onAction(LoginEvent.Login(
            email = state.email,
            password = state.password,
        ))
        focusManager.clearFocus()
    }

    // When authInfo is not null, we are Logged in -> navigate to AgendaScreen
    state.authInfo?.let { authInfo ->
        scope.launch {

            // Save the AuthInfo in the dataStore
            appSettingsRepository.saveAuthInfo(authInfo)

            navigator.navigate(
                AgendaScreenDestination(
                    // authInfo = authInfo
                    //username = authInfo.username,
                    //selectedDayIndex = 0
                )
            ) {
                launchSingleTop = true
                restoreState = true
            }
        }
    }

    fun navigateToRegister() {
        navigator.navigate(
            RegisterScreenDestination(
                username = username,
                email = state.email,
                password = state.password,
                confirmPassword = confirmPassword
            )
        ) {
            launchSingleTop = true
            restoreState = true
        }
    }

    BackHandler(true) {
        // todo: should we ask the user to quit?
        (context as MainActivity).exitApp()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = MaterialTheme.colors.onSurface)
    ) col1@{
        Spacer(modifier = Modifier.largeHeight())
        Text(
            text = stringResource(R.string.login_title),
            style = MaterialTheme.typography.h2,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colors.surface,
            modifier = Modifier
                .align(alignment = Alignment.CenterHorizontally)
        )
        Spacer(modifier = Modifier.mediumHeight())

        Column(
            modifier = Modifier
                .taskyScreenTopCorners(color = MaterialTheme.colors.surface)
                .verticalScroll(rememberScrollState())
                .weight(1f)
        ) {

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = DP.small, end = DP.small)
            ) colInnerScroll@{

                Spacer(modifier = Modifier.mediumHeight())

                // • EMAIL
                EmailField(
                    value = state.email,
                    label = null,
                    isError = state.isInvalidEmail,
                    onValueChange = {
                        onAction(LoginEvent.UpdateEmail(it))
                    }
                )
                AnimatedVisibility(state.isInvalidEmail && state.isInvalidEmailMessageVisible) {
                    Text(text = stringResource(R.string.error_invalid_email), color = Color.Red)
                }
                Spacer(modifier = Modifier.smallHeight())

                // • PASSWORD
                PasswordField(
                    value = state.password,
                    label = null,
                    isError = state.isInvalidPassword,
                    onValueChange = {
                        onAction(LoginEvent.UpdatePassword(it))
                    },
                    isPasswordVisible = state.isPasswordVisible,
                    clickTogglePasswordVisibility = {
                        onAction(
                            LoginEvent.SetIsPasswordVisible(!state.isPasswordVisible)
                        )
                    },
                    imeAction = ImeAction.Done,
                    doneAction = {
                        performLogin()
                    }
                )
                AnimatedVisibility(state.isInvalidPassword && state.isInvalidPasswordMessageVisible) {
                    Text(text = stringResource(R.string.error_invalid_password), color = Color.Red)
                }
                Spacer(modifier = Modifier.mediumHeight())

                // • LOGIN BUTTON
                Button(
                    onClick = {
                        performLogin()
                    },
                    modifier = Modifier
                        .taskyWideButton(color = MaterialTheme.colors.primary)
                        .align(alignment = Alignment.CenterHorizontally),
                    enabled = !state.isLoading,
                ) {
                    Text(
                        text = stringResource(R.string.login_button),
                        fontSize = MaterialTheme.typography.button.fontSize,
                    )
                    if (state.isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier
                                .padding(start = DP.small)
                                .size(DP.small)
                                .align(alignment = CenterVertically)
                        )
                    }
                }
                Spacer(modifier = Modifier.mediumHeight())

                // STATUS //////////////////////////////////////////

                AnimatedVisibility(state.errorMessage != null) {
                    state.errorMessage?.getOrNull?.let { errorMessage ->
                        Spacer(modifier = Modifier.extraSmallHeight())
                        Text(
                            text = errorMessage,
                            color = Color.Red,
                            modifier = Modifier
                                .animateContentSize()
                        )
                        Spacer(modifier = Modifier.extraSmallHeight())
                    }
                }
                AnimatedVisibility(state.statusMessage != null) {
                    state.statusMessage?.getOrNull?.let { message ->
                        Spacer(modifier = Modifier.extraSmallHeight())
                        Text(text = message)
                        Spacer(modifier = Modifier.extraSmallHeight())
                    }
                }

            }

            // • GO TO REGISTER BUTTON
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f)
                    .background(color = MaterialTheme.colors.surface)
                    .padding(bottom = DP.large)
            ) {
                this@col1.AnimatedVisibility(
                    visible = !isKeyboardOpen,
                    enter = fadeIn() + slideInVertically(
                        initialOffsetY = { it }
                    ),
                    exit = fadeOut(),
                    modifier = Modifier
                        .background(color = MaterialTheme.colors.surface)
                        .align(alignment = Alignment.BottomCenter)
                ) {
                    // • GO TO REGISTER TEXT BUTTON
                    Text(
                        text = stringResource(R.string.login_not_a_member_sign_up),
                        style = MaterialTheme.typography.body1,
                        color = MaterialTheme.colors.primaryVariant,
                        modifier = Modifier
                            .align(alignment = Alignment.BottomCenter)
                            .clickable(onClick = {
                                navigateToRegister()
                            })
                    )
                }
            }
        }
    }
}

@Preview(
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_NO,
    group = "Night mode=false",
    heightDp = 500
)
@Composable
fun LoginScreenPreview_LightMode() {
    TaskyTheme {
        LoginScreenPreviewContent()
    }
}

@Preview(
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    group = "Night mode=true",
    heightDp = 500
)
@Composable
fun LoginScreenPreview_NightMode() {
    TaskyTheme {
        LoginScreenPreviewContent()
    }
}

@Composable
fun LoginScreenPreviewContent() {
    LoginScreenContent(
        username = "Chris Athanas",
        confirmPassword = "Password1",
        state = LoginState(
            email = "chris@demo.com",
            password = "Password1",
            isLoading = false,
            isInvalidEmail = false,
            isInvalidPassword = false,
            isPasswordVisible = false,
            isInvalidEmailMessageVisible = false,
            isInvalidPasswordMessageVisible = false,
            errorMessage = null,
            statusMessage = null,
            authInfo = null,
        ),
        onAction = {},
        navigator = EmptyDestinationsNavigator,
//        object : DestinationsNavigator {
//            override fun popBackStack(
//                route: String,
//                inclusive: Boolean,
//                saveState: Boolean
//            ): Boolean {
//                TODO("Not yet implemented")
//            }
//            override fun clearBackStack(route: String): Boolean {
//                TODO("Not yet implemented")
//            }
//            override fun navigate(
//                route: String,
//                onlyIfResumed: Boolean,
//                navOptions: NavOptions?,
//                navigatorExtras: Navigator.Extras?
//            ) {
//                TODO("Not yet implemented")
//            }
//            override fun navigate(
//                route: String,
//                onlyIfResumed: Boolean,
//                builder: NavOptionsBuilder.() -> Unit
//            ) {
//                TODO("Not yet implemented")
//            }
//            override fun navigateUp(): Boolean = false
//            override fun popBackStack(): Boolean {
//                TODO("Not yet implemented")
//            }
//        },
        appSettingsRepository = object : IAppSettingsRepository {
            override val dataStore: DataStore<AppSettings>
                get() = TODO("Not yet implemented")

            override suspend fun saveAuthInfo(authInfo: AuthInfo) {
                TODO("Not yet implemented")
            }

            override suspend fun getAppSettings(): AppSettings {
                TODO("Not yet implemented")
            }

            override suspend fun saveIsSettingsInitialized(firstTime: Boolean) {
                TODO("Not yet implemented")
            }
        }
    )
}

//@Composable
//@Destination
//@RootNavGraph(start = true) // for some reason this line disables the Compose Previews... comment to see previews
//fun LoginScreen(
//    username: String? = "Chris Athanas",
//    @Suppress("UNUSED_PARAMETER")  // extracted from navArgs in the viewModel
//    email: String? = "chris3@demo.com",
//    @Suppress("UNUSED_PARAMETER")  // extracted from navArgs in the viewModel
//    password: String? = "Password1",
//    confirmPassword: String? = "Password1",
//    navigator: DestinationsNavigator,
//    viewModel: LoginViewModel = hiltViewModel(),
//) {
//    val loginState by viewModel.loginState.collectAsState()
//    val connectivityState by viewModel.onlineState.collectAsState(
//        initial = IInternetConnectivityObserver.OnlineStatus.OFFLINE // must start as Offline
//    )
//    val appSettingsRepository = viewModel.appSettingsRepository
//
//    LoginScreenContent(
//        username = username,                // passed to/from RegisterScreen (not used in LoginScreen)
//        confirmPassword = confirmPassword,  // passed to/from RegisterScreen (not used in LoginScreen)
//        state = loginState,
//        onAction = viewModel::sendEvent,
//        navigator = navigator,
//        appSettingsRepository = appSettingsRepository,
//    )
//
//    InternetAvailabilityIndicator(connectivityState)
//}
//
//
