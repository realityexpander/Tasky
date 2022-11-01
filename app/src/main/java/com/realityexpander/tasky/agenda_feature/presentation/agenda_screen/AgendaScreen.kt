package com.realityexpander.tasky.agenda_feature.presentation.agenda_screen

import android.content.res.Configuration
import android.view.ViewTreeObserver
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.hilt.navigation.compose.hiltViewModel
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.navigation.EmptyDestinationsNavigator
import com.realityexpander.tasky.MainActivity
import com.realityexpander.tasky.auth_feature.domain.AuthInfo
import com.realityexpander.tasky.core.presentation.common.modifiers.DP
import com.realityexpander.tasky.core.presentation.common.modifiers.extraSmallHeight
import com.realityexpander.tasky.core.presentation.common.modifiers.mediumHeight
import com.realityexpander.tasky.core.presentation.common.modifiers.taskyScreenTopCorners
import com.realityexpander.tasky.core.presentation.theme.TaskyTheme
import com.realityexpander.tasky.destinations.LoginScreenDestination

@Composable
@Destination
fun AgendaScreen(
//    @Suppress("UNUSED_PARAMETER")  // extracted from navArgs in the viewModel
//    username: String? = null,
//    @Suppress("UNUSED_PARAMETER")  // extracted from navArgs in the viewModel
//    email: String? = null,
//    @Suppress("UNUSED_PARAMETER")  // extracted from navArgs in the viewModel
//    password: String? = null,
    navigator: DestinationsNavigator,
    viewModel: AgendaViewModel = hiltViewModel(),
) {

    val agendaState by viewModel.agendaState.collectAsState()

    AgendaScreenContent(
        state = agendaState,
        onAction = viewModel::sendEvent,
        navigator = navigator,
    )
}

@Composable
fun AgendaScreenContent(
    state: AgendaState,
    onAction: (AgendaEvent) -> Unit,
    navigator: DestinationsNavigator,
) {

    val focusManager = LocalFocusManager.current
    val context = LocalContext.current

    fun navigateToLogin() {
        navigator.navigate(
            LoginScreenDestination(
                username = state.username,
                email = state.email,
            )
        ) {
            popUpTo(LoginScreenDestination.route) {
                inclusive = true
            }
            launchSingleTop = true
            restoreState = true
        }
    }

    // Guard against invalid authentication state OR perform logout
    SideEffect {
        if (state.isLoaded && state.authInfo == null) {
            onAction(AgendaEvent.SetIsLoaded(false))
            navigateToLogin()
        }
    }

    BackHandler(true) {
        // todo: should we ask the user to quit?
        (context as MainActivity).exitApp()
    }

    // Check keyboard open/closed (how to make this a function?)
    val view = LocalView.current
    var isKeyboardOpen by remember { mutableStateOf(false) }
    DisposableEffect(view) {
        val listener = ViewTreeObserver.OnGlobalLayoutListener {
            isKeyboardOpen = ViewCompat.getRootWindowInsets(view)
                ?.isVisible(WindowInsetsCompat.Type.ime()) ?: true
        }
        view.viewTreeObserver.addOnGlobalLayoutListener(listener)

        onDispose {
            view.viewTreeObserver.removeOnGlobalLayoutListener(listener)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = MaterialTheme.colors.onSurface)
    ) col1@ {
        Spacer(modifier = Modifier.mediumHeight())
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = DP.small, end = DP.small)
        ) {
            Row(
                modifier = Modifier
                    .alignByBaseline()
            ) {
                Text(
                    text = "MARCH", //stringResource(R.string.agenda_title),
                    style = MaterialTheme.typography.h3,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colors.surface,
                    modifier = Modifier
                        .wrapContentWidth(Alignment.Start)
                        .alignByBaseline()
                        .align(Alignment.CenterVertically)
                        .clickable {
                            onAction(AgendaEvent.ToggleLogoutDropdown)
                        }
                )
                Icon(
                    imageVector = if (state.isLogoutDropdownShowing)
                        Icons.Filled.KeyboardArrowUp
                    else
                        Icons.Filled.KeyboardArrowDown,
                    tint = MaterialTheme.colors.surface,
                    contentDescription = "Logout dropdown",
                    modifier = Modifier
                        .align(Alignment.CenterVertically)
                        .clickable {
                            onAction(AgendaEvent.ToggleLogoutDropdown)
                        }
                )
            }
            Text(
                text = "CA", //stringResource(R.string.agenda_title),
                style = MaterialTheme.typography.h4,
                textAlign = TextAlign.End,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colors.onSurface,
                modifier = Modifier
                    .alignByBaseline()
                    .align(Alignment.CenterVertically)
                    .weight(1f)
                    .wrapContentWidth(Alignment.End)
                    .padding(DP.tiny)
                    .drawBehind {
                        drawCircle(
                            color = Color.Black,
                            radius = this.size.maxDimension*.7f
                        )
                    }
            )

        }
        Spacer(modifier = Modifier.extraSmallHeight())

        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .taskyScreenTopCorners(color = MaterialTheme.colors.surface)
                .weight(1f)
        ) col2@ {
            Spacer(modifier = Modifier.mediumHeight())

            Text(
                "Hello, " + (state.authInfo?.username ?: "No username"),
                color = MaterialTheme.colors.onSurface
            )
//            // STATUS //////////////////////////////////////////
//
//            state.errorMessage.getOrNull?.let { errorMessage ->
//                Spacer(modifier = Modifier.smallHeight())
//                Text(
//                    text = "Error: $errorMessage",
//                    color = Color.Red,
//                )
//                Spacer(modifier = Modifier.extraSmallHeight())
//            }
//            state.statusMessage.getOrNull?.let { message ->
//                Spacer(modifier = Modifier.extraSmallHeight())
//                Text(text = message)
//                Spacer(modifier = Modifier.extraSmallHeight())
//            }
//
//            Box(
//                modifier = Modifier
//                    .fillMaxSize()
//                    .weight(1f)
//            ) {
//                this@col1.AnimatedVisibility(
//                    visible = !isKeyboardOpen,
//                    enter = fadeIn() + slideInVertically(
//                        initialOffsetY = { it }
//                    ),
//                    exit = fadeOut(),
//                    modifier = Modifier
//                        .background(color = MaterialTheme.colors.surface)
//                        .align(alignment = Alignment.BottomStart)
//                ) {
//                    // • BACK TO SIGN IN BUTTON
//                    Button(
//                        onClick = {
//                            navigateToLogin()
//                        },
//                        modifier = Modifier
//                            .align(alignment = Alignment.BottomStart)
//                            .taskyMediumButton(color = MaterialTheme.colors.primary)
//                    ) {
//                        Icon(
//                            imageVector = Icons.Filled.ChevronLeft,
//                            contentDescription = stringResource(R.string.register_description_back),
//                            modifier = Modifier
//                                .size(DP.large)
//                                .align(alignment = Alignment.CenterVertically)
//                        )
//                    }
//                }
//            }
        }
    }
}

