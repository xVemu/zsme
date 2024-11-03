package pl.vemu.zsme.injection

import android.content.Context
import androidx.room.Room
import org.koin.core.annotation.Module
import org.koin.core.annotation.Single
import pl.vemu.zsme.data.db.Database

@Module
class RoomModule {

    @Single
    fun provideDatabase(context: Context) = Room.databaseBuilder(
        context,
        Database::class.java,
        "zsme-database"
    )
        .fallbackToDestructiveMigration()
        .build()

    @Single
    fun providePostDAO(database: Database) = database.postDao()

    @Single
    fun provideRemoteKeyDAO(database: Database) = database.remoteKeyDao()

    @Single
    fun provideTimetableDAO(database: Database) = database.timetableDao()

    @Single
    fun provideLessonDAO(database: Database) = database.lessonDao()
}
