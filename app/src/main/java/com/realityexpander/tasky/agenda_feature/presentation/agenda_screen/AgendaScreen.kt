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
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
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
import com.realityexpander.tasky.agenda_feature.domain.AgendaItem
import com.realityexpander.tasky.agenda_feature.presentation.common.MenuItem
import com.realityexpander.tasky.agenda_feature.presentation.common.UserAcronymCircle
import com.realityexpander.tasky.agenda_feature.presentation.components.AgendaCard
import com.realityexpander.tasky.auth_feature.domain.AuthInfo
import com.realityexpander.tasky.core.presentation.common.modifiers.*
import com.realityexpander.tasky.core.presentation.theme.DaySelected
import com.realityexpander.tasky.core.presentation.theme.TaskyTheme
import com.realityexpander.tasky.core.util.UuidStr
import com.realityexpander.tasky.destinations.LoginScreenDestination
import java.time.LocalDate
import java.time.LocalDateTime

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

data class MenuItemInfo(
    var menuPosition: Offset = Offset.Zero,
    //var menuKind: Class<out AgendaItem>? = null,
    var agendaItem: AgendaItem? = null,
)

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


    // Dummy data for now
    val today = LocalDate.now()
    val todayDayOfWeek = today.dayOfWeek.value
    val todayDayOfMonth = today.dayOfMonth
    val todayMonth = today.month
    val todayYear = today.year
    val todayDate = LocalDate.of(todayYear, todayMonth, todayDayOfMonth)
    val agendaItems = remember { mutableStateListOf(
        AgendaItem.Event(
            id = "0001",
            title = "Meeting with John",
            from = LocalDateTime.of(todayYear, todayMonth, todayDayOfMonth, 10, 0),
            to = LocalDateTime.of(todayYear, todayMonth, todayDayOfMonth, 11, 0),
            remindAt = LocalDateTime.of(todayYear, todayMonth, todayDayOfMonth, 9, 0),
            description = "Discuss the new project"
        ),
        AgendaItem.Task(
            id = "0002",
            title = "Task with Jim",
            time = LocalDateTime.of(todayYear, todayMonth, todayDayOfMonth, 13, 0),
            remindAt = LocalDateTime.of(todayYear, todayMonth, todayDayOfMonth, 12, 30),
            description = "Do the old project"
        ),
        AgendaItem.Event(
            id = "0003",
            title = "Meeting with Jane",
            from = LocalDateTime.of(todayYear, todayMonth, todayDayOfMonth, 15, 0),
            to = LocalDateTime.of(todayYear, todayMonth, todayDayOfMonth, 16, 0),
            remindAt = LocalDateTime.of(todayYear, todayMonth, todayDayOfMonth, 14, 30),
            description = "Discuss the a different project"
        ),
        AgendaItem.Task(
            id = "0004",
            title = "Task with Joe",
            time = LocalDateTime.of(todayYear, todayMonth, todayDayOfMonth, 18, 0),
            remindAt = LocalDateTime.of(todayYear, todayMonth, todayDayOfMonth, 16, 30),
            description = "Do the the other project",
            isDone = true
        ),
        AgendaItem.Event(
            id = "0005",
            title = "Meeting with Jack",
            from = LocalDateTime.of(todayYear, todayMonth, todayDayOfMonth, 19, 0),
            to = LocalDateTime.of(todayYear, todayMonth, todayDayOfMonth, 20, 0),
            remindAt = LocalDateTime.of(todayYear, todayMonth, todayDayOfMonth, 18, 30),
            description = "Discuss the yet another project"
        ),
        AgendaItem.Task(
            id = "0006",
            title = "Task with Jill",
            time = LocalDateTime.of(todayYear, todayMonth, todayDayOfMonth, 22, 0),
            remindAt = LocalDateTime.of(todayYear, todayMonth, todayDayOfMonth, 20, 30),
            description = "Do the similar project"
        ),
        AgendaItem.Event(
            id = "0007",
            title = "Meeting with Jeremy",
            from = LocalDateTime.of(todayYear, todayMonth, todayDayOfMonth, 10, 0),
            to = LocalDateTime.of(todayYear, todayMonth, todayDayOfMonth, 11, 0),
            remindAt = LocalDateTime.of(todayYear, todayMonth, todayDayOfMonth, 9, 0),
            description = "Discuss the worse project"
        ),
        AgendaItem.Task(
            id = "0008",
            title = "Chore with Jason",
            time = LocalDateTime.of(todayYear, todayMonth, todayDayOfMonth, 14, 0),
            remindAt = LocalDateTime.of(todayYear, todayMonth, todayDayOfMonth, 12, 30),
            description = "Kill the better project",
            isDone = true
        ),
    )}

    // Keeps track of the dropdown menu offsets
    val agendaItemMenuInfos = remember { mutableStateMapOf<UuidStr, MenuItemInfo>() }

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

    // • HEADER FOR SCREEN (Month & Logout)
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = MaterialTheme.colors.onSurface)
            .padding(0.dp)
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
                        )
                    }
            )

        }
        Spacer(modifier = Modifier.smallHeight())

        // • HEADER FOR ITEMS (S, M, T, W, T, F, S, & Day Picker)
        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .taskyScreenTopCorners(color = MaterialTheme.colors.surface)
