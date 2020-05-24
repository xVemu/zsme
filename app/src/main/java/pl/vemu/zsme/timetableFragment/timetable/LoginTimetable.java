package pl.vemu.zsme.timetableFragment.timetable;


import android.os.AsyncTask;

import org.jsoup.HttpStatusException;

import java.io.IOException;

import lombok.RequiredArgsConstructor;
import pl.vemu.zsme.STATIC;
import pl.vemu.zsme.timetableFragment.Download;

@RequiredArgsConstructor
public class LoginTimetable extends AsyncTask<String, Void, Integer> implements Download {

    private final AsyncTaskContext context;

    @Override
    protected Integer doInBackground(String... strings) {
        try {
            STATIC.LOGIN = strings[0];
            getNews("lista.html");
            return 200;
        } catch (HttpStatusException e) {
            return e.getStatusCode();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return 404;
    }

    @Override
    protected void onPostExecute(Integer code) {
        if (code == 200) context.login();
        else context.wrong(code);
    }
}
