package pl.vemu.zsme.data.repo

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import org.koin.core.annotation.Single
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import org.koin.core.parameter.parametersOf
import pl.vemu.zsme.data.PAGE_SIZE
import pl.vemu.zsme.data.PostRemoteMediator
import pl.vemu.zsme.data.db.PostDAO
import pl.vemu.zsme.data.model.Author
import pl.vemu.zsme.data.model.Category
import pl.vemu.zsme.data.service.ZSMEService

@Single
class PostRepo(
    private val postDAO: PostDAO,
    private val zsmeService: ZSMEService,
) : KoinComponent {
    @OptIn(ExperimentalPagingApi::class)
    fun searchPosts(query: String, categories: List<Category>, authors: List<Author>) = Pager(
        config = PagingConfig(
            pageSize = PAGE_SIZE,
            maxSize = 3 * PAGE_SIZE,
        ),
        remoteMediator = get<PostRemoteMediator> { parametersOf(query, categories, authors) }
    ) {
        postDAO.searchPosts(
            query,
            authors.ifEmpty { null }?.map { it.name },
            categories.ifEmpty { null }?.map { it.name },
        )
    }.flow

    suspend fun getCategories() = zsmeService.getCategories()

    suspend fun getAuthors() = zsmeService.getAuthors()
}
