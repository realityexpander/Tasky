package com.realityexpander.tasky.agenda_feature.presentation.event_screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.DropdownMenu
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import com.realityexpander.tasky.agenda_feature.presentation.common.MenuItem
import com.realityexpander.tasky.agenda_feature.util.toTimeDifferenceHumanReadable
import com.realityexpander.tasky.core.presentation.common.modifiers.DP
import com.realityexpander.tasky.core.presentation.common.modifiers.smallWidth
import java.time.ZonedDateTime

@Composable
fun ColumnScope.RemindAtRow(
    modifier: Modifier = Modifier,
    isEditable: Boolean = false,
    isDropdownMenuVisible: Boolean = false,
    fromDateTime: ZonedDateTime,
    remindAtDateTime: ZonedDateTime,
    onEditRemindAtDateTime: () -> Unit,
    onDismissRequest: () -> Unit,
    onSaveRemindAtDateTime: (ZonedDateTime) -> Unit,
) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colors.surface)
    ) {

        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .weight(1f)
                .clickable(enabled = isEditable) {
                    onEditRemindAtDateTime()
                }
        ) {
            Row(
                horizontalArrangement = Arrangement.Start,
                modifier = Modifier
                    .weight(1f)
                    .padding(start = DP.small, end = DP.medium)
            ) {
                // • ALARM ICON
                Icon(
                    imageVector = Icons.Outlined.Notifications,
                    tint = MaterialTheme.colors.onSurface.copy(alpha = .3f),
                    contentDescription = "Remind At Button",
                    modifier = Modifier
                        .size(34.dp)
                        .clip(shape = RoundedCornerShape(5.dp))
                        .background(MaterialTheme.colors.onSurface.copy(alpha = .1f))
                        .padding(4.dp)
                        .align(Alignment.CenterVertically)
                        .weight(.2f)
                )
                Spacer(modifier = Modifier.smallWidth())
                Text(
                    fromDateTime.toTimeDifferenceHumanReadable(remindAtDateTime)
                        ?: "not set",
                    color = MaterialTheme.colors.onSurface,
                    textAlign = TextAlign.Start,
                    modifier = Modifier
                        .align(Alignment.CenterVertically)
                        .weight(1f)
                )
            }

            Icon(
                imageVector = Icons.Filled.ChevronRight,
                tint = if (isEditable) MaterialTheme.colors.onSurface else Color.Transparent,
                contentDescription = "Edit Remind At DateTime",
                modifier = Modifier
                    .weight(.1125f)
                    .align(Alignment.CenterVertically)
            )

            // • REMIND AT TIME OFFSET MENU
            DropdownMenu(
                expanded = isDropdownMenuVisible,
                offset = DpOffset(DP.small, 0.dp),
                onDismissRequest = { onDismissRequest() },
                modifier = Modifier
                    .background(color = MaterialTheme.colors.onSurface)

            ) {
                MenuItem(
                    title = "10 minutes before",
                    onClick = {
                        onSaveRemindAtDateTime(fromDateTime.minusMinutes(10))
                    },
                )
                MenuItem(
                    title = "30 minutes before",
                    onClick = {
                        onSaveRemindAtDateTime(fromDateTime.minusMinutes(30))
                    },
                )
                MenuItem(
                    title = "1 hour before",
                    onClick = {
                        onSaveRemindAtDateTime(fromDateTime.minusHours(1))
                    },
                )
                MenuItem(
                    title = "6 hours before",
                    onClick = {
                        onSaveRemindAtDateTime(fromDateTime.minusHours(6))
                    },
                )
                MenuItem(
                    title = "1 day before",
                    onClick = {
                        onSaveRemindAtDateTime(fromDateTime.minusDays(1))
                    },
                )
            }
        }

    }
}