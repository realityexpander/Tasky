package com.realityexpander.tasky.core.data.settings

import androidx.datastore.core.DataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

class DataStoreFake<T>(override val data: Flow<T>) : DataStore<T> {
    override suspend fun updateData(transform: suspend (T) -> T): T {
        return data.map {
            transform(it)
        }.first()
    }
}
