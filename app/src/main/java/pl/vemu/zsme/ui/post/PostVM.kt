package pl.vemu.zsme.ui.post

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flatMapLatest
import pl.vemu.zsme.data.repo.PostRepo
import javax.inject.Inject

@HiltViewModel
class PostVM @Inject constructor(
    postRepo: PostRepo,
) : ViewModel() {

    private val _query: MutableStateFlow<String?> = MutableStateFlow(null)
    val query = _query.asStateFlow()

    fun setQuery(query: String?) {
        this._query.value = query
    }

    @OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
    val posts =
        _query.debounce(500L).flatMapLatest { postRepo.searchPosts(it) }.cachedIn(viewModelScope)
}
