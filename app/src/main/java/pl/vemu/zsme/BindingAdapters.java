package pl.vemu.zsme;

import android.widget.ImageView;

import androidx.databinding.BindingAdapter;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bumptech.glide.Glide;

public class BindingAdapters {

    @BindingAdapter("imgUrl")
    public static void setImageUrl(ImageView view, String url) {
        Glide.with(view.getContext()).load(url).placeholder(R.drawable.zsme).centerCrop().into(view);
    }

    @BindingAdapter("refreshing")
    public static void setRefreshing(SwipeRefreshLayout view, boolean isRefreshing) {
        view.setRefreshing(isRefreshing);
    }
}
