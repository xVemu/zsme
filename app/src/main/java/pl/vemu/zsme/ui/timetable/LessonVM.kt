package pl.vemu.zsme.ui.timetable

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ramcosta.composedestinations.generated.destinations.LessonDestination
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.launch
import org.koin.android.annotation.KoinViewModel
import pl.vemu.zsme.Result
import pl.vemu.zsme.ResultList
import pl.vemu.zsme.data.LessonMediator
import pl.vemu.zsme.data.model.LessonModel

@KoinViewModel
class LessonVM(
    private val mediator: LessonMediator,
    state: SavedStateHandle,
) : ViewModel() {
    private val _list =
        MutableStateFlow<ResultList<LessonModel>>(Result.Loading)
    val list = _list.asStateFlow()

    private val url: String =
        LessonDestination.argsFrom(state).url

    init {
        downloadLessons()
    }

    fun downloadLessons() {
        viewModelScope.launch {
            _list.emitAll(mediator.getLessons(url))
        }
    }
}
