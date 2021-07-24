package pl.vemu.zsme.ui.news

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.ExperimentalPagingApi
import androidx.paging.cachedIn
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import pl.vemu.zsme.data.repo.PostRepo
import javax.inject.Inject

//TODO change name to PostViewModel, others viewmodels too and change News to Post
@HiltViewModel
class NewsFragmentVM @Inject constructor(
    postRepo: PostRepo,
) : ViewModel() {

    val query = MutableStateFlow("")


    @ExperimentalPagingApi
    val posts = query.flatMapLatest { postRepo.searchPosts(it) }.cachedIn(viewModelScope)
}