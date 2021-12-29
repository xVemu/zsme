package pl.vemu.zsme

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.work.ListenableWorker
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import androidx.work.testing.TestListenableWorkerBuilder
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.anyInt
import org.mockito.Mockito.anyString
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.stub
import pl.vemu.zsme.data.db.PostDAO
import pl.vemu.zsme.data.service.ZSMEService
import pl.vemu.zsme.ui.post.PostPreviewProvider
import pl.vemu.zsme.ui.post.PostWorker
import pl.vemu.zsme.util.mappers.PostMapper
import java.io.IOException
import java.util.concurrent.ExecutionException

@RunWith(MockitoJUnitRunner::class)
class Notifications {
    private lateinit var context: Context

    private val postDao = mock<PostDAO> {
        on { getAll() } doReturn flowOf(dummyPostModel)
    }

    @Mock
    private lateinit var zsmeService: ZSMEService

    private val postMapper = PostMapper()

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext()
    }

    @Throws(ExecutionException::class, InterruptedException::class, IOException::class)
    @Test
    fun singleNotification() = runBlocking { // notification stays only when debug, not run
        zsmeService.stub {
            onBlocking {
                searchPosts(
                    query = anyString(),
                    page = anyInt(),
                    perPage = anyInt()
                )
            } doReturn dummyPostEntities
        }
        val worker = TestListenableWorkerBuilder<PostWorker>(context)
            .setWorkerFactory(PostWorkerFactory())
            .build()
        val result = worker.doWork()
        assertTrue(result is ListenableWorker.Result.Success)
    }

    inner class PostWorkerFactory : WorkerFactory() {
        override fun createWorker(
            appContext: Context,
            workerClassName: String,
            workerParameters: WorkerParameters
        ): ListenableWorker =
            PostWorker(
                appContext,
                workerParameters,
                postDAO = postDao,
                zsmeService = zsmeService,
                postMapper = postMapper
            )
    }

    private val postItemPreviewProvider = PostPreviewProvider()

    private val dummyPostModel = postItemPreviewProvider.values.last()

    private val dummyPostEntities =
        postMapper.mapToEntityList(postItemPreviewProvider.values.toList())
}