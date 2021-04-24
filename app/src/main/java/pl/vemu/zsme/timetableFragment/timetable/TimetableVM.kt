package pl.vemu.zsme.timetableFragment.timetable

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class TimetableVM : ViewModel() {
    private val _list = MutableLiveData<Array<Array<Timetable>>>(emptyArray())
    val list: LiveData<Array<Array<Timetable>>>
        get() = _list

    fun downloadTimetable() {
        CoroutineScope(Dispatchers.IO).launch {
            _list.postValue(TimetableRepo.downloadTimetable())
        }
    }

    /*override fun onCleared() {
        list.value = ArrayList()
    }*/
}