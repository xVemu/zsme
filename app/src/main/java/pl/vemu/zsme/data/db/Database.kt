package pl.vemu.zsme.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import pl.vemu.zsme.data.model.LessonModel
import pl.vemu.zsme.data.model.PostModel
import pl.vemu.zsme.data.model.RemoteKeyModel
import pl.vemu.zsme.data.model.TimetableModel

@Database(
    entities = [PostModel::class, RemoteKeyModel::class, TimetableModel::class, LessonModel::class],
    version = 2,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class Database : RoomDatabase() {
    abstract fun postDao(): PostDAO
    abstract fun remoteKeyDao(): RemoteKeyDAO
    abstract fun timetableDao(): TimetableDAO
    abstract fun lessonDao(): LessonDAO
}
