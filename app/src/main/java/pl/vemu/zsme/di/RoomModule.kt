package pl.vemu.zsme.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ViewModelScoped
import pl.vemu.zsme.db.Database


@Module
@InstallIn(ViewModelComponent::class)
class RoomModule {

    @Provides
    @ViewModelScoped
    fun provideDatabase(@ApplicationContext context: Context) = Room.databaseBuilder(
            context,
            Database::class.java,
            "zsme-database"
    ).build()


    @Provides
    @ViewModelScoped
    fun providePostDAO(database: Database) = database.postDao()

}