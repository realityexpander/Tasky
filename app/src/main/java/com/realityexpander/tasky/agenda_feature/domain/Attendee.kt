package com.realityexpander.tasky.agenda_feature.domain

import android.os.Parcelable
import com.realityexpander.tasky.agenda_feature.data.common.serializers.ZonedDateTimeSerializer
import com.realityexpander.tasky.agenda_feature.util.AttendeeId
import com.realityexpander.tasky.agenda_feature.util.EventId
import com.realityexpander.tasky.agenda_feature.util.UrlStr
import com.realityexpander.tasky.core.util.Email
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable
import java.time.ZonedDateTime

@Parcelize
@Serializable  // for Room @TypeConverter
data class Attendee(
    val id: AttendeeId,
    val eventId: EventId,

    val email: Email,
    val fullName: String,
    val isGoing: Boolean,

    @Serializable(with = ZonedDateTimeSerializer::class) // for Room @TypeConverter
    val remindAt: ZonedDateTime,

    val photo: UrlStr,
) : Parcelable