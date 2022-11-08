package com.realityexpander.tasky.core.util

import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime

typealias UtcLong = Long

fun ZonedDateTime.toUtcLong(): UtcLong {
    return this.toOffsetDateTime().toInstant().toEpochMilli()
}

fun UtcLong.toZonedDateTime(zoneId: String = ZoneId.systemDefault().id): ZonedDateTime {
    return ZonedDateTime.ofInstant(
        Instant.ofEpochMilli(this),
        ZoneId.of(zoneId)
    )
}