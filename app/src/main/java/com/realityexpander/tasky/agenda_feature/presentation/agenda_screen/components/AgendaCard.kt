@file:OptIn(kotlinx.serialization.InternalSerializationApi::class)
package com.realityexpander.tasky.agenda_feature.presentation.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.DropdownMenu
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import com.realityexpander.tasky.R
import com.realityexpander.tasky.agenda_feature.domain.AgendaItem
import com.realityexpander.tasky.agenda_feature.presentation.common.MenuItem
import com.realityexpander.tasky.agenda_feature.presentation.common.util.isUserIdGoingAsAttendee
import com.realityexpander.tasky.auth_feature.domain.AuthInfo
import com.realityexpander.tasky.core.presentation.common.modifiers.DP
import com.realityexpander.tasky.core.presentation.common.modifiers.smallHeight
import com.realityexpander.tasky.core.presentation.common.modifiers.tinyHeight
import com.realityexpander.tasky.core.presentation.theme.TaskyLightBlue
import com.realityexpander.tasky.core.presentation.theme.TaskyPurple
import com.realityexpander.tasky.core.presentation.theme.TaskyShapes
import com.realityexpander.tasky.core.presentation.theme.TaskyTheme
import com.realityexpander.tasky.core.util.accessToken
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.math.roundToInt

@Composable
fun AgendaCard(
    modifier: Modifier = Modifier,
    backgroundColor: Color = MaterialTheme.colors.secondary,
    textColor: Color = MaterialTheme.colors.onSecondary,
    title: String = "",
    description: String? = "",
    fromDateTime: ZonedDateTime = ZonedDateTime.now(),
    toDateTime: ZonedDateTime? = null,
    isTitleCrossedOut: Boolean = false,
    isCompleted: Boolean = false,
    onToggleCompleted: () -> Unit = {},
    itemTypeName: String? = "",
    onEdit: () -> Unit = {},
    onDelete: () -> Unit = {},
    onViewDetails: () -> Unit = {},
    zonedDateTimeNow: ZonedDateTime = ZonedDateTime.now(),
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .clip(shape = TaskyShapes.WideButtonRoundedCorners)
            .background(color = backgroundColor)
            .padding(12.dp)
    ) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
            ) {

                // • Check/Uncheck Icon
                Column(
                    modifier = Modifier
                        .alignByBaseline()
                ) {
                    Icon(
                        imageVector = if (isCompleted)
                                Icons.Filled.TaskAlt // √ with CircleOutline
                            else
                                // is event happening now?
                                if (zonedDateTimeNow.isAfter(fromDateTime)
                                    && toDateTime?.let { zonedDateTimeNow.isBefore(toDateTime) } == true
                                )
                                    Icons.Filled.PlayForWork // Down arrow in a half circle to indicate that the item is in progress
                                else
                                    Icons.Filled.RadioButtonUnchecked, // Circle outline
                        contentDescription = stringResource(R.string.agenda_isDone_icon_description),
                        tint = textColor,
                        modifier = Modifier
                            .align(Alignment.Start)
                            .offset(y = 4.dp)
                            .padding(end = 8.dp)
                            .clickable {
                                onToggleCompleted()
                            }
                    )
                }

                // • Title / Content
                Column(
                    modifier = Modifier
                        .alignByBaseline()
                        .weight(1f)
                ) {

                    if (isTitleCrossedOut || isCompleted) { // hacky way to cross out text
                        Text(
                            text = title,
                            style = MaterialTheme.typography.h3.copy(
                                textDecoration = TextDecoration.LineThrough
                            ),
                            color = textColor
                        )
                    } else {
                        Text(
                            text = title,
                            style = MaterialTheme.typography.h3,
                            color = textColor
                        )
                    }
                    Spacer(modifier = Modifier.tinyHeight())

                    Text(
                        text = description ?: "",
                        color = textColor
                    )
                    Spacer(modifier = Modifier.smallHeight())
                }

                // • Skewer menu
                Column(
                    modifier = Modifier
                        .alignByBaseline()
                ) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.End)
                    ) {
                        var isExpanded by remember { mutableStateOf(false) }

                        Icon(
                            imageVector = Icons.Filled.MoreHoriz,
                            contentDescription = "Agenda Item Menu",
                            tint = textColor,
                            modifier = Modifier
                                .offset(y = DP.micro)
                                .padding(end = DP.tiny)
                                .size(28.dp)
                                .clickable {
                                    isExpanded = !isExpanded
                                }
                        )

                        AgendaItemActionDropdown(
                            isExpanded = isExpanded,
                            onDismissRequest = { isExpanded = false },
                            onEdit = onEdit,
                            onDelete = onDelete,
                            onViewDetails = onViewDetails,
                        )
                    }
                }
            }

            // • Date & Time
            Text(
                text = itemTypeName +" "+
                    (fromDateTime.format(DateTimeFormatter.ofPattern("MMM d, h:mm a"))) +
                    (toDateTime?.let { " - " + it.format(DateTimeFormatter.ofPattern("MMM d, h:mm a")) } ?: ""),
                style = MaterialTheme.typography.subtitle1,
                textAlign = TextAlign.End,
                color = textColor,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(end = DP.extraSmall)
                    .align(Alignment.End)
            )

            // • Progress indicator for (for Events only)
            toDateTime?.let {
                AnimatedVisibility(
            zonedDateTimeNow.isAfter(fromDateTime)
                    && zonedDateTimeNow.isBefore(toDateTime)
                ) {
                    val timeNow = (zonedDateTimeNow.toEpochSecond() - fromDateTime.toEpochSecond()).toFloat()
                    val totalTime = (toDateTime.toEpochSecond() - fromDateTime.toEpochSecond()).toFloat()
                    val percentComplete = ((timeNow / totalTime )*100).roundToInt()

                    Text(
                        text = "In Progress: $percentComplete% Complete",
                        textAlign = TextAlign.End,
                        style = MaterialTheme.typography.subtitle1,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(end = DP.extraSmall)
                            .align(Alignment.End)
                    )
                }
            }
        }
    }
}

