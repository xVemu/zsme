package pl.vemu.zsme.timetableFragment.lesson

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class LessonFragmentVM(url: String) : ViewModel() {
    private val _list: MutableLiveData<List<List<Lesson>>> by lazy {
        MutableLiveData<List<List<Lesson>>>().also {
            CoroutineScope(Dispatchers.IO).launch {
                it.postValue(LessonRepo.downloadLessons(url))
            }
        }
    }
    val list: LiveData<List<List<Lesson>>>
        get() = _list

    /*override fun onCleared() {
        list.value = ArrayList()
    }*/
}