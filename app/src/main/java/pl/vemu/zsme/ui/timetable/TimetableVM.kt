package pl.vemu.zsme.ui.timetable

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import pl.vemu.zsme.Result
import pl.vemu.zsme.data.model.TimetableModel
import javax.inject.Inject

@HiltViewModel
class TimetableVM @Inject constructor(
    private val timetableRepo: TimetableRepo,
) : ViewModel() {
    private val _list: MutableStateFlow<Result<List<List<TimetableModel>>>> =
        MutableStateFlow(Result.Success(emptyList()))
    val list = _list.asStateFlow()

    init {
        downloadTimetable()
    }

    fun downloadTimetable() {
        viewModelScope.launch(Dispatchers.IO) {
            _list.value = try {
                Result.Success(timetableRepo.getTimetable())
            } catch (e: Exception) {
                Result.Failure(e)
            }
        }
    }
}