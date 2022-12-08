package com.realityexpander.tasky.core.util

import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime

fun ZonedDateTime.withCurrentHourMinute(): ZonedDateTime {
    return this.withHour(Instant.now().atZone(ZoneId.systemDefault()).hour)
        .withMinute(Instant.now().atZone(ZoneId.systemDefault()).minute)
}