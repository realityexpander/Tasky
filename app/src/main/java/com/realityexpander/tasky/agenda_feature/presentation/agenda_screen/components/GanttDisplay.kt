package com.realityexpander.tasky.agenda_feature.presentation.agenda_screen.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.HideImage
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.ZoomIn
import androidx.compose.material.icons.filled.ZoomOut
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.realityexpander.tasky.agenda_feature.domain.AgendaItem
import com.realityexpander.tasky.core.presentation.theme.TaskyGreen
import com.realityexpander.tasky.core.presentation.theme.TaskyLightBlue
import com.realityexpander.tasky.core.presentation.theme.TaskyPurple
import java.time.LocalDateTime

const val VIEW_SPAN_1_DAY = 24 * 3600L

@Composable
fun GanttDisplay(
    modifier: Modifier = Modifier,
    agendaItems : List<AgendaItem>,
    startDateTime: LocalDateTime,
) {
  val viewSpan = remember { mutableStateOf(VIEW_SPAN_1_DAY * 2) }
  val isEventTimesVisible = remember { mutableStateOf(true) }
  var isGanttDisplayVisible by remember { mutableStateOf(false) }

  val sectionsForCalendarEvents = remember(agendaItems) {

    val agendaItemEvents = mutableListOf<CalendarEvent>()
    val agendaItemTasks = mutableListOf<CalendarEvent>()
    val agendaItemReminders = mutableListOf<CalendarEvent>()

    agendaItems.forEach { agendaItem ->
      when (agendaItem) {
        is AgendaItem.Event -> {
          agendaItemEvents.add(
            CalendarEvent(
              startDate = agendaItem.startTime.toLocalDateTime(),
              endDate = agendaItem.to.toLocalDateTime(),
              name = agendaItem.title,
              description = agendaItem.description,
              color = TaskyGreen
            )
          )
        }
        is AgendaItem.Task -> {
          agendaItemTasks.add(
            CalendarEvent(
              startDate = agendaItem.startTime.toLocalDateTime(),
              endDate = agendaItem.startTime.plusMinutes(60).toLocalDateTime(),
              name = agendaItem.title,
              description = agendaItem.description,
              color = TaskyLightBlue
            )
          )
        }
        is AgendaItem.Reminder -> {
          agendaItemReminders.add(
            CalendarEvent(
              startDate = agendaItem.startTime.toLocalDateTime(),
              endDate = agendaItem.startTime.plusMinutes(60).toLocalDateTime(),
              name = agendaItem.title,
              description = agendaItem.description,
              color = TaskyPurple
            )
          )
        }
        else -> {
          println("Unknown AgendaItem type: $agendaItem")
        }
      }
    }

    listOf(
      CalendarSection("Events", agendaItemEvents),
      CalendarSection("Tasks", agendaItemTasks),
      CalendarSection("Reminders", agendaItemReminders)
    )
  }

    Column(
      modifier = modifier
        .wrapContentSize(Alignment.Center)
    ) {
      Row {
        Spacer(modifier = Modifier.width(8.dp))
        IconButton(onClick = {
          isGanttDisplayVisible = !(isGanttDisplayVisible)
        }) {
          Icon(
            imageVector = Icons.Default.Visibility,
            contentDescription = "hide gantt chart",
            tint = MaterialTheme.colors.onSurface.copy(alpha = 0.6f)
          )
        }
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
          isEventTimesVisible.value = !(isEventTimesVisible.value)
        }) {
          Icon(
            imageVector = Icons.Default.HideImage,
            contentDescription = "hide times",
            tint = MaterialTheme.colors.onSurface.copy(alpha = 0.6f)
          )
        }
      }

      val calendarState =
        rememberGanttScheduleState(
          referenceDateTime = startDateTime,
        )

      androidx.compose.animation.AnimatedVisibility(visible = isGanttDisplayVisible) {
        Spacer(modifier = Modifier.height(8.dp))

        GanttScheduleDisplay(
          state = calendarState,
          now = LocalDateTime.now(),
          eventTimesVisible = isEventTimesVisible.value,
          sections = sectionsForCalendarEvents,
          viewSpan = viewSpan.value
        )
      }
    }
}

//sections = listOf(
//CalendarSection(
//"Events",
//events = listOf(
//CalendarEvent(
//startDate = LocalDateTime.now().minusHours(6),
//endDate = LocalDateTime.now().plusHours(12),
//name = "Birthday for Mike",
//description = "",
//color = Color.Cyan
//),
//CalendarEvent(
//startDate = LocalDateTime.now().plusHours(24),
//endDate = LocalDateTime.now().plusHours(43),
//name = "Homecoming",
//description = "",
//color = Color.LightGray
//)
//)
//),
//CalendarSection(
//"Tasks",
//events = listOf(
//CalendarEvent(
//startDate = LocalDateTime.now().plusHours(6),
//endDate = LocalDateTime.now().plusHours(10),
//name = "Start project B",
//description = "",
//color = Color.Blue
//),
//CalendarEvent(
//startDate = LocalDateTime.now().plusHours(17),
//endDate = LocalDateTime.now().plusHours(21),
//name = "Cancel project A",
//description = "",
//color = Color.Magenta
//)
//)
//),
//CalendarSection(
//"Reminders",
//events = listOf(
//CalendarEvent(
//startDate = LocalDateTime.now().plusHours(2),
//endDate = LocalDateTime.now().plusHours(6),
//name = "Take out the trash",
//description = "",
//color = Color.Green
//),
//CalendarEvent(
//startDate = LocalDateTime.now().plusHours(6),
//endDate = LocalDateTime.now().plusHours(10),
//name = "Call Taha Kirca",
//description = "",
//color = Color.DarkGray
//)
//)
//)
//)