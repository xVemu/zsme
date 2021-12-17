package pl.vemu.zsme.data.db

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import pl.vemu.zsme.data.model.PostModel

@Dao
interface PostDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(postmodels: List<PostModel>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(postModel: PostModel)

    @Query("SELECT * FROM Posts WHERE (:query IS NULL OR excerpt LIKE '%' || :query || '%') ORDER by date DESC")
    fun searchPosts(query: String?): PagingSource<Int, PostModel>

    @Query("SELECT * FROM Posts ORDER by date DESC")
    fun getAll(): Flow<PostModel>

    @Query("SELECT * FROM Posts WHERE id=:id")
    suspend fun getById(id: Int): PostModel

    @Query("DELETE FROM Posts")
    suspend fun clearAll()

}