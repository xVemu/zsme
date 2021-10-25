package pl.vemu.zsme.ui.timetable

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import pl.vemu.zsme.data.model.TimetableModel
import javax.inject.Inject

@HiltViewModel
class TimetableVM @Inject constructor(
    private val timetableRepo: TimetableRepo,
) : ViewModel() {
    private val _list =
        MutableStateFlow<List<List<TimetableModel>>>(emptyList())
    val list = _list.asStateFlow()

    init {
        viewModelScope.launch(Dispatchers.IO) {
            _list.value = timetableRepo.getTimetable()
        }
    }
}