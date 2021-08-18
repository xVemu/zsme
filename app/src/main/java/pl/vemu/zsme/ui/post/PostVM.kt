package pl.vemu.zsme.ui.post

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import pl.vemu.zsme.data.repo.PostRepo
import javax.inject.Inject

@HiltViewModel
class PostVM @Inject constructor(
    postRepo: PostRepo,
) : ViewModel() {

    val query = MutableStateFlow("")


    val posts = query.flatMapLatest { postRepo.searchPosts(it) }.cachedIn(viewModelScope)
}