//                .weight(1f)
                .padding(0.dp)
        ) col2@{
            Spacer(modifier = Modifier.tinyHeight())

            var selectedDay by remember { mutableStateOf(0) }

            Row {
                // show days of the week
                val daysOfWeek = listOf("S", "M", "T", "W", "T", "F", "S")
                daysOfWeek.forEachIndexed { i, day ->
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .wrapContentWidth(Alignment.CenterHorizontally)
                            .drawBehind {
                                if (selectedDay == i) {
                                    val paint = Paint().apply {
                                        color = DaySelected.toArgb()
                                        strokeWidth = 1f
                                        style = Paint.Style.STROKE
                                    }
                                    drawRoundRect(
                                        color = DaySelected,
                                        topLeft = Offset(0f, -25f),
                                        size = Size(size.width, size.height + 50f),
                                        cornerRadius = CornerRadius(
                                            DP.large.toPx(),
                                            DP.large.toPx()
                                        )
                                    )
                                }
                            }
                            .clickable {
                                //onAction(AgendaEvent.SetSelectedDay(i))
                                selectedDay = i
                            }
                    ) {
                        // Day of week (S, M, T, W, T, F, S)
                        Text(
                            text = day,
                            style = MaterialTheme.typography.subtitle2,
                            fontWeight = if (selectedDay == i)
                                    FontWeight.Bold
                                else
                                    FontWeight.SemiBold,
                            color = if (selectedDay == i)
                                    Color.Black
                                else
                                    MaterialTheme.colors.onSurface.copy(alpha = 0.3f),
                            modifier = Modifier
                                .align(Alignment.CenterHorizontally)
                                .wrapContentWidth(Alignment.CenterHorizontally)
                                .padding(horizontal = DP.small)
                        )
                        Spacer(modifier = Modifier.tinyHeight())

                        // Day Number of Month (1-31)
                        Text(
                            text = (i + 1).toString(),
                            style = MaterialTheme.typography.h3,
                            fontWeight = FontWeight.Bold,
                            color = if (selectedDay == i)
                                    Color.Black
                                else
                                    MaterialTheme.colors.onSurface,
                            modifier = Modifier
                                .align(Alignment.CenterHorizontally)
                                .wrapContentWidth(Alignment.CenterHorizontally)
                                .padding(horizontal = DP.small)
                        )

                    }
                }
            }

            Spacer(modifier = Modifier.tinyHeight())
        }

        // • AGENDA ITEMS LIST
        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .background(color = MaterialTheme.colors.surface)
                .weight(1f)
                .padding(0.dp)
        ) {
            Spacer(modifier = Modifier.smallHeight())

            // show today
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
            Spacer(modifier = Modifier.smallHeight())

            // SHOW AGENDA ITEMS LIST

            agendaItems.forEachIndexed { i, agendaItem ->

                AgendaCard(
                    agendaItem = agendaItem,
                    onMenuClick = {
                        onAction(AgendaEvent.ShowAgendaItemDropdown(agendaItem.id))
                    },
                    onSetMenuPosition = { coordinates ->
                        agendaItemMenuInfos[agendaItem.id] = MenuItemInfo(
                                menuPosition = coordinates.positionInRoot(),
                                agendaItem = agendaItem
                            )
                    },
                    modifier = Modifier
                        .padding(start = DP.tiny, end = DP.tiny)
                        .clickable {
                            performActionForAgendaItem(
                                agendaItem,
                                MenuAction.OPEN_DETAILS,
                                onAction
                            )
                        }
                )

                if (i < agendaItems.size - 1) {
                    Spacer(modifier = Modifier.smallHeight())
                }
            }
        }

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

        // • AgendaItem open/edit/delete dropdown
        if(state.agendaItemIdForMenu != null) {
            DropdownMenu(
                expanded = true,
                onDismissRequest = { onAction(AgendaEvent.ShowAgendaItemDropdown(null)) },
                offset = DpOffset(
                    x = with(LocalDensity.current) {
                            agendaItemMenuInfos[state.agendaItemIdForMenu]?.menuPosition?.x?.toDp()
                        } ?: 0.dp,
                    y = with(LocalDensity.current) {
                           agendaItemMenuInfos[state.agendaItemIdForMenu]?.menuPosition?.y?.toDp()
                        } ?: 0.dp
                ),
                modifier = Modifier
                    .background(color = MaterialTheme.colors.onSurface)
            ) {
                MenuItem(
                    title = "Open",
                    icon = Icons.Filled.OpenInNew,
                    onClick = {
                        performActionForAgendaItem(
                            agendaItemMenuInfos[state.agendaItemIdForMenu]?.agendaItem,
                            action = MenuAction.OPEN_DETAILS,
                            onAction = onAction
                        )
                    },
                )
                MenuItem(
                    title = "Edit",
                    icon = Icons.Filled.Edit,
                    onClick = {
                        performActionForAgendaItem(
                            agendaItemMenuInfos[state.agendaItemIdForMenu]?.agendaItem,
                            action = MenuAction.EDIT,
                            onAction = onAction
                        )
                    },
                )
                MenuItem(
                    title = "Delete",
                    icon = Icons.Filled.Delete,
                    onClick = {
                        performActionForAgendaItem(
                            agendaItemMenuInfos[state.agendaItemIdForMenu]?.agendaItem,
                            action = MenuAction.DELETE,
                            onAction = onAction
                        )
                    },
                )
            }
        }
    }
}

