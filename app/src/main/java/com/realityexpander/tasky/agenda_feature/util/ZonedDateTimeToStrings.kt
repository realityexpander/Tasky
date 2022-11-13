package com.realityexpander.tasky.agenda_feature.util

import androidx.compose.ui.text.capitalize
import androidx.compose.ui.text.intl.Locale
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

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

fun ZonedDateTime.differenceTimeHumanReadable(remindAtTime: ZonedDateTime): String {
    //val now = ZonedDateTime.of(dateTime.year, dateTime.monthValue, dateTime.dayOfMonth, dateTime.hour, dateTime.minute, dateTime.second, 0, dateTime.zone)
    val diff = remindAtTime.toEpochSecond() - this.toEpochSecond()
    if (diff == 0L) return "same time"

    val beforeOrAfter = if (diff < 0) "before" else "after"
    val diffAbs = Math.abs(diff)

    val days = diffAbs / 86400
    val hours = (diffAbs % 86400) / 3600
    val minutes = (diffAbs % 3600) / 60
    val seconds = diffAbs % 60


    return when {
        days > 0 -> {
            if (days == 1L) {
                "1 day $beforeOrAfter"
            } else {
                "$days days $beforeOrAfter"
            }
        }
        hours > 0 -> {
            if (hours == 1L) {
                "$hours hour $beforeOrAfter"
            } else {
                "$hours hours $beforeOrAfter"
            }
        }
        minutes > 0 -> {
            if (minutes == 1L) {
                "$minutes minute $beforeOrAfter"
            } else {
                "$minutes minutes $beforeOrAfter"
            }
        }
        else -> {
            if (seconds == 1L) {
                "$seconds second $beforeOrAfter"
            } else {
                "$seconds seconds $beforeOrAfter"
            }
        }
    }
}



// Local tests
fun main() {
    val remindAt = ZonedDateTime.of(2021, 1, 1, 0, 0, 0, 0, java.time.ZoneId.of("UTC"))

    println("remindAt.toLongMonthDayYear() == \"JANUARY 1 2021\": ${remindAt.toLongMonthDayYear() == "JANUARY 1 2021"}")
    println("remindAt.toShortMonthDayYear() == \"Jan 1 2021\": ${remindAt.toShortMonthDayYear() == "Jan 1 2021"}")


    val date2 = ZonedDateTime.of(2021, 12, 31, 0, 0, 0, 0, java.time.ZoneId.of("UTC"))
    println("date2.toLongMonthDayYear() == \"DECEMBER 31 2021\": ${date2.toLongMonthDayYear() == "DECEMBER 31 2021"}")
    println("date2.toShortMonthDayYear() == \"Dec 31 2021\": ${date2.toShortMonthDayYear() == "Dec 31 2021"}")

    println()
    println("remindAt.toTime12Hour() == \"12:00 AM\": ${remindAt.toTime12Hour() == "12:00 AM"}")
    println("remindAt.plusMinutes(20).toTime12Hour() == \"12:20 AM\": ${remindAt.plusMinutes(20).toTime12Hour() == "12:20 AM"}")
    println("remindAt.plusHours(1).toTime12Hour() == \"1:00 AM\": ${remindAt.plusHours(1).toTime12Hour() == "1:00 AM"}")


    println()
    val date = ZonedDateTime.of(2021, 1, 1, 0, 0, 0, 0, java.time.ZoneId.of("UTC"))
    println(remindAt.differenceTimeHumanReadable(remindAt.minusMinutes(30)))

    println()
    println("date.differenceTimeHumanReadable(date) == \"same time\": " +
            "${date.differenceTimeHumanReadable(date) == "same time"}")
    println("date.differenceTimeHumanReadable(remindAt.minusSeconds(1)) == \"1 second before\": " +
            "${date.differenceTimeHumanReadable(remindAt.minusSeconds(1)) == "1 second before"}")
    println("date.differenceTimeHumanReadable(remindAt.minusSeconds(59)) == \"59 seconds before\": " +
            "${date.differenceTimeHumanReadable(remindAt.minusSeconds(59)) == "59 seconds before"}")
    println("date.differenceTimeHumanReadable(remindAt.minusMinutes(1)) == \"1 minute before\": " +
            "${date.differenceTimeHumanReadable(remindAt.minusMinutes(1)) == "1 minute before"}")
    println("date.differenceTimeHumanReadable(remindAt.minusMinutes(59)) == \"59 minutes before\": " +
            "${date.differenceTimeHumanReadable(remindAt.minusMinutes(59)) == "59 minutes before"}")
    println("date.differenceTimeHumanReadable(remindAt.minusHours(1)) == \"1 hour before\": " +
            "${date.differenceTimeHumanReadable(remindAt.minusHours(1)) == "1 hour before"}")
    println("date.differenceTimeHumanReadable(remindAt.minusHours(23)) == \"23 hours before\": " +
            "${date.differenceTimeHumanReadable(remindAt.minusHours(23)) == "23 hours before"}")
    println("date.differenceTimeHumanReadable(remindAt.minusDays(1)) == \"1 day ago before\": " +
            "${date.differenceTimeHumanReadable(remindAt.minusDays(1)) == "1 day before"}")
    println("date.differenceTimeHumanReadable(remindAt.minusDays(2)) == \"2 days ago before\": " +
            "${date.differenceTimeHumanReadable(remindAt.minusDays(2)) == "2 days before"}")

    println()
    println("date.differenceTimeHumanReadable(remindAt.plusSeconds(1)) == \"1 second after\": " +
            "${date.differenceTimeHumanReadable(remindAt.plusSeconds(1)) == "1 second after"}")
    println("date.differenceTimeHumanReadable(remindAt.plusSeconds(59)) == \"59 seconds after\": " +
            "${date.differenceTimeHumanReadable(remindAt.plusSeconds(59)) == "59 seconds after"}")
    println("date.differenceTimeHumanReadable(remindAt.plusMinutes(1)) == \"1 minute after\": " +
            "${date.differenceTimeHumanReadable(remindAt.plusMinutes(1)) == "1 minute after"}")
    println("date.differenceTimeHumanReadable(remindAt.plusMinutes(59)) == \"59 minutes after\": " +
            "${date.differenceTimeHumanReadable(remindAt.plusMinutes(59)) == "59 minutes after"}")
    println("date.differenceTimeHumanReadable(remindAt.plusHours(1)) == \"1 hour after\": " +
            "${date.differenceTimeHumanReadable(remindAt.plusHours(1)) == "1 hour after"}")
    println("date.differenceTimeHumanReadable(remindAt.plusHours(23)) == \"23 hours after\": " +
            "${date.differenceTimeHumanReadable(remindAt.plusHours(23)) == "23 hours after"}")
    println("date.differenceTimeHumanReadable(remindAt.plusDays(1)) == \"1 day after\": " +
            "${date.differenceTimeHumanReadable(remindAt.plusDays(1)) == "1 day after"}")
    println("date.differenceTimeHumanReadable(remindAt.plusDays(2)) == \"2 days after\": " +
            "${date.differenceTimeHumanReadable(remindAt.plusDays(2)) == "2 days after"}")
}