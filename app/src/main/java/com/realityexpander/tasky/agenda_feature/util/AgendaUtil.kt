package com.realityexpander.tasky.agenda_feature.util

import com.realityexpander.tasky.core.util.UuidStr

typealias EventId = UuidStr
typealias ReminderId = UuidStr
typealias TaskId = UuidStr
typealias AttendeeId = UuidStr
typealias PhotoId = UuidStr
const val emptyId = ""
typealias UrlStr = String

typealias TimeZoneStr = String  // ex: "America/Los_Angeles"

fun attendeeId(id: String): AttendeeId = id
fun eventId(id: String): EventId = id
fun reminderId(id: String): ReminderId = id
fun taskId(id: String): TaskId = id
fun photoId(id: String): PhotoId = id

