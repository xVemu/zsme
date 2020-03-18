package pl.vemu.zsme.timetableFragment.timetable;


import android.os.AsyncTask;

import org.jsoup.HttpStatusException;

import java.io.IOException;

import lombok.RequiredArgsConstructor;
import pl.vemu.zsme.STATIC;
import pl.vemu.zsme.timetableFragment.Download;

@RequiredArgsConstructor
public class LoginTimetable extends AsyncTask<String, Void, Boolean> implements Download {

    private final AsyncTaskContext context;

    @Override
    protected Boolean doInBackground(String... strings) {
        try {
            STATIC.LOGIN = strings[0];
            getNews("lista.html");
            return true;
        } catch (HttpStatusException e) {
            if (e.getStatusCode() == 401) return false;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    protected void onPostExecute(Boolean isCorrect) {
        if (isCorrect) context.login();
        else context.wrong();
    }
}
