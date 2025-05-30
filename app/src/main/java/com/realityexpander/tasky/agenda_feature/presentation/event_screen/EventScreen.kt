package com.realityexpander.tasky.agenda_feature.presentation.event_screen

import android.content.res.Configuration
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Circle
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight.Companion.Bold
import androidx.compose.ui.text.font.FontWeight.Companion.SemiBold
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import coil.compose.SubcomposeAsyncImage
import com.ramcosta.composedestinations.annotation.DeepLink
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.navigation.EmptyDestinationsNavigator
import com.realityexpander.tasky.R
import com.realityexpander.tasky.agenda_feature.data.common.utils.isImageSizeTooLargeToUpload
import com.realityexpander.tasky.agenda_feature.domain.AgendaItem
import com.realityexpander.tasky.agenda_feature.domain.Attendee
import com.realityexpander.tasky.agenda_feature.domain.Photo
import com.realityexpander.tasky.agenda_feature.presentation.common.components.RemindAtRow
import com.realityexpander.tasky.agenda_feature.presentation.common.components.SmallHeightHorizontalDivider
import com.realityexpander.tasky.agenda_feature.presentation.common.components.TimeDateRow
import com.realityexpander.tasky.agenda_feature.presentation.common.util.isUserIdGoingAsAttendee
import com.realityexpander.tasky.agenda_feature.presentation.common.util.toLongMonthDayYear
import com.realityexpander.tasky.agenda_feature.presentation.event_screen.EventScreenEvent.CancelEditMode
import com.realityexpander.tasky.agenda_feature.presentation.event_screen.EventScreenEvent.DeleteEvent
import com.realityexpander.tasky.agenda_feature.presentation.event_screen.EventScreenEvent.DismissAlertDialog
import com.realityexpander.tasky.agenda_feature.presentation.event_screen.EventScreenEvent.EditMode
import com.realityexpander.tasky.agenda_feature.presentation.event_screen.EventScreenEvent.JoinEvent
import com.realityexpander.tasky.agenda_feature.presentation.event_screen.EventScreenEvent.LeaveEvent
import com.realityexpander.tasky.agenda_feature.presentation.event_screen.EventScreenEvent.OneTimeEvent
import com.realityexpander.tasky.agenda_feature.presentation.event_screen.EventScreenEvent.OneTimeEvent.LaunchPhotoPicker
import com.realityexpander.tasky.agenda_feature.presentation.event_screen.EventScreenEvent.OneTimeEvent.NavigateBack
import com.realityexpander.tasky.agenda_feature.presentation.event_screen.EventScreenEvent.OneTimeEvent.ShowToast
import com.realityexpander.tasky.agenda_feature.presentation.event_screen.EventScreenEvent.SaveEvent
import com.realityexpander.tasky.agenda_feature.presentation.event_screen.EventScreenEvent.SetEditMode
import com.realityexpander.tasky.agenda_feature.presentation.event_screen.EventScreenEvent.SetIsEditable
import com.realityexpander.tasky.agenda_feature.presentation.event_screen.EventScreenEvent.ShowAlertDialog
import com.realityexpander.tasky.agenda_feature.presentation.event_screen.components.AttendeeList
import com.realityexpander.tasky.agenda_feature.presentation.event_screen.components.PillButton
import com.realityexpander.tasky.auth_feature.domain.AuthInfo
import com.realityexpander.tasky.core.data.isAvailable
import com.realityexpander.tasky.core.presentation.animatedTransitions.ScreenTransitions
import com.realityexpander.tasky.core.presentation.common.modifiers.DP
import com.realityexpander.tasky.core.presentation.common.modifiers.extraSmallHeight
import com.realityexpander.tasky.core.presentation.common.modifiers.extraSmallWidth
import com.realityexpander.tasky.core.presentation.common.modifiers.largeHeight
import com.realityexpander.tasky.core.presentation.common.modifiers.mediumHeight
import com.realityexpander.tasky.core.presentation.common.modifiers.smallHeight
import com.realityexpander.tasky.core.presentation.common.modifiers.smallWidth
import com.realityexpander.tasky.core.presentation.common.modifiers.taskyScreenTopCorners
import com.realityexpander.tasky.core.presentation.theme.TaskyLightGreen
import com.realityexpander.tasky.core.presentation.theme.TaskyTheme
import com.realityexpander.tasky.core.presentation.util.UiText
import com.realityexpander.tasky.core.presentation.util.getStringSafe
import com.realityexpander.tasky.core.util.UPLOAD_IMAGE_MAX_SIZE
import com.realityexpander.tasky.core.util.UuidStr
import kotlinx.coroutines.launch
import java.time.ZonedDateTime
import java.util.UUID

