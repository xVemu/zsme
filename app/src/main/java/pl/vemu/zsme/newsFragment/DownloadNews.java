package pl.vemu.zsme.newsFragment;

import android.os.AsyncTask;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;

import lombok.RequiredArgsConstructor;
import lombok.Setter;

@RequiredArgsConstructor
public class DownloadNews extends AsyncTask<NewsAdapter, Void, NewsAdapter> implements IMakeNewsItem {

    private final int page;
    private final String search;
    @Setter
    private static IAsyncTaskContext context;

    public DownloadNews(int page) {
        this(page, null);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        context.startRefreshing();
    }

    @Override
    protected NewsAdapter doInBackground(NewsAdapter... newsAdapters) {
        try {
            String url;
            if (search == null) url =  String.format("http://zsme.tarnow.pl/wp/page/%s/", page);
            else {
                url = String.format("http://zsme.tarnow.pl/wp/page/%s/?s=", page) + search;
                newsAdapters[0].removeAllItems();
            }
            Document document = Jsoup.connect(url).get();
            if (document.selectFirst(".column-one") == null) return null;
            Elements columnOneNews = document.selectFirst(".column-one").children();
            Elements columnTwoNews = document.selectFirst(".column-two").children();
            for (int i = 0; i < columnOneNews.size(); i++) {
                newsAdapters[0].addNewsItem(makeNewsItem(columnOneNews.get(i)));
                newsAdapters[0].addNewsItem(makeNewsItem(columnTwoNews.get(i)));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return newsAdapters[0];
    }

    @Override
    protected void onPostExecute(NewsAdapter newsAdapter) {
        if (newsAdapter == null) {
            context.setIsFound(false);
        } else {
            super.onPostExecute(newsAdapter);
            context.setIsFound(true);
            newsAdapter.notifyDataSetChanged();
        }
    }
}
