package pl.vemu.zsme.ui.detail

import android.annotation.SuppressLint
import android.content.res.Configuration
import android.text.format.DateFormat
import android.webkit.WebView
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.ExtendedFloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.PhotoLibrary
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
import pl.vemu.zsme.R
import pl.vemu.zsme.paddingTop
import pl.vemu.zsme.ui.post.HTMLText
import pl.vemu.zsme.ui.post.SmallColoredText

@Composable
fun Detail(
    navController: NavController,
    postModelId: Int,
    vm: DetailVM = hiltViewModel()
) {
    vm.init(postModelId)
    val context = LocalContext.current
    val postModelByVm by vm.postModel.collectAsState()
    val detailByVm by vm.detail.collectAsState()
    postModelByVm?.let { postModel ->
        detailByVm?.let { detailModel ->
            Scaffold(
                floatingActionButton = {
                    detailModel.images?.let {
                        ExtendedFloatingActionButton(
                            text = { Text(text = stringResource(id = R.string.gallery)) },
                            onClick = {
                                navController.navigate("gallery")
                            },
                            icon = {
                                Icon(
                                    imageVector = Icons.Rounded.PhotoLibrary,
                                    contentDescription = stringResource(id = R.string.gallery)
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
                        modifier = Modifier
                            .fillMaxWidth()
                            .paddingTop(8.dp),
                        bold = true,
                        centered = true,
                        fontSize = 18F
                    )
                    SelectionContainer {
                        WebView(html = detailModel.html)
                    }
                    SmallColoredText(
                        text = DateFormat.getMediumDateFormat(context).format(postModel.date)
                    )
                    SmallColoredText(text = postModel.author)
                    SmallColoredText(text = postModel.category)
                }
            }
        }
    }
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

/*
@AndroidEntryPoint
class DetailFragment : Fragment() {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setHasOptionsMenu(true)
        binding.webView.settings.javaScriptEnabled = true
        if (WebViewFeature.isFeatureSupported(WebViewFeature.FORCE_DARK)) {
            val nightModeFlags = requireContext().resources.configuration.uiMode and
                    Configuration.UI_MODE_NIGHT_MASK
            val theme = when (nightModeFlags) {
                Configuration.UI_MODE_NIGHT_YES -> WebSettingsCompat.FORCE_DARK_ON
                Configuration.UI_MODE_NIGHT_NO -> WebSettingsCompat.FORCE_DARK_OFF
                else -> WebSettingsCompat.FORCE_DARK_ON
            }
            WebSettingsCompat.setForceDark(binding.webView.settings, theme)
        }
        lifecycleScope.launchWhenStarted {
            viewModel.detail.collect {
                when (it) {
                    is State.Success -> {
                        binding.progressBarDetail.visibility = View.GONE
                        binding.webView.loadData(it.data.html, "text/html; charset=UTF-8", null)
                        it.data.images?.let { images ->
                            binding.gallery.visibility = View.VISIBLE
                            binding.gallery.setOnClickListener(
                                Navigation.createNavigateOnClickListener(
                                    DetailFragmentDirections.actionDetailFragmentToGalleryFragment(
                                        images.toTypedArray()
                                    )
                                )
                            )
                        }
                    }
                    is State.Loading -> binding.progressBarDetail.visibility = View.VISIBLE
                    is State.Error -> {
                        binding.progressBarDetail.visibility = View.GONE
                        Snackbar.make(binding.root, R.string.error, Snackbar.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean = when (item.itemId) {
        R.id.app_bar_share -> {
            val intent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT, args.postModel.link)
                putExtra(Intent.EXTRA_TITLE, args.postModel.title)
                type = "text/plain"
            }
            val shareIntent = Intent.createChooser(intent, "Wybierz")
            startActivity(shareIntent)
            true
        }
        else -> super.onOptionsItemSelected(item)
    }

}*/
/*      <ProgressBar
        android:id="@+id/progress_bar_detail"
        style="?android:attr/progressBarStyle"
        android:layout_width="0dp"
        android:layout_height="wrap_content"
        app:layout_constraintBottom_toBottomOf="parent"
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintTop_toTopOf="parent" />*/