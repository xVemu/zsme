package pl.vemu.zsme;

import android.content.Context;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.work.ListenableWorker;
import androidx.work.testing.TestListenableWorkerBuilder;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.concurrent.ExecutionException;

import pl.vemu.zsme.newsFragment.NewsWorker;

import static org.junit.Assert.assertEquals;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {

    private Context mContext;

    @Before
    public void setUp() {
        mContext = ApplicationProvider.getApplicationContext();
    }


    @Test
    public void notification() throws ExecutionException, InterruptedException {
        // Context of the app under test.
//        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        ListenableWorker worker = TestListenableWorkerBuilder.from(mContext, NewsWorker.class).build();
        ListenableWorker.Result result = worker.startWork().get();
        assertEquals(result, ListenableWorker.Result.success());

//        assertEquals("pl.vemu.zsme", appContext.getPackageName());
    }
}
