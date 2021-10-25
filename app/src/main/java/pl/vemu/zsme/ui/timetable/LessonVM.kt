package pl.vemu.zsme.ui.timetable

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import pl.vemu.zsme.data.model.LessonModel
import pl.vemu.zsme.data.repo.LessonRepo
import javax.inject.Inject

@HiltViewModel
class LessonVM @Inject constructor(
    private val lessonRepo: LessonRepo
) : ViewModel() {
    private val _list = MutableStateFlow<List<List<LessonModel>>>(emptyList())
    val list = _list.asStateFlow()

    fun init(url: String) {
        viewModelScope.launch(Dispatchers.IO) {
            _list.value = lessonRepo.getLesson(url)
        }
    }
}