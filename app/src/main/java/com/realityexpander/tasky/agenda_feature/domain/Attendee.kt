package com.realityexpander.tasky.agenda_feature.domain

import android.os.Parcelable
import com.realityexpander.tasky.core.util.Email
import kotlinx.parcelize.Parcelize
import java.time.ZonedDateTime

@Parcelize
data class Attendee(
    val id: AttendeeId,
    val eventId: EventId? = null,

    val email: Email,
    val fullName: String,
    val isGoing: Boolean = false,

    val remindAt: ZonedDateTime? = null,

    val photo: UrlStr? = null,
) : Parcelable
