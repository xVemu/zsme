package pl.vemu.zsme.data

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import pl.vemu.zsme.data.db.Database
import pl.vemu.zsme.data.db.PostDAO
import pl.vemu.zsme.data.db.RemoteKeyDAO
import pl.vemu.zsme.data.model.PostModel
import pl.vemu.zsme.data.model.RemoteKeyModel
import pl.vemu.zsme.data.service.ZSMEService
import pl.vemu.zsme.util.mappers.PostMapper
import java.io.InvalidObjectException

private const val DEFAULT_PAGE = 1

@OptIn(ExperimentalPagingApi::class)
class PostRemoteMediator constructor(
        private val query: String,
        private val postDAO: PostDAO,
        private val remoteKeyDAO: RemoteKeyDAO,
        private val database: Database,
        private val zsmeService: ZSMEService,
        private val postMapper: PostMapper,
) : RemoteMediator<Int, PostModel>() {
    override suspend fun load(loadType: LoadType, state: PagingState<Int, PostModel>): MediatorResult {
        val page = when (loadType) {
            LoadType.REFRESH -> {
                val remoteKeys = getRemoteKeyClosestToCurrentPosition(state)
                remoteKeys?.nextKey?.minus(1) ?: DEFAULT_PAGE
            }
            LoadType.PREPEND -> {
                val remoteKeys = getRemoteKeyForFirstItem(state)
                        ?: throw InvalidObjectException("Result is empty") // TODO
                remoteKeys.prevKey ?: return MediatorResult.Success(true)
            }
            LoadType.APPEND -> {
                val remoteKeys = getRemoteKeyForLastItem(state)
                        ?: throw InvalidObjectException("Result is empty")
                remoteKeys.nextKey ?: return MediatorResult.Success(true)
            }
        }
        val response = zsmeService.searchPosts(query, page, state.config.pageSize)
        val endOfPaginationReached = response.size < state.config.pageSize
        database.withTransaction {
            if (loadType == LoadType.REFRESH) {
                remoteKeyDAO.clearAll()
                postDAO.clearAll()
            }
            val prevKey = if (page == DEFAULT_PAGE) null else page - 1
            val nextKey = if (endOfPaginationReached) null else page + 1
            val keys = response.map { remoteKeyModel ->
                RemoteKeyModel(remoteKeyModel.id, prevKey, nextKey)
            }
            remoteKeyDAO.insertAll(keys)
            postDAO.insertAll(postMapper.mapFromEntityList(response))
        }
        return MediatorResult.Success(endOfPaginationReached)
    }

    private suspend fun getRemoteKeyForLastItem(state: PagingState<Int, PostModel>): RemoteKeyModel? =
            state.lastItemOrNull()?.let { post ->
                database.withTransaction {
                    remoteKeyDAO.remoteKeyByQueryId(post.id)
                }
            }

    private suspend fun getRemoteKeyForFirstItem(state: PagingState<Int, PostModel>): RemoteKeyModel? =
            state.firstItemOrNull()?.let { post ->
                database.withTransaction {
                    remoteKeyDAO.remoteKeyByQueryId(post.id)
                }
            }

    private suspend fun getRemoteKeyClosestToCurrentPosition(state: PagingState<Int, PostModel>): RemoteKeyModel? =
            state.anchorPosition?.let { position ->
                state.closestItemToPosition(position)?.id?.let { id ->
                    remoteKeyDAO.remoteKeyByQueryId(id)
                }
            }


}