package pl.vemu.zsme.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import pl.vemu.zsme.Converters
import pl.vemu.zsme.model.PostModel

@Database(entities = [PostModel::class], version = 1)
@TypeConverters(Converters::class)
abstract class Database : RoomDatabase() {
    abstract fun postDao(): PostDAO
}
