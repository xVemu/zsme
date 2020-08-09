package pl.vemu.zsme.newsFragment;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.List;

public class NewsFragmentVM extends ViewModel {

    private final MutableLiveData<List<NewsItem>> list;
    private final MutableLiveData<Boolean> notFound;
    private final MutableLiveData<Boolean> isRefreshing;

    public NewsFragmentVM() {
        list = DownloadNews.INSTANCE.getList();
        isRefreshing = DownloadNews.INSTANCE.getIsRefreshing();
        notFound = DownloadNews.INSTANCE.getNotFound();
    }

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
        DownloadNews.INSTANCE.downloadNews(query);
    }

    public void clearList() {
        notFound.postValue(false);
        list.postValue(new ArrayList<>());
        DownloadNews.INSTANCE.setPage(1);
    }
}
