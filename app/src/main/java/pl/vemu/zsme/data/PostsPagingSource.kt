package pl.vemu.zsme.data

import androidx.paging.PagingSource
import androidx.paging.PagingState
import pl.vemu.zsme.data.model.PostModel
import pl.vemu.zsme.data.service.ZSMEService
import pl.vemu.zsme.util.mappers.PostMapper
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

private const val DEFAULT_PAGE = 1

class PostsPagingSource @Inject constructor(
        private val zsmeService: ZSMEService,
        private val postMapper: PostMapper, //TODO remove?
        private val query: String,
) : PagingSource<Int, PostModel>() {
    override fun getRefreshKey(state: PagingState<Int, PostModel>): Int? = state.anchorPosition?.let { anchorPosition ->
        val anchorPage = state.closestPageToPosition(anchorPosition)
        anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
    }

    //TODO error handling
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, PostModel> {
        return try {
            val page = params.key ?: DEFAULT_PAGE
            val posts = postMapper.mapFromEntityList(zsmeService.searchPosts(query, page, params.loadSize))
            LoadResult.Page(
                    data = posts,
                    prevKey = if (page == DEFAULT_PAGE) null else page - 1,
                    nextKey = if (posts.isEmpty()) null else page + 1,
            )
        } catch (e: IOException) {
            LoadResult.Error(e)
        } catch (e: HttpException) {
            LoadResult.Error(e)
        }
    }
}