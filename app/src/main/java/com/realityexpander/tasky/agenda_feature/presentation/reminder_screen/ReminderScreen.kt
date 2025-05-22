package com.realityexpander.tasky.agenda_feature.presentation.reminder_screen

import android.content.res.Configuration
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Circle
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
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
import com.realityexpander.tasky.agenda_feature.presentation.common.components.TimeDateRow
import com.realityexpander.tasky.agenda_feature.presentation.common.components.RemindAtRow
import com.realityexpander.tasky.agenda_feature.presentation.common.components.SmallHeightHorizontalDivider
import com.realityexpander.tasky.agenda_feature.presentation.reminder_screen.ReminderScreenEvent.*
import com.realityexpander.tasky.agenda_feature.presentation.common.util.toLongMonthDayYear
import com.realityexpander.tasky.auth_feature.domain.AuthInfo
import com.realityexpander.tasky.core.presentation.animatedTransitions.ScreenTransitions
import com.realityexpander.tasky.core.presentation.common.modifiers.*
import com.realityexpander.tasky.core.presentation.theme.TaskyLightGreen
import com.realityexpander.tasky.core.presentation.theme.TaskyTheme
import com.realityexpander.tasky.core.presentation.util.UiText
import com.realityexpander.tasky.core.presentation.util.getStringSafe
import com.realityexpander.tasky.core.util.UuidStr
import kotlinx.coroutines.launch
import java.time.ZonedDateTime

