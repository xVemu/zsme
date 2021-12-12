package pl.vemu.zsme.ui.post

import android.content.Context
import android.text.format.DateFormat
import android.view.View
import android.widget.TextView
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Card
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.*
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
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.SwipeRefreshIndicator
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import pl.vemu.zsme.R
import pl.vemu.zsme.data.model.PostModel
import pl.vemu.zsme.paddingBottom
import pl.vemu.zsme.paddingTop


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
                contentColor = MaterialTheme.colorScheme.primary
            )
        },
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection)
    ) {
        /*lazyMovieItems.apply { TODO
            when {
                loadState.refresh is LoadState.Loading -> {
                    item { LoadingView(modifier = Modifier.fillParentMaxSize()) }
                }
                loadState.append is LoadState.Loading -> {
                    item { LoadingItem() }
                }
                loadState.refresh is LoadState.Error -> {
                    val e = lazyMovieItems.loadState.refresh as LoadState.Error
                    item {
                        ErrorItem(
                            message = e.error.localizedMessage!!,
                            modifier = Modifier.fillParentMaxSize(),
                            onClickRetry = { retry() }
                        )
                    }
                }
                loadState.append is LoadState.Error -> {
                    val e = lazyMovieItems.loadState.append as LoadState.Error
                    item {
                        ErrorItem(
                            message = e.error.localizedMessage!!,
                            onClickRetry = { retry() }
                        )
                    }
                }
            }*/
        LazyColumn {
            items(pagingItems) { post ->
                post?.let {
                    PostCard(navController, it, context)
                }
            }
        }
    }
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
                    painter = rememberImagePainter(postModel.thumbnail) {
                        placeholder(R.drawable.zsme)
                        crossfade(true)
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
                        text = DateFormat.getMediumDateFormat(context).format(postModel.date)
                    )
                    SmallText(text = postModel.author)
                    SmallText(text = postModel.category)
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