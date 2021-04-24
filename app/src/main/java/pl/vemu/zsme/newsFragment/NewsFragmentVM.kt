package pl.vemu.zsme.newsFragment

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import pl.vemu.zsme.model.Post
import pl.vemu.zsme.repo.NewsRepo
import javax.inject.Inject

@HiltViewModel
class NewsFragmentVM @Inject constructor(
        private val savedStateHandle: SavedStateHandle,
        private val newsRepo: NewsRepo,
) : ViewModel() {

    private val _list: MutableLiveData<Array<Post>> by lazy {
        MutableLiveData<Array<Post>>().also {
            CoroutineScope(Dispatchers.IO).launch {
                it.postValue(newsRepo.getPosts().toTypedArray())
            }
        }
    }
    val list: LiveData<Array<Post>>
        get() = _list

    /* TODO private val notFound = MutableLiveData(false)
    private val isRefreshing = MutableLiveData(false)
    private var page = 1

    fun getNotFound(): LiveData<Boolean> {
        return notFound
    }

    fun getIsRefreshing(): LiveData<Boolean> {
        return isRefreshing
    }*/

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