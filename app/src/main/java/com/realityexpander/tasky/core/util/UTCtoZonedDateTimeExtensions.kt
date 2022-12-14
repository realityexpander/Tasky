package com.realityexpander.tasky.core.util

import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.temporal.ChronoUnit

typealias EpochMilli = Long
typealias EpochSecond = Long
typealias ZonedDateTimeStr = String

fun ZonedDateTime.toEpochMilli(): EpochMilli {
    return this.toOffsetDateTime().toInstant().toEpochMilli()
}

fun EpochMilli.toZonedDateTime(zoneId: String = ZoneId.systemDefault().id): ZonedDateTime {
    return ZonedDateTime.ofInstant(
        Instant.ofEpochMilli(this),
        ZoneId.of(zoneId)
    ).truncatedTo(ChronoUnit.MINUTES) // drop the seconds and milliseconds
}