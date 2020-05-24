package pl.vemu.zsme.detailedNews;

import android.os.AsyncTask;
import android.view.View;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class DownloadDetailedNews extends AsyncTask<String, Integer, String> {

    private final IAsyncTaskContext context;

    @Override
    protected void onPreExecute() {
        context.setProgressVisibility(View.VISIBLE);
    }

    @Override
    protected String doInBackground(String... strings) {
        try {
            Document document = Jsoup.connect(strings[0]).get();

            Element s = document.selectFirst(".single-post");
            if (s == null) return "Wystąpił błąd";
            Elements a_img = s.select("a img");
            for (Element element : a_img) {
                element.parent().unwrap();
            }
            return s.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "Wystąpił błąd";
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        context.setProgress(values[0]);
    }

    @Override
    protected void onPostExecute(String string) {
        context.setProgressVisibility(View.GONE);
        context.setDetailText(string);
    }
}
