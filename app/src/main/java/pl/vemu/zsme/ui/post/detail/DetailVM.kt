package pl.vemu.zsme.ui.post.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import pl.vemu.zsme.Result
import pl.vemu.zsme.ResultList
import pl.vemu.zsme.data.model.HtmlString
import pl.vemu.zsme.data.model.ImageUrl
import pl.vemu.zsme.data.repo.DetailRepo
import com.ramcosta.composedestinations.generated.destinations.DetailDestination
import org.koin.android.annotation.KoinViewModel
import javax.inject.Inject

@KoinViewModel
class DetailVM(
    private val detailRepo: DetailRepo,
    state: SavedStateHandle,
) : ViewModel() {
    private val _detail = MutableStateFlow<Result<HtmlString>>(Result.Loading)
    private val _images = MutableStateFlow<ResultList<ImageUrl>>(Result.Loading)

    val detail = _detail.asStateFlow()
    val images = _images.asStateFlow()

    init {
        val navArgs = DetailDestination.argsFrom(state)
        navArgs.postModel.run { init(id, content) }
    }

    private fun init(postId: Int, postContent: String) {
        viewModelScope.launch {
            launch {
                _detail.value = try {
                    Result.Success(detailRepo.removeImgsFromContent(postContent))
                } catch (e: Exception) {
                    Result.Failure(e)
                }
            }
            launch {
                _images.value = try {
                    Result.Success(detailRepo.getImages(postId))
                } catch (e: Exception) {
                    Result.Failure(e)
                }
            }
        }
    }
}
