package pl.vemu.zsme.detailedNews;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

public class DetailFragmentViewModelFactory extends ViewModelProvider.AndroidViewModelFactory {

    private final Application application;
    private final String url;

    public DetailFragmentViewModelFactory(@NonNull Application application, String url) {
        super(application);
        this.application = application;
        this.url = url;
    }

    @SuppressWarnings("unchecked")
    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(DetailFragmentViewModel.class))
            return (T) new DetailFragmentViewModel(application, url);
        throw new IllegalArgumentException("ViewModel class not found");
    }
}
