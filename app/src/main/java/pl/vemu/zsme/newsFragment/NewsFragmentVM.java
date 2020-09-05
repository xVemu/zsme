package pl.vemu.zsme.newsFragment;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class NewsFragmentVM extends ViewModel {

    private final MutableLiveData<List<NewsItem>> list = new MutableLiveData<>(new ArrayList<>());
    private final MutableLiveData<Boolean> notFound = new MutableLiveData<>(false);
    private final MutableLiveData<Boolean> isRefreshing = new MutableLiveData<>(false);
    private int page = 1;

    public LiveData<List<NewsItem>> getList() {
        return list;
    }

    public LiveData<Boolean> getNotFound() {
        return notFound;
    }

    public LiveData<Boolean> getIsRefreshing() {
        return isRefreshing;
    }

    public void downloadNews(Queries query) {
        new Thread(() -> {
            isRefreshing.postValue(true);
            try {
                List<NewsItem> items = new ArrayList<>(Objects.requireNonNull(list.getValue()));
                items.addAll(DownloadNews.INSTANCE.downloadNews(query, page));
                list.postValue(items);
                page++;
            } catch (IOException e) {
                e.printStackTrace();
                if (page == 1)
                    notFound.postValue(true);
            } finally {
                isRefreshing.postValue(false);
            }
        }).start();
    }

    public void clearList() {
        page = 1;
        notFound.postValue(false);
        list.postValue(new ArrayList<>());
    }
}
