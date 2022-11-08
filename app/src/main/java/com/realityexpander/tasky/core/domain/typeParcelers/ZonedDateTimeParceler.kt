package com.realityexpander.tasky.core.domain.typeParcelers

import android.os.Parcel
import kotlinx.parcelize.Parceler
import java.time.ZonedDateTime

object ZonedDateTimeParceler : Parceler<ZonedDateTime> {
    override fun create(parcel: Parcel): ZonedDateTime {
        return ZonedDateTime.parse(parcel.readString())
    }

    override fun ZonedDateTime.write(parcel: Parcel, flags: Int) {
        parcel.writeString(this.toString())
    }

}