package pl.vemu.zsme.ui.post.detail

import android.view.ViewGroup
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import coil.load
import com.github.chrisbanes.photoview.PhotoView
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken


@ExperimentalPagerApi
@Composable
fun Gallery(imagesJson: String) {
    val imageType = object : TypeToken<List<String>>() {}.type
    val images: List<String> = Gson().fromJson(imagesJson, imageType)
    HorizontalPager(state = rememberPagerState(0), count = images.size) { page ->
        PhotoView(image = images[page])                 // setOnClickListener { fragment.switchUiVisibility() }
    }
}

@Composable
fun PhotoView(image: String, modifier: Modifier = Modifier) {
    AndroidView(
        modifier = modifier,
        factory = { context ->
            PhotoView(context).apply {
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
                load(image)
            }
        },
        update = {
            it.load(image)
        })
}

/*private var isUiVisible = true

override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    switchUiVisibility()
}

override fun onDestroyView() {
    isUiVisible = false
    switchUiVisibility()
}

@Suppress("DEPRECATION")
fun switchUiVisibility() {
    val decorView = requireActivity().window.decorView
    //        val bottomNav: BottomNavigationView = requireActivity().findViewById(R.id.bottom_nav)
    val actionBar = (requireActivity() as AppCompatActivity).supportActionBar
    if (isUiVisible) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            decorView.windowInsetsController?.hide(WindowInsets.Type.navigationBars() or WindowInsets.Type.statusBars())
            decorView.windowInsetsController?.systemBarsBehavior =
                WindowInsetsController.BEHAVIOR_SHOW_BARS_BY_SWIPE
        } else {
            decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_IMMERSIVE
                    or View.SYSTEM_UI_FLAG_FULLSCREEN
                    or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION)
        }
        actionBar?.hide()
        //            bottomNav.visibility = View.GONE
        isUiVisible = false
    } else {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            decorView.windowInsetsController?.show(WindowInsets.Type.statusBars() or WindowInsets.Type.navigationBars())
        } else {
            decorView.systemUiVisibility = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR
            else 0
        }
        actionBar?.show()
        //            bottomNav.visibility = View.VISIBLE
        isUiVisible = true
    }
}*/
