package pl.vemu.zsme;

import androidx.databinding.BindingAdapter;

import com.google.android.material.textfield.TextInputLayout;

public class BindingAdapters {
    @BindingAdapter("app:errorText")
    public static void setErrorMessage(TextInputLayout view, String errorMessage) {
        view.setError(errorMessage);
    }
}
