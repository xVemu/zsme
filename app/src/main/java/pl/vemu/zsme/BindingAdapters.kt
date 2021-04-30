package pl.vemu.zsme

import android.webkit.WebView
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import coil.load

@BindingAdapter("imgUrl")
fun ImageView.setImageUrl(url: String) {
    load(url) {
        placeholder(R.drawable.zsme)
    }
}

@BindingAdapter("webData")
fun WebView.setWebData(data: String) {
    loadData(data, "text/html", null)
}