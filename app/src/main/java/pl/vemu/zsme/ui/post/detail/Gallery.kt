package pl.vemu.zsme.ui.post.detail

import android.view.ViewGroup
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavController
import coil.load
import com.github.chrisbanes.photoview.PhotoView
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import pl.vemu.zsme.R
import pl.vemu.zsme.ui.components.simpleSmallAppBar


@OptIn(ExperimentalPagerApi::class, ExperimentalMaterial3Api::class)
@Composable
fun Gallery(imagesJson: String, navController: NavController) {
    val imageType = object : TypeToken<List<String>>() {}.type
    val images: List<String> = Gson().fromJson(imagesJson, imageType)
    Scaffold(
        topBar = simpleSmallAppBar(
            title = R.string.gallery,
            navController = navController
        )
    ) { padding ->
        HorizontalPager(
            state = rememberPagerState(0),
            count = images.size,
            modifier = Modifier.padding(padding)
        ) { page ->
            PhotoView(image = images[page])
        }
    }
}

@Composable
private fun PhotoView(image: String) {
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
