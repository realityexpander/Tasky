package com.realityexpander.tasky.agenda_feature.domain

import android.os.Parcelable
import com.realityexpander.tasky.core.util.UuidStr
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.Serializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import java.time.LocalDateTime
import java.util.*

// NOTE: Skeleton data structures for the Agenda feature
// **temporary** for UI development

@OptIn(ExperimentalSerializationApi::class)
@Serializer(forClass = LocalDateTime::class)
object LocalDateTimeSerializer : KSerializer<LocalDateTime> {
    override val descriptor = PrimitiveSerialDescriptor("LocalDateTime", PrimitiveKind.STRING)

    override fun deserialize(decoder: Decoder): LocalDateTime {
        return LocalDateTime.parse((decoder.decodeString()))
    }

    override fun serialize(encoder: Encoder, value: LocalDateTime) {
        encoder.encodeString(value.toString())
    }
}

abstract class AgendaItem(
    open val id: UuidStr,
    open val title: String,
    open val description: String? = null,
    @Serializable(with = LocalDateTimeSerializer::class)
    open val remindAt: LocalDateTime,
) {
    abstract fun isEmpty(): Boolean

    @Parcelize
    class Event(
        override val id: UuidStr,
        override val title: String,
        override val description: String? = null,
        @Serializable(with = LocalDateTimeSerializer::class)
        override val remindAt: LocalDateTime,
        @Serializable(with = LocalDateTimeSerializer::class)
        val from: LocalDateTime,
        @Serializable(with = LocalDateTimeSerializer::class)
        val to: LocalDateTime,
        val attendeeIds: List<UuidStr>? = null,
        val photos: List<UuidStr>? = null,
    ): AgendaItem(id, title, description, remindAt), Parcelable {

        companion object {
            val EMPTY = Event("", "", "", LocalDateTime.MIN, LocalDateTime.MIN, LocalDateTime.MIN, emptyList())
        }

        override fun isEmpty() = this == EMPTY

        fun copy(
            id: UuidStr = this.id,
            title: String = this.title,
            description: String? = this.description,
            remindAt: LocalDateTime = this.remindAt,
            from: LocalDateTime = this.from,
            to: LocalDateTime = this.to,
            attendeeIds: List<UuidStr>? = this.attendeeIds,
            photos: List<UuidStr>? = this.photos,
        ) = Event(
            id = id,
            title = title,
            description = description,
            remindAt = remindAt,
            from = from,
            to = to,
            attendeeIds = attendeeIds,
            photos = photos,
        )
    }

    @Parcelize
    class Task(
        override val id: UuidStr,
        override val title: String,
        override val description: String? = null,
        @Serializable(with = LocalDateTimeSerializer::class)
        override val remindAt: LocalDateTime,
        @Serializable(with = LocalDateTimeSerializer::class)
        val time: LocalDateTime,
        val isDone: Boolean = false,
    ): AgendaItem(id, title, description, remindAt), Parcelable {

        companion object {
            val EMPTY = Task("", "", "", LocalDateTime.MIN, LocalDateTime.MIN)
        }

        override fun isEmpty() = this == EMPTY

        fun copy(
            id: UuidStr = this.id,
            title: String = this.title,
            description: String? = this.description,
            remindAt: LocalDateTime = this.remindAt,
            time: LocalDateTime = this.time,
            isDone: Boolean = this.isDone,
        ) = Task(
            id = id,
            title = title,
            description = description,
            remindAt = remindAt,
            time = time,
            isDone = isDone,
        )
    }

    @Parcelize
    class Reminder(
        override val id: UuidStr,
        override val title: String,
        override val description: String? = null,
        @Serializable(with = LocalDateTimeSerializer::class)
        override val remindAt: LocalDateTime,
        val time: LocalDateTime,
    ): AgendaItem(id, title, description, remindAt), Parcelable {

        companion object {
            val EMPTY = Reminder("", "", "", LocalDateTime.MIN, LocalDateTime.MIN)
        }

        override fun isEmpty() = this == EMPTY

        fun copy(
            id: UuidStr = this.id,
            title: String = this.title,
            description: String? = this.description,
            remindAt: LocalDateTime = this.remindAt,
            time: LocalDateTime = this.time,
        ) = Reminder(
            id = id,
            title = title,
            description = description,
            remindAt = remindAt,
            time = time,
        )
    }

}
