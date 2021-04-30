package pl.vemu.zsme.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import pl.vemu.zsme.db.Database
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

}