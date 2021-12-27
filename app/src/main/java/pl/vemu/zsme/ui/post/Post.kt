package pl.vemu.zsme.ui.post

import android.content.Context
import android.text.format.DateUtils
import android.view.View
import android.widget.TextView
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.text.HtmlCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.items
import coil.annotation.ExperimentalCoilApi
import coil.compose.rememberImagePainter
import coil.transform.RoundedCornersTransformation
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.SwipeRefreshIndicator
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import kotlinx.coroutines.launch
import pl.vemu.zsme.R
import pl.vemu.zsme.data.model.PostModel
import pl.vemu.zsme.paddingBottom
import pl.vemu.zsme.paddingTop
import java.net.UnknownHostException
import java.util.*


@Composable
fun Post(
    navController: NavController,
    scrollBehavior: TopAppBarScrollBehavior,
    vm: PostVM = hiltViewModel()
) {
    val context = LocalContext.current
    val pagingItems = vm.posts.collectAsLazyPagingItems()
    var isLoading by remember { mutableStateOf(false) }
    pagingItems.apply {
        isLoading = when {
            loadState.refresh is LoadState.Loading -> true
            loadState.append is LoadState.Loading -> false
            else -> false
        }
    }
    SwipeRefresh(
        state = rememberSwipeRefreshState(isRefreshing = isLoading),
        onRefresh = { pagingItems.refresh() },
        indicator = { state, trigger ->
            SwipeRefreshIndicator(
                state = state,
                refreshTriggerDistance = trigger,
                contentColor = MaterialTheme.colorScheme.primary,
                backgroundColor = MaterialTheme.colorScheme.surface
            )
        },
        modifier = Modifier
            .nestedScroll(scrollBehavior.nestedScrollConnection)
            .fillMaxSize()
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            val snackbarHostState = remember { SnackbarHostState() }
            val coroutineScope = rememberCoroutineScope()
            val errorMsg = stringResource(R.string.error)
            val retryMsg = stringResource(R.string.retry)
            val noConnectionMsg = stringResource(R.string.no_connection)
            LazyColumn {
                items(
                    items = pagingItems,
                    key = { postModel -> postModel.id }
                ) { post ->
                    PostCard(navController = navController, postModel = post!!, context = context)
                }
                pagingItems.apply {
                    when {
                        loadState.append is LoadState.Loading -> {
                            item { LoadingItem() }
                        }
                        loadState.refresh is LoadState.Error || loadState.append is LoadState.Error -> {
                            val error =
                                (loadState.refresh as? LoadState.Error)?.error
                                    ?: (loadState.append as LoadState.Error).error
                            coroutineScope.launch {
                                val result = snackbarHostState.showSnackbar(
                                    message = if (error is UnknownHostException) noConnectionMsg else errorMsg,
                                    actionLabel = retryMsg.uppercase(),
                                    duration = SnackbarDuration.Indefinite
                                )
                                if (result == SnackbarResult.ActionPerformed)
                                    pagingItems.retry()
                            }
                        }
                    }
                }
            }
            SnackbarHost(
                hostState = snackbarHostState,
                modifier = Modifier.align(Alignment.BottomCenter),
            ) {
                Snackbar(
                    backgroundColor = MaterialTheme.colorScheme.primaryContainer,
                    actionColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    snackbarData = it
                )
            }
        }
    }
}

@Composable
private fun LoadingItem() {
    CircularProgressIndicator(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .wrapContentWidth(Alignment.CenterHorizontally),
        color = MaterialTheme.colorScheme.primary
    )
}

@OptIn(ExperimentalMaterialApi::class, ExperimentalCoilApi::class)
@Composable
private fun PostCard(
    navController: NavController,
    postModel: PostModel,
    context: Context
) {
    Card( // TODO change to material 3 card
        modifier = Modifier
            .heightIn(150.dp)
            .padding(8.dp),
        elevation = 2.dp,
        backgroundColor = MaterialTheme.colorScheme.surface,
        onClick = {
            navController.navigate("detail/${postModel.id}")
        }
    ) {
        Row {
            Column(modifier = Modifier.padding(8.dp)) {
                Image(
                    painter = rememberImagePainter(postModel.thumbnail ?: R.drawable.zsme) {
                        placeholder(R.drawable.zsme)
                        crossfade(true)
                        transformations(RoundedCornersTransformation(radius = 16f))
                    },
                    contentDescription = stringResource(R.string.thumbnail),
                    modifier = Modifier
                        .size(108.dp),
                    contentScale = ContentScale.Crop
                )
                Column(
                    modifier = Modifier
                        .width(108.dp)
                        .paddingTop(24.dp)
                ) {
                    SmallText(
                        DateUtils.getRelativeTimeSpanString(
                            postModel.date.time,
                            Date().time,
                            DateUtils.DAY_IN_MILLIS
                        ).toString()
                    )
                    SmallText(postModel.author)
                    SmallText(postModel.category)
                }
            }
            MainText(postModel)
        }
    }
}

@Composable
private fun MainText(
    postModel: PostModel
) {
    Column(
        modifier = Modifier.padding(8.dp)
    ) {
        HTMLText(
            html = postModel.title,
            centered = true,
            color = MaterialTheme.colorScheme.primary,
            textStyle = MaterialTheme.typography.labelLarge
        )
        HTMLText(
            html = postModel.excerpt,
            modifier = Modifier.paddingBottom(8.dp)
        )
    }
}

@Composable
fun SmallText(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.bodyMedium
    )
}

@Composable
fun HTMLText(
    html: String,
    modifier: Modifier = Modifier,
    centered: Boolean = false,
    color: Color = MaterialTheme.colorScheme.onSurface,
    textStyle: TextStyle = MaterialTheme.typography.bodyMedium
) {
    AndroidView(
        modifier = modifier.fillMaxWidth(),
        factory = { context ->
            TextView(context).apply {
                text = HtmlCompat.fromHtml(html, HtmlCompat.FROM_HTML_MODE_COMPACT).trimEnd()
                if (centered) textAlignment =
                    View.TEXT_ALIGNMENT_CENTER
                setTextColor(color.toArgb())
                textSize = textStyle.fontSize.value
                setLineSpacing(textStyle.lineHeight.value - textStyle.fontSize.value, 1F)
                letterSpacing = textStyle.letterSpacing.value * 0.0624F
            }
        },
        update = {
            it.text = HtmlCompat.fromHtml(html, HtmlCompat.FROM_HTML_MODE_COMPACT).trimEnd()
        }
    )
}


/*TODO
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_search, menu)
        val searchView = menu.findItem(R.id.app_bar_search).actionView as SearchView
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean = query?.let {
                viewModel.query.value = it
                searchView.clearFocus()
                binding.recyclerView.scrollToPosition(0)
                true
            } ?: false

            override fun onQueryTextChange(newText: String?): Boolean = true
        })
        searchView.setOnCloseListener {
            viewModel.query.value = ""
            searchView.onActionViewCollapsed()
            binding.recyclerView.scrollToPosition(0)
            true
        }
    }
}*/