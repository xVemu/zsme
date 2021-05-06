package pl.vemu.zsme.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import pl.vemu.zsme.State
import pl.vemu.zsme.model.DetailModel
import pl.vemu.zsme.repo.DetailRepo

class DetailFragmentVM constructor(
        private val detailRepo: DetailRepo,
        content: String,
) : ViewModel() {
    private val _detail = MutableStateFlow<State<DetailModel>>(State.Loading())

    val detail: StateFlow<State<DetailModel>>
        get() = _detail


    init {
        viewModelScope.launch {
            _detail.emit(State.Loading())
            try {
                _detail.emit(State.Success(detailRepo.getDetail(content)))
            } catch (e: Exception) {
                _detail.emit(State.Error(Throwable(e.message)))
            }
        }
    }

    /*override fun onCleared() {
        detail.value = null
    }*/

    /*init {
        val finalUrl = if (!url.startsWith("http") && !url.startsWith("https")) application.getString(R.string.zsme_default_link) + url else url
    }*/
}