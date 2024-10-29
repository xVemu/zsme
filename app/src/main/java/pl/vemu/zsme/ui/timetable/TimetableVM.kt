package pl.vemu.zsme.ui.timetable

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.launch
import org.koin.android.annotation.KoinViewModel
import pl.vemu.zsme.Result
import pl.vemu.zsme.ResultList
import pl.vemu.zsme.data.TimetableMediator
import pl.vemu.zsme.data.model.TimetableModel

@KoinViewModel
class TimetableVM(
    private val mediator: TimetableMediator,
) : ViewModel() {
    private val _list =
        MutableStateFlow<ResultList<TimetableModel>>(Result.Loading)
    val list = _list.asStateFlow()

    init {
        downloadTimetable()
    }

    fun downloadTimetable() {
        viewModelScope.launch {
            _list.emitAll(mediator.getTimetable())
        }
    }
}
