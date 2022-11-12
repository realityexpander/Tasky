package com.realityexpander.tasky.core.util

import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.temporal.ChronoUnit

fun ZonedDateTime.toUtcMillis(): UtcMillis {
    return this.toOffsetDateTime().toInstant().toEpochMilli()
}

fun UtcMillis.toZonedDateTime(zoneId: String = ZoneId.systemDefault().id): ZonedDateTime {
    return ZonedDateTime.ofInstant(
        Instant.ofEpochMilli(this),
        ZoneId.of(zoneId)
    ).truncatedTo(ChronoUnit.MINUTES) // drop the seconds and milliseconds
}