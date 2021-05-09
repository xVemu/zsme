package pl.vemu.zsme.ui.news

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import pl.vemu.zsme.data.repo.PostRepo
import javax.inject.Inject

//TODO change name to PostViewModel, others viewmodels too and change News to Post
@HiltViewModel
class NewsFragmentVM @Inject constructor(
        private val postRepo: PostRepo,
) : ViewModel() {

    val query = MutableStateFlow("")

    var posts = query.flatMapLatest { postRepo.searchPosts(it) }.cachedIn(viewModelScope)

    fun fetchPosts() {
        posts = postRepo.searchPosts(query.value).cachedIn(viewModelScope)
    }
}