// For Event
@Composable
fun AgendaCard(
    modifier: Modifier = Modifier,
    agendaItem: AgendaItem,
    authInfo: AuthInfo,
    onToggleCompleted: () -> Unit = {},
    onEdit: () -> Unit = {},
    onDelete: () -> Unit = {},
    zonedDateTimeNow: ZonedDateTime? = null,
    onViewDetails: () -> Unit = {},
) {
    when(agendaItem) {
        is AgendaItem.Event ->
            AgendaCard(
                modifier = modifier,
                title = agendaItem.title,
                description = agendaItem.description,
                fromDateTime = agendaItem.from,
                toDateTime = agendaItem.to,
                isTitleCrossedOut = !isUserIdGoingAsAttendee(authInfo.userId, agendaItem.attendees),
                itemTypeName = agendaItem::class.java.simpleName,
                onEdit = onEdit,
                onDelete = onDelete,
                onViewDetails = onViewDetails,
                zonedDateTimeNow = zonedDateTimeNow ?: ZonedDateTime.now(),
            )
        is AgendaItem.Task ->
            AgendaCard(
                modifier = modifier,
                backgroundColor = TaskyLightBlue,
                textColor = MaterialTheme.colors.onPrimary,
                title = agendaItem.title,
                description = agendaItem.description,
                fromDateTime = agendaItem.time,
                isCompleted = agendaItem.isDone,
                onToggleCompleted = onToggleCompleted,
                itemTypeName = agendaItem::class.java.simpleName,
                onEdit = onEdit,
                onDelete = onDelete,
                onViewDetails = onViewDetails,
            )
        is AgendaItem.Reminder ->
            AgendaCard(
                modifier = modifier,
                backgroundColor = TaskyPurple,
                textColor = MaterialTheme.colors.onPrimary,
                title = agendaItem.title,
                description = agendaItem.description,
                fromDateTime = agendaItem.time,
                itemTypeName = agendaItem::class.java.simpleName,
                onEdit = onEdit,
                onDelete = onDelete,
                onViewDetails = onViewDetails,
            )
    }
}

@Composable
fun AgendaItemActionDropdown(
    modifier: Modifier = Modifier,
    isExpanded: Boolean = true,
    onEdit: () -> Unit = {},
    onDelete: () -> Unit = {},
    onViewDetails: () -> Unit = {},
    onDismissRequest: () -> Unit = {},
) {
    DropdownMenu(
        expanded = isExpanded,
        onDismissRequest = onDismissRequest,
        offset = DpOffset(0.dp, (-20).dp),
        modifier = modifier
            .wrapContentSize()
            .background(color = MaterialTheme.colors.onSurface)
    ) {
        MenuItem(
            title = "Open",
            vectorIcon = Icons.Filled.OpenInNew,
            onClick = {
                onDismissRequest()
                onViewDetails()
            },
        )
        MenuItem(
            title = "Edit",
            vectorIcon = Icons.Filled.Edit,
            onClick = {
                onDismissRequest()
                onEdit()
            },
        )
        MenuItem(
            title = "Delete",
            vectorIcon = Icons.Filled.Delete,
            onClick = {
                onDismissRequest()
                onDelete()
            },
        )
    }
}

@Preview
@Composable
fun AgendaCardPreview() {
    TaskyTheme {
        AgendaCard(
            authInfo = AuthInfo(
                userId = "123",
                email = "chris@demo.com",
                username = "Chris",
                accessToken = accessToken("123"),
                refreshToken = "123",
                accessTokenExpirationTimestampEpochMilli = System.currentTimeMillis() + 1000000,
            ),
            agendaItem = AgendaItem.Event(
                id = UUID.randomUUID().toString(),
                title = "Event Title",
                description = "Event Description",
                from = ZonedDateTime.now(),
                to = ZonedDateTime.now().plusHours(1),
                remindAt = ZonedDateTime.now().minusHours(1),
            ),
            onEdit = {},
            onDelete = {},
        ) {}
    }
}
