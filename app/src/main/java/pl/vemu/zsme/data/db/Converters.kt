package pl.vemu.zsme.data.db

import android.os.Parcel
import androidx.room.TypeConverter
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlinx.parcelize.Parceler

class Converters {
    @TypeConverter
    fun fromISO(value: String) = LocalDateTime.parse(value)

    @TypeConverter
    fun toISO(date: LocalDateTime) = date.toString()

    @TypeConverter
    fun fromTime(time: String) = LocalTime.parse(time)

    @TypeConverter
    fun toTime(time: LocalTime) = time.toString()
}

object LocalDateTimeClassParceler : Parceler<LocalDateTime> {
    override fun create(parcel: Parcel) = LocalDateTime.parse(parcel.readString()!!)

    override fun LocalDateTime.write(parcel: Parcel, flags: Int) {
        parcel.writeString(this.toString())
    }
}
