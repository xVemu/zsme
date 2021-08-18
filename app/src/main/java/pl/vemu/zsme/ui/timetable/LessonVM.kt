package pl.vemu.zsme.ui.timetable

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import pl.vemu.zsme.State
import pl.vemu.zsme.data.model.LessonModel
import pl.vemu.zsme.data.repo.LessonRepo

class LessonVM constructor(
    lessonRepo: LessonRepo,
    url: String,
) : ViewModel() {
    private val _list = MutableStateFlow<State<List<List<LessonModel>>>>(State.Success(emptyList()))
    val list = _list.asStateFlow()

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