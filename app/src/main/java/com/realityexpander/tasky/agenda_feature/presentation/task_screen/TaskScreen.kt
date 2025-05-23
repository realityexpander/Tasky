package com.realityexpander.tasky.agenda_feature.presentation.task_screen

import android.content.res.Configuration
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.AlertDialog
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.TaskAlt
import androidx.compose.material.icons.outlined.Circle
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight.Companion.SemiBold
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.navigation.EmptyDestinationsNavigator
import com.realityexpander.tasky.R
import com.realityexpander.tasky.agenda_feature.domain.AgendaItem
import com.realityexpander.tasky.agenda_feature.presentation.common.components.RemindAtRow
import com.realityexpander.tasky.agenda_feature.presentation.common.components.SmallHeightHorizontalDivider
import com.realityexpander.tasky.agenda_feature.presentation.common.components.TimeDateRow
import com.realityexpander.tasky.agenda_feature.presentation.common.util.toLongMonthDayYear
import com.realityexpander.tasky.agenda_feature.presentation.task_screen.TaskScreenEvent.*
import com.realityexpander.tasky.auth_feature.domain.AuthInfo
import com.realityexpander.tasky.core.presentation.animatedTransitions.ScreenTransitions
import com.realityexpander.tasky.core.presentation.common.modifiers.DP
import com.realityexpander.tasky.core.presentation.common.modifiers.extraSmallWidth
import com.realityexpander.tasky.core.presentation.common.modifiers.mediumHeight
import com.realityexpander.tasky.core.presentation.common.modifiers.smallHeight
import com.realityexpander.tasky.core.presentation.common.modifiers.smallWidth
import com.realityexpander.tasky.core.presentation.common.modifiers.taskyScreenTopCorners
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
fun TaskScreen(
    @Suppress("UNUSED_PARAMETER")  // extracted from navArgs in the viewModel
    initialTaskId: UuidStr? = null,
    @Suppress("UNUSED_PARAMETER")  // extracted from navArgs in the viewModel
    isEditable: Boolean = false,
    @Suppress("UNUSED_PARAMETER")  // extracted from navArgs in the viewModel
    startDate : ZonedDateTime? = ZonedDateTime.now(),
    navigator: DestinationsNavigator = EmptyDestinationsNavigator,
    viewModel: TaskViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsState()
    val oneTimeEvent by viewModel.oneTimeEvent.collectAsState(null)

    if (state.isLoaded) {
        TaskScreenContent(
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
fun TaskScreenContent(
    state: TaskScreenState,
    oneTimeEvent: OneTimeEvent?,
    onAction: (TaskScreenEvent) -> Unit,
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
                    contentDescription = stringResource(R.string.event_description_close),
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
                            onAction(SaveTask)
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
                                contentDescription = stringResource(R.string.event_description_edit_event),
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


        // • EVENT HEADER & MAIN CONTENT
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .taskyScreenTopCorners(color = MaterialTheme.colors.surface)
                .verticalScroll(rememberScrollState())
        ) col2@{

            // • TASK TITLE & DESCRIPTION
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
                        stringResource(R.string.task_task_title),
                        fontWeight = SemiBold,
                        color = MaterialTheme.colors.onSurface,
                        modifier = Modifier
                            .align(Alignment.CenterVertically)
                    )
                }
                Spacer(modifier = Modifier.smallHeight())

                // • TASK TITLE
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {

                    Row(
                        modifier = Modifier
                            .weight(1f)
                    ) {

                        // • Visual Circle
                        Icon(
                            imageVector = if (state.task?.isDone == true)
                                    Icons.Filled.TaskAlt // √ with CircleOutline
                                        else
                                    Icons.Outlined.Circle,
                            tint = MaterialTheme.colors.onSurface,
                            contentDescription = stringResource(R.string.description_title_marker),
                            modifier = Modifier
                                .size(26.dp)
                                .offset(0.dp, 8.dp)
                                .align(Alignment.Top)
                                .clickable {
                                    onAction(ToggleIsDone)
                                }
                        )
                        Spacer(modifier = Modifier.extraSmallWidth())
                        if (state.task?.isDone == true) { // hack to get the `line-through` to work
                            Text(
                                text = state.task.title,
                                color = MaterialTheme.colors.onSurface,
                                fontWeight = SemiBold,
                                style = MaterialTheme.typography.h2,
                                textDecoration = TextDecoration.LineThrough,
                                modifier = Modifier
                                    .align(Alignment.CenterVertically)
                                    .weight(1f)
                            )
                        } else {
                            Text(
                                text = state.task?.title ?: "",
                                color = MaterialTheme.colors.onSurface,
                                fontWeight = SemiBold,
                                style = MaterialTheme.typography.h2,
                                modifier = Modifier
                                    .align(Alignment.CenterVertically)
                                    .weight(1f)
                            )
                        }
                    }

                    val editTextStyle =
                        MaterialTheme.typography.h2  // can only access in Composable scope
                    Icon(
                        imageVector = Icons.Filled.ChevronRight,
                        tint = if (isEditable) MaterialTheme.colors.onSurface else Color.Transparent,
                        contentDescription = stringResource(R.string.event_edit_event_title),
                        modifier = Modifier
                            .size(28.dp)
                            .weight(.1f)
                            .align(Alignment.CenterVertically)
                            .clickable(isEditable) {
                                onAction(
                                    SetEditMode(
                                        EditMode.ChooseTitleText(
                                            state.task?.title ?: "",
                                            editTextStyle = editTextStyle
                                        )
                                    )
                                )
                            }
                    )

                }

                SmallHeightHorizontalDivider()

                // • TASK DESCRIPTION
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {

                    Row(
                        modifier = Modifier
                            .weight(1f)
                    ) {
                        Text(
                            text = state.task?.description
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
                        contentDescription = stringResource(R.string.event_description_edit_event_description),
                        modifier = Modifier
                            .size(28.dp, 28.dp)
                            .weight(.1f)
                            .align(Alignment.CenterVertically)
                            .clickable(enabled = isEditable) {
                                onAction(
                                    SetEditMode(
                                        EditMode.ChooseDescriptionText(
                                            state.task?.description ?: "",
                                            editTextStyle = editTextStyle
                                        )
                                    )
                                )
                            }
                    )
                }
            }


            // • TASK TIMES & DATES & REMIND AT
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colors.surface)
                    .padding(start = DP.small, end = DP.small),
            ) col3@{

                SmallHeightHorizontalDivider()

                // • TIME/DATE ROW
                TimeDateRow(
                    title = stringResource(R.string.event_from),
                    date = state.task?.time ?: ZonedDateTime.now(),
                    isEditable = isEditable ,
                    onEditDate = {
                        onAction(
                            SetEditMode(
                                EditMode.ChooseDate(state.task?.time ?: ZonedDateTime.now())
                            )
                        )
                    },
                    onEditTime = {
                        onAction(
                            SetEditMode(
                                EditMode.ChooseTime(state.task?.time ?: ZonedDateTime.now())
                            )
                        )
                    }
                )
                SmallHeightHorizontalDivider()

                // • REMIND AT ROW
                RemindAtRow(
                    fromDateTime = state.task?.time ?: ZonedDateTime.now(),
                    remindAtDateTime = state.task?.remindAt ?: ZonedDateTime.now(),
                    isEditable = isEditable,
                    isDropdownMenuVisible = state.editMode is EditMode.ChooseRemindAtDateTime,
                    onEditRemindAtDateTime = {
                        onAction(
                            SetEditMode(
                                EditMode.ChooseRemindAtDateTime(
                                    state.task?.remindAt ?: ZonedDateTime.now()
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

            // • DELETE TASK BUTTON
            val showAlertDialogActionDeleteTitle = UiText.Res(
                R.string.confirm_action_dialog_title_phrase,
                context.getStringSafe(ShowAlertDialogActionType.DeleteTask.title.asResIdOrNull),
                context.getString(R.string.agenda_item_type_task)
            )
            val showAlertDialogActionDeleteMessage = UiText.Res(
                R.string.confirm_action_dialog_text_phrase,
                context.getStringSafe(ShowAlertDialogActionType.DeleteTask.title.asResIdOrNull).lowercase(),
                context.getString(R.string.agenda_item_type_task).lowercase()
            )
            TextButton(
                onClick = {
                    onAction(ShowAlertDialog(
                        title = showAlertDialogActionDeleteTitle,
                        message = showAlertDialogActionDeleteMessage,
                        confirmButtonLabel =  ShowAlertDialogActionType.DeleteTask.title,
                        onConfirm = {
                            onAction(DeleteTask)
                        }
                    ))
                },
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Text(
                    stringResource(R.string.task_delete_task),
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

    // • EDITORS FOR TASK PROPERTIES
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
            TaskPropertyEditors(
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


@Composable
private fun PreviewContent() {
    val authInfo = AuthInfo(
        userId = "X0001",
        accessToken = "1010101010101",
        username = "Cameron Anderson",
        email = "cameron@gmail.com",
        refreshToken = "1010101010101",
        accessTokenExpirationTimestampEpochMilli = System.currentTimeMillis() + 1000000,
    )

    TaskScreenContent(
        state = TaskScreenState(
            authInfo = authInfo,
            username = "Cameron Anderson",
            task = AgendaItem.Task(
                id = "0001",
                title = "Title of Task",
                description = "Description of Task",
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
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    group = "Night Mode=true",
    widthDp = 400,
    heightDp = 500,
//    backgroundColor = 0xFF000000,
)
@Composable
fun Preview_night_mode() {
    TaskyTheme {
        PreviewContent()
    }
}

@Preview(
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_NO,
    group = "Night Mode=false",
    apiLevel = 28,
    widthDp = 400,
    heightDp = 500,
    locale = "de"
)
@Composable
fun Preview1() {
    TaskyTheme {
        PreviewContent()
    }
}
