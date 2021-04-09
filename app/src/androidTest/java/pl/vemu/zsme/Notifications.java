package pl.vemu.zsme;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.work.ListenableWorker;
import androidx.work.testing.TestListenableWorkerBuilder;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

import pl.vemu.zsme.newsFragment.DownloadNews;
import pl.vemu.zsme.newsFragment.NewsItem;
import pl.vemu.zsme.newsFragment.NewsWorker;
import pl.vemu.zsme.newsFragment.Queries;

import static org.junit.Assert.assertEquals;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class Notifications {

    private Context mContext;

    @Before
    public void setUp() {
        mContext = ApplicationProvider.getApplicationContext();
    }


    @Test
    public void multipleNotification() throws ExecutionException, InterruptedException, IOException {
        SharedPreferences memory = mContext.getSharedPreferences("latest-news", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = memory.edit();
        NewsItem downloaded = DownloadNews.INSTANCE.downloadNews(new Queries.Page(), 1).get(4);
        editor.putInt("article", downloaded.hashCode()).apply();
        ListenableWorker worker = TestListenableWorkerBuilder.from(mContext, NewsWorker.class).build();
        ListenableWorker.Result result = worker.startWork().get();
        assertEquals(ListenableWorker.Result.success(), result);
    }

    @Test
    public void singleNotification() throws ExecutionException, InterruptedException, IOException {
        SharedPreferences memory = mContext.getSharedPreferences("latest-news", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = memory.edit();
        NewsItem downloaded = DownloadNews.INSTANCE.downloadNews(new Queries.Page(), 1).get(1);
        editor.putInt("article", downloaded.hashCode()).apply();
        ListenableWorker worker = TestListenableWorkerBuilder.from(mContext, NewsWorker.class).build();
        ListenableWorker.Result result = worker.startWork().get();
        assertEquals(ListenableWorker.Result.success(), result);
    }
}
