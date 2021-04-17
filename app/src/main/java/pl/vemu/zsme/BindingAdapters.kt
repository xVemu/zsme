package pl.vemu.zsme

import android.webkit.WebView
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import coil.load
import coil.transform.CircleCropTransformation

@BindingAdapter("imgUrl")
fun ImageView.setImageUrl(url: String) {
    load(url) {
        placeholder(R.drawable.zsme)
        transformations(CircleCropTransformation())
    }
}

@BindingAdapter("refreshing")
fun SwipeRefreshLayout.setIsRefreshing(isRefreshing: Boolean) {
    this.isRefreshing = isRefreshing
}

@BindingAdapter("webData")
fun WebView.setWebData(data: String) {
    loadData(data, "text/html", null)
}