package com.realityexpander.tasky.agenda_feature.data.repositories

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.realityexpander.tasky.agenda_feature.data.common.typeConverters.AttendeeListTypeConverter
import com.realityexpander.tasky.agenda_feature.data.common.typeConverters.PhotoEntityListTypeConverter
import com.realityexpander.tasky.agenda_feature.data.common.typeConverters.StringListTypeConverter
import com.realityexpander.tasky.agenda_feature.data.common.typeConverters.ZonedDateTimeTypeConverter
import com.realityexpander.tasky.agenda_feature.data.repositories.eventRepository.local.entities.EventEntity
import com.realityexpander.tasky.agenda_feature.data.repositories.eventRepository.local.eventDao.eventDaoImpls.EventDaoImpl


@Database(
    entities = [
        EventEntity::class,
        ModifiedAgendaItemEntity::class
    ],
    version = 1,
)
@TypeConverters(
    ZonedDateTimeTypeConverter::class,
    StringListTypeConverter::class,     // for List<T> of UserId, PhotoId, UuidStr, etc.
    AttendeeListTypeConverter::class,   // for List<Attendee>
    PhotoEntityListTypeConverter::class,      // for List<Photo>
)
abstract class TaskyDatabase : RoomDatabase() {

    abstract fun eventDao(): EventDaoImpl

    companion object {
        const val DATABASE_NAME = "tasky_database"
    }
}
