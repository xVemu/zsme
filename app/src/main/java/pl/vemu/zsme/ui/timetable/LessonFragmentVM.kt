package pl.vemu.zsme.ui.timetable

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import pl.vemu.zsme.State
import pl.vemu.zsme.model.LessonModel
import pl.vemu.zsme.repo.LessonRepo

class LessonFragmentVM constructor(
        lessonRepo: LessonRepo,
        url: String,
) : ViewModel() {
    private val _list = MutableStateFlow<State<List<List<LessonModel>>>>(State.Success(emptyList()))
    val list: StateFlow<State<List<List<LessonModel>>>>
        get() = _list

    init {
        viewModelScope.launch(Dispatchers.IO) {
            _list.emit(State.Loading())
            try {
                _list.emit(State.Success(lessonRepo.getLesson(url)))
            } catch (e: Exception) {
                _list.emit(State.Error(e))
            }
        }
    }

    /*override fun onCleared() {
        list.value = ArrayList()
    }*/
}