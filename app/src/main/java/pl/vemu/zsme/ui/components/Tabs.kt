package pl.vemu.zsme.ui.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.pager.PagerState
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
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
            onClick = {
                coroutineScope.launch {
                    pagerState.animateScrollToPage(index)
                }
            }
        )
    }
}
