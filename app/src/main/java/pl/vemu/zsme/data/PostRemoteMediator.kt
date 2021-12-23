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

@OptIn(ExperimentalPagingApi::class)
class PostRemoteMediator constructor(
    private val query: String?,
    private val postDAO: PostDAO,
    private val remoteKeyDAO: RemoteKeyDAO,
    private val db: Database,
    private val zsmeService: ZSMEService,
    private val postMapper: PostMapper,
) : RemoteMediator<Int, PostModel>() {

    override suspend fun load(
        loadType: LoadType, state: PagingState<Int, PostModel>
    ): MediatorResult {
        return try {
            val page = when (val pageKeyData = getKeyPageData(loadType, state)) {
                is MediatorResult.Success -> {
                    return pageKeyData
                }
                else -> {
                    pageKeyData as Int
                }
            }
            val response = zsmeService.searchPosts(query, page, state.config.pageSize)
            val isEndOfList = response.isEmpty() or (response.size < state.config.pageSize)
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
            MediatorResult.Success(endOfPaginationReached = isEndOfList)
        } catch (exception: IOException) {
            MediatorResult.Error(exception)
        } catch (exception: HttpException) {
            MediatorResult.Error(exception)
        }
    }

    private suspend fun getKeyPageData(
        loadType: LoadType,
        state: PagingState<Int, PostModel>
    ) =
        when (loadType) {
            LoadType.REFRESH -> {
                val remoteKeys = getRemoteKeyClosestToCurrentPosition(state)
                remoteKeys?.nextKey?.minus(1) ?: DEFAULT_PAGE
            }
            LoadType.APPEND -> {
                val remoteKeys = getRemoteKeyForLastItem(state)
                val nextKey = remoteKeys?.nextKey
                nextKey ?: MediatorResult.Success(endOfPaginationReached = false)
            }
            LoadType.PREPEND -> {
                val remoteKeys = getRemoteKeyForFirstItem(state)
                val prevKey = remoteKeys?.prevKey
                prevKey ?: MediatorResult.Success(endOfPaginationReached = false)
            }
        }

    private suspend fun getRemoteKeyClosestToCurrentPosition(state: PagingState<Int, PostModel>): RemoteKeyModel? =
        state.anchorPosition?.let { position ->
            state.closestItemToPosition(position)?.id?.let { postModelId ->
                remoteKeyDAO.remoteKeyByQueryId(postModelId)
            }
        }


    private suspend fun getRemoteKeyForLastItem(state: PagingState<Int, PostModel>): RemoteKeyModel? =
        state.lastItemOrNull()?.let { postModel -> remoteKeyDAO.remoteKeyByQueryId(postModel.id) }


    private suspend fun getRemoteKeyForFirstItem(state: PagingState<Int, PostModel>): RemoteKeyModel? =
        state.firstItemOrNull()?.let { postModel -> remoteKeyDAO.remoteKeyByQueryId(postModel.id) }

}