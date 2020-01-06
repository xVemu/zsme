package pl.vemu.zsme;

import android.os.AsyncTask;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;

public class DownloadNews extends AsyncTask<NewsAdapter, Void, NewsAdapter> {

    private final int page;

    DownloadNews(int page) {
        this.page = page;
    }

    @Override
    protected NewsAdapter doInBackground(NewsAdapter... newsAdapters) {
        try {
            String url = String.format("http://zsme.tarnow.pl/wp/page/%s/", page);
            Document document = Jsoup.connect(url).get();
            Elements description = document.select(".article-entry");
            Elements title = document.select(".article-title");
            Elements imgs = document.select(".futured");
            for (int i = 0; i < title.size(); i++) {
                newsAdapters[0].newsItems.add(new NewsItem(title.get(i).text(),
                        description.get(i).text(),
                        imgs.get(i).childNode(0).childNode(0).attr("src")));
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
