package pl.vemu.zsme.data.repo

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import pl.vemu.zsme.data.PAGE_SIZE
import pl.vemu.zsme.data.PostRemoteMediator
import pl.vemu.zsme.data.db.Database
import pl.vemu.zsme.data.db.PostDAO
import pl.vemu.zsme.data.db.RemoteKeyDAO
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
    @OptIn(ExperimentalPagingApi::class)
    fun searchPosts(query: String?) = Pager(
        config = PagingConfig(
            pageSize = PAGE_SIZE,
            maxSize = 3 * PAGE_SIZE,
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
