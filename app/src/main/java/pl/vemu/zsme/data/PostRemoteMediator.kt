package pl.vemu.zsme.data

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import org.koin.core.annotation.Factory
import org.koin.core.annotation.InjectedParam
import pl.vemu.zsme.data.db.Database
import pl.vemu.zsme.data.db.PostDAO
import pl.vemu.zsme.data.db.RemoteKeyDAO
import pl.vemu.zsme.data.model.*
import pl.vemu.zsme.data.service.ZSMEService
import pl.vemu.zsme.util.EntityMapper

const val DEFAULT_PAGE = 1
const val PAGE_SIZE = 10

@OptIn(ExperimentalPagingApi::class)
@Factory([PostRemoteMediator::class])
class PostRemoteMediator(
    @InjectedParam private val query: String,
    @InjectedParam private val categories: List<Category>,
    @InjectedParam private val authors: List<Author>,
    private val postDAO: PostDAO,
    private val remoteKeyDAO: RemoteKeyDAO,
    private val db: Database,
    private val zsmeService: ZSMEService,
    private val postMapper: EntityMapper<PostEntity, PostModel>,
) : RemoteMediator<Int, PostModel>() {

    override suspend fun load(
        loadType: LoadType, state: PagingState<Int, PostModel>,
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

            val response =
                zsmeService.searchPosts(
                    query,
                    page,
                    state.config.pageSize,
                    categories.map { it.id },
                    authors.map { it.id },
                )
            val isEndOfList = response.isEmpty() || response.size < state.config.pageSize

            db.withTransaction {
                if (loadType == LoadType.REFRESH) {
                    postDAO.delete(
                        query,
                        authors.ifEmpty { null }?.map { it.name },
                        categories.ifEmpty { null }?.map { it.name },
                    )
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
        } catch (e: Exception) {
            MediatorResult.Error(e)
        }
    }

    private suspend fun PagingState<Int, PostModel>.getRemoteKeyForLastItem(): RemoteKeyModel? =
        lastItemOrNull()?.let { postModel -> remoteKeyDAO.remoteKeyByQueryId(postModel.id) }
}
