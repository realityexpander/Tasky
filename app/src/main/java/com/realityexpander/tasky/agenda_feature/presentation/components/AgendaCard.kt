package com.realityexpander.tasky.agenda_feature.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material.icons.filled.TaskAlt
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.realityexpander.tasky.agenda_feature.domain.AgendaItem
import com.realityexpander.tasky.core.presentation.common.modifiers.DP
import com.realityexpander.tasky.core.presentation.common.modifiers.smallHeight
import com.realityexpander.tasky.core.presentation.common.modifiers.tinyHeight
import com.realityexpander.tasky.core.presentation.theme.TaskyLightBlue
import com.realityexpander.tasky.core.presentation.theme.TaskyPurple
import com.realityexpander.tasky.core.presentation.theme.TaskyShapes
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Composable
fun AgendaCard(
    modifier: Modifier = Modifier,
    backgroundColor: Color = MaterialTheme.colors.secondary,
    textColor: Color = MaterialTheme.colors.onSecondary,
    title: String = "",
    description: String? = "",
    fromDateTime: LocalDateTime = LocalDateTime.MIN,
    toDateTime: LocalDateTime? = null,
    onMenuClick: () -> Unit = {},
    completed: Boolean? = null,
    onToggleCompleted: () -> Unit = {},
    setMenuPositionCallback : (LayoutCoordinates) -> Unit = {},
    itemTypeName: String? = "",
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .clip(shape = TaskyShapes.WideButtonRoundedCorners)
            .background(color = backgroundColor)
            .padding(12.dp)
    ) {
        Column() {
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
                        imageVector = if (completed == null || !completed)
                            Icons.Filled.RadioButtonUnchecked
                        else
                            Icons.Filled.TaskAlt, // CheckCircleOutline
                        contentDescription = "Event",
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

                    Text(
                        text = title,
                        style = MaterialTheme.typography.h3.copy(
                            textDecoration = if (completed == null || !completed)
                                TextDecoration.None
                            else
                                TextDecoration.LineThrough,
                        ),
                        color = textColor
                    )
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
                    Icon(
                        imageVector = Icons.Filled.MoreHoriz,
                        contentDescription = "Agenda Item Menu",
                        tint = textColor,
                        modifier = Modifier
                            .align(Alignment.End)
                            .offset(y = DP.micro)
                            .padding(end = DP.tiny)
                            .clickable(onClick = onMenuClick)
                            .size(28.dp)
                            .onGloballyPositioned { setMenuPositionCallback(it) }
                    )
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
        }
    }
}

// For Event
@Composable
fun AgendaCard(
    modifier: Modifier = Modifier,
    agendaItem: AgendaItem,
    onMenuClick: () -> Unit = {},
    setMenuPositionCallback : (LayoutCoordinates) -> Unit = {},
    onToggleCompleted: () -> Unit = {},
) {
    when(agendaItem) {
        is AgendaItem.Event ->
            AgendaCard(
                modifier = modifier,
                title = agendaItem.title,
                description = agendaItem.description,
                fromDateTime = agendaItem.from,
                toDateTime = agendaItem.to,
                onMenuClick = onMenuClick,
                completed = null,
                setMenuPositionCallback = setMenuPositionCallback,
                itemTypeName = agendaItem::class.java.simpleName,
            )
        is AgendaItem.Task ->
            AgendaCard(
                modifier = modifier,
                backgroundColor = TaskyLightBlue,
                textColor = MaterialTheme.colors.onPrimary,
                title = agendaItem.title,
                description = agendaItem.description,
                fromDateTime = agendaItem.time,
                onMenuClick = onMenuClick,
                completed = agendaItem.isDone,
                setMenuPositionCallback = setMenuPositionCallback,
                itemTypeName = agendaItem::class.java.simpleName,
                onToggleCompleted = onToggleCompleted,
            )
        is AgendaItem.Reminder ->
            AgendaCard(
                modifier = modifier,
                backgroundColor = TaskyPurple,
                textColor = MaterialTheme.colors.onPrimary,
                title = agendaItem.title,
                description = agendaItem.description,
                fromDateTime = agendaItem.time,
                onMenuClick = onMenuClick,
                setMenuPositionCallback = setMenuPositionCallback,
                itemTypeName = agendaItem::class.java.simpleName,
            )
    }
}
