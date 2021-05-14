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
import retrofit2.HttpException
import java.io.IOException
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
    //TODO on first load, loading overall
    override suspend fun load(loadType: LoadType, state: PagingState<Int, PostModel>): MediatorResult {
        val page = when (loadType) {
            LoadType.APPEND -> {
                val remoteKeys = getRemoteKeyForLastItem(state)

                val nextKey = remoteKeys?.nextKey
                        ?: return MediatorResult.Success(remoteKeys != null)
                nextKey
            }
            LoadType.PREPEND -> {
                MediatorResult.Success(true)
                /*val remoteKeys = getRemoteKeyForFirstItem(state)

                val prevKey = remoteKeys?.prevKey
                        ?: return MediatorResult.Success(remoteKeys != null)
                prevKey*/
            }
            LoadType.REFRESH -> {
                val remoteKeys = getRemoteKeyClosestToCurrentPosition(state)
                remoteKeys?.nextKey?.minus(1) ?: DEFAULT_PAGE
            }
        }
        try {
            val posts = postMapper.mapFromEntityList(zsmeService.searchPosts(query, page, state.config.pageSize))
            database.withTransaction {
                // clear all tables in the database
                if (loadType == LoadType.REFRESH) {
                    remoteKeyDAO.clearAll()
                    postDAO.clearAll()
                }
                val prevKey = if (page == DEFAULT_PAGE) null else page - 1
                val nextKey = if (posts.isEmpty()) null else page + 1
                val keys = posts.map {
                    RemoteKeyModel(it.id, prevKey, nextKey)
                }
                remoteKeyDAO.insertAll(keys)
                postDAO.insertAll(posts)
            }
            return MediatorResult.Success(posts.isEmpty())
        } catch (exception: IOException) {
            return MediatorResult.Error(exception)
        } catch (exception: HttpException) {
            return MediatorResult.Error(exception)
        }
    }

    private suspend fun getRemoteKeyForFirstItem(state: PagingState<Int, PostModel>): RemoteKeyModel? =
            state.pages
                    .firstOrNull() { it.data.isNotEmpty() }
                    ?.data?.firstOrNull()
                    ?.let { post -> remoteKeyDAO.remoteKeyByQueryId(post.id) }

    private suspend fun getRemoteKeyForLastItem(state: PagingState<Int, PostModel>): RemoteKeyModel? =
            state.pages
                    .lastOrNull { it.data.isNotEmpty() }
                    ?.data?.lastOrNull()
                    ?.let { post -> remoteKeyDAO.remoteKeyByQueryId(post.id) }

    private suspend fun getRemoteKeyClosestToCurrentPosition(state: PagingState<Int, PostModel>): RemoteKeyModel? =
            state.anchorPosition?.let { position ->
                state.closestItemToPosition(position)?.id?.let { id ->
                    remoteKeyDAO.remoteKeyByQueryId(id)
                }
            }

    private suspend fun getKeyPageData(loadType: LoadType, state: PagingState<Int, PostModel>): Any? {
        return when (loadType) {
            LoadType.REFRESH -> {
                val remoteKeys = getRemoteKeyClosestToCurrentPosition(state)
                remoteKeys?.nextKey?.minus(1) ?: DEFAULT_PAGE
            }
            LoadType.APPEND -> {
                val remoteKeys = getRemoteKeyForLastItem(state)
                        ?: throw InvalidObjectException("Remote key should not be null for $loadType")
                remoteKeys.nextKey
            }
            LoadType.PREPEND -> {
                val remoteKeys = getRemoteKeyForFirstItem(state)
                        ?: throw InvalidObjectException("Invalid state, key should not be null")
                //end of list condition reached
                remoteKeys.prevKey ?: return MediatorResult.Success(endOfPaginationReached = true)
                remoteKeys.prevKey
            }
        }
    }

}