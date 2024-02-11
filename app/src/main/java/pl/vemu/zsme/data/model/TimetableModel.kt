package pl.vemu.zsme.data.model

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.MeetingRoom
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.School
import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.room.Entity
import androidx.room.PrimaryKey
import pl.vemu.zsme.R

@Immutable
@Entity(tableName = "timetable")
data class TimetableModel(
    val name: String,
    @PrimaryKey val url: String,
    val type: TimetableType,
)

enum class TimetableType(val selector: String, val title: Int, val icon: ImageVector) {
    GROUP("#oddzialy", R.string.timetable_groups, Icons.Rounded.School),
    TEACHER("#nauczyciele", R.string.timetable_teachers, Icons.Rounded.Person),
    ROOM("#sale", R.string.timetable_rooms, Icons.Rounded.MeetingRoom),
}
