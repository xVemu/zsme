package pl.vemu.zsme.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import pl.vemu.zsme.model.PostModel

@Dao
interface PostDAO {

    @Query("SELECT * FROM Posts")
    suspend fun getAll(): List<PostModel>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(postModels: List<PostModel>)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(postModel: PostModel)

}