package com.realityexpander.tasky.agenda_feature.presentation.agenda_screen.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.HideImage
import androidx.compose.material.icons.filled.ZoomIn
import androidx.compose.material.icons.filled.ZoomOut
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import java.time.LocalDateTime

@Composable
fun ScheduleCalendarDemo() {
  val viewSpan = remember { mutableStateOf(48 * 3600L) }
  val eventTimesVisible = remember { mutableStateOf(true) }

  Column(
    modifier = Modifier
      .wrapContentSize(Alignment.Center)
  ) {
    Row {
      IconButton(
        onClick = {
        viewSpan.value = (viewSpan.value * 2).coerceAtMost(96 * 3600)
      }) {
        Icon(
          imageVector = Icons.Default.ZoomOut,
          contentDescription = "zoom out",
          tint = MaterialTheme.colors.onSurface.copy(alpha = 0.6f)
        )
      }
      Spacer(modifier = Modifier.width(8.dp))
      IconButton(onClick = {
        viewSpan.value = (viewSpan.value / 2).coerceAtLeast(3 * 3600)
      }) {
        Icon(
          imageVector = Icons.Default.ZoomIn,
          contentDescription = "zoom in",
          tint = MaterialTheme.colors.onSurface.copy(alpha = 0.6f)
        )
      }
      Spacer(modifier = Modifier.width(8.dp))
      IconButton(onClick = {
        eventTimesVisible.value = !(eventTimesVisible.value)
      }) {
        Icon(
          imageVector = Icons.Default.HideImage,
          contentDescription = "hide times",
          tint = MaterialTheme.colors.onSurface.copy(alpha = 0.6f)
        )
      }
    }

    val calendarState = rememberScheduleCalendarState()

    Spacer(modifier = Modifier.height(8.dp))

    ScheduleCalendar(
      state = calendarState,
      now = LocalDateTime.now(), //.plusHours(8),
      eventTimesVisible = eventTimesVisible.value,
      sections = listOf(
        CalendarSection(
          "Events",
          events = listOf(
            CalendarEvent(
              startDate = LocalDateTime.now().minusHours(6),
              endDate = LocalDateTime.now().plusHours(12),
              name = "Birthday for Mike",
              description = "",
              color = Color.Cyan
            ),
            CalendarEvent(
              startDate = LocalDateTime.now().plusHours(24),
              endDate = LocalDateTime.now().plusHours(43),
              name = "Homecoming",
              description = "",
              color = Color.LightGray
            )
          )
        ),
        CalendarSection(
          "Tasks",
          events = listOf(
            CalendarEvent(
              startDate = LocalDateTime.now().plusHours(6),
              endDate = LocalDateTime.now().plusHours(10),
              name = "Start project B",
              description = "",
              color = Color.Blue
            ),
            CalendarEvent(
              startDate = LocalDateTime.now().plusHours(17),
              endDate = LocalDateTime.now().plusHours(21),
              name = "Cancel project A",
              description = "",
              color = Color.Magenta
            )
          )
        ),
        CalendarSection(
          "Reminders",
          events = listOf(
            CalendarEvent(
              startDate = LocalDateTime.now().plusHours(2),
              endDate = LocalDateTime.now().plusHours(6),
              name = "Take out the trash",
              description = "",
              color = Color.Green
            ),
            CalendarEvent(
              startDate = LocalDateTime.now().plusHours(6),
              endDate = LocalDateTime.now().plusHours(10),
              name = "Call Taha Kirca",
              description = "",
              color = Color.DarkGray
            )
          )
        )
      ),
      viewSpan = viewSpan.value
    )
  }
}