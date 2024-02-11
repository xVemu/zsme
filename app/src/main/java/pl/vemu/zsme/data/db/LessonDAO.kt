package pl.vemu.zsme.data.db

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import pl.vemu.zsme.data.model.LessonModel

@Dao
interface LessonDAO {
    @Query("DELETE FROM lesson WHERE parentUrl = :url")
    suspend fun deleteAllWithUrl(url: String)

    @Query("SELECT * FROM lesson WHERE parentUrl = :url")
    suspend fun getAllWithUrl(url: String): List<LessonModel>

    @Upsert
    suspend fun insertAll(postmodels: List<LessonModel>)

    @Transaction
    suspend fun updateAllWithUrl(url: String, postmodels: List<LessonModel>) {
        deleteAllWithUrl(url)
        insertAll(postmodels)
    }
}
