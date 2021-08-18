package pl.vemu.zsme.ui.detail

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
    private val _detail = MutableStateFlow<DetailModel?>(null)
    private val _postModel = MutableStateFlow<PostModel?>(null)

    val detail = _detail.asStateFlow()
    val postModel = _postModel.asStateFlow()

    fun init(id: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            val postModelById = detailRepo.getPostModelById(id)
            _postModel.value = postModelById
            _detail.value = detailRepo.getDetail(postModelById.content)
        }
    }
}