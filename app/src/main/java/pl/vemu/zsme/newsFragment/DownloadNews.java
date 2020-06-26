package pl.vemu.zsme.newsFragment;

import android.os.AsyncTask;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;

import lombok.RequiredArgsConstructor;
import lombok.Setter;

@RequiredArgsConstructor
public class DownloadNews extends AsyncTask<NewsAdapter, Void, NewsAdapter> {

    @Setter
    private static AsyncTaskContext context;
    private final int page;
    private final String id;

    public DownloadNews(int page) {
        this(page, null);
    }

    @Override
    protected void onPreExecute() {
        context.startRefreshing();
    }

    @Override
    protected NewsAdapter doInBackground(NewsAdapter... newsAdapters) {
        try {
            String url = "https://zsme.tarnow.pl/wp/";
            if (id == null) url += "/page/" + page;
            else if (id.startsWith("author")) url += id + "/page/" + page;
            else url += "/page/" + page + "/?s=" + id;
            Document document = Jsoup.connect(url).get();
            if (document.selectFirst(".column-one") == null) return null;
            Elements columnOneNews = document.selectFirst(".column-one").children();
            Elements columnTwoNews = document.selectFirst(".column-two").children();
            for (int i = 0; i < columnOneNews.size(); i++) {
                newsAdapters[0].addNewsItem(NewsItem.makeNewsItem(columnOneNews.get(i)));
                if (columnTwoNews.get(i).selectFirst(".article-title") != null)
                    newsAdapters[0].addNewsItem(NewsItem.makeNewsItem(columnTwoNews.get(i)));
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
            context.setIsFound(true);
            newsAdapter.notifyDataSetChanged();
        }
    }
}
