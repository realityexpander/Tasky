package com.realityexpander.tasky.agenda_feature.presentation.event_screen

import android.content.res.Configuration
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Circle
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight.Companion.Bold
import androidx.compose.ui.text.font.FontWeight.Companion.SemiBold
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.navigation.EmptyDestinationsNavigator
import com.realityexpander.tasky.R
import com.realityexpander.tasky.agenda_feature.domain.AgendaItem
import com.realityexpander.tasky.agenda_feature.domain.Attendee
import com.realityexpander.tasky.agenda_feature.presentation.common.components.TimeDateRow
import com.realityexpander.tasky.agenda_feature.presentation.event_screen.EventScreenEvent.*
import com.realityexpander.tasky.agenda_feature.presentation.event_screen.components.AttendeeList
import com.realityexpander.tasky.agenda_feature.presentation.event_screen.components.PillButton
import com.realityexpander.tasky.agenda_feature.presentation.event_screen.components.SmallHeightHorizontalDivider
import com.realityexpander.tasky.agenda_feature.util.toLongMonthDayYear
import com.realityexpander.tasky.auth_feature.domain.AuthInfo
import com.realityexpander.tasky.core.presentation.common.modifiers.*
import com.realityexpander.tasky.core.presentation.theme.TaskyLightGreen
import com.realityexpander.tasky.core.presentation.theme.TaskyTheme
import java.time.ZonedDateTime
import java.util.*

