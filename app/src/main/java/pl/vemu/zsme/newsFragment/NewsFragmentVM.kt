package pl.vemu.zsme.newsFragment

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import pl.vemu.zsme.State
import pl.vemu.zsme.model.PostModel
import pl.vemu.zsme.repo.NewsRepo
import javax.inject.Inject

@HiltViewModel
class NewsFragmentVM @Inject constructor(
        private val savedStateHandle: SavedStateHandle,
        private val newsRepo: NewsRepo,
) : ViewModel() {

    private val _posts = MutableStateFlow<State<List<PostModel>>>(State.Success(emptyList()))

    val posts: StateFlow<State<List<PostModel>>>
        get() = _posts

    init {
        fetchPosts()
    }

    fun fetchPosts() {
        viewModelScope.launch {
            _posts.emit(State.Loading())
            try {
                _posts.emit(State.Success(newsRepo.getPosts()))
            } catch (e: Exception) {
                _posts.emit(State.Error(Throwable(e.message)))
            }
        }
    }

    /*private var page = 1*/

    /*fun downloadNews(query: Queries?) {
        Thread {
            isRefreshing.postValue(true)
            try {
                val items: MutableList<NewsItem> = ArrayList(Objects.requireNonNull(list.value))
                items.addAll(DownloadNews.INSTANCE.downloadNews(query, page))
                list.postValue(items)
                page++
            } catch (e: IOException) {
                e.printStackTrace()
                if (page == 1) notFound.postValue(true)
            } finally {
                isRefreshing.postValue(false)
            }
        }.start()
    }

    fun clearList() {
        page = 1
        notFound.postValue(false)
        list.postValue(ArrayList())
    }*/
}