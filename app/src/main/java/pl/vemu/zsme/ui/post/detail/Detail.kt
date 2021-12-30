package pl.vemu.zsme.ui.post.detail

import android.content.res.Configuration
import android.graphics.Color
import android.webkit.WebView
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.PhotoLibrary
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
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import androidx.webkit.WebSettingsCompat
import androidx.webkit.WebViewFeature
import coil.annotation.ExperimentalCoilApi
import coil.compose.rememberImagePainter
import com.google.gson.Gson
import pl.vemu.zsme.R
import pl.vemu.zsme.data.model.DetailModel
import pl.vemu.zsme.isNetworkAvailable
import pl.vemu.zsme.ui.post.HTMLText
import java.text.DateFormat


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun detail(
    navController: NavController,
    postModelId: Int,
    slug: String? = null,
    vm: DetailVM = hiltViewModel()
): String? { // returns postModelLink to use it in share button
    vm.init(postModelId)
    val context = LocalContext.current
    val detailByVM by vm.detail.collectAsState()
    detailByVM?.let { detailModel ->
        Scaffold(
            floatingActionButton = {
                if (!context.isNetworkAvailable()) return@Scaffold
                detailModel.images?.let { images ->
                    DetailFloatingButton(navController, images)
                }
            }
        ) {
            DetailItem(detailModel)
        }
    }
    return detailByVM?.postModel?.link
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

@OptIn(ExperimentalCoilApi::class)
@Composable
private fun DetailItem(
    detailModel: DetailModel
) {
    val (postModel) = detailModel
    Column(Modifier.verticalScroll(rememberScrollState())) {
        Image(
            painter = rememberImagePainter(
                postModel.fullImage
            ) {
                crossfade(true)
            },
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
    AndroidView(
        factory = { context ->
            WebView(context).apply {
                //noinspection SetJavaScriptEnabled
                settings.javaScriptEnabled = true
                if (WebViewFeature.isFeatureSupported(WebViewFeature.FORCE_DARK)) {
                    val nightModeFlags = context.resources.configuration.uiMode and
                            Configuration.UI_MODE_NIGHT_MASK
                    val theme = when (nightModeFlags) {
                        Configuration.UI_MODE_NIGHT_YES -> WebSettingsCompat.FORCE_DARK_ON
                        Configuration.UI_MODE_NIGHT_NO -> WebSettingsCompat.FORCE_DARK_OFF
                        else -> WebSettingsCompat.FORCE_DARK_ON
                    }
                    WebSettingsCompat.setForceDark(settings, theme)
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