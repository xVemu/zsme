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
import pl.vemu.zsme.data.model.PostModel
import pl.vemu.zsme.data.service.ZSMEService
import pl.vemu.zsme.ui.post.PostWorker
import pl.vemu.zsme.util.mappers.PostMapper
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
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

    private val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US)

    private val dummyPostModel = PostModel(
        id = 27451,
        date = dateFormat.parse("2021-11-07T12:49:33")!!,
        link = "https://zsme.tarnow.pl/wp/przygotowania-do-kiermaszu-bozonarodzeniowego/",
        title = "Przygotowania do kiermaszu bożonarodzeniowego",
        content = "\n<p>W ZSME trwają przygotowania do kiermaszu bożonarodzeniowego. W bibliotece wykonywane są stroiki świąteczne. Zwracamy się z uprzejmą prośbą o przynoszenie: sztucznych choinek i gwiazd betlejemskich, figurek aniołków, mikołajów, reniferów, gwiazdek, świeczek, itp. Powstaną z nich oryginalne dekoracje, które sprzedane zostaną na kiermaszu przy Kościele Księży Misjonarzy w Tarnowie. Dochód z przedsięwzięcia w całości przeznaczony zostanie na działania statutowe Tarnowskiego Hospicjum Domowego. Mile widziane są także gotowe stroiki!</p>\n\n\n\n<p>Koordynatorki akcji: Renata Chlupka – Kosiba i Agnieszka Gawron</p>\n",
        excerpt = "<p>W ZSME trwają przygotowania do kiermaszu bożonarodzeniowego. W bibliotece wykonywane są stroiki świąteczne. Zwracamy się z uprzejmą prośbą o przynoszenie: sztucznych choinek i gwiazd betlejemskich, figurek aniołków, mikołajów, reniferów, gwiazdek, świeczek, itp. Powstaną z nich oryginalne dekoracje, które sprzedane zostaną na kiermaszu przy Kościele Księży Misjonarzy w Tarnowie. Dochód z przedsięwzięcia w całości przeznaczony zostanie [&hellip;]</p>\n",
        author = "Renata Chlupka-Kosiba",
        thumbnail = "https://zsme.tarnow.pl/wp/wp-content/uploads/2021/11/thumb-100x91.jpg",
        category = "Wolontariat"
    )

    private val dummyPostEntities = postMapper.mapToEntityList(
        listOf(
            PostModel(
                id = 27490,
                date = dateFormat.parse("2021-12-06T16:13:55")!!,
                link = "https://zsme.tarnow.pl/wp/turniej-szachowy-2/",
                title = "Turniej Szachowy",
                content = "\n<p>W dniu 15.12.2021r. (środa) o godz. 10:40 odbędzie się Turniej Szachowy o puchar Dyrektora ZSME. Zapisywać można się u nauczycieli wychowania fizycznego do 13.12.2021 r. (poniedziałek). Szczegółowych informacji na temat zawodów udziela pan Marcin Rej. </p>\n\n\n\n<p>Zapraszamy. </p>\n",
                excerpt = "<p>W dniu 15.12.2021r. (środa) o godz. 10:40 odbędzie się Turniej Szachowy o puchar Dyrektora ZSME. Zapisywać można się u nauczycieli wychowania fizycznego do 13.12.2021 r. (poniedziałek). Szczegółowych informacji na temat zawodów udziela pan Marcin Rej. Zapraszamy. </p>\n",
                author = "Marcin Rej",
                thumbnail = "https://zsme.tarnow.pl/wp/wp-content/uploads/2019/05/thumb.jpg",
                category = "Konkursy i turnieje"
            ),
            PostModel(
                id = 27451,
                date = dateFormat.parse("2021-11-07T12:49:33")!!,
                link = "https://zsme.tarnow.pl/wp/przygotowania-do-kiermaszu-bozonarodzeniowego/",
                title = "Przygotowania do kiermaszu bożonarodzeniowego",
                content = "\n<p>W ZSME trwają przygotowania do kiermaszu bożonarodzeniowego. W bibliotece wykonywane są stroiki świąteczne. Zwracamy się z uprzejmą prośbą o przynoszenie: sztucznych choinek i gwiazd betlejemskich, figurek aniołków, mikołajów, reniferów, gwiazdek, świeczek, itp. Powstaną z nich oryginalne dekoracje, które sprzedane zostaną na kiermaszu przy Kościele Księży Misjonarzy w Tarnowie. Dochód z przedsięwzięcia w całości przeznaczony zostanie na działania statutowe Tarnowskiego Hospicjum Domowego. Mile widziane są także gotowe stroiki!</p>\n\n\n\n<p>Koordynatorki akcji: Renata Chlupka – Kosiba i Agnieszka Gawron</p>\n",
                excerpt = "<p>W ZSME trwają przygotowania do kiermaszu bożonarodzeniowego. W bibliotece wykonywane są stroiki świąteczne. Zwracamy się z uprzejmą prośbą o przynoszenie: sztucznych choinek i gwiazd betlejemskich, figurek aniołków, mikołajów, reniferów, gwiazdek, świeczek, itp. Powstaną z nich oryginalne dekoracje, które sprzedane zostaną na kiermaszu przy Kościele Księży Misjonarzy w Tarnowie. Dochód z przedsięwzięcia w całości przeznaczony zostanie [&hellip;]</p>\n",
                author = "Renata Chlupka-Kosiba",
                thumbnail = "https://zsme.tarnow.pl/wp/wp-content/uploads/2021/11/thumb-100x91.jpg",
                category = "Wolontariat"
            )
        )
    )
}