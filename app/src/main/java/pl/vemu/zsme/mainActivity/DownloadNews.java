package pl.vemu.zsme.mainActivity;

import android.os.AsyncTask;
import android.util.Log;
import android.widget.ProgressBar;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;

import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@AllArgsConstructor
public class DownloadNews extends AsyncTask<NewsAdapter, Void, NewsAdapter> {

    private final int page;
    private final String search;

    public DownloadNews(int page) {
        this(page, null);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected NewsAdapter doInBackground(NewsAdapter... newsAdapters) {
        try {
            String url;
            if (search == null) url =  String.format("http://zsme.tarnow.pl/wp/page/%s/", page);
            else if (search.equals("")) {
                url =  String.format("http://zsme.tarnow.pl/wp/page/%s/", page);
                newsAdapters[0].removeAllItems();
            }
            else {
                url = String.format("http://zsme.tarnow.pl/wp/page/%s/?s=", page) + search;
                newsAdapters[0].removeAllItems();
            }
            Document document = Jsoup.connect(url).get();
            Elements columnOneNews = document.selectFirst(".column-one").children();
            Elements columnTwoNews = document.selectFirst(".column-two").children();
            for (int i = 0; i < columnOneNews.size(); i++) {
                newsAdapters[0].addNewsItem(new NewsItem(columnOneNews.get(i).selectFirst(".article-title").text(),
                        columnOneNews.get(i).selectFirst(".article-entry").text(),
                        columnOneNews.get(i).selectFirst("img").attr("src"),
                        columnOneNews.get(i).selectFirst("a").attr("href")));
                newsAdapters[0].addNewsItem(new NewsItem(columnTwoNews.get(i).selectFirst(".article-title").text(),
                        columnTwoNews.get(i).selectFirst(".article-entry").text(),
                        columnTwoNews.get(i).selectFirst("img").attr("src"),
                        columnTwoNews.get(i).selectFirst("a").attr("href")));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return newsAdapters[0];
    }

    @Override
    protected void onPostExecute(NewsAdapter newsAdapter) {
        newsAdapter.notifyDataSetChanged();
        super.onPostExecute(newsAdapter);
    }
}
