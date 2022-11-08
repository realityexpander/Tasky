package com.realityexpander.tasky.agenda_feature.data.repositories

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.realityexpander.tasky.agenda_feature.data.common.typeConverters.LocalDateTimeConverter
import com.realityexpander.tasky.agenda_feature.data.common.typeConverters.ZonedDateTimeConverter
import com.realityexpander.tasky.agenda_feature.data.repositories.eventRepository.local.entities.EventEntity
import com.realityexpander.tasky.agenda_feature.data.repositories.eventRepository.local.eventDao.eventDaoImpls.EventDaoImpl


// todo: implement database fully

@Database(
    entities = [EventEntity::class],
    version = 1
)
@TypeConverters(
    LocalDateTimeConverter::class,
    ZonedDateTimeConverter::class
)
abstract class TaskyDatabase : RoomDatabase() {
    abstract fun eventDao(): EventDaoImpl
}
