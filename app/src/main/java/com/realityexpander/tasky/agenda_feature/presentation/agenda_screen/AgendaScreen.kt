package com.realityexpander.tasky.agenda_feature.presentation.agenda_screen

import android.content.res.Configuration
import android.graphics.Paint
import android.view.ViewTreeObserver
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.hilt.navigation.compose.hiltViewModel
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.navigation.EmptyDestinationsNavigator
import com.realityexpander.tasky.MainActivity
import com.realityexpander.tasky.R
import com.realityexpander.tasky.agenda_feature.common.util.EventId
import com.realityexpander.tasky.agenda_feature.domain.AgendaItem
import com.realityexpander.tasky.agenda_feature.presentation.common.MenuItem
import com.realityexpander.tasky.agenda_feature.presentation.common.components.UserAcronymCircle
import com.realityexpander.tasky.agenda_feature.presentation.common.enums.AgendaItemType
import com.realityexpander.tasky.agenda_feature.presentation.components.AgendaCard
import com.realityexpander.tasky.auth_feature.domain.AuthInfo
import com.realityexpander.tasky.core.presentation.common.modifiers.*
import com.realityexpander.tasky.core.presentation.theme.DaySelected
import com.realityexpander.tasky.core.presentation.theme.TaskyShapes
import com.realityexpander.tasky.core.presentation.theme.TaskyTheme
import com.realityexpander.tasky.destinations.EventScreenDestination
import com.realityexpander.tasky.destinations.LoginScreenDestination
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.ZonedDateTime
import java.time.format.TextStyle
import java.util.*

@Composable
@Destination
fun AgendaScreen(
    @Suppress("UNUSED_PARAMETER")  // extracted from navArgs in the viewModel
    selectedDayIndex: Int? = 0,

    navigator: DestinationsNavigator,
    viewModel: AgendaViewModel = hiltViewModel(),
) {
    val state by viewModel.agendaState.collectAsState()
    val oneTimeEvent by viewModel.oneTimeEvent.collectAsState(null)

    AgendaScreenContent(
        state = state,
        onAction = viewModel::sendEvent,
        oneTimeEvent = oneTimeEvent,
        navigator = navigator,
    )

    if (state.isProgressVisible) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = .5f))
        ) {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center)
            )
        }
    }
}