fun getUserAcronym(username: String): String {
    if(username.isBlank()) return ""

    val words = username.split(" ")
    if (words.size > 1) {
        return words[0].substring(0, 1) + words[1].substring(0, 1)
    }

    return username.substring(0, 2)
}


//    // • BACK TO SIGN IN BUTTON (alternate design)
//    Text(
//        text = stringResource(R.string.register_already_a_member_sign_in),
//        style = MaterialTheme.typography.body2,
//        color = MaterialTheme.colors.primaryVariant,
//        modifier = Modifier
//            .align(alignment = Alignment.CenterHorizontally)
//            .clickable(onClick = {
//                navigateToLogin()
//            })
//    )

@Composable
@Preview(
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    showSystemUi = false,
    name = "Agenda Screen Dark",
    apiLevel = 28
)
fun AgendaScreenPreview() {
    TaskyTheme {
        AgendaScreenContent(
            state = AgendaState(
                authInfo = AuthInfo(
                    username = "username",
                ),
                isLoaded = true,
            ),
            onAction = {},
            navigator = EmptyDestinationsNavigator,
        )
    }
}

//@Composable
//@Preview(
//    showBackground = true,
//    uiMode = Configuration.UI_MODE_NIGHT_YES,
//    group= "Night Mode=true"
//)
//fun RegisterScreenPreview() {
//    TaskyTheme {
//        Surface {
//            RegisterScreenContent(
//                navigator = EmptyDestinationsNavigator,
//                state = RegisterState(
//                    email = "chris@demo.com",
//                    password = "1234567Az",
//                    confirmPassword = "1234567Az",
//                    isInvalidEmail = false,
//                    isInvalidPassword = false,
//                    isInvalidConfirmPassword = false,
//                    isPasswordsMatch = true,
//                    isShowInvalidEmailMessage = false,
//                    isShowInvalidPasswordMessage = false,
//                    isShowInvalidConfirmPasswordMessage = false,
//                    isPasswordVisible = false,
//                    isLoading = false,
//                    isLoggedIn = false,
//                    errorMessage = UiText.None,  // UiText.Res(R.string.error_invalid_email),
//                    statusMessage = UiText.None, // UiText.Res(R.string.login_logged_in),
//                ),
//                onAction = {},
//            )
//        }
//    }
//}
//
//@Composable
//@Preview(
//    showBackground = true,
//    uiMode = Configuration.UI_MODE_NIGHT_NO,
//    group="Night Mode=false"
//)
//fun RegisterScreenPreview_NightMode_NO() {
//    RegisterScreenPreview()
//}
//
//@Composable
//@Preview(
//    showBackground = true,
//    uiMode = Configuration.UI_MODE_NIGHT_YES,
//    group = "ViewModel"
//)
//fun RegisterScreenPreview_Interactive() {
//    TaskyTheme {
//        Surface {
//            RegisterScreen(
//                email = "hello",
//                navigator = EmptyDestinationsNavigator,
//                viewModel = RegisterViewModel(
//                    authRepository = AuthRepositoryFakeImpl(
//                        authApi = AuthApiFakeImpl(),
//                        authDao = AuthDaoFakeImpl(),
//                        validateUsername = ValidateUsername(),
//                        validateEmail = ValidateEmailRegexImpl(),
//                        validatePassword = ValidatePassword(),
//                    ),
//                    savedStateHandle = SavedStateHandle().apply {
//                        // For Live Preview / interactive mode
//                        set("username", "c")
//                        set("email", "chris@demo.com")
//                        set("password", "123456Aa")
//                        set("confirmPassword", "123456Aa")
//                        set("invalidUsername", true)
//                        set("invalidEmail", true)
////                        set("invalidPassword", true)
////                        set("invalidConfirmPassword", true)
////                        set("isPasswordsMatch", false)
//                        set("isShowInvalidUsernameMessage", true)
//                        set("isShowInvalidEmailMessage", true)
////                        set("isShowInvalidPasswordMessage", true)
////                        set("isShowInvalidConfirmPasswordMessage", true)
////                        set("isLoading", true)
////                        set("errorMessage", "Error Message")
////                        set("statusMessage", "Status Message")
////                        set("isLoggedIn", true)
//                    }
//                )
//            )
//        }
//    }
//}