@Composable
@Destination
fun EventScreen(
    navigator: DestinationsNavigator,
    viewModel: EventViewModel = hiltViewModel(),
) {

    val state by viewModel.state.collectAsState()

    if (state.isLoaded) {
        AddEventScreenContent(
            state = state,
            onAction = viewModel::sendEvent,
            navigator = navigator,
        )
    }

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

enum class AttendeeListType {
    ALL,
    GOING,
    NOT_GOING,
}

@Composable
fun AddEventScreenContent(
    state: EventScreenState,
    onAction: (EventScreenEvent) -> Unit,
    navigator: DestinationsNavigator,
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var attendeeListType by remember { mutableStateOf(AttendeeListType.ALL) }
    val isEditable = state.isEditable

    fun popBack() {
        navigator.popBackStack()

//        navigate(
//            LoginScreenDestination(
//                username = null,
//                email = null,
//                password = null,
//                confirmPassword = null,
//            )
//        ) {
//            popUpTo(LoginScreenDestination.route) {
//                inclusive = true
//            }
//            launchSingleTop = true
//            restoreState = true
//        }
    }

    // Handle stateful one-time events
    LaunchedEffect(state) {
//        if (state.scrollToItemId != null) {
//            val item = agendaItems.indexOfFirst { it.id == state.scrollToItemId }
//            if (item >= 0) {
//                scope.launch {
//                    scrollState.animateScrollToItem(item)
//
//            }
//            onAction(AgendaEvent.StatefulOneTimeEvent.ResetScrollTo)
//        }
    }

    // todo handle "un-stateful" one-time events (like Snackbar), will use a Channel or SharedFlow

    // • MAIN CONTAINER
    Column(
        modifier = Modifier
            .background(color = MaterialTheme.colors.onSurface)
            .padding(0.dp)
    ) col1@{
        Spacer(modifier = Modifier.mediumHeight())

        // • HEADER FOR SCREEN (Close, Current Date, Edit/Save)
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = DP.small, end = DP.small)
        ) {

            // • CLOSE BUTTON
            Icon(
                imageVector = Icons.Filled.Close,
                tint = MaterialTheme.colors.surface,
                contentDescription = stringResource(R.string.event_description_close),
                modifier = Modifier
                    .size(30.dp)
                    .alignByBaseline()
                    .align(Alignment.CenterVertically)
            )

            // • TODAY'S DATE
            Text(
                ZonedDateTime.now().toLongMonthDayYear(),
                color = MaterialTheme.colors.surface,
                textAlign = TextAlign.Center,
                fontWeight = SemiBold,
                modifier = Modifier
                    .align(Alignment.CenterVertically)
                    .weight(1f)
            )

            // • EDIT / SAVE BUTTON
            if (isEditable) {
                Text(
                    text = stringResource(R.string.event_save),
                    color = MaterialTheme.colors.surface,
                    textAlign = TextAlign.End,
                    modifier = Modifier
                        .align(Alignment.CenterVertically)
                        .alignByBaseline()
                        .width(40.dp)
                        .clickable {
                            onAction(SetIsEditable(false))
                        }
                )
            } else {
                Icon(
                    imageVector = Icons.Filled.Edit,
                    tint = MaterialTheme.colors.surface,
                    contentDescription = stringResource(R.string.event_description_edit_event),
                    modifier = Modifier
                        .align(Alignment.CenterVertically)
                        .width(40.dp)
                        .clickable {
                            onAction(SetIsEditable(true))
                        }
                )
            }

        }
        Spacer(modifier = Modifier.smallHeight())


        // • EVENT HEADER / DESCRIPTION
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .taskyScreenTopCorners(color = MaterialTheme.colors.surface)
                .verticalScroll(rememberScrollState())
        ) col2@{
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = DP.small, end = DP.small)
            ) {
                Spacer(modifier = Modifier.smallHeight())

                // • AGENDA ITEM TYPE (EVENT)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    Box(
                        modifier = Modifier
                            .size(20.dp)
                            .offset(2.dp, 0.dp)
                            .background(color = TaskyLightGreen)
                            .align(Alignment.CenterVertically)
                    )
                    Spacer(modifier = Modifier.extraSmallWidth())
                    Text(
                        stringResource(R.string.event_event_title),
                        fontWeight = SemiBold,
                        color = MaterialTheme.colors.onSurface,
                        modifier = Modifier
                            .align(Alignment.CenterVertically)
                    )
                }
                Spacer(modifier = Modifier.smallHeight())

                // • EVENT TITLE
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {

                    Row(
                        modifier = Modifier
                            .weight(1f)
                    ) {
                        // • Visual Circle
                        Icon(
                            imageVector = Icons.Outlined.Circle,
                            tint = MaterialTheme.colors.onSurface,
                            contentDescription = stringResource(R.string.event_description_meeting_title_marker),
                            modifier = Modifier
                                .size(26.dp)
                                .offset(0.dp, 8.dp)
                                .align(Alignment.Top)
                        )
                        Spacer(modifier = Modifier.extraSmallWidth())
                        Text(
                            state.event?.title ?: stringResource(R.string.event_no_title_set),
                            style = MaterialTheme.typography.h2,
                            color = MaterialTheme.colors.onSurface,
                            modifier = Modifier
                                .align(Alignment.CenterVertically)
                        )
                    }

                    Icon(
                        imageVector = Icons.Filled.ChevronRight,
                        tint = if (isEditable) MaterialTheme.colors.onSurface else Color.Transparent,
                        contentDescription = stringResource(R.string.event_edit_event_title),
                        modifier = Modifier
                            .size(28.dp)
                            .weight(.1f)
                            .align(Alignment.CenterVertically)
                            .clickable(enabled = isEditable) {
                                onAction(
                                    SetEditMode(
                                        EditMode.ChooseTitleText(
                                            state.event?.title ?: "",
                                        )
                                    )
                                )
                            }
                    )

                }

                SmallHeightHorizontalDivider()

                // • EVENT DESCRIPTION
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {

                    Row(
                        modifier = Modifier
                            .weight(1f)
                    ) {
                        Text(
                            text = state.event?.description ?: stringResource(R.string.event_no_description_set),
                            style = MaterialTheme.typography.h5,
                            color = MaterialTheme.colors.onSurface,
                            modifier = Modifier
                                .fillMaxWidth()
                                .wrapContentHeight()
                        )
                    }

                    Icon(
                        imageVector = Icons.Filled.ChevronRight,
                        tint = if (isEditable) MaterialTheme.colors.onSurface else Color.Transparent,
                        contentDescription = stringResource(R.string.event_description_edit_event_description),
                        modifier = Modifier
                            .size(28.dp, 28.dp)
                            .weight(.1f)
                            .align(Alignment.CenterVertically)
                            .clickable(enabled = isEditable) {
                                onAction(
                                    SetEditMode(
                                        EditMode.ChooseDescriptionText(
                                            state.event?.description ?: "",
                                        )
                                    )
                                )
                            }
                    )
                }
                Spacer(modifier = Modifier.smallHeight())
            }

            // • PHOTO PICKER / ADD / REMOVE
            Box(
                modifier = Modifier
                    .background(MaterialTheme.colors.onSurface.copy(alpha = .1f))
                    .fillMaxWidth()
                    .padding(DP.small)
                    .border(0.dp, Color.Transparent)
                    .wrapContentHeight()
            ) {

                if (state.event?.photos.isNullOrEmpty()) {
                    // • NO PHOTOS
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .align(Alignment.Center)
                            .padding(top = DP.medium, bottom = DP.medium)
                    ) {
                        // • Add Photo Icon
                        Icon(
                            imageVector = Icons.Filled.Add,
                            tint = MaterialTheme.colors.onSurface.copy(alpha = .3f),
                            contentDescription = stringResource(R.string.event_description_add_photo),
                            modifier = Modifier
                                .size(26.dp)
                        )
                        Spacer(modifier = Modifier.smallWidth())
                        Text(
                            stringResource(R.string.event_add_photos),
                            modifier = Modifier
                                .offset(y = 2.dp),
                            fontWeight = Bold,
                            color = MaterialTheme.colors.onSurface.copy(alpha = .3f)
                        )
                    }
                } else {
                    // • LIST OF PHOTO IMAGES
                    Column(
                        modifier = Modifier
                            .wrapContentHeight()
                    ) {
                        // • Photos Header
                        Row(
                            horizontalArrangement = Arrangement.Start,
                            modifier = Modifier
                                .fillMaxWidth()
                        ) {
                            Text(
                                stringResource(R.string.event_photos),
                                color = MaterialTheme.colors.onSurface,
                                style = MaterialTheme.typography.h3,
                                fontWeight = SemiBold,
                            )
                        }
                        Spacer(modifier = Modifier.extraSmallHeight())

                        // • Photo Items
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .wrapContentHeight()
                                .horizontalScroll(state = rememberScrollState())
                        ) {

                            (1..9).forEach {
                                // • Photo content box
                                Box(
                                    modifier = Modifier
                                        .size(60.dp)
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(Color.Transparent)
                                        .border(
                                            2.dp,  // Border width
                                            MaterialTheme.colors.onSurface.copy(alpha = .3f),
                                            RoundedCornerShape(10.dp)
                                        )
                                ) {
                                    // • Add Photo Icon
                                    Icon(
                                        imageVector = Icons.Filled.Add,
                                        tint = MaterialTheme.colors.onSurface.copy(alpha = .3f),
                                        contentDescription = stringResource(R.string.event_description_add_photo),
                                        modifier = Modifier
                                            .size(36.dp)
                                            .align(Alignment.Center)
                                    )
                                }
                                Spacer(
                                    modifier = Modifier
                                        .width(10.dp)
                                )
                            }
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.smallHeight())


            // • EVENT TIMES & DATES (FROM, TO, REMIND AT)
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colors.surface)
                    .padding(start = DP.small, end = DP.small)
            ) col3@{

                SmallHeightHorizontalDivider()

                // • FROM TIME/DATE ROW
                TimeDateRow(
                    title = stringResource(R.string.event_from),
                    date = state.event?.from ?: ZonedDateTime.now(),
                    isEditable = isEditable,
                    onEditDate = {
                        onAction(
                            SetEditMode(
                                EditMode.ChooseFromDate(state.event?.from ?: ZonedDateTime.now())
                            )
                        )
                    },
                    onEditTime = {
                        onAction(
                            SetEditMode(
                                EditMode.ChooseFromTime(state.event?.from ?: ZonedDateTime.now())
                            )
                        )
                    }
                )
                SmallHeightHorizontalDivider()

                // • TO TIME/DATE ROW
                TimeDateRow(
                    title = stringResource(R.string.event_to),
                    date = state.event?.to ?: ZonedDateTime.now(),
                    isEditable = isEditable,
                    onEditDate = {
                        onAction(
                            SetEditMode(
                                EditMode.ChooseToDate(state.event?.to ?: ZonedDateTime.now())
                            )
                        )
                    },
                    onEditTime = {
                        onAction(
                            SetEditMode(
                                EditMode.ChooseToTime(state.event?.to ?: ZonedDateTime.now())
                            )
                        )
                    }
                )
                SmallHeightHorizontalDivider()

                // • REMIND AT ROW
                RemindAtRow(
                    fromDateTime = state.event?.from ?: ZonedDateTime.now(),
                    remindAtDateTime = state.event?.remindAt ?: ZonedDateTime.now(),
                    isEditable = isEditable,
                    isDropdownMenuVisible = state.editMode is EditMode.ChooseRemindAtDateTime,
                    onEditRemindAtDateTime = {
                        onAction(
                            SetEditMode(
                                EditMode.ChooseRemindAtDateTime(
                                    state.event?.remindAt ?: ZonedDateTime.now()
                                )
                            )
                        )
                    },
                    onDismissRequest = { onAction(CancelEditMode) },
                    onSaveRemindAtDateTime = { dateTime ->
                        onAction(
                            EditMode.UpdateDateTime(dateTime)
                        )
                    }
                )
                SmallHeightHorizontalDivider()
                Spacer(modifier = Modifier.smallHeight())


                // • ATTENDEES HEADER (VISITORS & ADD ATTENDEE BUTTON)
                Row {
                    Text(
                        stringResource(R.string.event_visitors),
                        color = MaterialTheme.colors.onSurface,
                        style = MaterialTheme.typography.h3,
                        modifier = Modifier
                    )
                    Spacer(modifier = Modifier.smallWidth())

                    // • Add Attendee Button
                    Icon(
                        imageVector = Icons.Outlined.Add,
                        tint = if (isEditable) MaterialTheme.colors.onSurface.copy(alpha = .3f) else Color.Transparent,
                        contentDescription = stringResource(R.string.event_description_add_attendee_button),
                        modifier = Modifier
                            .offset(y = (-4).dp)
                            .size(38.dp)
                            .clip(shape = RoundedCornerShape(5.dp))
                            .background(
                                if (isEditable)
                                    MaterialTheme.colors.onSurface.copy(alpha = .1f)
                                else
                                    Color.Transparent
                            )
                            .padding(4.dp)
                            .align(Alignment.CenterVertically)
                            .clickable(enabled = isEditable) {
                                onAction(
                                    SetEditMode(EditMode.ChooseAddAttendee())
                                )
                            }
                    )
                }
                Spacer(modifier = Modifier.largeHeight())


                // • ATTENDEE LIST SELECTOR: ALL / GOING / NOT GOING
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colors.surface)
                ) {
                    PillButton(
                        text = stringResource(R.string.event_all),
                        isSelected = attendeeListType == AttendeeListType.ALL,
                        onClick = {
                            attendeeListType = AttendeeListType.ALL
                        }
                    )
                    Spacer(modifier = Modifier.smallWidth())

                    PillButton(
                        text = stringResource(R.string.event_going),
                        isSelected = attendeeListType == AttendeeListType.GOING,
                        onClick = {
                            attendeeListType = AttendeeListType.GOING
                        }
                    )
                    Spacer(modifier = Modifier.smallWidth())

                    PillButton(
                        text = stringResource(R.string.event_not_going),
                        isSelected = attendeeListType == AttendeeListType.NOT_GOING,
                        onClick = {
                            attendeeListType = AttendeeListType.NOT_GOING
                        }
                    )
                }
                Spacer(modifier = Modifier.mediumHeight())


                // • ATTENDEES - GOING
                if (attendeeListType == AttendeeListType.ALL
                    || attendeeListType == AttendeeListType.GOING
                ) {
                    AttendeeList(
                        loggedInUserId = state.authInfo?.userId
                            ?: throw IllegalStateException("user not logged in"),
                        isUserEventCreator = true,
                        header = stringResource(R.string.event_going),
                        attendees = state.event?.attendees?.filter { it.isGoing } ?: emptyList(),
                        onAttendeeClick = {},
                        onAttendeeRemoveClick = { attendee ->
                            onAction(
                                SetEditMode(
                                    EditMode.ConfirmRemoveAttendee(attendee)
                                )
                            )
                        }
                    )
                    Spacer(modifier = Modifier.mediumHeight())
                }

                // • ATTENDEES - NOT GOING
                if (attendeeListType == AttendeeListType.ALL
                    || attendeeListType == AttendeeListType.NOT_GOING
                ) {
                    AttendeeList(
                        loggedInUserId = state.authInfo?.userId
                            ?: throw IllegalStateException("user not logged in"),
                        isUserEventCreator = true,
                        header = stringResource(R.string.event_not_going),
                        attendees = state.event?.attendees?.filter { !it.isGoing } ?: emptyList(),
                        onAttendeeClick = {},
                        onAttendeeRemoveClick = { attendee ->
                            onAction(
                                SetEditMode(
                                    EditMode.ConfirmRemoveAttendee(attendee)
                                )
                            )
                        }
                    )
                }

                Spacer(modifier = Modifier.largeHeight())

                // • JOIN/LEAVE/DELETE EVENT BUTTON
                Text(
                    if (state.event?.isUserEventCreator == true) stringResource(R.string.event_delete_event)
                        else if (state.event?.isGoing == true) stringResource(R.string.event_leave_event)
                        else stringResource(R.string.event_join_event),
                    style = MaterialTheme.typography.h4,
                    color = MaterialTheme.colors.onSurface.copy(alpha = 0.3f),
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                )
                Spacer(modifier = Modifier.mediumHeight())
            }
        }
    }

    // • EDITORS FOR EVENT PROPERTIES
    state.editMode?.let {
        EventPropertyEditors(
            editMode = it,
            state = state,
            onAction = onAction
        )
    }
}


