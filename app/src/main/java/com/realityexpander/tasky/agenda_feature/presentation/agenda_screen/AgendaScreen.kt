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
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.platform.*
import androidx.compose.ui.res.stringResource
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
import com.realityexpander.tasky.R
import com.realityexpander.tasky.agenda_feature.domain.AgendaItem
import com.realityexpander.tasky.agenda_feature.presentation.common.MenuItem
import com.realityexpander.tasky.agenda_feature.presentation.common.UserAcronymCircle
import com.realityexpander.tasky.agenda_feature.presentation.components.AgendaCard
import com.realityexpander.tasky.auth_feature.domain.AuthInfo
import com.realityexpander.tasky.core.presentation.common.modifiers.*
import com.realityexpander.tasky.core.presentation.theme.DaySelected
import com.realityexpander.tasky.core.presentation.theme.TaskyShapes
import com.realityexpander.tasky.core.presentation.theme.TaskyTheme
import com.realityexpander.tasky.core.util.UuidStr
import com.realityexpander.tasky.destinations.LoginScreenDestination
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.*

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

    var selectedDay by remember { mutableStateOf(0) }

    var logoutButtonSize by remember { mutableStateOf(Size.Zero)}
    var logoutButtonOffset by remember { mutableStateOf(Offset.Zero)}

    val agendaItems = state.agendaItems

    // Keeps track of the dropdown menu offsets
    val agendaItemMenuInfos = remember { mutableStateMapOf<UuidStr, MenuItemInfo>() }

    // create days of the week for top of screen
    val daysInitialsAndDayOfWeek = remember(LocalDate.now().dayOfMonth) {   // initial of day of week, day of month
        val days = mutableListOf<Pair<String, Int>>() // initial of day of week, day of month

        for (i in 0..5) {
            val date = LocalDate.now().plusDays(i.toLong())
            val dayOfWeek =
                date.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.getDefault())
            days += Pair(dayOfWeek.first().toString(), date.dayOfMonth)
        }

        days
    }

    // Display month name
    val month = remember { LocalDate.now().month.getDisplayName(TextStyle.FULL, Locale.getDefault()).uppercase() }

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

    // • Main Container
    Column(
        modifier = Modifier
            .background(color = MaterialTheme.colors.onSurface)
            .padding(0.dp)
    ) col1@ {
        Spacer(modifier = Modifier.mediumHeight())

        // • HEADER FOR SCREEN (Month Dropdown, User Acronym, Logout)
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
                    text = month,
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

        // • HEADER FOR AGENDA ITEMS (S, M, T, W, T, F, S, & Day Picker)
        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .taskyScreenTopCorners(color = MaterialTheme.colors.surface)
                .padding(0.dp)
        ) col2@{
            Spacer(modifier = Modifier.tinyHeight())

            // • DAYS OF WEEK & Day PICKER
            Row {
                daysInitialsAndDayOfWeek.forEachIndexed { i, (dayInitial, dayOfMonth) ->

                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .wrapContentWidth(Alignment.CenterHorizontally)
                            .drawBehind {
                                if (selectedDay == i) {
                                    Paint().apply {
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
                            text = dayInitial,
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
                            text = dayOfMonth.toString(),
                            style = MaterialTheme.typography.h3,
                            fontWeight = FontWeight.Bold,
                            color = if (selectedDay == i)
                                    Color.Black
                                else
                                    MaterialTheme.colors.onSurface,
                            modifier = Modifier
                                .align(Alignment.CenterHorizontally)
                                .wrapContentWidth(Alignment.CenterHorizontally)
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
                .fillMaxSize()
                .weight(1f)
                .padding(0.dp)
        ) {
            Spacer(modifier = Modifier.smallHeight())

            //val todayTasks = state.tasks.filter { it.date == todayDate }
            //val todayTasksCount = todayTasks.size

            // • SHOW TODAY'S DATE
            Text(
                text =
                    when (selectedDay) {
                        0 -> "Today"
                        1 -> "Tomorrow"
                        else -> {
                            val date = LocalDate.now().plusDays(selectedDay.toLong())
                            val dayOfWeek = date.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.getDefault())
                            val dayOfMonth = date.dayOfMonth.toString()
                            val monthName = date.month.getDisplayName(TextStyle.SHORT, Locale.getDefault())
                            "$dayOfWeek, $monthName $dayOfMonth"
                        }
                    },
                style = MaterialTheme.typography.h3,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colors.onSurface,
                modifier = Modifier
                    .padding(start = DP.small, end = DP.small)
            )
            Spacer(modifier = Modifier.smallHeight())

            // • SHOW AGENDA ITEMS LIST
            agendaItems.forEachIndexed { i, agendaItem ->

                AgendaCard(
                    agendaItem = agendaItem,
                    onMenuClick = {
                        onAction(AgendaEvent.ShowAgendaItemDropdown(agendaItem.id))
                    },
                    onToggleCompleted = {
                        if(agendaItem is AgendaItem.Task) {
                             onAction(AgendaEvent.ToggleTaskCompleted(agendaItem.id))
                        }
                    },
                    setMenuPositionCallback = { coordinates ->
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
                                AgendaItemAction.OPEN_DETAILS,
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
            .fillMaxSize()
    ) {
        val screenHeight = LocalConfiguration.current.screenHeightDp.dp

        // • Logout user dropdown
        DropdownMenu(
            expanded = state.isLogoutDropdownVisible,
            onDismissRequest = { onAction(AgendaEvent.ToggleLogoutDropdown) },
            offset = DpOffset(
                x = logoutButtonOffset.x.dp - logoutButtonSize.width.dp * 3f,
                y = -screenHeight
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
                            (agendaItemMenuInfos[state.agendaItemIdForMenu]?.menuPosition?.x ?: 0f).toDp()
                        },
                    y = with(LocalDensity.current) {
                            (-screenHeight + (agendaItemMenuInfos[state.agendaItemIdForMenu]?.menuPosition?.y ?: 0f).toDp())
                        }
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
                            action = AgendaItemAction.OPEN_DETAILS,
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
                            action = AgendaItemAction.EDIT,
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
                            action = AgendaItemAction.DELETE,
                            onAction = onAction
                        )
                    },
                )
            }
        }

        // • FAB to add new agenda item
        IconButton(
            onClick = {
                // onAction(AgendaEvent.ShowAddAgendaItemDialog)
            },
            modifier = Modifier
                .size(DP.XXXLarge)
                .offset(x = -DP.medium, y = -DP.medium)
                .clip(shape = TaskyShapes.MediumButtonRoundedCorners)
                .background(color = MaterialTheme.colors.onSurface)
                .align(alignment = Alignment.BottomEnd)
        ) {
            Icon(
                tint = MaterialTheme.colors.surface,
                imageVector = Icons.Filled.Add,
                contentDescription = stringResource(R.string.agenda_add_agenda_item),
                modifier = Modifier
                    .align(alignment = Alignment.Center)
                    .size(DP.XXXLarge)
                    .padding(start = 6.dp, end = 6.dp) // fine tunes the icon size (weird)
            )
        }
    }
}

fun performActionForAgendaItem(
    agendaItem: AgendaItem?,
    action: AgendaItemAction,
    onAction: (AgendaEvent)-> Unit
) {
    onAction(AgendaEvent.ShowAgendaItemDropdown(null)) // close the menu

    agendaItem ?: return

    when (agendaItem) {
        is AgendaItem.Event -> {
            when (action) {
                AgendaItemAction.OPEN_DETAILS -> {
                    println("OPEN DETAILS FOR EVENT ${agendaItem.id}")
//                    onAction(AgendaEvent.NavigateToOpenEvent(agendaItem))
                }
                AgendaItemAction.EDIT -> {
                    println("EDIT EVENT ${agendaItem.id}")
//                    onAction(AgendaEvent.NavigateToEditEvent(agendaItem))
                }
                AgendaItemAction.DELETE -> {
                    println("DELETE EVENT ${agendaItem.id}")
//                    onAction(AgendaEvent.DeleteEvent(agendaItem))
                }
                else -> {}
            }
        }
        is AgendaItem.Task -> {
            when (action) {
                AgendaItemAction.OPEN_DETAILS -> {
                    println("OPEN DETAILS FOR TASK ${agendaItem.id}")
//                    onAction(AgendaEvent.NavigateToOpenTask(agendaItem))
                }
                AgendaItemAction.EDIT -> {
                    println("EDIT TASK ${agendaItem.id}")
//                    onAction(AgendaEvent.NavigateToEditTask(agendaItem))
                }
                AgendaItemAction.DELETE -> {
                    println("DELETE TASK ${agendaItem.id}")
//                    onAction(AgendaEvent.DeleteTask(agendaItem))
                }
                AgendaItemAction.MARK_AS_DONE -> {
                    println("MARK AS DONE TASK ${agendaItem.id}")
//                    onAction(AgendaEvent.MarkTaskAsDone(agendaItem))
                }
                AgendaItemAction.MARK_AS_NOT_DONE -> {
                    println("MARK AS NOT DONE TASK ${agendaItem.id}")
//                    onAction(AgendaEvent.MarkTaskAsNotDone(agendaItem))
                }
            }
        }
        is AgendaItem.Reminder -> {
            when (action) {
                AgendaItemAction.OPEN_DETAILS -> {
                    println("OPEN DETAILS FOR REMINDER ${agendaItem.id}")
//                    onAction(AgendaEvent.NavigateToOpenReminder(agendaItem))
                }
                AgendaItemAction.EDIT -> {
                    println("EDIT REMINDER ${agendaItem.id}")
//                    onAction(AgendaEvent.NavigateToEditReminder(agendaItem))
                }
                AgendaItemAction.DELETE -> {
                    println("DELETE REMINDER ${agendaItem.id}")
//                    onAction(AgendaEvent.DeleteReminder(agenda))
                }
                else -> {}
            }
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














