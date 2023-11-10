package pl.vemu.zsme.ui.post.detail

import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.webkit.WebView
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.PhotoLibrary
import androidx.compose.material.icons.rounded.Share
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.text.HtmlCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.webkit.WebSettingsCompat
import androidx.webkit.WebViewFeature
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.google.gson.Gson
import com.ramcosta.composedestinations.annotation.DeepLink
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.FULL_ROUTE_PLACEHOLDER
import com.ramcosta.composedestinations.navargs.DestinationsNavTypeSerializer
import com.ramcosta.composedestinations.navargs.NavTypeSerializer
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.navigation.EmptyDestinationsNavigator
import pl.vemu.zsme.R
import pl.vemu.zsme.data.model.DetailModel
import pl.vemu.zsme.data.model.PostModel
import pl.vemu.zsme.isNetworkAvailable
import pl.vemu.zsme.ui.components.HTMLText
import pl.vemu.zsme.ui.components.SlideTransition
import pl.vemu.zsme.ui.destinations.GalleryDestination
import pl.vemu.zsme.ui.post.PostNavGraph
import pl.vemu.zsme.ui.theme.isDark
import java.text.DateFormat


@PostNavGraph
@Destination(
    route = "post/detail",
    deepLinks = [DeepLink(uriPattern = "zsme://detail/$FULL_ROUTE_PLACEHOLDER")],
    style = SlideTransition::class
)
@Composable
fun Detail(
    postModel: PostModel,
    navController: DestinationsNavigator,
//    slug: String? = null, TODO
    vm: DetailVM = hiltViewModel(),
) {
    vm.init(postModel)
    val ctx = LocalContext.current
    val detailModel by vm.detail.collectAsState()
    Scaffold(
        floatingActionButton = {
            if (!ctx.isNetworkAvailable()) return@Scaffold
            detailModel.images?.let { images ->
                DetailFloatingButton(navController, images)
            }
        },
        topBar = {
            AppBar(postModel.link, navController)
        }
    ) { padding ->
        DetailItem(postModel, detailModel, modifier = Modifier.padding(padding))
    }
}

@NavTypeSerializer
class PostNavTypeSerializer : DestinationsNavTypeSerializer<PostModel> {
    override fun fromRouteString(routeStr: String): PostModel {
        return Gson().fromJson(routeStr, PostModel::class.java)
    }

    override fun toRouteString(value: PostModel): String = Gson().toJson(value)

}

/*deepLinks = listOf(
    navDeepLink { uriPattern = "$DEFAULT_URL/wp/{slug}/" },
    navDeepLink { uriPattern = "$DEFAULT_URL/wp/{slug}" }
)*/

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AppBar(link: String, navController: DestinationsNavigator) {
    TopAppBar(
        title = { Text(stringResource(R.string.post)) },
        navigationIcon = {
            IconButton(onClick = {
                navController.popBackStack()
            }) {
                Icon(
                    imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
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
    navController: DestinationsNavigator,
    images: List<String>,
) {
    ExtendedFloatingActionButton(
        text = { Text(stringResource(R.string.gallery)) },
        onClick = {
            navController.navigate(GalleryDestination(images.toTypedArray()))
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
    postModel: PostModel,
    detailModel: DetailModel,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.verticalScroll(rememberScrollState())
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
            WebView(
                html = HtmlCompat.fromHtml(
                    detailModel.html,
                    HtmlCompat.FROM_HTML_MODE_COMPACT
                ).toString() //fixes unicode chars
            )
        }
    }
}

@Composable
private fun WebView(html: String) {
    val isDarkTheme = MaterialTheme.isDark()
    AndroidView(
        factory = { context ->
            WebView(context).apply {
                //noinspection SetJavaScriptEnabled
                settings.javaScriptEnabled = true
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    settings.isAlgorithmicDarkeningAllowed = true
                }
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
    DetailFloatingButton(navController = EmptyDestinationsNavigator, images = emptyList())
}
