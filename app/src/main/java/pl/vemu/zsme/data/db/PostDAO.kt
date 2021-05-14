package pl.vemu.zsme.data.db

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import pl.vemu.zsme.data.model.PostModel

@Dao
interface PostDAO {

    @Query("SELECT * FROM Posts ORDER by id DESC")
    suspend fun getAll(): List<PostModel>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(postmodels: List<PostModel>)

    @Query("SELECT * FROM Posts WHERE excerpt LIKE :query")
    fun searchPosts(query: String): PagingSource<Int, PostModel>

    @Query("DELETE FROM Posts")
    suspend fun clearAll()


}