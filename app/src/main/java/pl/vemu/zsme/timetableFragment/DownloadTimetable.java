package pl.vemu.zsme.timetableFragment;


import android.os.AsyncTask;
import android.view.View;
import android.widget.TextView;

import org.jsoup.Connection;
import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.w3c.dom.Text;

import java.io.IOException;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class DownloadTimetable extends AsyncTask<String, Void, Boolean> {

    private final IAsyncTaskContext context;

    // TODO Handle exception
    @Override
    protected Boolean doInBackground(String... strings) {
        try {
            String url = strings[0];
            String base64login = strings[1];
            Jsoup.connect(url)
                    .timeout(30000)
                    .method(Connection.Method.GET)
                    .userAgent(
                            "Mozilla/5.0 (Windows NT 10.0; WOW64; rv:41.0) Gecko/20100101 Firefox/41.0")
                    .header("Authorization", "Basic " + base64login)
                    .execute();
            return true;
        } catch (HttpStatusException e) {
            if (e.getStatusCode() == 401) return false;
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    protected void onPostExecute(Boolean isCorrect) {
        super.onPostExecute(isCorrect);
        if (isCorrect) context.login();
        else context.wrong();
    }
}
