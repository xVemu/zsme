package pl.vemu.zsme.data.db

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow
import pl.vemu.zsme.data.model.PostModel

@Dao
interface PostDAO {

    @Upsert
    suspend fun insertAll(postmodels: List<PostModel>)

    @Upsert
    suspend fun insert(postModel: PostModel)

    @Query("SELECT * FROM posts WHERE (:query IS NULL OR excerpt LIKE '%' || :query || '%') ORDER BY date DESC")
    fun searchPosts(query: String?): PagingSource<Int, PostModel>

    @Query("DELETE FROM posts WHERE (:query IS NULL OR excerpt LIKE '%' || :query || '%')")
    fun deleteByQuery(query: String?)

    @Query("SELECT * FROM posts ORDER by date DESC")
    fun getAll(): Flow<PostModel>
}
