package pl.vemu.zsme

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.filters.SmallTest
import androidx.work.ListenableWorker
import androidx.work.testing.TestListenableWorkerBuilder
import kotlinx.coroutines.runBlocking
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Before
import org.junit.Test
import pl.vemu.zsme.ui.post.PostWorker
import java.io.IOException
import java.util.concurrent.ExecutionException

@SmallTest
class Notifications {
    private lateinit var context: Context

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext()
    }

    /*@Test
    @Throws(ExecutionException::class, InterruptedException::class, IOException::class)
    fun multipleNotification() {
        val memory = context.getSharedPreferences("latest-news", Context.MODE_PRIVATE)
        val editor = memory.edit()
        val downloaded: NewsItem = DownloadNews.INSTANCE.downloadNews(Page(), 1).get(4)
        editor.putInt("article", downloaded.hashCode()).apply()
        val worker = TestListenableWorkerBuilder.from(context!!, NewsWorker::class.java).build()
        val result = worker.startWork().get()
        Assert.assertEquals(ListenableWorker.Result.success(), result)
    }*/

    @Test
    @Throws(ExecutionException::class, InterruptedException::class, IOException::class)
    fun singleNotification() {
//        val memory = context.getSharedPreferences("latest-news", Context.MODE_PRIVATE)
//        val editor = memory.edit()
//        val downloaded: NewsItem = DownloadNews.INSTANCE.downloadNews(Page(), 1).get(1)
//        editor.putInt("article", downloaded.hashCode()).apply()
        val worker = TestListenableWorkerBuilder<PostWorker>(context).build()
        runBlocking {
            val result = worker.doWork()
            assertThat(result, `is`(ListenableWorker.Result.success()))
        }
    }
}