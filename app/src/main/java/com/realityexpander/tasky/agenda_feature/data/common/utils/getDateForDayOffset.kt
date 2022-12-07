package com.realityexpander.tasky.agenda_feature.data.common.utils

import java.time.ZoneId
import java.time.ZonedDateTime

fun getDateForDayOffset(
    startDate: ZonedDateTime?,
    selectedDayOffset: Int?    // can be positive or negative number of days. 0 is today.
): ZonedDateTime =
    (startDate ?: ZonedDateTime.now(ZoneId.systemDefault()))
        .plusDays(selectedDayOffset?.toLong() ?: 0)
