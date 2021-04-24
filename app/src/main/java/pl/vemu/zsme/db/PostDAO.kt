package pl.vemu.zsme.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import pl.vemu.zsme.model.Post

@Dao
interface PostDAO {

    @Query("SELECT * FROM Post")
    suspend fun getAll(): List<Post>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(posts: List<Post>)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(post: Post)

}