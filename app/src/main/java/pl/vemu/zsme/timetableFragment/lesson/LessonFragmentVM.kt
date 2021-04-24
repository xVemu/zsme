package pl.vemu.zsme.timetableFragment.lesson

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class LessonFragmentVM(url: String) : ViewModel() {
    private val _list: MutableLiveData<Array<Array<Lesson>>> by lazy {
        MutableLiveData<Array<Array<Lesson>>>().also {
            CoroutineScope(Dispatchers.IO).launch {
                it.postValue(LessonRepo.downloadLessons(url))
            }
        }
    }
    val list: LiveData<Array<Array<Lesson>>>
        get() = _list

    /*override fun onCleared() {
        list.value = ArrayList()
    }*/
}