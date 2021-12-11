package pl.vemu.zsme.ui.post.detail

import android.annotation.SuppressLint
import android.content.res.Configuration
import android.text.format.DateFormat
import android.webkit.WebView
import androidx.compose.foundation.layout.Column
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.webkit.WebSettingsCompat
import androidx.webkit.WebViewFeature
import com.google.gson.Gson
import pl.vemu.zsme.R
import pl.vemu.zsme.paddingStart
import pl.vemu.zsme.paddingTop
import pl.vemu.zsme.ui.post.HTMLText

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun detail(
    navController: NavController,
    postModelId: Int,
    slug: String? = null,
    vm: DetailVM = hiltViewModel()
): String? { // returns postModelLink to use it in share button
    vm.init(postModelId) // TODO optimize
    val context = LocalContext.current
    val postModelByVm by vm.postModel.collectAsState()
    val detailByVm by vm.detail.collectAsState()
    postModelByVm?.let { postModel ->
        detailByVm?.let { detailModel ->
            Scaffold(
                floatingActionButton = {
                    detailModel.images?.let { images ->
                        ExtendedFloatingActionButton(
                            text = { Text(text = stringResource(R.string.gallery)) },
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
                }
            ) {
                Column(
                    modifier = Modifier.verticalScroll(rememberScrollState())
                ) {
                    HTMLText(
                        html = postModel.title,
                        modifier = Modifier.paddingTop(8.dp),
                        centered = true,
                        color = MaterialTheme.colorScheme.primary,
                        textStyle = MaterialTheme.typography.bodyLarge
                    )
                    SelectionContainer {
                        WebView(html = detailModel.html)
                    }
                    Column(
                        modifier = Modifier.paddingStart(8.dp)
                    ) {
                        Text(
                            text = stringResource(R.string.date) + DateFormat.getMediumDateFormat(
                                context
                            ).format(postModel.date)
                        )
                        Text(text = stringResource(R.string.author) + postModel.author)
                        Text(text = stringResource(R.string.category) + postModel.category)
                    }
                }
            }
        }
    }
    return postModelByVm?.link
}

@SuppressLint("SetJavaScriptEnabled")
@Composable
private fun WebView(
    html: String,
    modifier: Modifier = Modifier
) {
    AndroidView(
        modifier = modifier,
        factory = { context ->
            WebView(context).apply {
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
                loadData(html, "text/html; charset=UTF-8", null)
            }
        },
        update = {
            it.loadData(html, "text/html; charset=UTF-8", null)
        })
}