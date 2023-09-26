package pl.vemu.zsme.ui.post

import android.text.format.DateUtils
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import coil.compose.AsyncImage
import coil.request.ImageRequest
import coil.transform.RoundedCornersTransformation
import com.ramcosta.composedestinations.annotation.DeepLink
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.NavGraph
import com.ramcosta.composedestinations.annotation.RootNavGraph
import com.ramcosta.composedestinations.navigation.navigate
import kotlinx.coroutines.launch
import pl.vemu.zsme.DEFAULT_URL
import pl.vemu.zsme.R
import pl.vemu.zsme.data.model.PostModel
import pl.vemu.zsme.paddingBottom
import pl.vemu.zsme.paddingTop
import pl.vemu.zsme.ui.components.HTMLText
import pl.vemu.zsme.ui.components.SimpleSnackbar
import pl.vemu.zsme.ui.components.SlideTransition
import pl.vemu.zsme.ui.components.SmallText
import pl.vemu.zsme.ui.destinations.DetailDestination
import java.net.UnknownHostException
import java.text.SimpleDateFormat
import java.util.*

@RootNavGraph(start = true)
@NavGraph
annotation class PostNavGraph(
    val start: Boolean = false
)


@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
@PostNavGraph(start = true)
@Destination(
    route = "post/main",
    deepLinks = [DeepLink(uriPattern = DEFAULT_URL)],
    style = SlideTransition::class
)
@Composable
fun Post(
    navController: NavController,
    vm: PostVM = hiltViewModel()
) {
    val pagingItems = vm.posts.collectAsLazyPagingItems()
    var isLoading by remember { mutableStateOf(false) }
    pagingItems.apply {
        isLoading = when {
            loadState.refresh is LoadState.Loading -> true
            loadState.append is LoadState.Loading -> false
            else -> false
        }
    }
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    Scaffold(topBar = {
        TopAppBar(scrollBehavior) {
            vm.query.value = it
        }
    }) { padding ->

        val pullRefreshState = rememberPullRefreshState(isLoading, { pagingItems.refresh() })

        Box(
            Modifier
                .fillMaxSize()
                .padding(padding)
                .nestedScroll(scrollBehavior.nestedScrollConnection)
                .pullRefresh(pullRefreshState)
        ) {
            val snackbarHostState = remember { SnackbarHostState() }
            val coroutineScope = rememberCoroutineScope()
            val errorMsg = stringResource(R.string.error)
            val retryMsg = stringResource(R.string.retry)
            val noConnectionMsg = stringResource(R.string.no_connection)
            LazyColumn(
                Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .nestedScroll(scrollBehavior.nestedScrollConnection)
                    .pullRefresh(pullRefreshState)
            ) {
                items(
                    count = pagingItems.itemCount,
                    key = { index -> pagingItems[index]?.id ?: index }
                ) { index ->
                    PostCard(navController = navController, postModel = pagingItems[index]!!)
                }

                pagingItems.apply {
                    when {
                        loadState.append is LoadState.Loading -> {
                            item { LoadingItem() }
                        }

                        loadState.refresh is LoadState.Error || loadState.append is LoadState.Error -> {
                            val error =
                                (loadState.refresh as? LoadState.Error)?.error
                                    ?: (loadState.append as LoadState.Error).error
                            coroutineScope.launch {
                                val result = snackbarHostState.showSnackbar(
                                    message = if (error is UnknownHostException) noConnectionMsg else errorMsg,
                                    actionLabel = retryMsg,
                                    duration = SnackbarDuration.Indefinite
                                )
                                if (result == SnackbarResult.ActionPerformed)
                                    pagingItems.retry()
                            }
                        }
                    }
                }
            }
            SimpleSnackbar(snackbarHostState)
            PullRefreshIndicator(isLoading, pullRefreshState, Modifier.align(Alignment.TopCenter))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TopAppBar(
    scrollBehavior: TopAppBarScrollBehavior,
    onQueryChange: (query: String?) -> Unit
) {
    CenterAlignedTopAppBar(
        title = { Text(text = stringResource(R.string.post)) },
        scrollBehavior = scrollBehavior,
        actions = {
            var isSearching by remember { mutableStateOf(false) }
            if (!isSearching)
                IconButton(onClick = {
                    isSearching = true
                }) {
                    Icon(
                        imageVector = Icons.Rounded.Search,
                        contentDescription = stringResource(R.string.search)
                    )
                }
            else {
                IconButton(onClick = {
                    isSearching = false
                    onQueryChange(null)
                }) {
                    Icon(imageVector = Icons.Rounded.ArrowBack, contentDescription = "Close")
                }
                var query by remember { mutableStateOf("") }
                val focusRequester = FocusRequester()
                TextField(
                    value = query,
                    onValueChange = {
                        query = it
                        onQueryChange(it)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .focusRequester(focusRequester),
                    singleLine = true,
                    placeholder = { Text(stringResource(R.string.search)) },
                    colors = TextFieldDefaults.colors(
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                    )
                )
                LaunchedEffect("RequestFocus") {
                    focusRequester.requestFocus()
                }
            }
        }
    )
}

@Preview
@Composable
private fun LoadingItem() {
    CircularProgressIndicator(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .wrapContentWidth(Alignment.CenterHorizontally),
        color = MaterialTheme.colorScheme.primary
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PostCard(
    navController: NavController,
    postModel: PostModel
) {
    ElevatedCard(
        modifier = Modifier
            .heightIn(150.dp)
            .padding(8.dp),
        onClick = {
            navController.navigate(DetailDestination(postModel))
        }
    ) {
        Row {
            Column(Modifier.padding(8.dp)) {
                AsyncImage(
                    model = ImageRequest.Builder(context = LocalContext.current)
                        .data(postModel.thumbnail ?: R.drawable.zsme)
                        .crossfade(true)
                        .transformations(RoundedCornersTransformation(radius = 12f))
                        .build(),
//                    placeholder = painterResource(R.drawable.zsme), TODO
                    contentDescription = stringResource(R.string.thumbnail),
                    modifier = Modifier
                        .size(108.dp),
                    contentScale = ContentScale.Crop
                )
                Column(
                    Modifier
                        .width(108.dp)
                        .paddingTop(24.dp)
                ) {
                    SmallText(
                        DateUtils.getRelativeTimeSpanString(
                            postModel.date.time,
                            Date().time,
                            DateUtils.DAY_IN_MILLIS
                        ).toString()
                    )
                    SmallText(postModel.author)
                    SmallText(postModel.category)
                }
            }
            MainText(postModel)
        }
    }
}

@Composable
private fun MainText(
    postModel: PostModel
) {
    Column(Modifier.padding(8.dp)) {
        HTMLText(
            html = postModel.title,
            centered = true,
            color = MaterialTheme.colorScheme.primary,
            textStyle = MaterialTheme.typography.labelLarge
        )
        HTMLText(
            html = postModel.excerpt,
            modifier = Modifier.paddingBottom(8.dp)
        )
    }
}

@Preview
@Composable
private fun PostCardPreview(@PreviewParameter(PostPreviewProvider::class) postModel: PostModel) {
    PostCard(navController = rememberNavController(), postModel = postModel)
}

class PostPreviewProvider : PreviewParameterProvider<PostModel> {

    private val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US)

    override val values = sequenceOf(
        PostModel(
            id = 27524,
            date = dateFormat.parse("2021-12-13T12:50:41")!!,
            link = "https://zsme.tarnow.pl/wp/konkurs-cyberskiller-etap-szkolny/",
            title = "Konkurs CyberSkiller &#8211; etap szkolny",
            content = "\n<p>Wszystkich uczniów, którzy zarejestrowali się w konkursie CyberSkiller, zapraszam do etapu szkolnego, który odbędzie się 14 grudnia 2021 r. w pracowni 38 w godzinach od 8:00 &#8211; 10:00. Osoby, które z przyczyn niezależnych nie mogą uczestniczyć w konkursie ze szkoły, proszę o kontakt mailowy lub przez e-dziennik.</p>\n",
            excerpt = "<p>Wszystkich uczniów, którzy zarejestrowali się w konkursie CyberSkiller, zapraszam do etapu szkolnego, który odbędzie się 14 grudnia 2021 r. w pracowni 38 w godzinach od 8:00 &#8211; 10:00. Osoby, które z przyczyn niezależnych nie mogą uczestniczyć w konkursie ze szkoły, proszę o kontakt mailowy lub przez e-dziennik.</p>\n",
            author = "Marcin Kowalski",
            thumbnail = "https://zsme.tarnow.pl/wp/wp-content/uploads/2021/06/cscs-300x300.jpg",
            fullImage = "https://zsme.tarnow.pl/wp/wp-content/uploads/2021/06/cscs.jpg",
            category = "Konkursy i turnieje"
        ),
        PostModel(
            id = 27493,
            date = dateFormat.parse("2021-12-12T10:18:04")!!,
            link = "https://zsme.tarnow.pl/wp/mosty-zamiast-murow/",
            title = "„Mosty zamiast murów”",
            content = "\n<p>„budujMY… jak budować mosty zamiast murów w relacjach rodzinnych, szkolnych i społecznych?&#8221; to tytuł konferencji zorganizowanej przez Fundację Alegoria &#8211; ośrodek dialogu, formacji, misji. Naszą szkołę reprezentowała pani Marta Piszczek oraz pani Renata Chlupka – Kosiba.</p>\n\n\n\n<p>Na spotkaniu poruszano następujące tematy:</p>\n\n\n\n<ul><li>jak traktować siebie samego z&nbsp;empatią, by móc potem w&nbsp;taki sposób traktować bliskie Ci osoby,</li><li>jak bez oceniania możesz przekazywać informacje o&nbsp;tym, jak widzisz i&nbsp;odbierasz rzeczywistość rodzinną, szkolną i&nbsp;społeczną,</li><li>trzy kroki do lepszego rozumienia tego, co dzieje się z&nbsp;nastolatkiem w&nbsp;domu i&nbsp;szkole,</li><li>system otwartych dialogów, który&nbsp; wspiera dzieci i&nbsp;młodzież w&nbsp;kryzysie psychicznym,</li><li>działania profilaktyczne, które pomogą chronić przed niepożądanymi zachowaniami przemocowymi. &nbsp;</li></ul>\n\n\n\n<p>Każdy z uczestników otrzymał książkę pt. &#8222;Mosty zamiast murów&#8221;. Voluminy będą dostępne w bibliotece szkolnej.</p>\n\n\n\n<p>Zachęcamy też do udziału w prawdziwej uczcie filmowo-dyskusyjnej, w XI Festiwalu Filmowym Vitae Valor.</p>\n\n\n\n<div class=\"wp-block-image\"><figure class=\"aligncenter size-large is-resized\"><img loading=\"lazy\" src=\"https://zsme.tarnow.pl/wp/wp-content/uploads/2021/12/img-762x1024.jpg\" alt=\"\" class=\"wp-image-27496\" width=\"591\" height=\"794\" srcset=\"https://zsme.tarnow.pl/wp/wp-content/uploads/2021/12/img-762x1024.jpg 762w, https://zsme.tarnow.pl/wp/wp-content/uploads/2021/12/img-223x300.jpg 223w, https://zsme.tarnow.pl/wp/wp-content/uploads/2021/12/img-74x100.jpg 74w, https://zsme.tarnow.pl/wp/wp-content/uploads/2021/12/img-768x1032.jpg 768w, https://zsme.tarnow.pl/wp/wp-content/uploads/2021/12/img.jpg 1072w\" sizes=\"(max-width: 591px) 100vw, 591px\" /></figure></div>\n",
            excerpt = "<p>„budujMY… jak budować mosty zamiast murów w relacjach rodzinnych, szkolnych i społecznych?&#8221; to tytuł konferencji zorganizowanej przez Fundację Alegoria &#8211; ośrodek dialogu, formacji, misji. Naszą szkołę reprezentowała pani Marta Piszczek oraz pani Renata Chlupka – Kosiba. Na spotkaniu poruszano następujące tematy: jak traktować siebie samego z&nbsp;empatią, by móc potem w&nbsp;taki sposób traktować bliskie Ci osoby, [&hellip;]</p>\n",
            author = "Agnieszka Gawron",
            thumbnail = "https://zsme.tarnow.pl/wp/wp-content/uploads/2021/12/la-300x290.jpg",
            fullImage = "https://zsme.tarnow.pl/wp/wp-content/uploads/2021/12/la.jpg",
            category = "Wydarzenia kulturalne"
        ),
        PostModel(
            id = 27490,
            date = dateFormat.parse("2021-12-06T16:13:55")!!,
            link = "https://zsme.tarnow.pl/wp/turniej-szachowy-2/",
            title = "Turniej Szachowy",
            content = "\n<p>W dniu 15.12.2021r. (środa) o godz. 10:40 odbędzie się Turniej Szachowy o puchar Dyrektora ZSME. Zapisywać można się u nauczycieli wychowania fizycznego do 13.12.2021 r. (poniedziałek). Szczegółowych informacji na temat zawodów udziela pan Marcin Rej. </p>\n\n\n\n<p>Zapraszamy. </p>\n",
            excerpt = "<p>W dniu 15.12.2021r. (środa) o godz. 10:40 odbędzie się Turniej Szachowy o puchar Dyrektora ZSME. Zapisywać można się u nauczycieli wychowania fizycznego do 13.12.2021 r. (poniedziałek). Szczegółowych informacji na temat zawodów udziela pan Marcin Rej. Zapraszamy. </p>\n",
            author = "Marcin Rej",
            thumbnail = "https://zsme.tarnow.pl/wp/wp-content/uploads/2019/05/thumb-300x300.jpg",
            fullImage = "https://zsme.tarnow.pl/wp/wp-content/uploads/2019/05/thumb.jpg",
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
            thumbnail = "https://zsme.tarnow.pl/wp/wp-content/uploads/2021/11/thumb-300x272.jpg",
            fullImage = "https://zsme.tarnow.pl/wp/wp-content/uploads/2021/11/thumb.jpg",
            category = "Wolontariat"
        )
    )
}
