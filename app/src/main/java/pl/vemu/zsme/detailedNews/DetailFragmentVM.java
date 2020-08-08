package pl.vemu.zsme.detailedNews;

import android.app.Application;
import android.text.Spanned;
import android.view.View;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.navigation.Navigation;

import java.util.ArrayList;

import pl.vemu.zsme.R;

public class DetailFragmentVM extends AndroidViewModel implements View.OnClickListener {
    //TODO check if can be livedata with value in constructor
    private final MutableLiveData<Spanned> text;
    private final MutableLiveData<Boolean> isUpdating;
    private final MutableLiveData<ArrayList<String>> images;

    public DetailFragmentVM(Application application, String url) {
        super(application);
        text = DetailFragmentRepository.INSTANCE.getText();
        isUpdating = DetailFragmentRepository.INSTANCE.getIsUpdating();
        images = DetailFragmentRepository.INSTANCE.getImages();
        if (!url.startsWith("http") && !url.startsWith("https"))
            url = application.getString(R.string.zsme_default_link) + url;
        DetailFragmentRepository.INSTANCE.downloadText(url, application.getString(R.string.error));
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


    @Override
    public void onClick(View v) {
        String[] imagesArray = new String[images.getValue().size()];
        images.getValue().toArray(imagesArray);
        Navigation.findNavController(v).navigate(DetailFragmentDirections.actionDetailFragmentToGalleryFragment(imagesArray));
    }
}
