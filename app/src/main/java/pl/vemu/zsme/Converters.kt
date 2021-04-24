package pl.vemu.zsme

import androidx.room.TypeConverter
import java.util.*

// TODO
class Converters {
    @TypeConverter
    fun fromTimestamp(value: Long) = Date(value)

    @TypeConverter
    fun toTimestamp(date: Date) = date.time

    @TypeConverter
    fun fromList(list: List<Int>) = list[0]

    @TypeConverter
    fun toList(int: Int) = listOf(int)
}