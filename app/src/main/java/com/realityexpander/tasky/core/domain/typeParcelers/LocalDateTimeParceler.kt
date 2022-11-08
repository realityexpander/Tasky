package com.realityexpander.tasky.core.domain.typeParcelers

import android.os.Parcel
import kotlinx.parcelize.Parceler
import java.time.LocalDateTime

object LocalDateTimeParceler : Parceler<LocalDateTime> {
    override fun create(parcel: Parcel): LocalDateTime {
        return LocalDateTime.parse(parcel.readString())
    }

    override fun LocalDateTime.write(parcel: Parcel, flags: Int) {
        parcel.writeString(this.toString())
    }

}