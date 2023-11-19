package pl.vemu.zsme.ui.post.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import pl.vemu.zsme.Result
import pl.vemu.zsme.ResultList
import pl.vemu.zsme.data.model.HtmlString
import pl.vemu.zsme.data.model.ImageUrl
import pl.vemu.zsme.data.repo.DetailRepo
import javax.inject.Inject

@HiltViewModel
class DetailVM @Inject constructor(
    private val detailRepo: DetailRepo,
) : ViewModel() {
    private val _detail = MutableStateFlow<Result<HtmlString>>(Result.Loading)
    private val _images = MutableStateFlow<ResultList<ImageUrl>>(Result.Loading)

    val detail = _detail.asStateFlow()
    val images = _images.asStateFlow()

    fun init(id: Int, content: String) {
        viewModelScope.launch {
            launch {
                _detail.value = try {
                    Result.Success(detailRepo.removeImgsFromContent(content))
                } catch (e: Exception) {
                    Result.Failure(e)
                }
            }
            launch {
                _images.value = try {
                    Result.Success(detailRepo.getImages(id))
                } catch (e: Exception) {
                    Result.Failure(e)
                }
            }
        }
    }
}
