package pl.vemu.zsme.ui.timetable

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import pl.vemu.zsme.Result
import pl.vemu.zsme.ResultList
import pl.vemu.zsme.data.model.LessonModel
import pl.vemu.zsme.data.repo.LessonRepo
import pl.vemu.zsme.ui.destinations.LessonDestination
import pl.vemu.zsme.ui.navArgs
import javax.inject.Inject

@HiltViewModel
class LessonVM @Inject constructor(
    private val lessonRepo: LessonRepo,
    state: SavedStateHandle,
) : ViewModel() {
    private val _list =
        MutableStateFlow<ResultList<List<LessonModel>>>(Result.Loading)
    val list = _list.asStateFlow()

    private val url: String

    init {
        val navArgs: LessonDestination.NavArgs = state.navArgs()
        url = navArgs.url
        downloadLessons()
    }

    fun downloadLessons() {
        viewModelScope.launch {
            _list.value = Result.Loading
            _list.value = try {
                Result.Success(lessonRepo.getLesson(url))
            } catch (e: Exception) {
                Result.Failure(e)
            }
        }
    }
}
