package pl.vemu.zsme.ui.post.detail

import android.view.ViewGroup
import androidx.compose.runtime.Composable
import androidx.compose.ui.viewinterop.AndroidView
import coil.load
import com.github.chrisbanes.photoview.PhotoView
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken


@OptIn(ExperimentalPagerApi::class)
@Composable
fun Gallery(imagesJson: String) {
    val imageType = object : TypeToken<List<String>>() {}.type
    val images: List<String> = Gson().fromJson(imagesJson, imageType)
    HorizontalPager(state = rememberPagerState(0), count = images.size) { page ->
        PhotoView(image = images[page])
    }
}

@Composable
fun PhotoView(image: String) {
    AndroidView(
        factory = { context ->
            PhotoView(context).apply {
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
                load(image)
            }
        },
        update = {
            it.load(image)
        })
}
