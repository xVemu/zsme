package pl.vemu.zsme.ui.post.detail

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import kotlinx.coroutines.launch
import net.engawapg.lib.zoomable.rememberZoomState
import net.engawapg.lib.zoomable.zoomable
import pl.vemu.zsme.R
import pl.vemu.zsme.modifiers.noRippleClickable
import pl.vemu.zsme.paddingBottom
import pl.vemu.zsme.ui.components.SimpleSmallAppBar

import pl.vemu.zsme.ui.fullScreen
import pl.vemu.zsme.ui.post.PostNavGraph
import pl.vemu.zsme.util.changeUiVisibility
import pl.vemu.zsme.util.rememberInsetsController


@OptIn(
    ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class
)
@PostNavGraph
@Destination("post/gallery")
@Composable
fun Gallery(images: Array<String>, navController: DestinationsNavigator) {

    BackHandler {
        fullScreen = false
        navController.popBackStack()
    }

    val systemUi = rememberInsetsController()
    LaunchedEffect(fullScreen) {
        systemUi?.changeUiVisibility(!fullScreen)
    }

    Scaffold(
        topBar = {
            AnimatedVisibility(
                !fullScreen,
                enter = fadeIn() + slideInVertically(),
                exit = fadeOut() + slideOutVertically()
            ) {
                SimpleSmallAppBar(
                    title = R.string.gallery, navController = navController
                )
            }
        },
        modifier = Modifier.noRippleClickable(onDoubleClick = {
            if (!fullScreen) fullScreen = true
        }) {
            fullScreen = !fullScreen
        },
    ) { padding ->
        Box(Modifier.padding(padding)) {
            val pagerState = rememberPagerState { images.size }
            val coroutineScope = rememberCoroutineScope()

            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxSize(),
            ) { page ->
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current).data(images[page])
                        .crossfade(true).build(),
                    modifier = Modifier
                        .fillMaxSize()
                        .zoomable(rememberZoomState()),
                    contentDescription = null
                )
            }
            if (pagerState.pageCount > 1)
                AnimatedVisibility(
                    !fullScreen,
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .paddingBottom(16.dp),
                    enter = fadeIn() + scaleIn(),
                    exit = fadeOut() + scaleOut(),
                ) {
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        repeat(pagerState.pageCount) { iteration ->
                            Box(modifier = Modifier
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.outlineVariant)
                                .clickable {
                                    coroutineScope.launch {
                                        pagerState.animateScrollToPage(iteration)
                                    }
                                }
                                .size(10.dp))
                        }
                    }
                    Box(
                        modifier = Modifier
                            .slidingLineTransition(pagerState, 16.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary)
                            .size(10.dp)
                    )
                }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
private fun Modifier.slidingLineTransition(pagerState: PagerState, distance: Dp) = graphicsLayer {
    val scrollPosition = pagerState.currentPage + pagerState.currentPageOffsetFraction
    translationX = scrollPosition * distance.roundToPx()
}
