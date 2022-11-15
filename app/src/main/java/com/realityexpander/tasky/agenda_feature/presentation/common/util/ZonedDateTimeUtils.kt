package com.realityexpander.tasky.agenda_feature.presentation.common.util

import java.time.Duration
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZonedDateTime

fun LocalDateTime.toZonedDateTime(): ZonedDateTime {
    return ZonedDateTime.of(this, ZoneId.systemDefault())
}

fun ZonedDateTime.getOffset(other: ZonedDateTime): Duration {
    return Duration.between(this, other)
}

fun min(first: ZonedDateTime, second: ZonedDateTime): ZonedDateTime {
    return if (first.isBefore(second)) first else second
}

fun max(first: ZonedDateTime, second: ZonedDateTime): ZonedDateTime {
    return if (first.isAfter(second)) first else second
}