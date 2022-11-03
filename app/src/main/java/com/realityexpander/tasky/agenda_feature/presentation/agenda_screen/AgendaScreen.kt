package com.realityexpander.tasky.agenda_feature.presentation.agenda_screen

import android.content.res.Configuration
import android.graphics.Paint
import android.view.ViewTreeObserver
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.DropdownMenu
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Logout
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.toSize
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.hilt.navigation.compose.hiltViewModel
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.navigation.EmptyDestinationsNavigator
import com.realityexpander.tasky.MainActivity
import com.realityexpander.tasky.agenda_feature.presentation.common.MenuItem
import com.realityexpander.tasky.agenda_feature.presentation.common.UserAcronymCircle
import com.realityexpander.tasky.auth_feature.domain.AuthInfo
import com.realityexpander.tasky.core.presentation.common.modifiers.*
import com.realityexpander.tasky.core.presentation.theme.DaySelected
import com.realityexpander.tasky.core.presentation.theme.TaskyTheme
import com.realityexpander.tasky.destinations.LoginScreenDestination
import java.time.LocalDate

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

    var logoutButtonSize by remember { mutableStateOf(Size.Zero)}
    var logoutButtonOffset by remember { mutableStateOf(Offset.Zero)}

    fun navigateToLogin() {
        navigator.navigate(
            LoginScreenDestination(
                username = state.username,
                email = state.email,
                password = null,
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
                    text = "AUGUST", //stringResource(R.string.agenda_title),
                    style = MaterialTheme.typography.h4,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colors.surface,
                    modifier = Modifier
                        .wrapContentWidth(Alignment.Start)
                        .alignByBaseline()
                        .align(Alignment.CenterVertically)
                        .clickable {
                            // show date picker
                        }
                )
                Icon(
                    imageVector = Icons.Filled.ArrowDropDown,
                    tint = MaterialTheme.colors.surface,
                    contentDescription = "Logout dropdown",
                    modifier = Modifier
                        .align(Alignment.CenterVertically)
                )
            }
            UserAcronymCircle(
                username = state.authInfo?.username,
                modifier = Modifier
                    .alignByBaseline()
                    .align(Alignment.CenterVertically)
                    .weight(1f)
                    .wrapContentWidth(Alignment.End)
                    .clickable {
                        onAction(AgendaEvent.ToggleLogoutDropdown)
                    }
                    .onGloballyPositioned { coordinates ->
                        logoutButtonSize = coordinates.size.toSize()
                        logoutButtonOffset = coordinates.localToRoot(
                            Offset.Zero
                    )}
            )

        }
        Spacer(modifier = Modifier.smallHeight())

        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .taskyScreenTopCorners(color = MaterialTheme.colors.surface)
                .weight(1f)
        ) col2@ {
            Spacer(modifier = Modifier.tinyHeight())

            var selectedDay by remember { mutableStateOf(0) }

            Row(
            ) {
                // show days of the week
                val daysOfWeek = listOf("S", "M", "T", "W", "T", "F", "S")
                daysOfWeek.forEachIndexed { i, day ->
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .wrapContentWidth(Alignment.CenterHorizontally)
                            .drawBehind {
                                if(selectedDay == i) {
                                    val paint = Paint().apply {
                                        color = DaySelected.toArgb()
                                        strokeWidth = 1f
                                        style = Paint.Style.STROKE
                                    }
                                    drawRoundRect(
                                        color = DaySelected,
                                        topLeft = Offset(0f, -25f),
                                        size = Size(size.width, size.height + 50f),
                                        cornerRadius = CornerRadius(32.dp.toPx(), 32.dp.toPx())
                                    )
                                }
                            }
                            .clickable {
                                //onAction(AgendaEvent.SetSelectedDay(i))
                                selectedDay = i
                            }
                    ) {
                        Text(
                            text = day,
                            style = MaterialTheme.typography.subtitle2,
                            fontWeight = if (selectedDay == i)
                                    FontWeight.Bold
                                else
                                    FontWeight.SemiBold,
                            color = if (selectedDay == i)
                                    MaterialTheme.colors.onSurface
                                else
                                    MaterialTheme.colors.onSurface.copy(alpha = 0.3f),
                            modifier = Modifier
                                .align(Alignment.CenterHorizontally)
                                .wrapContentWidth(Alignment.CenterHorizontally)
                                .padding(horizontal = DP.small)
                        )
                        Spacer(modifier = Modifier.tinyHeight())
                        Text(
                            text = (i+1).toString(),
                            style = MaterialTheme.typography.h3,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colors.onSurface,
                            modifier = Modifier
                                .align(Alignment.CenterHorizontally)
                                .wrapContentWidth(Alignment.CenterHorizontally)
                                .padding(horizontal = DP.small)
                        )

                    }
                }
            }

            Spacer(modifier = Modifier.smallHeight())

            // show today
            val today = LocalDate.now()
            val todayDayOfWeek = today.dayOfWeek.value
            val todayDayOfMonth = today.dayOfMonth
            val todayMonth = today.month
            val todayYear = today.year
            val todayDate = LocalDate.of(todayYear, todayMonth, todayDayOfMonth)
            //val todayTasks = state.tasks.filter { it.date == todayDate }
            //val todayTasksCount = todayTasks.size

            Text(
                text = "Today",
                style = MaterialTheme.typography.h3,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colors.onSurface,
                modifier = Modifier
                    .padding(start = DP.small, end = DP.small)
            )

            ////// STATUS ///////
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

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
    ) {
        // • Logout user dropdown
        DropdownMenu(
            expanded = state.isLogoutDropdownShowing,
            onDismissRequest = { onAction(AgendaEvent.ToggleLogoutDropdown) },
            offset = DpOffset(
                x = logoutButtonOffset.x.dp - logoutButtonSize.width.dp * 3f,
                y = 0.dp
            ),
            modifier = Modifier
                .width(with(LocalDensity.current) {
                    (logoutButtonSize.width * 6f).toDp()
                })
                .background(color = MaterialTheme.colors.onSurface)
        ) {
            MenuItem(
                title = "Logout",
                icon = Icons.Filled.Logout,
                onClick = {
                    onAction(AgendaEvent.ToggleLogoutDropdown)
                    onAction(AgendaEvent.Logout)
                },
            )
        }
    }
}


@Composable
@Preview(
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    showSystemUi = false,
    name = "Agenda Screen Dark",
    apiLevel = 28,
    widthDp = 350,
    group= "Night Mode=true"
)
fun AgendaScreenPreview() {
    TaskyTheme {
        AgendaScreenContent(
            state = AgendaState(
                authInfo = AuthInfo(
                    username = "Chris Athanas",
                ),
                isLoaded = true,
                //isLogoutDropdownShowing = true,
            ),
            onAction = {},
            navigator = EmptyDestinationsNavigator,
        )
    }
}

@Composable
@Preview(
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_NO,
    group="Night Mode=false",
    apiLevel = 28,
    widthDp = 350,
)
fun AgendaScreenPreview_NightMode_NO() {
    AgendaScreenPreview()
}














