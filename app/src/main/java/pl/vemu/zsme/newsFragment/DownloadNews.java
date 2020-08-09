package pl.vemu.zsme.newsFragment;

import androidx.lifecycle.MutableLiveData;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
enum DownloadNews {
    INSTANCE;

    private final MutableLiveData<List<NewsItem>> list = new MutableLiveData<>(new ArrayList<>());
    private final MutableLiveData<Boolean> notFound = new MutableLiveData<>(false);
    private final MutableLiveData<Boolean> isRefreshing = new MutableLiveData<>(false);
    @Setter
    private int page = 1;

    public void downloadNews(Queries query) {
        isRefreshing.postValue(true);
        downloadNewsThread(query);
    }

    private void downloadNewsThread(Queries query) {
        new Thread(() -> {
            try {
                String url = "https://zsme.tarnow.pl/wp/" + query.parseUrl(page);
                page++;
                Document document = Jsoup.connect(url).get();
                if (document.selectFirst(".column-one") == null) {
                    notFound.postValue(true);
                    return;
                }
                Elements columnOneNews = document.selectFirst(".column-one").children();
                Elements columnTwoNews = document.selectFirst(".column-two").children();
                List<NewsItem> newsItems = list.getValue();
                for (int i = 0; i < columnOneNews.size(); i++) {
                    newsItems.add(NewsItem.makeNewsItem(columnOneNews.get(i)));
                    if (columnTwoNews.get(i).selectFirst(".article-title") != null)
                        newsItems.add(NewsItem.makeNewsItem(columnTwoNews.get(i)));
                }
                list.postValue(newsItems);
            } catch (IOException e) {
                e.printStackTrace();
                notFound.postValue(true);
            } finally {
                isRefreshing.postValue(false);
            }
        }).start();
    }
}
