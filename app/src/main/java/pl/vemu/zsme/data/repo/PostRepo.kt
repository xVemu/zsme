package pl.vemu.zsme.data.repo

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import pl.vemu.zsme.data.PostRemoteMediator
import pl.vemu.zsme.data.db.Database
import pl.vemu.zsme.data.db.PostDAO
import pl.vemu.zsme.data.db.RemoteKeyDAO
import pl.vemu.zsme.data.model.PostModel
import pl.vemu.zsme.data.service.ZSMEService
import pl.vemu.zsme.util.mappers.PostMapper
import javax.inject.Inject

class PostRepo @Inject constructor(
        private val postDAO: PostDAO,
        private val remoteKeyDAO: RemoteKeyDAO,
        private val database: Database,
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
    @OptIn(ExperimentalPagingApi::class)
    fun searchPosts(query: String) = Pager(
            config = PagingConfig(
                    pageSize = 10,
                    enablePlaceholders = false,
                    maxSize = 100,
            ),
            remoteMediator = PostRemoteMediator(
                    query,
                    postDAO,
                    remoteKeyDAO,
                    database,
                    zsmeService,
                    postMapper,
            )
    ) {
        postDAO.searchPosts(query)
    }.flow
}