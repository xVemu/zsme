package pl.vemu.zsme.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import pl.vemu.zsme.Converters
import pl.vemu.zsme.model.Post

@Database(entities = [Post::class], version = 1,/*TODO*/ exportSchema = false)
@TypeConverters(Converters::class)
abstract class Database : RoomDatabase() {
    abstract fun postDao(): PostDAO
}
