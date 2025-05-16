package com.realityexpander.tasky.agenda_feature.presentation.common.util

import androidx.compose.ui.text.capitalize
import androidx.compose.ui.text.intl.Locale
import com.realityexpander.tasky.R
import com.realityexpander.tasky.core.presentation.util.UiText
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import kotlin.math.abs

fun ZonedDateTime.toLongMonthDayYear(): String {
    return ("${this.month.name} " +
            "${this.dayOfMonth} " +
            "${this.year}").uppercase()
}

fun ZonedDateTime.toShortMonthDayYear(): String {
    return "${this.month.name.substring(0, 3).lowercase().capitalize(Locale.current)} " +
            "${this.dayOfMonth} " +
            "${this.year}"
}

fun ZonedDateTime.toTime12Hour(): String {
    return DateTimeFormatter.ofPattern("h:mm a").format(this)
}

fun ZonedDateTime.toTimeDifferenceHumanReadable(remindAtTime: ZonedDateTime): UiText {
    val diff = remindAtTime.toEpochSecond() - this.toEpochSecond()
    if (diff == 0L) return UiText.ResOrStr(R.string.zonedDateTimeToString_same_time, "Same time")

    val beforeOrAfter = if (diff < 0) "before" else "after"
    val diffAbs = abs(diff)

    val days = diffAbs / 86400
    val hours = (diffAbs % 86400) / 3600
    val minutes = (diffAbs % 3600) / 60
    val seconds = diffAbs % 60

    return when {
        days > 0 -> {
            if (days == 1L) {
                UiText.ResOrStr(R.string.zonedDateTimeToString_number_day_string,
                    "1 day $beforeOrAfter", days, beforeOrAfter)
            } else {
                UiText.ResOrStr(R.string.zonedDateTimeToString_number_days_string,
                    "$days days $beforeOrAfter", days, beforeOrAfter)
            }
        }
        hours > 0 -> {
            if (hours == 1L) {
                UiText.ResOrStr(R.string.zonedDateTimeToString_number_hour_string,
                    "1 hour $beforeOrAfter", hours, beforeOrAfter)
            } else {
                UiText.ResOrStr(R.string.zonedDateTimeToString_number_hours_string,
                    "$hours hours $beforeOrAfter", hours, beforeOrAfter)
            }
        }
        minutes > 0 -> {
            if (minutes == 1L) {
                UiText.ResOrStr(R.string.zonedDateTimeToString_number_minute_string,
                    "1 minute $beforeOrAfter", minutes, beforeOrAfter)
            } else {
                UiText.ResOrStr(R.string.zonedDateTimeToString_number_minutes_string,
                    "$minutes minutes $beforeOrAfter", minutes, beforeOrAfter)
            }
        }
        else -> {
            if (seconds == 1L) {
                UiText.ResOrStr(R.string.zonedDateTimeToString_number_second_string,
                    "1 second $beforeOrAfter", seconds, beforeOrAfter)
            } else {
                UiText.ResOrStr(R.string.zonedDateTimeToString_number_seconds_string,
                    "$seconds seconds $beforeOrAfter", seconds, beforeOrAfter)
            }
        }
    }
}



