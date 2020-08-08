package pl.vemu.zsme.detailedNews;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

public class DetailFragmentVMFactory extends ViewModelProvider.AndroidViewModelFactory {

    private final Application application;
    private final String url;

    public DetailFragmentVMFactory(@NonNull Application application, String url) {
        super(application);
        this.application = application;
        this.url = url;
    }

    @SuppressWarnings("unchecked")
    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(DetailFragmentVM.class))
            return (T) new DetailFragmentVM(application, url);
        throw new IllegalArgumentException("ViewModel class not found");
    }
}