@Composable
@Destination(
    style= ScreenTransitions::class
)
fun ReminderScreen(
    @Suppress("UNUSED_PARAMETER")  // extracted from navArgs in the viewModel
    initialReminderId: UuidStr? = null,
    @Suppress("UNUSED_PARAMETER")  // extracted from navArgs in the viewModel
    isEditable: Boolean = false,
    @Suppress("UNUSED_PARAMETER")  // extracted from navArgs in the viewModel
    startDate : ZonedDateTime? = ZonedDateTime.now(),
    navigator: DestinationsNavigator,
    viewModel: ReminderViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsState()
    val oneTimeEvent by viewModel.oneTimeEvent.collectAsState(null)

    if (state.isLoaded) {
        ReminderScreenContent(
            state = state,
            oneTimeEvent = oneTimeEvent,
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

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun ReminderScreenContent(
    state: ReminderScreenState,
    oneTimeEvent: OneTimeEvent?,
    onAction: (ReminderScreenEvent) -> Unit,
    navigator: DestinationsNavigator,
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val isEditable = state.isEditable

    fun popBack() {
        navigator.popBackStack()
    }

    BackHandler(true) {
        if(state.editMode != null) {
            scope.launch {
                onAction(CancelEditMode)
            }
        } else {
            popBack()
        }
    }

    // • One-time events (like Navigation, Toasts, etc) are handled here
    LaunchedEffect(oneTimeEvent) {
        when (oneTimeEvent) {
            is OneTimeEvent.NavigateBack -> {
                popBack()
            }
            is OneTimeEvent.ShowToast -> {
                Toast.makeText(
                    context,
                    context.getString(
                        oneTimeEvent.message.asResIdOrNull
                            ?: R.string.error_invalid_string_resource_id
                    ), Toast.LENGTH_SHORT).show()
            }
            null -> {}
        }
    }

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
            IconButton(
                onClick = { popBack() },
            ) {
                Icon(
                    imageVector = Icons.Filled.Close,
                    tint = MaterialTheme.colors.surface,
                    contentDescription = stringResource(R.string.reminder_description_close),
                    modifier = Modifier
                        .size(30.dp)
                        .alignByBaseline()
                        .align(Alignment.CenterVertically)
                )
            }

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
                    TextButton(
                        onClick = {
                            onAction(SetIsEditable(false))
                            onAction(SaveReminder)
                        },
                        modifier = Modifier.weight(.25f)
                    ) {
                        Text(
                            text = stringResource(R.string.save),
                            color = MaterialTheme.colors.surface,
                            textAlign = TextAlign.End,
                            modifier = Modifier
                                .align(Alignment.CenterVertically)
                                .alignByBaseline()
                                .width(40.dp)
                        )
                    }
                } else {
                    IconButton(
                        onClick = {
                            onAction(SetIsEditable(true))
                        },
                        modifier = Modifier.weight(.25f)
                    ) {
                        Row {
                            Spacer(modifier = Modifier.smallWidth())
                            Icon(
                                imageVector = Icons.Filled.Edit,
                                tint = MaterialTheme.colors.surface,
                                contentDescription = stringResource(R.string.reminder_description_edit_event),
                                modifier = Modifier
                                    .align(Alignment.CenterVertically)
                                    .width(40.dp)
                            )
                        }
                    }
                }

        }
        //Spacer(modifier = Modifier.smallHeight())

        // • ERROR MESSAGE
        if (state.errorMessage != null) {
            Text(
                text = state.errorMessage.get,
                color = MaterialTheme.colors.error,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = DP.small, end = DP.small)
            )
            Spacer(modifier = Modifier.smallHeight())
        }


        // • REMINDER HEADER & MAIN CONTENT
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .taskyScreenTopCorners(color = MaterialTheme.colors.surface)
                .verticalScroll(rememberScrollState())
        ) col2@{

            // • REMINDER TITLE & DESCRIPTION
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = DP.small, end = DP.small)
            ) {
                Spacer(modifier = Modifier.smallHeight())

                // • AGENDA ITEM TYPE (REMINDER)
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
                        stringResource(R.string.agenda_item_type_reminder),
                        fontWeight = SemiBold,
                        color = MaterialTheme.colors.onSurface,
                        modifier = Modifier
                            .align(Alignment.CenterVertically)
                    )
                }
                Spacer(modifier = Modifier.smallHeight())

                // • REMINDER TITLE
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {

                    Row(
                        modifier = Modifier
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Circle,
                            tint = MaterialTheme.colors.onSurface,
                            contentDescription = stringResource(R.string.description_title_marker),
                            modifier = Modifier
                                .size(26.dp)
                                .offset(0.dp, 8.dp)
                                .align(Alignment.Top)
                        )
                        Spacer(modifier = Modifier.extraSmallWidth())
                    }

                    Text(
                        text = state.reminder?.title ?: "",
                        color = MaterialTheme.colors.onSurface,
                        fontWeight = SemiBold,
                        style = MaterialTheme.typography.h2,
                        modifier = Modifier
                            .align(Alignment.CenterVertically)
                            .weight(1f)
                    )

                    val editTextStyle =
                        MaterialTheme.typography.h2  // can only access in Composable scope
                    Icon(
                        imageVector = Icons.Filled.ChevronRight,
                        tint = if (isEditable) MaterialTheme.colors.onSurface else Color.Transparent,
                        contentDescription = stringResource(R.string.reminder_edit_reminder_title),
                        modifier = Modifier
                            .size(28.dp)
                            .weight(.1f)
                            .align(Alignment.CenterVertically)
                            .clickable(isEditable) {
                                onAction(
                                    SetEditMode(
                                        EditMode.ChooseTitleText(
                                            state.reminder?.title ?: "",
                                            editTextStyle = editTextStyle
                                        )
                                    )
                                )
                            }
                    )

                }

                SmallHeightHorizontalDivider()

                // • REMINDER DESCRIPTION
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {

                    Row(
                        modifier = Modifier
                            .weight(1f)
                    ) {
                        Text(
                            text = state.reminder?.description
                                ?: stringResource(R.string.event_no_description_set),
                            style = MaterialTheme.typography.h5,
                            color = MaterialTheme.colors.onSurface,
                            modifier = Modifier
                                .fillMaxWidth()
                                .wrapContentHeight()
                        )
                    }
                    val editTextStyle =
                        MaterialTheme.typography.body1  // can only access in Composable scope
                    Icon(
                        imageVector = Icons.Filled.ChevronRight,
                        tint = if (isEditable) MaterialTheme.colors.onSurface else Color.Transparent,
                        contentDescription = stringResource(R.string.reminder_description_edit_reminder_description),
                        modifier = Modifier
                            .size(28.dp, 28.dp)
                            .weight(.1f)
                            .align(Alignment.CenterVertically)
                            .clickable(enabled = isEditable) {
                                onAction(
                                    SetEditMode(
                                        EditMode.ChooseDescriptionText(
                                            state.reminder?.description ?: "",
                                            editTextStyle = editTextStyle
                                        )
                                    )
                                )
                            }
                    )
                }
            }


            // • REMINDER TIMES & DATES & REMIND AT
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colors.surface)
                    .padding(start = DP.small, end = DP.small)
            ) col3@{

                SmallHeightHorizontalDivider()

                // • TIME/DATE ROW
                TimeDateRow(
                    title = stringResource(R.string.event_from),
                    date = state.reminder?.time ?: ZonedDateTime.now(),
                    isEditable = isEditable ,
                    onEditDate = {
                        onAction(
                            SetEditMode(
                                EditMode.ChooseDate(state.reminder?.time ?: ZonedDateTime.now())
                            )
                        )
                    },
                    onEditTime = {
                        onAction(
                            SetEditMode(
                                EditMode.ChooseTime(state.reminder?.time ?: ZonedDateTime.now())
                            )
                        )
                    }
                )
                SmallHeightHorizontalDivider()

                // • REMIND-AT ROW
                RemindAtRow(
                    fromDateTime = state.reminder?.time ?: ZonedDateTime.now(),
                    remindAtDateTime = state.reminder?.remindAt ?: ZonedDateTime.now(),
                    isEditable = isEditable,
                    isDropdownMenuVisible = state.editMode is EditMode.ChooseRemindAtDateTime,
                    onEditRemindAtDateTime = {
                        onAction(
                            SetEditMode(
                                EditMode.ChooseRemindAtDateTime(
                                    state.reminder?.remindAt ?: ZonedDateTime.now()
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

            }
            Spacer(modifier = Modifier.weight(1.0f))

            // • DELETE REMINDER BUTTON
            val showAlertDialogActionDeleteTitle = UiText.Res(
                R.string.confirm_action_dialog_title_phrase,
                context.getStringSafe(ShowAlertDialogActionType.DeleteReminder.title.asResIdOrNull),
                context.getString(R.string.agenda_item_type_reminder)
            )
            val showAlertDialogActionDeleteMessage = UiText.Res(
                R.string.confirm_action_dialog_text_phrase,
                context.getStringSafe(ShowAlertDialogActionType.DeleteReminder.title.asResIdOrNull).lowercase(),
                context.getString(R.string.agenda_item_type_reminder).lowercase()
            )
            TextButton(
                onClick = {
                    onAction(ShowAlertDialog(
                        title = showAlertDialogActionDeleteTitle,
                        message = showAlertDialogActionDeleteMessage,
                        confirmButtonLabel =  ShowAlertDialogActionType.DeleteReminder.title,
                        onConfirm = {
                            onAction(DeleteReminder)
                        }
                    ))
                },
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Text(
                    stringResource(R.string.reminder_delete_reminder),
                    style = MaterialTheme.typography.h4,
                    color = MaterialTheme.colors.onSurface.copy(alpha = 0.4f),
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                )
            }
            Spacer(modifier = Modifier.mediumHeight())
        }
    }

    // • EDITORS FOR REMINDER PROPERTIES
    AnimatedContent(
        targetState = state.editMode,
        modifier = Modifier.fillMaxSize(),
        transitionSpec = {
            slideInVertically(animationSpec = tween(1000),
                        initialOffsetY = { fullHeight -> fullHeight }
                    ).togetherWith(
                slideOutVertically(animationSpec = tween(1000),
                        targetOffsetY = { fullHeight -> fullHeight }
                    ))
        }
    ) { targetState ->
        targetState?.let { editMode ->
            ReminderPropertyEditors(
                editMode = editMode,
                onAction = onAction,
            )
        }
    }

    state.showAlertDialog?.let { dialogInfo ->
        AlertDialog(
            title = { Text(dialogInfo.title.get) },
            text = { Text(dialogInfo.message.get) },
            onDismissRequest = { onAction(DismissAlertDialog) },
            confirmButton = {
                TextButton(
                    onClick = {
                        onAction(DismissAlertDialog)
                        dialogInfo.onConfirm()
                    },
                    colors = ButtonDefaults
                        .textButtonColors(backgroundColor = Color.Transparent)
                ) {
                    Text(dialogInfo.confirmButtonLabel.get.uppercase())
                }
            },
            dismissButton = {
                if(dialogInfo.isDismissButtonVisible) {
                    TextButton(
                        onClick = { onAction(DismissAlertDialog) },
                        colors = ButtonDefaults
                            .textButtonColors(backgroundColor = Color.Transparent)
                    ) {
                        Text(stringResource(android.R.string.cancel).uppercase())
                    }
                }
            }
        )
    }
}


@Preview(
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    showBackground = true,
    group = "Night Mode=false",
    apiLevel = 28,
    widthDp = 400,
    heightDp = 600,
)
@Composable
fun Preview() {
    TaskyTheme {
        val authInfo = AuthInfo(
            userId = "X0001",
            accessToken = "1010101010101",
            username = "Cameron Anderson",
            refreshToken = "1010101010101",
            email = "cameron@gmail.com",
            accessTokenExpirationTimestampEpochMilli = System.currentTimeMillis() + 1000 * 60 * 60 * 10,
        )

        ReminderScreenPreview(authInfo)
    }
}

@Composable
private fun ReminderScreenPreview(authInfo: AuthInfo) {
    ReminderScreenContent(
        state = ReminderScreenState(
            authInfo = authInfo,
            username = "Cameron Anderson",
            reminder = AgendaItem.Reminder(
                id = "0001",
                title = "Title of Reminder",
                description = "Description of Reminder",
                time = ZonedDateTime.now().plusHours(1),
                remindAt = ZonedDateTime.now().plusMinutes(30),
            )
        ),
        onAction = { println("ACTION: $it") },
        navigator = EmptyDestinationsNavigator,
        oneTimeEvent = null,
    )
}

@Preview(
    uiMode = Configuration.UI_MODE_NIGHT_NO,
    group = "Night Mode=true",
    widthDp = 400,
    heightDp = 600,
    locale = "de"
)
@Composable
fun Preview_night_mode() {
    TaskyTheme {
        val authInfo = AuthInfo(
            userId = "X0001",
            accessToken = "1010101010101",
            username = "Cameron Anderson",
            refreshToken = "1010101010101",
            email = "cameron@gmail.com",
            accessTokenExpirationTimestampEpochMilli = System.currentTimeMillis() + 1000 * 60 * 60 * 10,
        )

        ReminderScreenPreview(authInfo)
    }
}
