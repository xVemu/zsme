package pl.vemu.zsme.ui.components

import androidx.compose.material.ContentAlpha
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.PagerState
import kotlinx.coroutines.launch

@OptIn(ExperimentalPagerApi::class)
@Composable
fun Tabs(
    pagerState: PagerState,
    names: List<String>
) {
    val coroutineScope = rememberCoroutineScope()
    names.forEachIndexed { index, name ->
        Tab(
            text = { Text(name) },
            selected = pagerState.currentPage == index,
            unselectedContentColor = MaterialTheme.colorScheme.onSurface.copy(alpha = ContentAlpha.medium),
            onClick = {
                coroutineScope.launch {
                    pagerState.animateScrollToPage(index)
                }
            }
        )
    }
}