package pl.vemu.zsme;

import android.os.AsyncTask;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;

public class DownloadNews extends AsyncTask<NewsAdapter, Void, NewsAdapter> {

    @Override
    protected NewsAdapter doInBackground(NewsAdapter... newsAdapters) {
        try {
            String url = "http://zsme.tarnow.pl/";
            Document document = Jsoup.connect(url).get();
            Elements description = document.select(".article-entry");
            Elements title = document.select(".article-title");
            for (int i = 0; i < title.size(); i++) {
                newsAdapters[0].newsItems.add(new NewsItem(title.get(i).text(), description.get(i).text()));
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
