package pl.vemu.zsme.data.db

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import pl.vemu.zsme.data.model.PostModel

@Dao
interface PostDAO {

    @Upsert
    suspend fun insertAll(postmodels: List<PostModel>)

    @Query(
        """SELECT * FROM post WHERE
        excerpt LIKE '%' || :query || '%'
        AND (author IN (:authors) OR coalesce(:authors, 'null') = 'null')
        AND (category IN (:categories) OR coalesce(:categories, 'null') = 'null')
        ORDER BY date DESC
        """
    )
    fun searchPosts(
        query: String,
        authors: List<String>?,
        categories: List<String>?,
    ): PagingSource<Int, PostModel>

    @Query(
        """DELETE FROM post WHERE
        excerpt LIKE '%' || :query || '%'
        AND (author IN (:authors) OR coalesce(:authors, 'null') = 'null')
        AND (category IN (:categories) OR coalesce(:categories, 'null') = 'null')
        """
    )
    suspend fun delete(query: String, authors: List<String>?, categories: List<String>?)
}
