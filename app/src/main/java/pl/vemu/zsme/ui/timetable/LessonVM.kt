package pl.vemu.zsme.ui.timetable

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.launch
import pl.vemu.zsme.Result
import pl.vemu.zsme.ResultList
import pl.vemu.zsme.data.LessonMediator
import pl.vemu.zsme.data.model.LessonModel
import pl.vemu.zsme.ui.destinations.LessonDestination
import pl.vemu.zsme.ui.navArgs
import javax.inject.Inject

@HiltViewModel
class LessonVM @Inject constructor(
    private val mediator: LessonMediator,
    state: SavedStateHandle,
) : ViewModel() {
    private val _list =
        MutableStateFlow<ResultList<LessonModel>>(Result.Loading)
    val list = _list.asStateFlow()

    private val url: String =
        state.navArgs<LessonDestination.NavArgs>().url

    init {
        downloadLessons()
    }

    fun downloadLessons() {
        viewModelScope.launch {
            _list.emitAll(mediator.getLessons(url))
        }
    }
}
