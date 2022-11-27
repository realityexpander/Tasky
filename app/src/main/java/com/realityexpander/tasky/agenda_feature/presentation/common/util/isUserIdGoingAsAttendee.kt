package com.realityexpander.tasky.agenda_feature.presentation.common.util

import com.realityexpander.tasky.agenda_feature.domain.Attendee
import com.realityexpander.tasky.core.util.UserId

fun isUserIdGoingAsAttendee(userId: UserId?, attendees: List<Attendee>?): Boolean {
    attendees ?: return false
    userId ?: return false

    return attendees.any { attendee ->
        attendee.id == userId && attendee.isGoing
    }
}