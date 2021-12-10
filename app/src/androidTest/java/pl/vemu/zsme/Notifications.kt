package pl.vemu.zsme

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.work.ListenableWorker
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import androidx.work.testing.TestListenableWorkerBuilder
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.`is`
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.junit.MockitoJUnitRunner
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

    @Mock
    private lateinit var postDao: PostDAO

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
        `when`(postDao.getAll()).thenReturn(flowOf(dummyPostModel))
        `when`(zsmeService.searchPosts(anyString(), anyInt(), anyInt())).thenReturn(
            dummyPostEntities
        )
        val worker = TestListenableWorkerBuilder<PostWorker>(context)
            .setWorkerFactory(PostWorkerFactory())
            .build()
        val result = worker.doWork()
        assertThat(result, `is`(ListenableWorker.Result.success()))
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

    private val dummyPostModelFromDb = PostModel(
        id = 27441,
        date = dateFormat.parse("2021-11-05T11:17:59")!!,
        link = "https://zsme.tarnow.pl/wp/upoluj-swoja-ksiazke-2/",
        title = "Upoluj swoją książkę",
        content = "\n<p>Od kilku lat w listopadzie, biblioteka szkolna zaprasza do skorzystania z propozycji portalu Czytaj PL, który organizuje akcję „Upoluj swoją książkę”. Głównym celem przedsięwzięcia jest promocja czytelnictwa oraz popularyzowanie usług polegających na korzystaniu z publikacji elektronicznych (e-booków i audiobooków).</p>\n\n\n\n<p>Zobacz jakie to proste! Wejdź do akcji Czytaj PL!</p>\n\n\n\n<ol><li>Pobierz darmową aplikację Woblink (Android/iOS).</li><li>Zeskanuj kod QR.</li><li>Czytaj i słuchaj za darmo do 30.11.2021 roku.</li></ol>\n\n\n\n<p></p>\n\n\n\n<p></p>\n\n\n\n<figure class=\"wp-block-gallery columns-3 is-cropped\"><ul class=\"blocks-gallery-grid\"><li class=\"blocks-gallery-item\"><figure><img loading=\"lazy\" width=\"719\" height=\"1024\" src=\"https://zsme.tarnow.pl/wp/wp-content/uploads/2021/11/1-1-719x1024.png\" alt=\"\" data-id=\"27443\" data-full-url=\"https://zsme.tarnow.pl/wp/wp-content/uploads/2021/11/1-1.png\" data-link=\"https://zsme.tarnow.pl/wp/?attachment_id=27443\" class=\"wp-image-27443\" srcset=\"https://zsme.tarnow.pl/wp/wp-content/uploads/2021/11/1-1-719x1024.png 719w, https://zsme.tarnow.pl/wp/wp-content/uploads/2021/11/1-1-210x300.png 210w, https://zsme.tarnow.pl/wp/wp-content/uploads/2021/11/1-1-70x100.png 70w, https://zsme.tarnow.pl/wp/wp-content/uploads/2021/11/1-1-768x1095.png 768w, https://zsme.tarnow.pl/wp/wp-content/uploads/2021/11/1-1-1078x1536.png 1078w, https://zsme.tarnow.pl/wp/wp-content/uploads/2021/11/1-1.png 1437w\" sizes=\"(max-width: 719px) 100vw, 719px\" /></figure></li><li class=\"blocks-gallery-item\"><figure><img loading=\"lazy\" width=\"800\" height=\"800\" src=\"https://zsme.tarnow.pl/wp/wp-content/uploads/2021/11/2.png\" alt=\"\" data-id=\"27444\" data-full-url=\"https://zsme.tarnow.pl/wp/wp-content/uploads/2021/11/2.png\" data-link=\"https://zsme.tarnow.pl/wp/?attachment_id=27444\" class=\"wp-image-27444\" srcset=\"https://zsme.tarnow.pl/wp/wp-content/uploads/2021/11/2.png 800w, https://zsme.tarnow.pl/wp/wp-content/uploads/2021/11/2-300x300.png 300w, https://zsme.tarnow.pl/wp/wp-content/uploads/2021/11/2-100x100.png 100w, https://zsme.tarnow.pl/wp/wp-content/uploads/2021/11/2-768x768.png 768w\" sizes=\"(max-width: 800px) 100vw, 800px\" /></figure></li><li class=\"blocks-gallery-item\"><figure><img loading=\"lazy\" width=\"533\" height=\"533\" src=\"https://zsme.tarnow.pl/wp/wp-content/uploads/2021/11/3.png\" alt=\"\" data-id=\"27445\" data-full-url=\"https://zsme.tarnow.pl/wp/wp-content/uploads/2021/11/3.png\" data-link=\"https://zsme.tarnow.pl/wp/?attachment_id=27445\" class=\"wp-image-27445\" srcset=\"https://zsme.tarnow.pl/wp/wp-content/uploads/2021/11/3.png 533w, https://zsme.tarnow.pl/wp/wp-content/uploads/2021/11/3-300x300.png 300w, https://zsme.tarnow.pl/wp/wp-content/uploads/2021/11/3-100x100.png 100w\" sizes=\"(max-width: 533px) 100vw, 533px\" /></figure></li><li class=\"blocks-gallery-item\"><figure><img loading=\"lazy\" width=\"533\" height=\"533\" src=\"https://zsme.tarnow.pl/wp/wp-content/uploads/2021/11/4.png\" alt=\"\" data-id=\"27446\" data-full-url=\"https://zsme.tarnow.pl/wp/wp-content/uploads/2021/11/4.png\" data-link=\"https://zsme.tarnow.pl/wp/?attachment_id=27446\" class=\"wp-image-27446\" srcset=\"https://zsme.tarnow.pl/wp/wp-content/uploads/2021/11/4.png 533w, https://zsme.tarnow.pl/wp/wp-content/uploads/2021/11/4-300x300.png 300w, https://zsme.tarnow.pl/wp/wp-content/uploads/2021/11/4-100x100.png 100w\" sizes=\"(max-width: 533px) 100vw, 533px\" /></figure></li><li class=\"blocks-gallery-item\"><figure><img loading=\"lazy\" width=\"534\" height=\"533\" src=\"https://zsme.tarnow.pl/wp/wp-content/uploads/2021/11/5.png\" alt=\"\" data-id=\"27447\" data-full-url=\"https://zsme.tarnow.pl/wp/wp-content/uploads/2021/11/5.png\" data-link=\"https://zsme.tarnow.pl/wp/?attachment_id=27447\" class=\"wp-image-27447\" srcset=\"https://zsme.tarnow.pl/wp/wp-content/uploads/2021/11/5.png 534w, https://zsme.tarnow.pl/wp/wp-content/uploads/2021/11/5-300x300.png 300w, https://zsme.tarnow.pl/wp/wp-content/uploads/2021/11/5-100x100.png 100w\" sizes=\"(max-width: 534px) 100vw, 534px\" /></figure></li></ul></figure>\n",
        excerpt = "<p>Od kilku lat w listopadzie, biblioteka szkolna zaprasza do skorzystania z propozycji portalu Czytaj PL, który organizuje akcję „Upoluj swoją książkę”. Głównym celem przedsięwzięcia jest promocja czytelnictwa oraz popularyzowanie usług polegających na korzystaniu z publikacji elektronicznych (e-booków i audiobooków). Zobacz jakie to proste! Wejdź do akcji Czytaj PL! Pobierz darmową aplikację Woblink (Android/iOS). Zeskanuj kod [&hellip;]</p>\n",
        author = "Renata Chlupka-Kosiba",
        thumbnail = "https://zsme.tarnow.pl/wp/wp-content/uploads/2021/11/2-100x100.png",
        category = "Wydarzenia kulturalne"
    )
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