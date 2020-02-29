package pl.vemu.zsme.detailedNews;

import android.os.AsyncTask;
import android.view.View;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class DownloadDetailedNews extends AsyncTask<String, Integer, DetailItem> {

    private final IAsyncTaskContext context;

    @Override
    protected void onPreExecute() {
        context.setProgressVisibility(View.VISIBLE);
    }

    @Override
    protected DetailItem doInBackground(String... strings) {
        try {
            Document document = Jsoup.connect(strings[0]).get();
            return new DetailItem.DetailItemBuilder()
                    .author(document.selectFirst(".article-author").text())
                    .date(document.selectFirst(".article-date").text())
                    .detailText(document.selectFirst(".article-entry").text()).build();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        context.setProgress(values[0]);
    }

    @Override
    protected void onPostExecute(DetailItem detailItem) {
        context.setProgressVisibility(View.GONE);
        context.setDetailText(detailItem.getDetailText());
        context.setDate(detailItem.getDate());
        context.setAuthor(detailItem.getAuthor());
    }
}