// Local tests
fun main() {
    val remindAt = ZonedDateTime.of(2021, 1, 1, 0, 0, 0, 0, ZoneId.of("UTC"))

    println("remindAt.toLongMonthDayYear() == \"JANUARY 1 2021\": ${remindAt.toLongMonthDayYear() == "JANUARY 1 2021"}")
    println("remindAt.toShortMonthDayYear() == \"Jan 1 2021\": ${remindAt.toShortMonthDayYear() == "Jan 1 2021"}")


    val date2 = ZonedDateTime.of(2021, 12, 31, 0, 0, 0, 0, ZoneId.of("UTC"))
    println("date2.toLongMonthDayYear() == \"DECEMBER 31 2021\": ${date2.toLongMonthDayYear() == "DECEMBER 31 2021"}")
    println("date2.toShortMonthDayYear() == \"Dec 31 2021\": ${date2.toShortMonthDayYear() == "Dec 31 2021"}")

    println()
    println("remindAt.toTime12Hour() == \"12:00 AM\": ${remindAt.toTime12Hour() == "12:00 AM"}")
    println("remindAt.plusMinutes(20).toTime12Hour() == \"12:20 AM\": ${remindAt.plusMinutes(20).toTime12Hour() == "12:20 AM"}")
    println("remindAt.plusHours(1).toTime12Hour() == \"1:00 AM\": ${remindAt.plusHours(1).toTime12Hour() == "1:00 AM"}")


    val date = ZonedDateTime.of(2021, 1, 1, 0, 0, 0, 0, ZoneId.of("UTC"))

    println()
    println("date.toTimeDifferenceHumanReadable(date) == \"Same time\": " +
    "${date.toTimeDifferenceHumanReadable(date).asStrOrNull() == "Same time"}")
    println("date.toTimeDifferenceHumanReadable(remindAt.minusSeconds(1)) == \"1 second before\": " +
    "${date.toTimeDifferenceHumanReadable(remindAt.minusSeconds(1)).asStrOrNull() == "1 second before"}")
    println("date.toTimeDifferenceHumanReadable(remindAt.minusSeconds(59)) == \"59 seconds before\": " +
    "${date.toTimeDifferenceHumanReadable(remindAt.minusSeconds(59)).asStrOrNull() == "59 seconds before"}")
    println("date.toTimeDifferenceHumanReadable(remindAt.minusMinutes(1)) == \"1 minute before\": " +
    "${date.toTimeDifferenceHumanReadable(remindAt.minusMinutes(1)).asStrOrNull() == "1 minute before"}")
    println("date.toTimeDifferenceHumanReadable(remindAt.minusMinutes(59)) == \"59 minutes before\": " +
    "${date.toTimeDifferenceHumanReadable(remindAt.minusMinutes(59)).asStrOrNull() == "59 minutes before"}")
    println("date.toTimeDifferenceHumanReadable(remindAt.minusHours(1)) == \"1 hour before\": " +
    "${date.toTimeDifferenceHumanReadable(remindAt.minusHours(1)).asStrOrNull() == "1 hour before"}")
    println("date.toTimeDifferenceHumanReadable(remindAt.minusHours(23)) == \"23 hours before\": " +
    "${date.toTimeDifferenceHumanReadable(remindAt.minusHours(23)).asStrOrNull() == "23 hours before"}")
    println("date.toTimeDifferenceHumanReadable(remindAt.minusDays(1)) == \"1 day ago before\": " +
    "${date.toTimeDifferenceHumanReadable(remindAt.minusDays(1)).asStrOrNull() == "1 day before"}")
    println("date.toTimeDifferenceHumanReadable(remindAt.minusDays(2)) == \"2 days ago before\": " +
    "${date.toTimeDifferenceHumanReadable(remindAt.minusDays(2)).asStrOrNull() == "2 days before"}")

    println()
    println("date.toTimeDifferenceHumanReadable(remindAt.plusSeconds(1)) == \"1 second after\": " +
    "${date.toTimeDifferenceHumanReadable(remindAt.plusSeconds(1)).asStrOrNull() == "1 second after"}")
    println("date.toTimeDifferenceHumanReadable(remindAt.plusSeconds(59)) == \"59 seconds after\": " +
    "${date.toTimeDifferenceHumanReadable(remindAt.plusSeconds(59)).asStrOrNull() == "59 seconds after"}")
    println("date.toTimeDifferenceHumanReadable(remindAt.plusMinutes(1)) == \"1 minute after\": " +
    "${date.toTimeDifferenceHumanReadable(remindAt.plusMinutes(1)).asStrOrNull() == "1 minute after"}")
    println("date.toTimeDifferenceHumanReadable(remindAt.plusMinutes(59)) == \"59 minutes after\": " +
    "${date.toTimeDifferenceHumanReadable(remindAt.plusMinutes(59)).asStrOrNull() == "59 minutes after"}")
    println("date.toTimeDifferenceHumanReadable(remindAt.plusHours(1)) == \"1 hour after\": " +
    "${date.toTimeDifferenceHumanReadable(remindAt.plusHours(1)).asStrOrNull() == "1 hour after"}")
    println("date.toTimeDifferenceHumanReadable(remindAt.plusHours(23)) == \"23 hours after\": " +
    "${date.toTimeDifferenceHumanReadable(remindAt.plusHours(23)).asStrOrNull() == "23 hours after"}")
    println("date.toTimeDifferenceHumanReadable(remindAt.plusDays(1)) == \"1 day after\": " +
    "${date.toTimeDifferenceHumanReadable(remindAt.plusDays(1)).asStrOrNull() == "1 day after"}")
    println("date.toTimeDifferenceHumanReadable(remindAt.plusDays(2)) == \"2 days after\": " +
    "${date.toTimeDifferenceHumanReadable(remindAt.plusDays(2)).asStrOrNull() == "2 days after"}")

}
