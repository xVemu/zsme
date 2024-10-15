package pl.vemu.zsme.data.model

import androidx.compose.runtime.Immutable
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import kotlinx.datetime.*

@Immutable
@Entity(
    tableName = "lesson",
    primaryKeys = ["index", "day", "name"],
    indices = [Index(value = ["parentUrl"])],
    foreignKeys = [
        ForeignKey(
            entity = TimetableModel::class,
            parentColumns = ["url"],
            childColumns = ["parentUrl"],
            deferred = true,
        ),
    ],
)
data class LessonModel(
    val name: String,
    val room: String?,
    val teacher: String?,
    val timeStart: LocalTime,
    val timeFinish: LocalTime,
    val index: Int,
    val showIndex: Boolean,
    val day: DayOfWeek,
    val parentUrl: String,
) {
    val isActive: Boolean
        get() = Clock.System.now()
            .toLocalDateTime(TimeZone.currentSystemDefault())
            .let { it.dayOfWeek == day && it.time in timeStart..timeFinish }
}
