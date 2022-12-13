package com.realityexpander.tasky.agenda_feature.presentation.agenda_screen

import android.content.res.Configuration
import android.graphics.Paint
import android.view.ViewTreeObserver
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExitTransition
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.hilt.navigation.compose.hiltViewModel
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.navigation.EmptyDestinationsNavigator
import com.realityexpander.observeconnectivity.IInternetConnectivityObserver
import com.realityexpander.tasky.MainActivity
import com.realityexpander.tasky.R
import com.realityexpander.tasky.agenda_feature.common.util.EventId
import com.realityexpander.tasky.agenda_feature.common.util.ReminderId
import com.realityexpander.tasky.agenda_feature.common.util.TaskId
import com.realityexpander.tasky.agenda_feature.data.common.utils.getDateForDayOffset
import com.realityexpander.tasky.agenda_feature.domain.AgendaItem
import com.realityexpander.tasky.agenda_feature.presentation.agenda_screen.AgendaScreenEvent.*
import com.realityexpander.tasky.agenda_feature.presentation.common.MenuItem
import com.realityexpander.tasky.agenda_feature.presentation.common.components.UserAcronymCircle
import com.realityexpander.tasky.agenda_feature.presentation.common.enums.AgendaItemType
import com.realityexpander.tasky.agenda_feature.presentation.common.util.toZonedDateTime
import com.realityexpander.tasky.agenda_feature.presentation.components.AgendaCard
import com.realityexpander.tasky.auth_feature.domain.AuthInfo
import com.realityexpander.tasky.core.presentation.common.modifiers.*
import com.realityexpander.tasky.core.presentation.theme.DaySelected
import com.realityexpander.tasky.core.presentation.theme.TaskyShapes
import com.realityexpander.tasky.core.presentation.theme.TaskyTheme
import com.realityexpander.tasky.destinations.EventScreenDestination
import com.realityexpander.tasky.destinations.LoginScreenDestination
import com.realityexpander.tasky.destinations.ReminderScreenDestination
import com.realityexpander.tasky.destinations.TaskScreenDestination
import com.vanpra.composematerialdialogs.MaterialDialog
import com.vanpra.composematerialdialogs.datetime.date.datepicker
import com.vanpra.composematerialdialogs.rememberMaterialDialogState
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.ZonedDateTime
import java.time.format.TextStyle
import java.time.temporal.ChronoUnit
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
//    val connectivityState by viewModel.connectivityState.collectAsState(
//        initial = IInternetConnectivityObserver.Status.Unavailable // must start as Unavailable
//    )
    val connectivityState by viewModel.onlineState.collectAsState(
        initial = IInternetConnectivityObserver.OnlineStatus.OFFLINE // must start as Offline
    )
    val zonedDateTimeNow by viewModel.zonedDateTimeNow.collectAsState()

    AgendaScreenContent(
        state = state,
        onAction = viewModel::sendEvent,
        oneTimeEvent = oneTimeEvent,
        zonedDateTimeNow = zonedDateTimeNow,
        navigator = navigator,
    )

    if (state.isProgressVisible) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = .25f))
        ) {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center)
            )
        }
    }

    // Offline? Show delayed warning in case starting up
    val isOfflineBannerVisible = remember { mutableStateOf(false) }
    LaunchedEffect(connectivityState) {
        isOfflineBannerVisible.value = false

//        if (connectivityState == IInternetConnectivityObserver.Status.Lost
//            || connectivityState == IInternetConnectivityObserver.Status.Unavailable
        if (connectivityState == IInternetConnectivityObserver.OnlineStatus.OFFLINE
        ) {
            delay(1000)
            isOfflineBannerVisible.value = true
        }
    }
    AnimatedVisibility(isOfflineBannerVisible.value) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Transparent)
        ) {
            Text(
                text = stringResource(R.string.no_internet),
                color = Color.Black,
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .background(Color.Red.copy(alpha = .5f))
                    .fillMaxWidth()
                    .align(Alignment.Center),

                )
        }
    }
}