@Preview(
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_NO,
    group = "Night Mode=false",
    apiLevel = 28,
    widthDp = 400,
    heightDp = 1200,
)
@Composable
fun Preview() {
    TaskyTheme {
        val authInfo = AuthInfo(
            userId = "X0001",
            authToken = "1010101010101",
            username = "Cameron Anderson"
        )

        AddEventScreenContent(
            state = EventScreenState(

                authInfo = authInfo,
                username = "Cameron Anderson",
                event = AgendaItem.Event(
                    id = "0001",
                    title = "Title of Event",
                    description = "Description of Event",
                    isUserEventCreator = false,
                    from = ZonedDateTime.now().plusHours(1),
                    to = ZonedDateTime.now().plusHours(2),
                    remindAt = ZonedDateTime.now().plusMinutes(30),
                    isGoing = true,
                    attendees = listOf(
                        Attendee(
                            eventId = "0001",
                            isGoing = true,
                            fullName = authInfo.username!!,
                            email = "cameron@demo.com",
                            remindAt = ZonedDateTime.now(),
                            id = authInfo.userId!!,
                            photo = "https://randomuser.me/api/portraits/men/75.jpg"
                        ),
                        Attendee(
                            eventId = "0001",
                            isGoing = true,
                            fullName = "Jeremy Johnson",
                            remindAt = ZonedDateTime.now(),
                            email = "jj@demo.com",
                            id = UUID.randomUUID().toString(),
                            photo = "https://randomuser.me/api/portraits/men/75.jpg"
                        ),
                        Attendee(
                            eventId = "0001",
                            isGoing = true,
                            fullName = "Fred Flintstone",
                            remindAt = ZonedDateTime.now(),
                            email = "ff@demo.com",
                            id = UUID.randomUUID().toString(),
                            photo = "https://randomuser.me/api/portraits/men/71.jpg"
                        ),
                        Attendee(
                            eventId = "0001",
                            isGoing = true,
                            fullName = "Sam Bankman",
                            remindAt = ZonedDateTime.now(),
                            email = "sb@demo.com",
                            id = UUID.randomUUID().toString(),
                            photo = "https://randomuser.me/api/portraits/men/70.jpg"
                        ),
                    ),
                )
            ),
            onAction = { println("ACTION: $it") },
            navigator = EmptyDestinationsNavigator,
        )
    }
}

@Preview(
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    group = "Night Mode=true",
)
@Composable
fun Preview_night_mode() {
    Preview()
}