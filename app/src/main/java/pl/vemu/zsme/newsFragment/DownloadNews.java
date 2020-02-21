package pl.vemu.zsme.newsFragment;

import android.os.AsyncTask;
import android.view.View;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;

import lombok.RequiredArgsConstructor;
import lombok.Setter;
import pl.vemu.zsme.detailedNews.IAsyncTaskContext;

@RequiredArgsConstructor
class DownloadNews extends AsyncTask<NewsAdapter, Integer, NewsAdapter> implements IMakeNewsItem {

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
        context.setProgressVisibility(View.VISIBLE);
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
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
        context.setProgress(values[0]);
    }

    @Override
    protected void onPostExecute(NewsAdapter newsAdapter) {
        super.onPostExecute(newsAdapter);
        newsAdapter.notifyDataSetChanged();
        context.setProgressVisibility(View.GONE);
    }
}