@Composable
fun AgendaScreenContent(
    state: AgendaState,
    onAction: (AgendaScreenEvent) -> Unit,
    oneTimeEvent: AgendaScreenEvent.OneTimeEvent?,
    navigator: DestinationsNavigator,
) {
    val focusManager = LocalFocusManager.current
    val context = LocalContext.current
    val scrollState = rememberLazyListState()
    val scope = rememberCoroutineScope()

    val agendaItems by state.agendaItems.collectAsState(initial = emptyList())
    val selectedDayIndex = state.selectedDayIndex

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
    val month = remember(LocalDate.now().month) { LocalDate.now().month.getDisplayName(TextStyle.FULL, Locale.getDefault()).uppercase() }

    fun navigateToLoginScreen() {
        navigator.navigate(
            LoginScreenDestination(
                username = null,
                email = null,
                password = null,
                confirmPassword = null,
            )
        ) {
            popUpTo(LoginScreenDestination.route) {
                inclusive = true
            }
            launchSingleTop = true
            restoreState = true
        }
    }

    fun navigateToEventScreen(eventId: EventId?, isEditable: Boolean = false) {
        navigator.navigate(
            EventScreenDestination(
                eventId = eventId,  // create new event
                isEditable = isEditable,
            )
        ) {
            launchSingleTop = true
            restoreState = true
        }
    }

    // Guard against invalid authentication state OR perform logout
    SideEffect {
        if (state.isLoaded && state.authInfo == null) {
            onAction(AgendaScreenEvent.SetIsLoaded(false))
            navigateToLoginScreen()
        }
    }

    BackHandler(true) {
        // todo: should we ask the user to quit?
        (context as MainActivity).exitApp()
    }

    // Handle stateful one-time events
    LaunchedEffect(state.agendaItems) {
        if(state.scrollToItemId != null) {
            val item = agendaItems.indexOfFirst { it.id == state.scrollToItemId }
            if (item >= 0) {
                scope.launch {
                    scrollState.animateScrollToItem(item)
                }
            }
            onAction(AgendaScreenEvent.StatefulOneTimeEvent.ResetScrollTo)
        }
        if(state.scrollToTop) {
            scope.launch {
                scrollState.animateScrollToItem(0)
            }
            onAction(AgendaScreenEvent.StatefulOneTimeEvent.ResetScrollTo)
        }
        if(state.scrollToBottom) {
            scope.launch {
                scrollState.animateScrollToItem(agendaItems.size - 1)
            }
            onAction(AgendaScreenEvent.StatefulOneTimeEvent.ResetScrollTo)
        }
    }

    // • One-time events (like Navigation, SnackBars, etc) are handled here
    LaunchedEffect(oneTimeEvent) {
        when (oneTimeEvent) {
            AgendaScreenEvent.OneTimeEvent.NavigateToCreateEvent -> {
                navigateToEventScreen(null, true)
            }
            is AgendaScreenEvent.OneTimeEvent.NavigateToOpenEvent -> {
                navigateToEventScreen(oneTimeEvent.eventId)
            }
            is AgendaScreenEvent.OneTimeEvent.NavigateToEditEvent -> {
                navigateToEventScreen(oneTimeEvent.eventId, true)
            }
            null -> {}
        }
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

    // • MAIN CONTAINER
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

            Box(
                modifier = Modifier
                    .alignByBaseline()
                    .align(Alignment.CenterVertically)
                    .weight(1f)
                    .wrapContentWidth(Alignment.End)
            ) {
                var isLogoutMenuExpanded by remember { mutableStateOf(false) }

                UserAcronymCircle(
                    username = state.authInfo?.username,
                    modifier = Modifier
                        .clickable {
                            isLogoutMenuExpanded = true
                        }
                )

                // • Logout user dropdown
                DropdownMenu(
                    expanded = isLogoutMenuExpanded,
                    onDismissRequest = { isLogoutMenuExpanded = false },
                    modifier = Modifier
                        .background(color = MaterialTheme.colors.onSurface)
                ) {
                    MenuItem(
                        title = "Logout",
                        vectorIcon = Icons.Filled.Logout,
                        onClick = {
                            isLogoutMenuExpanded = false
                            onAction(AgendaScreenEvent.Logout)
                        },
                    )
                }
            }

        }

        Spacer(modifier = Modifier.tinyHeight())

        // • HEADER FOR AGENDA ITEMS (S, M, T, W, T, F, & Day Picker)
        Column(
            modifier = Modifier
                .taskyScreenTopCorners(color = MaterialTheme.colors.surface)
                .padding(0.dp)
        ) col2@{
            Spacer(modifier = Modifier.smallHeight())

            // • DAYS OF WEEK & Day PICKER
            Row {
                daysInitialsAndDayOfWeek.forEachIndexed { dayIndex, (dayInitial, dayOfMonth) ->

                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .wrapContentWidth(Alignment.CenterHorizontally)
                            .drawBehind {
                                if (selectedDayIndex == dayIndex) {
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
                                onAction(AgendaScreenEvent.SetSelectedDayIndex(dayIndex))
                            }
                    ) {
                        // Day of week (S, M, T, W, T, F, S)
                        Text(
                            text = dayInitial,
                            style = MaterialTheme.typography.subtitle2,
                            fontWeight = if (selectedDayIndex == dayIndex)
                                    FontWeight.Bold
                                else
                                    FontWeight.SemiBold,
                            color = if (selectedDayIndex == dayIndex)
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
                            color = if (selectedDayIndex == dayIndex)
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

            Spacer(modifier = Modifier.mediumHeight())
        }

        // • SHOW TODAY'S DATE
        Text(
            text =
            when (selectedDayIndex) {
                0 -> "Today"
                1 -> "Tomorrow"
                else -> {
                    val date = LocalDate.now().plusDays((selectedDayIndex ?: 0).toLong())
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
                .fillMaxWidth()
                .background(color = MaterialTheme.colors.surface)
                .padding(start = DP.small)
        )
        Spacer(modifier = Modifier
            .fillMaxWidth()
            .background(color = MaterialTheme.colors.surface)
            .smallHeight()
        )

        //val todayTasks = state.tasks.filter { it.date == todayDate }
        //val todayTasksCount = todayTasks.size

        // • SHOW AGENDA ITEMS LIST
        LazyColumn(
            state = scrollState,
            modifier = Modifier
                .background(color = MaterialTheme.colors.surface)
                .fillMaxSize()
                .padding(start = DP.tiny, end = DP.tiny)
        ) {
            itemsIndexed(items = agendaItems) { index, agendaItem ->
                Box {
                    AgendaCard(
                        agendaItem = agendaItem,
                        onToggleCompleted = {
                            if (agendaItem is AgendaItem.Task) {
                                onAction(AgendaScreenEvent.ToggleTaskCompleted(agendaItem.id))
                            }
                        },
                        modifier = Modifier
                            .padding(start = DP.tiny, end = DP.tiny)
                            .clickable {
                                performActionForAgendaItem(
                                    agendaItem,
                                    AgendaItemAction.OPEN_DETAILS,
                                    onAction
                                )
                            },
                        onEdit = {
                            performActionForAgendaItem(
                                agendaItem,
                                AgendaItemAction.EDIT,
                                onAction
                            )
                        },
                        onDelete = {
                            performActionForAgendaItem(
                                agendaItem,
                                AgendaItemAction.DELETE,
                                onAction
                            )
                        },
                        onViewDetails = {
                            performActionForAgendaItem(
                                agendaItem,
                                AgendaItemAction.OPEN_DETAILS,
                                onAction
                            )
                        }
                    )
                }

                if (index < agendaItems.size - 1) {
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

    }

    // • FAB BUTTON
    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {

        var isFabMenuExpanded by remember { mutableStateOf(false) }

        // • FAB to create new agenda item
        IconButton(
            onClick = {
                isFabMenuExpanded = true
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

            // • Create AgendaItem Event/Task/Reminder
            DropdownMenu(
                expanded = isFabMenuExpanded,
                onDismissRequest = { isFabMenuExpanded = false },
                modifier = Modifier
                    .background(color = MaterialTheme.colors.onSurface)
            ) {
                MenuItem(
                    title = "Event",
                    painterIcon = painterResource(id = R.drawable.calendar_add_on),
                    onClick = {
                        isFabMenuExpanded = false
                        onAction(AgendaScreenEvent.CreateAgendaItem(AgendaItemType.Event))
                    },
                )
                MenuItem(
                    title = "Task",
                    vectorIcon = Icons.Filled.AddTask,
                    onClick = {
                        isFabMenuExpanded = false
                        onAction(AgendaScreenEvent.CreateAgendaItem(AgendaItemType.Task))
                    },
                )
                MenuItem(
                    title = "Reminder",
                    vectorIcon = Icons.Filled.NotificationAdd,
                    onClick = {
                        isFabMenuExpanded = false
                        onAction(AgendaScreenEvent.CreateAgendaItem(AgendaItemType.Reminder))
                    },
                )
            }
        }
    }

    // • Confirm `Delete Agenda Item` Dialog
    state.confirmDeleteAgendaItem?.let { agendaItem ->

        val agendaItemType =
            when(agendaItem) {
                is AgendaItem.Event -> {
                    stringResource(R.string.agenda_item_type_event)
                }
                is AgendaItem.Task -> {
                    stringResource(R.string.agenda_item_type_task)
                }
                is AgendaItem.Reminder -> {
                    stringResource(R.string.agenda_item_type_reminder)
                }
                else -> throw IllegalStateException("Unknown AgendaItem type")
            }

        AlertDialog(
            title = {
                Text("Delete $agendaItemType?")
            },
            text = {
                Text("Are you sure you want to delete $agendaItemType: '${agendaItem.title}'?")
            },
            onDismissRequest = { onAction(AgendaScreenEvent.DismissDeleteAgendaItem) },
            confirmButton = {
                TextButton(
                    onClick = {
                        onAction(AgendaScreenEvent.DeleteAgendaItem(agendaItem))
                    },
                    colors = ButtonDefaults.textButtonColors(
                        backgroundColor = Color.Transparent
                    )
                ) {
                    Text(stringResource(R.string.delete))
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        onAction(AgendaScreenEvent.DismissDeleteAgendaItem)
                    },
                    colors = ButtonDefaults.textButtonColors(
                        backgroundColor = Color.Transparent
                    )
                ) {
                    Text(stringResource(R.string.cancel))
                }
            }
        )
    }

}

fun performActionForAgendaItem(
    agendaItem: AgendaItem?,
    action: AgendaItemAction,
    onAction: (AgendaScreenEvent)-> Unit
) {
    agendaItem ?: return

    when (agendaItem) {
        is AgendaItem.Event -> {
            when (action) {
                AgendaItemAction.OPEN_DETAILS -> {
                    onAction(AgendaScreenEvent.OneTimeEvent.NavigateToOpenEvent(agendaItem.id))
                }
                AgendaItemAction.EDIT -> {
                    onAction(AgendaScreenEvent.OneTimeEvent.NavigateToEditEvent(agendaItem.id))
                }
                AgendaItemAction.DELETE -> {
                    onAction(AgendaScreenEvent.ConfirmDeleteAgendaItem(agendaItem))
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
                agendaItems = flow {
                    listOf(
                        AgendaItem.Event(
                            id = "1",
                            title = "Event 1",
                            description = "Event Description 1",
                            from = ZonedDateTime.now(),
                            to = ZonedDateTime.now().plusHours(1),
                            remindAt = ZonedDateTime.now(),
                            host = "Chris Athanas",
                        ),
                        AgendaItem.Task(
                            id = "2",
                            title = "Task 2",
                            description = "Task Description 2",
                            time = ZonedDateTime.now().plusHours(3),
                            isDone = false,
                            remindAt = ZonedDateTime.now().plusHours(2),
                        ),
                        AgendaItem.Reminder(
                            id = "3",
                            title = "Reminder 3",
                            description = "Reminder Description 3",
                            time = ZonedDateTime.now(),
                            remindAt = ZonedDateTime.now().plusDays(1),
                        ),
                    )
                },
                isLoaded = true,
            ),
            onAction = { println("ACTION: $it") },
            navigator = EmptyDestinationsNavigator,
            oneTimeEvent = null,
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












