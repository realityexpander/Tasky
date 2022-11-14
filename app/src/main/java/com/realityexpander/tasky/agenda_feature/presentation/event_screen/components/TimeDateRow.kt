package com.realityexpander.tasky.agenda_feature.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.realityexpander.tasky.agenda_feature.presentation.event_screen.EventScreenEvent
import com.realityexpander.tasky.agenda_feature.util.toShortMonthDayYear
import com.realityexpander.tasky.agenda_feature.util.toTime12Hour
import com.realityexpander.tasky.core.presentation.common.modifiers.DP
import java.time.ZonedDateTime

@Composable
fun ColumnScope.TimeDateRow(
    modifier: Modifier = Modifier,
    title: String,  // `From` or `To`
    date: ZonedDateTime,
    isEditable: Boolean,
    onAction: (EventScreenEvent) -> Unit,
) {
    // • FROM TIME / DATE
    Row(
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colors.surface)
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .weight(1f)
                .padding(start = DP.medium, end = DP.medium)
                .align(Alignment.CenterVertically)
        ) {
            Text(
                title,
                color = MaterialTheme.colors.onSurface,
                textAlign = TextAlign.Start,
                modifier = Modifier
                    .weight(.5f)
                    .align(Alignment.CenterVertically)
            )
            Text(
                date.toTime12Hour() ?: "not set",
                color = MaterialTheme.colors.onSurface,
                textAlign = TextAlign.End,
                modifier = Modifier
                    .weight(1f)
                    .align(Alignment.CenterVertically)
            )
        }
        Icon(
            imageVector = Icons.Filled.ChevronRight,
            tint = if (isEditable) MaterialTheme.colors.onSurface else Color.Transparent,
            contentDescription = "Edit Event `$title` Time",
            modifier = Modifier
                .weight(.2f)
                .width(32.dp)
                .height(26.dp)
                .align(Alignment.CenterVertically)
                .clickable {
                    if(isEditable) {
                        onAction(
                            EventScreenEvent.SetEditMode(
                                EventScreenEvent.EditMode.FromTime(
                                    date
                                )
                            )
                        )
                    }
                }
        )
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .weight(1f)
                .padding(start = DP.medium, end = DP.tiny)
                .align(Alignment.CenterVertically)
        ) {
            Text(
                date.toShortMonthDayYear() ?: "not set",
                color = MaterialTheme.colors.onSurface,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .weight(.8f)
                    .align(Alignment.CenterVertically)
            )
            Icon(
                imageVector = Icons.Filled.ChevronRight,
                tint = if (isEditable) MaterialTheme.colors.onSurface else Color.Transparent,
                contentDescription = "Edit Event `$title` Date",
                modifier = Modifier
                    .weight(.2f)
                    .width(32.dp)
                    .height(26.dp)
                    .align(Alignment.CenterVertically)
                    .clickable {
                        if(isEditable) {
                            onAction(
                                EventScreenEvent.SetEditMode(
                                    EventScreenEvent.EditMode.FromDate(
                                        date
                                    )
                                )
                            )
                        }
                    }
            )
        }
    }
}