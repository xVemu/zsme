package pl.vemu.zsme.ui.post.detail

import android.content.Intent
import android.graphics.Color
import android.webkit.WebView
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.PhotoLibrary
import androidx.compose.material.icons.rounded.Share
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import androidx.webkit.WebSettingsCompat
import androidx.webkit.WebViewFeature
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.google.gson.Gson
import pl.vemu.zsme.R
import pl.vemu.zsme.data.model.DetailModel
import pl.vemu.zsme.isNetworkAvailable
import pl.vemu.zsme.ui.components.HTMLText
import java.text.DateFormat


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Detail(
    navController: NavController,
    postModelId: Int,
    slug: String? = null,
    vm: DetailVM = hiltViewModel()
) {
    vm.init(postModelId)
    val ctx = LocalContext.current
    val detailByVM by vm.detail.collectAsState()
    detailByVM?.let { detailModel ->
        Scaffold(
            floatingActionButton = {
                if (!ctx.isNetworkAvailable()) return@Scaffold
                detailModel.images?.let { images ->
                    DetailFloatingButton(navController, images)
                }
            },
            topBar = {
                AppBar(detailModel.postModel.link, navController)
            }
        ) { padding ->
            DetailItem(detailModel, padding)
        }
    }
}

@Composable
private fun AppBar(link: String, navController: NavController) {
    SmallTopAppBar(
        title = { Text(stringResource(R.string.post)) },
        navigationIcon = {
            IconButton(onClick = {
                navController.popBackStack()
            }) {
                Icon(
                    imageVector = Icons.Rounded.ArrowBack,
                    contentDescription = stringResource(R.string.back_button),
                )
            }
        },
        actions = {
            val ctx = LocalContext.current
            IconButton(onClick = {
                val intent = Intent().apply {
                    action = Intent.ACTION_SEND
                    putExtra(Intent.EXTRA_TEXT, link)
                    type = "text/plain"
                }
                val shareIntent = Intent.createChooser(intent, null)
                ctx.startActivity(shareIntent)
            }) {
                Icon(
                    imageVector = Icons.Rounded.Share,
                    contentDescription = stringResource(R.string.share)
                )
            }
        }
    )
}

@Composable
private fun DetailFloatingButton(
    navController: NavController,
    images: List<String>
) {
    ExtendedFloatingActionButton(
        text = { Text(stringResource(R.string.gallery)) },
        onClick = {
            navController.navigate("gallery?images=" + Gson().toJson(images))
        },
        icon = {
            Icon(
                imageVector = Icons.Rounded.PhotoLibrary,
                contentDescription = stringResource(R.string.gallery)
            )
        }
    )
}

@Composable
private fun DetailItem(
    detailModel: DetailModel,
    padding: PaddingValues
) {
    val (postModel) = detailModel
    Column(
        Modifier
            .verticalScroll(rememberScrollState())
            .padding(padding)
    ) {
        AsyncImage(
            model = ImageRequest.Builder(context = LocalContext.current)
                .data(postModel.fullImage)
                .crossfade(true)
                .build(),
            contentDescription = stringResource(R.string.image),
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(16 / 9f)
        )
        HTMLText(
            html = postModel.title,
            modifier = Modifier.padding(8.dp),
            color = MaterialTheme.colorScheme.primary,
            textStyle = MaterialTheme.typography.titleLarge
        )
        Text(
            text = "${postModel.author} • ${
                DateFormat.getDateTimeInstance().format(postModel.date)
            } • ${postModel.category}",
            modifier = Modifier.padding(8.dp),
            style = MaterialTheme.typography.bodyMedium
        )
        SelectionContainer {
            WebView(html = detailModel.html)
        }
    }
}

@Composable
private fun WebView(html: String) {
    val isDarkTheme = MaterialTheme.colorScheme.surface.luminance() < .5
    AndroidView(
        factory = { context ->
            WebView(context).apply {
                //noinspection SetJavaScriptEnabled
                settings.javaScriptEnabled = true
                if (WebViewFeature.isFeatureSupported(WebViewFeature.FORCE_DARK)) {
                    WebSettingsCompat.setForceDark(
                        settings,
                        if (isDarkTheme) WebSettingsCompat.FORCE_DARK_ON else WebSettingsCompat.FORCE_DARK_OFF
                    )
                }
                isVerticalScrollBarEnabled = false
                isHorizontalScrollBarEnabled = false
                setBackgroundColor(Color.TRANSPARENT)
                loadData(html, "text/html; charset=UTF-8", null)
            }
        },
        update = {
            it.loadData(html, "text/html; charset=UTF-8", null)
        })
}

@Preview
@Composable
private fun DetailFloatingButtonPreview() {
    DetailFloatingButton(navController = rememberNavController(), images = emptyList())
}