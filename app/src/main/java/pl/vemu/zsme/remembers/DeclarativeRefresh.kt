package pl.vemu.zsme.remembers

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.pulltorefresh.PullToRefreshState
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import kotlinx.coroutines.CoroutineScope

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun rememberDeclarativeRefresh(
    refreshing: Boolean,
    onRefresh: suspend CoroutineScope.() -> Unit,
): PullToRefreshState {
    val pullRefreshState = rememberPullToRefreshState()

    val isRefreshing by remember {
        derivedStateOf {
            pullRefreshState.progress >= 1f // TODO https://issuetracker.google.com/issues/317177683
        }
    }

    if (isRefreshing) LaunchedEffect(Unit, onRefresh)

    LaunchedEffect(refreshing) {
        if (refreshing)
            pullRefreshState.startRefresh()
        else
            pullRefreshState.endRefresh()
    }

    return pullRefreshState
}
