package pl.vemu.zsme.injection

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import pl.vemu.zsme.data.db.Database
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
class RoomModule {

    @Singleton
    @Provides
    fun provideDatabase(@ApplicationContext context: Context) = Room.databaseBuilder(
            context,
            Database::class.java,
            "zsme-database"
    ).build()


    @Singleton
    @Provides
    fun providePostDAO(database: Database) = database.postDao()

    @Singleton
    @Provides
    fun provideRemoteKeyDAO(database: Database) = database.remoteKeyDao()

}