package pl.vemu.zsme.data.db

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import pl.vemu.zsme.data.model.TimetableModel

@Dao
interface TimetableDAO {
    @Query("DELETE FROM timetable")
    suspend fun deleteAll()

    @Query("SELECT * FROM timetable")
    suspend fun getAll(): List<TimetableModel>

    @Upsert
    suspend fun insertAll(postmodels: List<TimetableModel>)

    @Transaction
    suspend fun updateAll(postmodels: List<TimetableModel>) {
        deleteAll()
        insertAll(postmodels)
    }
}
