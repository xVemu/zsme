package pl.vemu.zsme.data.repo

import androidx.paging.Pager
import androidx.paging.PagingConfig
import pl.vemu.zsme.data.PostsPagingSource
import pl.vemu.zsme.data.db.PostDAO
import pl.vemu.zsme.data.model.PostModel
import pl.vemu.zsme.data.service.ZSMEService
import pl.vemu.zsme.util.mappers.PostMapper
import javax.inject.Inject

class PostRepo @Inject constructor(
        private val postDAO: PostDAO,
        private val zsmeService: ZSMEService,
        private val postMapper: PostMapper,
) {

    suspend fun getPosts(): List<PostModel> {
        val postEntities = zsmeService.getPosts()
        val posts = postMapper.mapFromEntityList(postEntities)
        postDAO.insertAll(posts)
        return postDAO.getAll()
    }

    //TODO enable placeholders, change numbers
    fun searchPosts(query: String) = Pager(
            PagingConfig(
                    pageSize = 5,
                    enablePlaceholders = false,
                    maxSize = 15,
            )
    ) {
        PostsPagingSource(
                zsmeService,
                postMapper,
                query,
        )
    }.flow
}