@Composable
fun AgendaScreenContent(
    state: AgendaState,
    onAction: (AgendaScreenEvent) -> Unit,
    oneTimeEvent: OneTimeEvent?,
    zonedDateTimeNow: ZonedDateTime,
    navigator: DestinationsNavigator,
) {
    val focusManager = LocalFocusManager.current
    val context = LocalContext.current
    val scrollState = rememberLazyListState()
    val scope = rememberCoroutineScope()

    val agendaItems = state.agendaItems
    val weekStartDate = state.weekStartDate
    val selectedDayIndex = state.selectedDayIndex

    // Create days of the week for top of screen
    val daysInitialsAndDayOfWeek =
        remember(weekStartDate.dayOfYear) {   // initial of day of week, day of month
            val days = mutableListOf<Pair<String, Int>>() // initial of day of week, day of month

            for (i in 0..5) {
                val date = weekStartDate.plusDays(i.toLong())
                val dayOfWeek =
                    date.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.getDefault())
                days += Pair(dayOfWeek.first().toString(), date.dayOfMonth)
            }

            days
        }

    // Display month name
    val month = remember(weekStartDate.month) {
        weekStartDate.month.getDisplayName(TextStyle.FULL, Locale.getDefault()).uppercase()
    }

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
            // create new event
            EventScreenDestination(
                initialEventId = eventId,  // null = create new event
                isEditable = isEditable,
                startDate = getDateForDayOffset(weekStartDate, selectedDayIndex ?: 0),
            )
        ) {
            launchSingleTop = true
            restoreState = true
        }
    }

    fun navigateToTaskScreen(taskId: TaskId?, isEditable: Boolean = false) {
        navigator.navigate(
            TaskScreenDestination(
                initialTaskId = taskId,  // null = create new task
                isEditable = isEditable,
                startDate = getDateForDayOffset(weekStartDate, selectedDayIndex ?: 0),
            )
        ) {
            launchSingleTop = true
            restoreState = true
        }
    }

    fun navigateToReminderScreen(reminderId: ReminderId?, isEditable: Boolean = false) {
        navigator.navigate(
            ReminderScreenDestination(
                initialReminderId = reminderId,  // null = create new reminder
                isEditable = isEditable,
                startDate = getDateForDayOffset(weekStartDate, selectedDayIndex ?: 0),
            )
        ) {
            launchSingleTop = true
            restoreState = true
        }
    }

    // Guard against invalid authentication state OR perform logout
    SideEffect {
        if (state.isLoaded && state.authInfo == null) {
            navigateToLoginScreen()
        }
    }

    state.authInfo ?: return

    BackHandler(true) {
        // todo: should we ask the user to quit?
        (context as MainActivity).exitApp()
    }

    // Handle stateful one-time events
    LaunchedEffect(state.agendaItems) {
        if (state.scrollToItemId != null) {
            val item = agendaItems.indexOfFirst {
                it.id == state.scrollToItemId
            }
            if (item >= 0) {
                scope.launch {
                    scrollState.animateScrollToItem(item)
                }
            }
            onAction(StatefulOneTimeEvent.ResetScrollTo)
        }
        if (state.scrollToTop) {
            scope.launch {
                scrollState.animateScrollToItem(0)
            }
            onAction(StatefulOneTimeEvent.ResetScrollTo)
        }
        if (state.scrollToBottom) {
            scope.launch {
                scrollState.animateScrollToItem(agendaItems.size - 1)
            }
            onAction(StatefulOneTimeEvent.ResetScrollTo)
        }
    }

    // • One-time events (like Navigation, SnackBars, etc) are handled here
    LaunchedEffect(oneTimeEvent) {
        when (oneTimeEvent) {
            // • EVENT
            OneTimeEvent.NavigateToCreateEvent -> {
                navigateToEventScreen(null, true)
            }
            is OneTimeEvent.NavigateToOpenEvent -> {
                navigateToEventScreen(oneTimeEvent.eventId)
            }
            is OneTimeEvent.NavigateToEditEvent -> {
                navigateToEventScreen(oneTimeEvent.eventId, true)
            }

            // • TASK
            OneTimeEvent.NavigateToCreateTask -> {
                navigateToTaskScreen(null, true)
            }
            is OneTimeEvent.NavigateToOpenTask -> {
                navigateToTaskScreen(oneTimeEvent.taskId)
            }
            is OneTimeEvent.NavigateToEditTask -> {
                navigateToTaskScreen(oneTimeEvent.taskId, true)
            }

            // • REMINDER
            OneTimeEvent.NavigateToCreateReminder -> {
                navigateToReminderScreen(null, true)
            }
            is OneTimeEvent.NavigateToOpenReminder -> {
                navigateToReminderScreen(oneTimeEvent.reminderId)
            }
            is OneTimeEvent.NavigateToEditReminder -> {
                navigateToReminderScreen(oneTimeEvent.reminderId, true)
            }

            is OneTimeEvent.ShowToast -> {
                Toast.makeText(
                    context,
                    context.getString(
                        oneTimeEvent.message.asResIdOrNull
                            ?: R.string.error_invalid_string_resource_id
                    ), Toast.LENGTH_SHORT
                ).show()
            }
            null -> {}
        }
    }

    // Check keyboard open/closed (how to make this a function?) // todo remove soon
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
    ) col1@{
        Spacer(modifier = Modifier.mediumHeight())

        // • HEADER FOR SCREEN (Current Date, User Acronym, Logout)
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
                            onAction(ShowChooseCurrentDateDialog(state.weekStartDate))
                        }
                )
                Icon(
                    imageVector = Icons.Filled.ArrowDropDown,
                    tint = MaterialTheme.colors.surface,
                    contentDescription = "Logout dropdown",
                    modifier = Modifier
                        .align(Alignment.CenterVertically)
                        .clickable {
                            onAction(ShowChooseCurrentDateDialog(state.weekStartDate))
                        }
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
                    username = state.authInfo.username,
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
                            onAction(Logout)
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
                                onAction(SetSelectedDayIndex(dayIndex))
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
        val selectedDayIndexDayOfYear =
            state.weekStartDate
                .plusDays(selectedDayIndex?.toLong() ?: 0)
                .dayOfYear
        val nowDayOfYear = ZonedDateTime.now().dayOfYear
        Text(
            text =
            when (selectedDayIndexDayOfYear) {
                nowDayOfYear -> stringResource(R.string.agenda_today)
                nowDayOfYear + 1 -> stringResource(R.string.agenda_tomorrow)
                nowDayOfYear - 1 -> stringResource(R.string.agenda_yesterday)
                else -> {
                    val date = weekStartDate.plusDays((selectedDayIndex ?: 0).toLong())
                    val dayOfWeek =
                        date.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.getDefault())
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
        Spacer(
            modifier = Modifier
                .fillMaxWidth()
                .background(color = MaterialTheme.colors.surface)
                .tinyHeight()
        )

        // • SHOW AGENDA ITEMS LIST
        if (agendaItems.isNotEmpty()) {
            LazyColumn(
                state = scrollState,
                modifier = Modifier
                    .background(color = MaterialTheme.colors.surface)
                    .fillMaxSize()
                    .padding(start = DP.tiny, end = DP.tiny)

            ) {
                val sortedAgendaItems = agendaItems.sortedBy { agendaItem ->
                    agendaItem.startTime
                }
                val agendaItemsBeforeNow = sortedAgendaItems.filter { agendaItem ->
                    agendaItem.startTime.isBefore(zonedDateTimeNow)
                }
                val agendaItemsAfterNow = sortedAgendaItems.filter { agendaItem ->
                    agendaItem.startTime.isAfter(zonedDateTimeNow)
                }

                fun isToday() = (
                        weekStartDate.plusDays(selectedDayIndex?.toLong() ?: 0).year ==
                                ZonedDateTime.now().year
                                && weekStartDate.plusDays(
                            selectedDayIndex?.toLong() ?: 0
                        ).dayOfYear ==
                                ZonedDateTime.now().dayOfYear
                        )

                if (!isToday() || (isToday() && agendaItemsBeforeNow.isNotEmpty())) {
                    item("before_spacer_for_today") {
                        Spacer(
                            modifier = Modifier
                                .height(14.dp)
                                .fillMaxWidth()
                        )
                    }
                }

                itemsIndexed(items = agendaItemsBeforeNow) { index, agendaItem ->
                    Box {

                        AgendaCard(
                            modifier = Modifier
                                .padding(start = DP.tiny, end = DP.tiny)
                                .clickable {
                                    onActionForAgendaItem(
                                        AgendaItemAction.OPEN_DETAILS,
                                        agendaItem,
                                        onAction
                                    )
                                },
                            agendaItem = agendaItem,
                            authInfo = state.authInfo,
                            onToggleCompleted = {
                                if (agendaItem is AgendaItem.Task) {
                                    onAction(SetTaskCompleted(agendaItem, !agendaItem.isDone))
                                }
                            },
                            onEdit = {
                                onActionForAgendaItem(
                                    AgendaItemAction.EDIT,
                                    agendaItem,
                                    onAction
                                )
                            },
                            onDelete = {
                                onActionForAgendaItem(
                                    AgendaItemAction.DELETE,
                                    agendaItem,
                                    onAction
                                )
                            },
                            zonedDateTimeNow = zonedDateTimeNow
                        ) {
                            onActionForAgendaItem(
                                AgendaItemAction.OPEN_DETAILS,
                                agendaItem,
                                onAction
                            )
                        }
                    }
                    if (index < agendaItemsBeforeNow.size - 1
                    ) {
                        Spacer(modifier = Modifier.smallHeight())
                    }
                }

                // • TIME NEEDLE - only show on today (present day)
                if (isToday()) {
                    item("time_needle") {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(0.dp)
                                .height(14.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(2.dp)
                                    .background(color = MaterialTheme.colors.onSurface.copy(alpha = 0.9f))
                                    .align(Alignment.Center)
                            )
                            Icon(
                                imageVector = Icons.Filled.Circle,
                                contentDescription = null,
                                tint = MaterialTheme.colors.onSurface,
                                modifier = Modifier
                                    .size(14.dp)
                                    .align(Alignment.CenterStart)
                            )

                        }
                    }
                } else {
                    if (agendaItemsBeforeNow.isNotEmpty()) {
                        item("time_needle") {
                            Spacer(modifier = Modifier.smallHeight())
                        }
                    }
                }

                itemsIndexed(items = agendaItemsAfterNow) { index, agendaItem ->
                    Box {

                        AgendaCard(
                            modifier = Modifier
                                .padding(start = DP.tiny, end = DP.tiny)
                                .clickable {
                                    onActionForAgendaItem(
                                        AgendaItemAction.OPEN_DETAILS,
                                        agendaItem,
                                        onAction
                                    )
                                },
                            agendaItem = agendaItem,
                            authInfo = state.authInfo,
                            onToggleCompleted = {
                                if (agendaItem is AgendaItem.Task) {
                                    onAction(SetTaskCompleted(agendaItem, !agendaItem.isDone))
                                }
                            },
                            onEdit = {
                                onActionForAgendaItem(
                                    AgendaItemAction.EDIT,
                                    agendaItem,
                                    onAction
                                )
                            },
                            onDelete = {
                                onActionForAgendaItem(
                                    AgendaItemAction.DELETE,
                                    agendaItem,
                                    onAction
                                )
                            },
                            zonedDateTimeNow = zonedDateTimeNow
                        ) {
                            onActionForAgendaItem(
                                AgendaItemAction.OPEN_DETAILS,
                                agendaItem,
                                onAction
                            )
                        }
                    }
                    if (index < agendaItemsAfterNow.size - 1) {
                        Spacer(modifier = Modifier.smallHeight())
                    }
                }
            }
        }

        ////// STATUS ///////

        // • EMPTY AGENDA INDICATOR
        val showEmptyIndicator = remember { mutableStateOf(false) }
        LaunchedEffect(agendaItems.isEmpty()) {
            showEmptyIndicator.value = false

            if (agendaItems.isEmpty()) {
                delay(500)
                showEmptyIndicator.value = true
            }
        }
        Box(
            contentAlignment = Alignment.TopCenter,
            modifier = Modifier
                .fillMaxSize()
                .background(color = MaterialTheme.colors.surface)
        ) {

            Column {
                AnimatedVisibility(
                    visible = showEmptyIndicator.value,
                    exit = ExitTransition.None,
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(color = MaterialTheme.colors.surface)
                        .padding(start = DP.small, top = DP.small)
                        .align(Alignment.CenterHorizontally)
                ) {
                    Text(
                        text = stringResource(R.string.agenda_empty_list),
                        style = MaterialTheme.typography.h5,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colors.onSurface.copy(alpha = 0.60f),
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .background(color = MaterialTheme.colors.surface)
                            .fillMaxWidth()
                    )
                }
            }
        }

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
                        onAction(CreateAgendaItem(AgendaItemType.Event))
                    },
                )
                MenuItem(
                    title = "Task",
                    vectorIcon = Icons.Filled.AddTask,
                    onClick = {
                        isFabMenuExpanded = false
                        onAction(CreateAgendaItem(AgendaItemType.Task))
                    },
                )
                MenuItem(
                    title = "Reminder",
                    vectorIcon = Icons.Filled.NotificationAdd,
                    onClick = {
                        isFabMenuExpanded = false
                        onAction(CreateAgendaItem(AgendaItemType.Reminder))
                    },
                )
            }
        }
    }

    // • CONFIRM `DELETE AGENDA ITEM` DIALOG
    state.confirmDeleteAgendaItem?.let { agendaItem ->

        val agendaItemTypeName =
            when (agendaItem) {
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
                Text(
                    stringResource(
                        R.string.agenda_confirm_delete_item_dialog_title_phrase,
                        agendaItemTypeName
                    )
                )
            },
            text = {
                Text(
                    stringResource(
                        R.string.agenda_confirm_delete_item_dialog_text_phrase,
                        agendaItem.title
                    )
                )
            },
            onDismissRequest = { onAction(DismissConfirmDeleteAgendaItemDialog) },
            confirmButton = {
                TextButton(
                    onClick = {
                        onAction(DeleteAgendaItem(agendaItem))
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
                        onAction(DismissConfirmDeleteAgendaItemDialog)
                    },
                    colors = ButtonDefaults.textButtonColors(
                        backgroundColor = Color.Transparent
                    )
                ) {
                    Text(stringResource(android.R.string.cancel))
                }
            }
        )
    }

    // • SELECT CURRENT DATE FOR AGENDA
    var pickedDate by remember(weekStartDate) { mutableStateOf(weekStartDate) }
    val dateDialogState = rememberMaterialDialogState()
    LaunchedEffect(state.chooseWeekStartDateDialog) {
        state.chooseWeekStartDateDialog?.let {
            dateDialogState.show()
        }
    }
    MaterialDialog(
        dialogState = dateDialogState,
        onCloseRequest = {
            dateDialogState.hide()
            onAction(CancelChooseCurrentDateDialog)
        },
        buttons = {
            positiveButton(text = stringResource(android.R.string.ok)) {
                dateDialogState.hide()
                onAction(SetCurrentDate(pickedDate))
            }
            negativeButton(text = stringResource(android.R.string.cancel)) {
                dateDialogState.hide()
                onAction(CancelChooseCurrentDateDialog)
            }
            button(text = stringResource(R.string.agenda_choose_current_date_dialog_today_button_text)) {
                dateDialogState.hide()
                onAction(
                    SetCurrentDate(
                        ZonedDateTime.now().truncatedTo(ChronoUnit.DAYS)
                    )
                )
            }

        }
    ) {
        datepicker(
            initialDate = weekStartDate.toLocalDate(),
            title = stringResource(id = R.string.agenda_choose_current_date_dialog_title),
        ) {
            pickedDate = it.atTime(0, 0, 0, 0).toZonedDateTime()
        }
    }

}