@Destination(
    route = "event",
    deepLinks = [
        DeepLink(uriPattern = "https://realityexpander.com/event/{initialEventId}")
    ],
    style = ScreenTransitions::class
)
@Composable
fun EventScreen(
    @Suppress("UNUSED_PARAMETER")  // extracted from navArgs in the viewModel
    initialEventId: UuidStr? = null,
    @Suppress("UNUSED_PARAMETER")  // extracted from navArgs in the viewModel
    isEditable: Boolean = false,
    @Suppress("UNUSED_PARAMETER")  // extracted from navArgs in the viewModel
    startDate: ZonedDateTime? = ZonedDateTime.now(),
    navigator: DestinationsNavigator,
    viewModel: EventViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsState()
    val oneTimeEvent by viewModel.oneTimeEvent.collectAsState(null)

    if (state.isLoaded) {
        AddEventScreenContent(
            state = state,
            oneTimeEvent = oneTimeEvent,
            onAction = viewModel::sendEvent,
            navigator = navigator,
            startDate = startDate ?: ZonedDateTime.now(),
        )
    }

    // Show error messages for deep links
    state.errorMessage?.let { errorMessage ->
        if (!state.isLoaded) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Tasky logo image
                AsyncImage(
                    model = R.drawable.tasky_logo_for_splash,
                    contentDescription = null,
                    modifier = Modifier
                        .size(128.dp)
                        .clip(RoundedCornerShape(8.dp)),
                    contentScale = ContentScale.Crop,
                )

                Text(
                    text = errorMessage.get,
                    style = MaterialTheme.typography.h5,
                    textAlign = TextAlign.Center,
                )
            }
        }
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

