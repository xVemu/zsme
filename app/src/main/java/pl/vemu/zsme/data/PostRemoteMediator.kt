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

const val DEFAULT_PAGE = 1
const val PAGE_SIZE = 10

@ExperimentalPagingApi
class PostRemoteMediator constructor(
    private val query: String,
    private val postDAO: PostDAO,
    private val remoteKeyDAO: RemoteKeyDAO,
    private val db: Database,
    private val zsmeService: ZSMEService,
    private val postMapper: PostMapper,
) : RemoteMediator<Int, PostModel>() {

    override suspend fun load(
        loadType: LoadType, state: PagingState<Int, PostModel>
    ): MediatorResult { //TODO rewrite
        val page = when (val pageKeyData = getKeyPageData(loadType, state)) {
            is MediatorResult.Success -> {
                return pageKeyData
            }
            else -> {
                pageKeyData as Int
            }
        }

        try {
            val response = zsmeService.searchPosts(query, page, state.config.pageSize)
            val isEndOfList = response.isEmpty()
            db.withTransaction {
                if (loadType == LoadType.REFRESH) {
                    postDAO.clearAll()
                    remoteKeyDAO.clearAll()
                }
                val prevKey = if (page == DEFAULT_PAGE) null else page - 1
                val nextKey = if (isEndOfList) null else page + 1
                val keys = response.map {
                    RemoteKeyModel(it.id, prevKey, nextKey)
                }
                remoteKeyDAO.insertAll(keys)
                postDAO.insertAll(postMapper.mapFromEntityList(response))
            }
            return MediatorResult.Success(endOfPaginationReached = isEndOfList)
        } catch (exception: IOException) {
            return MediatorResult.Error(exception)
        } catch (exception: HttpException) {
            return MediatorResult.Error(exception)
        }
    }

    private suspend fun getKeyPageData(
        loadType: LoadType,
        state: PagingState<Int, PostModel>
    ): Any {
        return when (loadType) {
            LoadType.REFRESH -> {
                val remoteKeys = getRemoteKeyClosestToCurrentPosition(state)
                remoteKeys?.nextKey?.minus(1) ?: DEFAULT_PAGE
            }
            LoadType.APPEND -> {
                val remoteKeys = getLastRemoteKey(state)
                val nextKey = remoteKeys?.nextKey
                return nextKey ?: MediatorResult.Success(endOfPaginationReached = false)
            }
            LoadType.PREPEND -> {
                val remoteKeys = getFirstRemoteKey(state)
                val prevKey = remoteKeys?.prevKey ?: return MediatorResult.Success(
                    endOfPaginationReached = false
                )
                prevKey
            }
        }
    }

    private suspend fun getRemoteKeyClosestToCurrentPosition(state: PagingState<Int, PostModel>): RemoteKeyModel? {
        return state.anchorPosition?.let { position ->
            state.closestItemToPosition(position)?.id?.let { repoId ->
                remoteKeyDAO.remoteKeyByQueryId(repoId)
            }
        }
    }

    private suspend fun getLastRemoteKey(state: PagingState<Int, PostModel>): RemoteKeyModel? =
        state.lastItemOrNull()?.let { cat -> remoteKeyDAO.remoteKeyByQueryId(cat.id) }


    private suspend fun getFirstRemoteKey(state: PagingState<Int, PostModel>): RemoteKeyModel? =
        state.firstItemOrNull()?.let { cat -> remoteKeyDAO.remoteKeyByQueryId(cat.id) }

}