package pl.vemu.zsme.ui.post.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import pl.vemu.zsme.data.model.DetailModel
import pl.vemu.zsme.data.model.PostModel
import pl.vemu.zsme.data.repo.DetailRepo
import javax.inject.Inject

@HiltViewModel
class DetailVM @Inject constructor(
    private val detailRepo: DetailRepo
) : ViewModel() {
    private val _detail = MutableStateFlow(DetailModel("", null))

    val detail = _detail.asStateFlow()

    fun init(postModel: PostModel) {
        viewModelScope.launch(Dispatchers.IO) {
            _detail.value = detailRepo.getDetail(postModel)
        }
    }
}