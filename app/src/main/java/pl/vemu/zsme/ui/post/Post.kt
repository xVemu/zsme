package pl.vemu.zsme.ui.post

import android.graphics.Typeface
import android.text.format.DateFormat
import android.view.View
import android.widget.TextView
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Card
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.text.HtmlCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.items
import coil.compose.rememberImagePainter
import pl.vemu.zsme.R
import pl.vemu.zsme.paddingBottom


@OptIn(ExperimentalMaterialApi::class)
@Composable
fun Post(
    navController: NavController,
    vm: PostVM = hiltViewModel()
) {
    val context = LocalContext.current
    val pagingItems = vm.posts.collectAsLazyPagingItems()
    LazyColumn { // TODO remember position when back
        items(pagingItems) { post ->
            post?.let {
                Card(
                    modifier = Modifier
                        .heightIn(150.dp)
                        .padding(8.dp),
                    elevation = 2.dp,
                    onClick = {
                        navController.navigate("detail/${it.id}")
                    }
                ) {
                    Row {
                        Image(
                            painter = rememberImagePainter(it.thumbnail) {
                                placeholder(R.drawable.zsme)
                                crossfade(true)
                            },
                            contentDescription = stringResource(R.string.thumbnail),
                            modifier = Modifier
                                .size(108.dp)
                                .padding(8.dp),
                            contentScale = ContentScale.Crop
                        )
                        Column(
                            modifier = Modifier.padding(8.dp)
                        ) {
                            HTMLText(
                                html = it.title,
                                bold = true,
                                centered = true
                            )
                            HTMLText(
                                html = it.excerpt,
                                modifier = Modifier.paddingBottom(8.dp)
                            )
                            SmallColoredText(
                                text = DateFormat.getMediumDateFormat(context).format(it.date)
                            )
                            SmallColoredText(text = it.author)
                            SmallColoredText(text = it.category)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SmallColoredText(text: String, modifier: Modifier = Modifier) {
    Text(
        text = text,
        color = MaterialTheme.colors.secondary,
        fontSize = 12.sp,
        modifier = modifier
    )
}

@Composable
fun HTMLText(
    html: String,
    modifier: Modifier = Modifier,
    bold: Boolean = false,
    centered: Boolean = false,
    fontSize: Float? = null
) {
    AndroidView(
        modifier = modifier,
        factory = { context ->
            TextView(context).apply {
                text = HtmlCompat.fromHtml(html, HtmlCompat.FROM_HTML_MODE_COMPACT).trimEnd()
                if (centered) textAlignment =
                    View.TEXT_ALIGNMENT_CENTER // TODO rekrutacja 2021/2022 is not centered
                if (bold) setTypeface(typeface, Typeface.BOLD)
                fontSize?.let { textSize = it }
            }
        },
        update = {
            it.text = HtmlCompat.fromHtml(html, HtmlCompat.FROM_HTML_MODE_COMPACT).trimEnd()
        }
    )
}


/*
    @Inject
    lateinit var postAdapter: PostAdapter

    @Inject
    lateinit var postDao: PostDAO

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setHasOptionsMenu(true)
        setupRecyclerView()
        binding.refresh.apply {
            setOnRefreshListener {
                lifecycleScope.launch(Dispatchers.IO) {
                    postDao.clearAll()
                    withContext(Dispatchers.Main) {
                        postAdapter.refresh()
                        this@apply.isRefreshing = false
                    }
                }
            }
            setColorSchemeColors(resources.getColor(R.color.colorPrimary, null))
            setProgressBackgroundColorSchemeColor(resources.getColor(R.color.swipeBackground, null))
        }
        setupNetwork()
    }

    private fun setupNetwork() {
        val cm = context?.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkRequest = NetworkRequest.Builder().build()
        if (cm.activeNetwork == null) Toast.makeText(
            context,
            "Brak połaczenia z internetem",
            Toast.LENGTH_LONG
        ).show()
        */
/*cm.registerNetworkCallback(networkRequest, object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                lifecycleScope.launch {
                    viewModel.fetchPosts()
                }
            }
        })TODO*//*

    }


    private fun setupRecyclerView() {
        binding.recyclerView.adapter = postAdapter.withLoadStateHeaderAndFooter(
            header = PostLoadStateAdapter(postAdapter::retry),
            footer = PostLoadStateAdapter(postAdapter::retry),
        )
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.posts.collectLatest {
                postAdapter.submitData(it)
            }
        }
    }

    //TODO wrong color on dark mode
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