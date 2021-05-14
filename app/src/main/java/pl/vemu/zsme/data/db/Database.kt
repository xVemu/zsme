package pl.vemu.zsme.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import pl.vemu.zsme.data.model.PostModel
import pl.vemu.zsme.data.model.RemoteKeyModel

@Database(entities = [PostModel::class, RemoteKeyModel::class], version = 1)
@TypeConverters(Converters::class)
abstract class Database : RoomDatabase() {
    abstract fun postDao(): PostDAO
    abstract fun remoteKeyDao(): RemoteKeyDAO
}
