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
import com.realityexpander.tasky.core.presentation.common.modifiers.smallHeight
import com.realityexpander.tasky.core.presentation.common.modifiers.tinyHeight
import com.realityexpander.tasky.core.presentation.theme.TaskyShapes
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Composable
fun AgendaCard(
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colors.secondary,
    title: String = "",
    content: String = "",
    fromDateTime: LocalDateTime = LocalDateTime.MIN,
    toDateTime: LocalDateTime = LocalDateTime.MAX,
    onMenuClick: () -> Unit = {},
    completed: Boolean = false,
    onSetMenuPosition : (LayoutCoordinates) -> Unit = {},
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .clip(shape = TaskyShapes.WideButtonRoundedCorners)
            .background(color = color)
            .padding(12.dp)
    ) {
        Column() {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
            ) {

                // Check/Uncheck Icon
                Column(
                    modifier = Modifier
                        .alignByBaseline()
                ) {
                    Icon(
                        imageVector = if(completed)
                                Icons.Filled.TaskAlt // CheckCircleOutline
                            else
                                Icons.Filled.RadioButtonUnchecked,
                        contentDescription = "Event",
                        tint = MaterialTheme.colors.onSecondary,
                        modifier = Modifier
                            .align(Alignment.Start)
                            .offset(y = 4.dp)
                            .padding(end = 8.dp)
                    )
                }

                // Title / Content
                Column(
                    modifier = Modifier
                        .alignByBaseline()
                        .weight(1f)
                ) {

                    Text(
                        text = title,
                        style = MaterialTheme.typography.h3.copy(
                            textDecoration = if(completed)
                                TextDecoration.LineThrough
                            else
                                TextDecoration.None,
                        ),
                        color = MaterialTheme.colors.onSecondary
                    )
                    Spacer(modifier = Modifier.tinyHeight())

                    Text(
                        text = content,
                        color = MaterialTheme.colors.onSecondary
                    )
                    Spacer(modifier = Modifier.smallHeight())
                }

                // Skewer menu
                Column(
                    modifier = Modifier
                        .alignByBaseline()
                ) {
                    Icon(
                        imageVector = Icons.Filled.MoreHoriz,
                        contentDescription = "Event",
                        tint = MaterialTheme.colors.onSecondary,
                        modifier = Modifier
                            .align(Alignment.End)
                            .offset(y = 2.dp)
                            .padding(end = 8.dp)
                            .clickable(onClick = onMenuClick)
                            .onGloballyPositioned { onSetMenuPosition(it) }
                    )
                }
            }

            Text(
                text = "${fromDateTime.format(
                    DateTimeFormatter.ofPattern("MMM d, h:mm a")
                )} - ${toDateTime.format(
                    DateTimeFormatter.ofPattern("MMM d, h:mm a")
                )}",
                style = MaterialTheme.typography.subtitle1,
                textAlign = TextAlign.End,
                color = MaterialTheme.colors.onSecondary,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(end = 16.dp)
                    .align(Alignment.End)
            )
        }
    }
}

// For Event
@Composable
fun AgendaCard(
    modifier: Modifier = Modifier,
    agendaItem: AgendaItem.Event,
    onMenuClick: () -> Unit = {},
    completed: Boolean = false,
    onSetMenuPosition : (LayoutCoordinates) -> Unit = {},
) {
    AgendaCard(
        modifier = modifier,
        title = agendaItem.title,
        content = agendaItem.description,
        fromDateTime = agendaItem.from,
        toDateTime = agendaItem.to,
        onMenuClick = onMenuClick,
        completed = completed,
        onSetMenuPosition = onSetMenuPosition,
    )
}