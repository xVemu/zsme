package pl.vemu.zsme.newsFragment;

import android.os.AsyncTask;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class DownloadNews extends AsyncTask<NewsAdapter, Void, NewsAdapter> implements IMakeNewsItem {

    private final int page;
    private final String search;

    public DownloadNews(int page) {
        this(page, null);
    }

    @Override
    protected NewsAdapter doInBackground(NewsAdapter... newsAdapters) {
        // TODO Add throw expection
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
        newsAdapter.notifyDataSetChanged();
        super.onPostExecute(newsAdapter);
    }
}