const val ADD_PHOTO_PLACEHOLDER = "ADD_PHOTO_PLACEHOLDER"

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun AddEventScreenContent(
    state: EventScreenState,
    oneTimeEvent: OneTimeEvent?,
    onAction: (EventScreenEvent) -> Unit,
    navigator: DestinationsNavigator,
    startDate: ZonedDateTime,
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var attendeeListType by remember { mutableStateOf(AttendeeListType.ALL) }
    val isEditable = state.isEditable
    val isUserEventCreator = state.event?.isUserEventCreator == true

    fun popBack() {
        navigator.popBackStack()
    }

    BackHandler(true) {
        if (state.editMode != null) {
            scope.launch {
                onAction(CancelEditMode)
            }
        } else {
            popBack()
        }
    }

    val singlePhotoPickerLauncher =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.PickVisualMedia(),
            onResult = { photoUri ->

                onAction(CancelEditMode)

                photoUri?.let { uri ->

                    if (uri.isImageSizeTooLargeToUpload(context, UPLOAD_IMAGE_MAX_SIZE)) {
                        onAction(
                            ShowAlertDialog(
                                title = UiText.Res(R.string.event_error_image_too_big_title),
                                message = UiText.Res(R.string.event_error_image_too_big_message),
                                confirmButtonLabel = ShowAlertDialogActionType.ConfirmOK.title,
                                onConfirm = {
                                    onAction(DismissAlertDialog)
                                },
                                isDismissButtonVisible = false,
                            )
                        )

                        return@let
                    }

                    onAction(
                        EditMode.AddLocalPhoto(
                            Photo.Local(
                                id = UUID.randomUUID().toString(),
                                uri = uri
                            )
                        )
                    )
                }
            }
        )

    // • One-time events (like Navigation, Toasts, etc) are handled here
    LaunchedEffect(oneTimeEvent) {
        when (oneTimeEvent) {
            is NavigateBack -> {
                popBack()
            }

            is ShowToast -> {
                Toast.makeText(
                    context,
                    context.getString(
                        oneTimeEvent.message.asResIdOrNull
                            ?: R.string.error_invalid_string_resource_id
                    ), Toast.LENGTH_SHORT
                ).show()
            }

            is LaunchPhotoPicker -> {
                // Reference: https://github.com/philipplackner/NewPhotoPickerAndroid13/blob/master/app/src/main/java/com/plcoding/newphotopickerandroid13/MainActivity.kt
                singlePhotoPickerLauncher.launch(
                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                )
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
                        // Check Uri's of local photos are still available (they may have been deleted before the user pressed Save)
                        val unavailableLocalPhotos =
                            state.event?.photos
                                ?.filterIsInstance<Photo.Local>()
                                ?.filterNot { photoLocal ->
                                    photoLocal.uri.isAvailable(context)
                                }
                        if (unavailableLocalPhotos?.isNotEmpty() == true) {
                            // Remove the missing photos
                            unavailableLocalPhotos.forEach { photoLocal ->
                                onAction(EditMode.RemovePhoto(photoLocal))
                            }

                            // Tell user that some photos were removed
                            onAction(
                                ShowAlertDialog(
                                    title = UiText.Res(R.string.event_error_image_file_missing_title),
                                    message = UiText.Res(R.string.event_error_image_file_missing_message),
                                    confirmButtonLabel = ShowAlertDialogActionType.ConfirmOK.title,
                                    onConfirm = {
                                        onAction(DismissAlertDialog)
                                    },
                                    isDismissButtonVisible = false,
                                )
                            )

                            return@TextButton
                        }

                        onAction(SetIsEditable(false))
                        onAction(SaveEvent)
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
                .taskyScreenTopCorners(color = MaterialTheme.colors.surface)
                .verticalScroll(rememberScrollState())
        ) col2@{

            // • EVENT TITLE & DESCRIPTION
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
                            contentDescription = stringResource(R.string.description_title_marker),
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

                    val editTextStyle =
                        MaterialTheme.typography.h2  // can only access in Composable scope
                    Icon(
                        imageVector = Icons.Filled.ChevronRight,
                        tint = if (isEditable && isUserEventCreator) MaterialTheme.colors.onSurface else Color.Transparent,
                        contentDescription = stringResource(R.string.event_edit_event_title),
                        modifier = Modifier
                            .size(28.dp)
                            .weight(.1f)
                            .align(Alignment.CenterVertically)
                            .clickable(isEditable && isUserEventCreator) {
                                onAction(
                                    SetEditMode(
                                        EditMode.ChooseTitleText(
                                            state.event?.title ?: "",
                                            editTextStyle = editTextStyle
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
                            text = state.event?.description
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
                        tint = if (isEditable && isUserEventCreator) MaterialTheme.colors.onSurface else Color.Transparent,
                        contentDescription = stringResource(R.string.event_description_edit_event_description),
                        modifier = Modifier
                            .size(28.dp, 28.dp)
                            .weight(.1f)
                            .align(Alignment.CenterVertically)
                            .clickable(enabled = isEditable && isUserEventCreator) {
                                onAction(
                                    SetEditMode(
                                        EditMode.ChooseDescriptionText(
                                            state.event?.description ?: "",
                                            editTextStyle = editTextStyle
                                        )
                                    )
                                )
                            }
                    )
                }
            }

            // • PHOTO PICKER / ADD & REMOVE PHOTOS
            if ((isEditable && isUserEventCreator)
                || !state.event?.photos.isNullOrEmpty()
            ) {
                Spacer(modifier = Modifier.smallHeight())

                Box(
                    modifier = Modifier
                        .background(MaterialTheme.colors.onSurface.copy(alpha = .1f))
                        .fillMaxWidth()
                        .padding(DP.small)
                        .border(0.dp, Color.Transparent)
                        .wrapContentHeight()
                ) {

                    if ((isEditable && isUserEventCreator)
                        && state.event.photos.isEmpty()
                    ) {
                        // • NO PHOTOS
                        Row(
                            horizontalArrangement = Arrangement.Center,
                            modifier = Modifier
                                .fillMaxWidth()
                                .align(Alignment.Center)
                                .padding(top = DP.medium, bottom = DP.medium)
                                .clickable {
                                    onAction(
                                        SetEditMode(
                                            EditMode.ChooseAddPhoto()
                                        )
                                    )
                                    onAction(SetIsEditable(true)) // turn on edit mode
                                }
                        ) {
                            // • ADD PHOTO ICON
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
                        if ((isEditable && isUserEventCreator)
                            || state.event.photos.isNotEmpty()
                        ) {
                            // • LIST OF PHOTO IMAGES
                            Column(
                                modifier = Modifier
                                    .wrapContentHeight()
                            ) {
                                // • PHOTOS HEADER
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

                                // • PHOTO ITEMS
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .wrapContentHeight()
                                        .horizontalScroll(state = rememberScrollState())
                                ) {
                                    var photoList = state.event.photos

                                    // Add the "Add Photo" button if in edit mode
                                    if (isEditable && isUserEventCreator) {
                                        if (photoList.isEmpty()) {
                                            photoList = listOf(
                                                Photo.Local(
                                                    ADD_PHOTO_PLACEHOLDER,
                                                    uri = Uri.EMPTY
                                                )
                                            )
                                        } else {
                                            // Max 10 photos
                                            if (photoList.size <= 10) {
                                                photoList = photoList.plus(
                                                    Photo.Local(
                                                        ADD_PHOTO_PLACEHOLDER,
                                                        uri = Uri.EMPTY
                                                    )
                                                )
                                            }
                                        }
                                    }

                                    photoList.forEach { photo ->
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
                                            if (photo.id == ADD_PHOTO_PLACEHOLDER) {
                                                // • ADD PHOTO ICON
                                                Icon(
                                                    imageVector = Icons.Filled.Add,
                                                    tint = MaterialTheme.colors.onSurface.copy(
                                                        alpha = .3f
                                                    ),
                                                    contentDescription = stringResource(R.string.event_description_add_photo),
                                                    modifier = Modifier
                                                        .size(36.dp)
                                                        .align(Alignment.Center)
                                                        .clickable(isEditable && isUserEventCreator) {
                                                            onAction(
                                                                SetEditMode(
                                                                    EditMode.ChooseAddPhoto()
                                                                )
                                                            )
                                                        }
                                                )
                                            } else {
                                                // • PHOTO IMAGE
                                                SubcomposeAsyncImage(
                                                    model = when (photo) {
                                                        is Photo.Local -> photo.uri
                                                        is Photo.Remote -> photo.url
                                                    },
                                                    contentDescription = stringResource(id = R.string.event_description_photo),
                                                    contentScale = ContentScale.Crop,
                                                    onError = {
                                                        onAction(
                                                            ShowAlertDialog(
                                                                title = UiText.Res(R.string.event_error_image_file_missing_title),
                                                                message = UiText.Res(R.string.event_error_image_file_missing_message),
                                                                confirmButtonLabel = ShowAlertDialogActionType.ConfirmOK.title,
                                                                onConfirm = {
                                                                    onAction(DismissAlertDialog)
                                                                },
                                                                isDismissButtonVisible = false,
                                                            )
                                                        )
                                                    },
                                                    loading = {
                                                        CircularProgressIndicator(
                                                            color = MaterialTheme.colors.primary,
                                                            modifier = Modifier
                                                                .align(Alignment.Center)
                                                                .padding(10.dp)
                                                        )
                                                    },
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .clickable {
                                                            onAction(
                                                                SetEditMode(
                                                                    EditMode.ViewOrRemovePhoto(
                                                                        photo
                                                                    )
                                                                )
                                                            )
                                                        },
                                                )
                                            }
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
                }
                Spacer(modifier = Modifier.smallHeight())
            }


            // • EVENT TIMES & DATES & JOIN/DELETE/LEAVE (FROM, TO, REMIND AT)
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
                    date = state.event?.from ?: startDate,
                    isEditable = isEditable && isUserEventCreator,
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
                    date = state.event?.to ?: startDate,
                    isEditable = isEditable && isUserEventCreator,
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
                    fromDateTime = state.event?.from ?: startDate,
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
                        tint = if (isEditable && isUserEventCreator) MaterialTheme.colors.onSurface.copy(
                            alpha = .3f
                        ) else Color.Transparent,
                        contentDescription = stringResource(R.string.event_description_add_attendee_button),
                        modifier = Modifier
                            .offset(y = (-4).dp)
                            .size(38.dp)
                            .clip(shape = RoundedCornerShape(5.dp))
                            .background(
                                if (isEditable && isUserEventCreator)
                                    MaterialTheme.colors.onSurface.copy(alpha = .1f)
                                else
                                    Color.Transparent
                            )
                            .padding(4.dp)
                            .align(Alignment.CenterVertically)
                            .clickable(enabled = isEditable && isUserEventCreator) {
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
                        isUserEventCreator = state.event?.isUserEventCreator ?: false,
                        header = stringResource(R.string.event_going),
                        attendees = state.event?.attendees?.filter { it.isGoing } ?: emptyList(),
                        hostUserId = state.event?.host
                            ?: throw IllegalStateException("event host not found"),
                        onAttendeeClick = {},
                        onAttendeeRemoveClick = { attendee ->
                            onAction(
                                SetEditMode(
                                    EditMode.ConfirmRemoveAttendee(attendee)
                                )
                            )
                        },
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
                        isUserEventCreator = state.event?.isUserEventCreator ?: false,
                        header = stringResource(R.string.event_not_going),
                        attendees = state.event?.attendees?.filterNot { it.isGoing } ?: emptyList(),
                        hostUserId = state.event?.host
                            ?: throw IllegalStateException("event host not found"),
                        onAttendeeClick = {},
                        onAttendeeRemoveClick = { attendee ->
                            onAction(
                                SetEditMode(
                                    EditMode.ConfirmRemoveAttendee(attendee)
                                )
                            )
                        },
                    )
                }

                Spacer(modifier = Modifier.largeHeight())

                // • JOIN/LEAVE/DELETE EVENT BUTTON
                val showAlertDialogActionDeleteTitle = UiText.Res(
                    R.string.confirm_action_dialog_title_phrase,
                    context.getStringSafe(ShowAlertDialogActionType.DeleteEvent.title.asResIdOrNull),
                    context.getString(R.string.agenda_item_type_event)
                )
                val showAlertDialogActionDeleteMessage = UiText.Res(
                    R.string.confirm_action_dialog_text_phrase,
                    context.getStringSafe(ShowAlertDialogActionType.DeleteEvent.title.asResIdOrNull)
                        .lowercase(),
                    context.getString(R.string.agenda_item_type_event).lowercase()
                )
                TextButton(
                    onClick = {
                        if (isUserEventCreator)
                            onAction(
                                ShowAlertDialog(
                                    title = showAlertDialogActionDeleteTitle,
                                    message = showAlertDialogActionDeleteMessage,
                                    confirmButtonLabel = ShowAlertDialogActionType.DeleteEvent.title,
                                    onConfirm = {
                                        onAction(DeleteEvent)
                                    }
                                ))
                        else if (isUserIdGoingAsAttendee(
                                state.authInfo?.userId
                                    ?: throw IllegalStateException("user not logged in"),
                                state.event?.attendees
                            )
                        ) {
                            onAction(SetIsEditable(true))
                            onAction(LeaveEvent)
                        } else {
                            onAction(SetIsEditable(true))
                            onAction(JoinEvent)
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    Text(
                        if (state.event?.isUserEventCreator == true)
                            stringResource(R.string.event_delete_event)
                        else if (isUserIdGoingAsAttendee(
                                state.authInfo?.userId
                                    ?: throw IllegalStateException("user not logged in"),
                                state.event?.attendees
                            )
                        )
                            stringResource(R.string.event_leave_event)
                        else
                            stringResource(R.string.event_join_event),
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
    }

    // • EDITORS FOR EVENT PROPERTIES
    AnimatedContent(
        targetState = state.editMode,
        modifier = Modifier.fillMaxSize(),
        transitionSpec = {
            slideInVertically(
                animationSpec = tween(1000),
                initialOffsetY = { fullHeight -> fullHeight }
            ).togetherWith(
                slideOutVertically(
                    animationSpec = tween(1000),
                    targetOffsetY = { fullHeight -> fullHeight }
                ))
        }
    ) { targetState ->
        targetState?.let { editMode ->
            EventPropertyEditors(
                editMode = editMode,
                state = state,
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
                if (dialogInfo.isDismissButtonVisible) {
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
    group = "Night Mode=true",
    widthDp = 500,
    heightDp = 1000,
    showBackground = true,
    // locale = "de"
)
@Composable
fun Preview() {
    val previewContext = LocalContext.current

    TaskyTheme {
        CompositionLocalProvider(
            LocalContext provides previewContext
        ) {
            AddEventScreenPreviewContent()
        }
    }
}

@Composable
private fun AddEventScreenPreviewContent() {
    val authInfo = AuthInfo(
        userId = "X0001",
        accessToken = "1010101010101",
        username = "Cameron Anderson",
        email = "cameraon@dewmo.com",
        refreshToken = "1010101010101",
        accessTokenExpirationTimestampEpochMilli = System.currentTimeMillis() + 1000000,
    )

    @Suppress("SpellCheckingInspection")
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
                remindAt = ZonedDateTime.now().minusMinutes(10),
                host = authInfo.userId,
                attendees = listOf(
                    Attendee(
                        eventId = "0001",
                        isGoing = true,
                        fullName = authInfo.username!!,
                        email = "cameron@demo.com",
                        remindAt = ZonedDateTime.now(),
                        id = authInfo.userId!!,
                        photo = "https://randomuser.me/api/portraits/men/75.jpg",
                    ),
                    Attendee(
                        eventId = "0001",
                        isGoing = true,
                        fullName = "Jeremy Johnson",
                        remindAt = ZonedDateTime.now(),
                        email = "jj@demo.com",
                        id = UUID.randomUUID().toString(),
                        photo = "https://randomuser.me/api/portraits/men/75.jpg",
                    ),
                    Attendee(
                        eventId = "0001",
                        isGoing = true,
                        fullName = "Fred Flintstone",
                        remindAt = ZonedDateTime.now(),
                        email = "ff@demo.com",
                        id = UUID.randomUUID().toString(),
                        photo = "https://randomuser.me/api/portraits/men/71.jpg",
                    ),
                    Attendee(
                        eventId = "0001",
                        isGoing = true,
                        fullName = "Sam Bankman",
                        remindAt = ZonedDateTime.now(),
                        email = "sb@demo.com",
                        id = UUID.randomUUID().toString(),
                        photo = "https://randomuser.me/api/portraits/men/70.jpg",
                    ),
                ),
            )
        ),
        oneTimeEvent = null,
        onAction = { println("ACTION: $it") },
        navigator = EmptyDestinationsNavigator,
        startDate = ZonedDateTime.now(),
    )
}

@Preview(
    uiMode = Configuration.UI_MODE_NIGHT_NO,
    group = "Night Mode=false",
    widthDp = 500,
    heightDp = 1200,
    locale = "de"
)
@Composable
fun Preview_night_mode_de() {
    val previewContext = LocalContext.current

    TaskyTheme {
        CompositionLocalProvider(
            LocalContext provides previewContext
        ) {
            AddEventScreenPreviewContent()
        }
    }
}
