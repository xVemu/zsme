package pl.vemu.zsme.detailedNews;

import android.os.AsyncTask;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class DownloadDetailedNews extends AsyncTask<String, Void, String> {

    private final IAsyncTaskContext context;

    @Override
    protected String doInBackground(String... strings) {
        try {
            Document document = Jsoup.connect(strings[0]).get();
            return document.selectFirst(".article-entry").text();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "Wystąpił błąd";
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        context.setDetailText(s);
    }
}
