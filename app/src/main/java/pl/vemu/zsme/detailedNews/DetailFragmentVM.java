package pl.vemu.zsme.detailedNews;

import android.app.Application;
import android.view.View;

import androidx.core.text.HtmlCompat;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.navigation.Navigation;

import java.io.IOException;

import pl.vemu.zsme.R;

public class DetailFragmentVM extends AndroidViewModel implements View.OnClickListener {

    private final MutableLiveData<Detail> detail = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isRefreshing = new MutableLiveData<>(false);

    public DetailFragmentVM(Application application, String url) {
        super(application);
        String finalUrl = (!url.startsWith("http") && !url.startsWith("https")) ?
                application.getString(R.string.zsme_default_link) + url : url;
        new Thread(() -> {
            isRefreshing.postValue(true);
            try {
                Detail downloadedDetail = DetailRepo.INSTANCE.downloadText(finalUrl);
                if (downloadedDetail == null) {
                    throw new IOException("Not found");
                }
                detail.postValue(downloadedDetail);
            } catch (IOException e) {
                e.printStackTrace();
                detail.postValue(new Detail(HtmlCompat.fromHtml(application.getString(R.string.error), HtmlCompat.FROM_HTML_MODE_COMPACT), null));
            } finally {
                isRefreshing.postValue(false);
            }
        }).start();
    }

    public LiveData<Detail> getDetail() {
        return detail;
    }

    public LiveData<Boolean> getIsRefreshing() {
        return isRefreshing;
    }

    @Override
    protected void onCleared() {
        detail.setValue(null);
        isRefreshing.setValue(false);
    }

    @Override
    public void onClick(View v) {
        if (detail.getValue() == null) return;
        String[] imagesArray = new String[detail.getValue().getImages().size()];
        detail.getValue().getImages().toArray(imagesArray);
        Navigation.findNavController(v).navigate(DetailFragmentDirections.actionDetailFragmentToGalleryFragment(imagesArray));
    }
}
