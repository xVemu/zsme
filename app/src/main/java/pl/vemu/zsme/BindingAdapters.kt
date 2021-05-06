package pl.vemu.zsme

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import coil.load

@BindingAdapter("imgUrl")
fun ImageView.setImageUrl(url: String) {
    load(url) {
        placeholder(R.drawable.zsme)
    }
}