fun onActionForAgendaItem(
    action: AgendaItemAction,
    agendaItem: AgendaItem?,
    onAction: (AgendaScreenEvent) -> Unit
) {
    agendaItem ?: return

    when (agendaItem) {
        is AgendaItem.Event -> {
            when (action) {
                AgendaItemAction.OPEN_DETAILS -> {
                    onAction(OneTimeEvent.NavigateToOpenEvent(agendaItem.id))
                }
                AgendaItemAction.EDIT -> {
                    onAction(OneTimeEvent.NavigateToEditEvent(agendaItem.id))
                }
                AgendaItemAction.DELETE -> {
                    onAction(ShowConfirmDeleteAgendaItemDialog(agendaItem))
                }
                else -> {}
            }
        }
        is AgendaItem.Task -> {
            when (action) {
                AgendaItemAction.OPEN_DETAILS -> {
                    onAction(OneTimeEvent.NavigateToOpenTask(agendaItem.id))
                }
                AgendaItemAction.EDIT -> {
                    onAction(OneTimeEvent.NavigateToEditTask(agendaItem.id))
                }
                AgendaItemAction.DELETE -> {
                    onAction(ShowConfirmDeleteAgendaItemDialog(agendaItem))
                }
                AgendaItemAction.MARK_AS_DONE -> {
                    onAction(SetTaskCompleted(agendaItem, true))
                }
                AgendaItemAction.MARK_AS_NOT_DONE -> {
                    onAction(SetTaskCompleted(agendaItem, false))
                }
            }
        }
        is AgendaItem.Reminder -> {
            when (action) {
                AgendaItemAction.OPEN_DETAILS -> {
                    onAction(OneTimeEvent.NavigateToOpenReminder(agendaItem.id))
                }
                AgendaItemAction.EDIT -> {
                    onAction(OneTimeEvent.NavigateToEditReminder(agendaItem.id))
                }
                AgendaItemAction.DELETE -> {
                    onAction(ShowConfirmDeleteAgendaItemDialog(agendaItem))
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
    group = "Night Mode=true"
)
fun AgendaScreenPreview() {
    TaskyTheme {
        AgendaScreenContent(
            state = AgendaState(
                authInfo = AuthInfo(
                    username = "Chris Athanas",
                ),
//                agendaItems = flow {
                agendaItems =
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
                ),
                isLoaded = true,
            ),
            onAction = { println("ACTION: $it") },
            oneTimeEvent = null,
            zonedDateTimeNow = ZonedDateTime.now(),
            navigator = EmptyDestinationsNavigator,
        )
    }
}

@Composable
@Preview(
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_NO,
    group = "Night Mode=false",
    apiLevel = 28,
    widthDp = 350,
)
fun AgendaScreenPreview_NightMode_NO() {
    AgendaScreenPreview()
}












