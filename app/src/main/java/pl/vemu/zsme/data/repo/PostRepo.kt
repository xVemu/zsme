package pl.vemu.zsme.data.repo

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
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
        //TODO crash when first launch
        //TODO lags
        postDAO.searchPosts(query)
    }.flow
}