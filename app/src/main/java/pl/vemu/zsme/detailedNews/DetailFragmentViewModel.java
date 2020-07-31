package pl.vemu.zsme.detailedNews;

import android.app.Application;
import android.text.Spanned;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.ArrayList;

import pl.vemu.zsme.R;

public class DetailFragmentViewModel extends AndroidViewModel {
    private final MutableLiveData<Spanned> text;
    private final MutableLiveData<Boolean> isUpdating;
    private final MutableLiveData<ArrayList<String>> images;

    public DetailFragmentViewModel(Application application, String url) {
        super(application);
        if (!url.startsWith("http") && !url.startsWith("https"))
            url = application.getString(R.string.zsme_default_link) + url;
        text = DetailFragmentRepository.INSTANCE.downloadText(url, application.getString(R.string.error));
        isUpdating = DetailFragmentRepository.INSTANCE.getIsUpdating();
        images = DetailFragmentRepository.INSTANCE.getImages();
    }

    public LiveData<Spanned> getText() {
        return text;
    }

    public LiveData<Boolean> getIsUpdating() {
        return isUpdating;
    }

    public LiveData<ArrayList<String>> getImages() {
        return images;
    }

    @Override
    protected void onCleared() {
        text.setValue(null);
        isUpdating.setValue(false);
        images.setValue(null);
    }
}
