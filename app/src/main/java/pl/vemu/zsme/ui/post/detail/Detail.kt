package pl.vemu.zsme.ui.post.detail

import android.content.ClipData
import android.content.Context
import android.content.Intent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.PhotoLibrary
import androidx.compose.material.icons.rounded.Share
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.annotation.ExperimentalCoilApi
import coil.compose.AsyncImage
import coil.imageLoader
import coil.request.ImageRequest
import coil.transform.RoundedCornersTransformation
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.generated.destinations.GalleryDestination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.navigation.EmptyDestinationsNavigator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.androidx.compose.navigation.koinNavViewModel
import pl.vemu.zsme.R
import pl.vemu.zsme.Result
import pl.vemu.zsme.data.model.HtmlString
import pl.vemu.zsme.data.model.ImageUrl
import pl.vemu.zsme.data.model.PostModel
import pl.vemu.zsme.isNetworkAvailable
import pl.vemu.zsme.remembers.LinkProviderEffect
import pl.vemu.zsme.ui.components.CustomError
import pl.vemu.zsme.ui.components.Html
import pl.vemu.zsme.ui.components.SimpleSmallAppBar
import pl.vemu.zsme.ui.components.WebViewCompose
import pl.vemu.zsme.ui.post.PostNavGraph
import pl.vemu.zsme.util.Formatter
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Destination<PostNavGraph>
@Composable
fun Detail(
    postModel: PostModel,
    navController: DestinationsNavigator,
    vm: DetailVM = koinNavViewModel(),
) {
    LinkProviderEffect(postModel.link)

    val images by vm.images.collectAsStateWithLifecycle()
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

    Scaffold(floatingActionButton = {
        // TODO NET_CAPABILITY_VALIDATED
        if (!LocalContext.current.isNetworkAvailable()) return@Scaffold

        val data = images

        if (data !is Result.Success) return@Scaffold
        val imgs = data.value
        if (imgs.isEmpty()) return@Scaffold

        DetailFloatingButton(navController, imgs)
    }, topBar = {
        AppBar(postModel, navController, scrollBehavior)
    }) { padding ->
        val content by vm.detail.collectAsStateWithLifecycle()
        DetailItem(
            postModel, content,
            modifier = Modifier
                .nestedScroll(scrollBehavior.nestedScrollConnection)
                .verticalScroll(rememberScrollState())
                .padding(padding)
                .then(
                    if (images is Result.Success && (images as Result.Success).value.isNotEmpty()) Modifier.padding(
                        bottom = 72.dp // FAB height
                    ) else Modifier
                ),
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AppBar(
    postModel: PostModel,
    navController: DestinationsNavigator,
    scrollBehavior: TopAppBarScrollBehavior,
) {
    val shareText = stringResource(R.string.share)

    SimpleSmallAppBar(
        title = R.string.post,
        navController = navController,
        scrollBehavior = scrollBehavior,
        actions = {
            val context = LocalContext.current
            val coroutineScope = rememberCoroutineScope()

            IconButton(onClick = {
                coroutineScope.launch {
                    share(context, postModel, shareText)
                }
            }) {
                Icon(
                    imageVector = Icons.Rounded.Share,
                    contentDescription = stringResource(R.string.share)
                )
            }
        })
}

@OptIn(ExperimentalCoilApi::class)
private suspend fun share(context: Context, postModel: PostModel, shareText: String) =
    withContext(Dispatchers.IO) {
        val intent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, postModel.link)
            putExtra(Intent.EXTRA_TITLE, postModel.title)
            type = "text/plain"
            flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
        }

        val uri =
            context.imageLoader.diskCache?.openSnapshot(postModel.id.toString())?.use { snapshot ->
                val newFile =
                    File.createTempFile(
                        "shareImage",
                        ".png",
                        File(context.cacheDir, "/image_cache")
                    )

                snapshot.data.toFile().copyTo(newFile, true)
                FileProvider.getUriForFile(context, "pl.vemu.zsme.fileprovider", newFile)
            }


        if (uri != null)
            intent.clipData =
                ClipData.newUri(
                    context.contentResolver,
                    null,
                    uri
                ) // TODO https://github.com/coil-kt/coil/issues/1920

        val shareIntent = Intent.createChooser(intent, shareText)
        withContext(Dispatchers.Main) {
            context.startActivity(shareIntent)
        }
    }

@Composable
private fun DetailFloatingButton(
    navController: DestinationsNavigator,
    images: List<ImageUrl>,
) {
    ExtendedFloatingActionButton(
        containerColor = MaterialTheme.colorScheme.tertiaryContainer,
        contentColor = MaterialTheme.colorScheme.onTertiaryContainer,
        text = { Text(stringResource(R.string.gallery)) },
        onClick = {
            navController.navigate(GalleryDestination(images.toTypedArray()))
        },
        icon = {
            Icon(
                imageVector = Icons.Rounded.PhotoLibrary,
                contentDescription = stringResource(R.string.gallery)
            )
        },
    )
}

@Composable
private fun DetailItem(
    postModel: PostModel,
    htmlContent: Result<HtmlString>,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(postModel.fullImage ?: R.drawable.zsme).crossfade(true)
                .transformations(RoundedCornersTransformation(12f)).build(),
            contentDescription = stringResource(R.string.image),
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(16 / 9f)
        )
        Html(
            html = postModel.title,
            style = MaterialTheme.typography.titleLarge.copy(color = MaterialTheme.colorScheme.primary),
        )
        Text(
            text = "${postModel.author} • ${
                Formatter.fullDate(postModel.date)
            } • ${postModel.category}",
            style = MaterialTheme.typography.labelMedium,
        )
        when (htmlContent) {
            is Result.Success -> WebViewCompose(htmlContent.value)
            is Result.Failure -> CustomError()
            is Result.Loading -> {}
        }
    }
}

@Preview
@Composable
private fun DetailFloatingButtonPreview() {
    DetailFloatingButton(navController = EmptyDestinationsNavigator, images = emptyList())
}
