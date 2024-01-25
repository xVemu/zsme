package pl.vemu.zsme.ui.post

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch
import pl.vemu.zsme.Result
import pl.vemu.zsme.ResultList
import pl.vemu.zsme.data.model.Author
import pl.vemu.zsme.data.model.Category
import pl.vemu.zsme.data.repo.PostRepo
import javax.inject.Inject

@HiltViewModel
class PostVM @Inject constructor(
    private val postRepo: PostRepo,
) : ViewModel() {
    private val _query = MutableStateFlow("")
    private val _activeCategories = MutableStateFlow(emptyList<Category>())
    private val _categories = MutableStateFlow<ResultList<Category>>(Result.Loading)
    private val _activeAuthors = MutableStateFlow(emptyList<Author>())
    private val _authors = MutableStateFlow<ResultList<Author>>(Result.Loading)
    val query = _query.asStateFlow()
    val activeCategories = _activeCategories.asStateFlow()
    val categories = _categories.asStateFlow()
    val activeAuthors = _activeAuthors.asStateFlow()
    val authors = _authors.asStateFlow()

    fun setQuery(query: String) {
        this._query.value = query
    }

    fun setCategories(categories: List<Category>) {
        this._activeCategories.value = categories
    }

    fun setAuthors(authors: List<Author>) {
        this._activeAuthors.value = authors
    }

    init {
        downloadCategoriesAndAuthors()
    }

    fun downloadCategoriesAndAuthors() {
        viewModelScope.launch {
            launch {
                _categories.value = Result.Loading
                _categories.value = try {
                    Result.Success(postRepo.getCategories())
                } catch (e: Exception) {
                    Result.Failure(e)
                }
            }
            launch {
                _authors.value = Result.Loading
                _authors.value = try {
                    Result.Success(postRepo.getAuthors())
                } catch (e: Exception) {
                    Result.Failure(e)
                }
            }
        }
    }

    @OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
    val posts =
        combine(
            // can't be combineTransform, because callback is not called when flows emit.
            _query.debounce(500),
            _activeCategories,
            _activeAuthors,
            postRepo::searchPosts,
        ).flatMapLatest { it }.cachedIn(viewModelScope)
}
