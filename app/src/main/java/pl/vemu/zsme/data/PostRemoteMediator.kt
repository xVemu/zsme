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
            val page = when (loadType) {
                LoadType.REFRESH -> DEFAULT_PAGE
                LoadType.APPEND -> {
                    val remoteKey = state.getRemoteKeyForLastItem()
                    val nextKey = remoteKey?.nextKey
                    nextKey
                        ?: return MediatorResult.Success(endOfPaginationReached = remoteKey != null)
                }

                LoadType.PREPEND -> {
                    return MediatorResult.Success(endOfPaginationReached = true)
                }
            }

            val response = zsmeService.searchPosts(query, page, state.config.pageSize)
            val isEndOfList = response.isEmpty() || response.size < state.config.pageSize

            db.withTransaction {
                if (loadType == LoadType.REFRESH) {
                    postDAO.deleteByQuery(query)
                    remoteKeyDAO.clearAll()
                }
                val nextKey = if (isEndOfList) null else page + 1
                val keys = response.map {
                    RemoteKeyModel(it.id, nextKey)
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


    private suspend fun PagingState<Int, PostModel>.getRemoteKeyForLastItem(): RemoteKeyModel? =
        lastItemOrNull()?.let { postModel -> remoteKeyDAO.remoteKeyByQueryId(postModel.id) }

}
