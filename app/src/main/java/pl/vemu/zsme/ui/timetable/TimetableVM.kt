package pl.vemu.zsme.ui.timetable

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import pl.vemu.zsme.State
import pl.vemu.zsme.model.TimetableModel
import javax.inject.Inject

@HiltViewModel
class TimetableVM @Inject constructor(
        private val timetableRepo: TimetableRepo,
) : ViewModel() {
    private val _list = MutableStateFlow<State<List<List<TimetableModel>>>>(State.Success(emptyList()))
    val list: StateFlow<State<List<List<TimetableModel>>>>
        get() = _list

    fun fetchTimetable() {
        viewModelScope.launch(Dispatchers.IO) {
            _list.emit(State.Loading())
            try {
                _list.emit(State.Success(timetableRepo.getTimetable()))
            } catch (e: Exception) {
                _list.emit(State.Error(e))
            }
        }
    }

    /*override fun onCleared() {
        list.value = ArrayList()
    }*/
}