fun performActionForAgendaItem(agendaItem: AgendaItem?, action: MenuAction, onAction: (AgendaEvent)-> Unit) {
    onAction(AgendaEvent.ShowAgendaItemDropdown(null)) // close the menu

    agendaItem ?: return

    when (agendaItem) {
        is AgendaItem.Event -> {
            when (action) {
                MenuAction.OPEN_DETAILS -> {
                    println("OPEN DETAILS FOR EVENT ${agendaItem.id}")
                    //onAction(AgendaEvent.NavigateToOpenEvent(agendaItem))
                }
                MenuAction.EDIT -> {
                    println("EDIT EVENT ${agendaItem.id}")
                    //onAction(AgendaEvent.NavigateToEditEvent(agendaItem))
                }
                MenuAction.DELETE -> {
                    println("DELETE EVENT ${agendaItem.id}")
                    //onAction(AgendaEvent.DeleteEvent(agendaItem))
                }
                MenuAction.MARK_AS_DONE -> {
                    println("MARK AS DONE EVENT ${agendaItem.id}")
                    //onAction(AgendaEvent.MarkEventAsDone(agendaItem))
                }
                MenuAction.MARK_AS_NOT_DONE -> {
                    println("MARK AS NOT DONE EVENT ${agendaItem.id}")
                    //onAction(AgendaEvent.MarkEventAsNotDone(agendaItem))
                }
            }
        }
//                is AgendaItem.Task -> {
//                    when (action) {
//                        MenuActionKind.OPEN_DETAILS -> {
//                            onAction(AgendaEvent.NavigateToOpenTask(agendaItem))
//                        }
//                        MenuActionKind.EDIT -> {
//                            onAction(AgendaEvent.NavigateToEditTask(agendaItem))
//                        }
//                        MenuActionKind.DELETE -> {
//                            onAction(AgendaEvent.DeleteTask(agendaItem))
//                        }
//                        MenuActionKind.MARK_AS_DONE -> {
//                            onAction(AgendaEvent.MarkTaskAsDone(agendaItem))
//                        }
//                        MenuActionKind.MARK_AS_NOT_DONE -> {
//                            onAction(AgendaEvent.MarkTaskAsNotDone(agendaItem))
//                        }
//                    }
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














