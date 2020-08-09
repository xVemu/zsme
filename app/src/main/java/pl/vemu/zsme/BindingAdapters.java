package pl.vemu.zsme;

import android.widget.ImageView;

import androidx.databinding.BindingAdapter;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bumptech.glide.Glide;
import com.google.android.material.textfield.TextInputLayout;

public class BindingAdapters {
    @BindingAdapter("app:errorText")
    public static void setErrorMessage(TextInputLayout view, String errorMessage) {
        view.setError(errorMessage);
    }

    @BindingAdapter("app:imgUrl")
    public static void setImageUrl(ImageView view, String url) {
        Glide.with(view.getContext()).load(url).centerCrop().into(view);
    }

    @BindingAdapter("app:refreshing")
    public static void setRefreshing(SwipeRefreshLayout view, boolean isRefreshing) {
        view.setRefreshing(isRefreshing);
    }
}
