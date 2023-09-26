package pl.vemu.zsme.ui.post.detail

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.ramcosta.composedestinations.annotation.Destination
import pl.vemu.zsme.R
import pl.vemu.zsme.ui.components.SimpleSmallAppBar
import pl.vemu.zsme.ui.components.SlideTransition
import pl.vemu.zsme.ui.post.PostNavGraph
import soup.compose.photo.ExperimentalPhotoApi
import soup.compose.photo.PhotoBox


@OptIn(ExperimentalFoundationApi::class, ExperimentalPhotoApi::class)
@PostNavGraph
@Destination(route = "post/gallery", style = SlideTransition::class)
@Composable
fun Gallery(images: Array<String>, navController: NavController) {
    Scaffold(
        topBar = {
            SimpleSmallAppBar(
                title = R.string.gallery,
                navController = navController
            )
        }
    ) { padding ->
        HorizontalPager(
            state = rememberPagerState(pageCount = { images.size }),
            modifier = Modifier.padding(padding)
        ) { page ->
            PhotoBox {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(images[page])
                        .crossfade(true)
                        .build(),
                    modifier = Modifier.fillMaxSize(),
                    contentDescription = null
                )
            }
        }
    }
}
