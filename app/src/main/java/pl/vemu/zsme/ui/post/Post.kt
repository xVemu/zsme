package pl.vemu.zsme.ui.post

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarData
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarVisuals
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.pulltorefresh.PullToRefreshContainer
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import coil.compose.AsyncImage
import coil.request.ImageRequest
import coil.transform.RoundedCornersTransformation
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.NavGraph
import com.ramcosta.composedestinations.annotation.RootNavGraph
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import pl.vemu.zsme.R
import pl.vemu.zsme.data.model.PostModel
import pl.vemu.zsme.modifiers.noRippleClickable
import pl.vemu.zsme.paddingBottom
import pl.vemu.zsme.paddingTop
import pl.vemu.zsme.plus
import pl.vemu.zsme.remembers.rememberFloatingTopBar
import pl.vemu.zsme.ui.components.CustomError
import pl.vemu.zsme.ui.components.Html
import pl.vemu.zsme.ui.destinations.DetailDestination
import pl.vemu.zsme.ui.theme.Elevation
import pl.vemu.zsme.util.Formatter
import java.text.SimpleDateFormat
import java.util.Locale

@RootNavGraph(start = true)
@NavGraph
annotation class PostNavGraph(
    val start: Boolean = false,
)

private val toolbarHeight = 68.dp

@OptIn(ExperimentalMaterial3Api::class)
@PostNavGraph(start = true)
@Destination("post/main")
@Composable
fun Post(
    navController: DestinationsNavigator,
    vm: PostVM = hiltViewModel(),
) {
    val pagingItems = vm.posts.collectAsLazyPagingItems()
    val focusManager = LocalFocusManager.current

    Scaffold(
        Modifier
            .noRippleClickable { focusManager.clearFocus() }
            .statusBarsPadding()) { padding ->
        val refreshing by remember {
            derivedStateOf {
                pagingItems.loadState.refresh is LoadState.Loading
            }
        }

        val initial by remember {
            derivedStateOf {
                pagingItems.itemCount <= 0
            }
        }

        val pullRefreshState =
            rememberPullToRefreshState(toolbarHeight + 80.dp) // toolbar height + default

        if (pullRefreshState.isRefreshing) LaunchedEffect(Unit) {
            pagingItems.refresh()
        }

        if (!refreshing) LaunchedEffect(Unit) {
            pullRefreshState.endRefresh()
        }

        Crossfade(initial, label = "Post") { initialState ->
            if (initialState) Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                if (refreshing) CircularProgressIndicator()
                else if (pagingItems.loadState.refresh is LoadState.Error) CustomError {
                    pagingItems.retry()
                }
            }
            else Box(Modifier.nestedScroll(pullRefreshState.nestedScrollConnection)) {

                val nestedScrolling = rememberFloatingTopBar(toolbarHeight)

                LazyColumn(
                    Modifier
                        .fillMaxSize()
                        .nestedScroll(nestedScrolling.nestedScrollConnection),
                    contentPadding = padding.plus(top = 72.dp)
                ) {
                    items(pagingItems.itemCount, key = pagingItems.itemKey { it.id }) { idx ->
                        pagingItems[idx]?.let { post ->
                            PostCard(post) {
                                navController.navigate(DetailDestination(post))
                            }
                        }
                    }
                    item {
                        Box(
                            Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            contentAlignment = Alignment.Center,
                        ) {
                            when (pagingItems.loadState.append) {
                                is LoadState.Loading -> CircularProgressIndicator()

                                is LoadState.Error -> Row(modifier = Modifier
                                    .clickable { pagingItems.retry() }
                                    .paddingBottom(8.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Center) {
                                    Icon(
                                        Icons.Rounded.Refresh,
                                        contentDescription = stringResource(R.string.refresh)
                                    )
                                    Spacer(Modifier.width(4.dp))
                                    Text(stringResource(R.string.error_retry))
                                }

                                else -> {}
                            }
                        }
                    }
                }

                if (pagingItems.loadState.refresh is LoadState.Error) Snackbar(
                    modifier = Modifier
                        .padding(padding)
                        .align(Alignment.BottomCenter),
                    snackbarData = object : SnackbarData {
                        override val visuals = object : SnackbarVisuals {
                            override val actionLabel = stringResource(R.string.retry)
                            override val duration = SnackbarDuration.Indefinite
                            override val message = stringResource(R.string.error)
                            override val withDismissAction = false
                        }

                        override fun dismiss() = Unit

                        override fun performAction() {
                            pagingItems.retry()
                        }
                    },
                )


                PullToRefreshContainer(
                    state = pullRefreshState,
                    modifier = Modifier
                        .padding(padding)
                        .align(Alignment.TopCenter),
                )

                val query by vm.query.collectAsStateWithLifecycle()
                FloatingSearchBar(
                    offset = nestedScrolling.offset,
                    query = query ?: "",
                    onQueryChange = vm::setQuery,
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FloatingSearchBar(
    offset: Density.() -> IntOffset,
    query: String,
    onQueryChange: (query: String?) -> Unit,
) {
    val shadowPx = with(LocalDensity.current) { Elevation.Level3.roundToPx().toFloat() }
    val toolbarHeight = with(LocalDensity.current) { toolbarHeight.roundToPx().toFloat() }
    val shape = SearchBarDefaults.dockedShape

    Box(
        Modifier
            .padding(8.dp)
            .offset(offset)
            .graphicsLayer {
                this.shape = shape
                shadowElevation = if (offset().y <= -toolbarHeight) 0F else shadowPx
            }
            .background(MaterialTheme.colorScheme.surfaceColorAtElevation(Elevation.Level3), shape)
    ) {
        val interactionSource = remember { MutableInteractionSource() }
        val focusRequester = remember { FocusRequester() }

        BasicTextField(
            value = query,
            onValueChange = onQueryChange,
            modifier = Modifier
                .height(SearchBarDefaults.InputFieldHeight)
                .fillMaxWidth()
                .focusRequester(focusRequester)
                .clickable { focusRequester.requestFocus() },
            singleLine = true,
            textStyle = LocalTextStyle.current,
            cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
            interactionSource = interactionSource,
            decorationBox = @Composable { innerTextField ->
                TextFieldDefaults.DecorationBox(
                    value = query,
                    innerTextField = innerTextField,
                    singleLine = true,
                    visualTransformation = VisualTransformation.None,
                    leadingIcon = {
                        Icon(
                            Icons.Rounded.Search,
                            modifier = Modifier.offset(4.dp),
                            contentDescription = stringResource(R.string.search_post)
                        )
                    },
                    placeholder = { Text(stringResource(R.string.search_post)) },
                    shape = SearchBarDefaults.inputFieldShape,
                    colors = SearchBarDefaults.inputFieldColors(),
                    contentPadding = TextFieldDefaults.contentPaddingWithoutLabel(),
                    enabled = true,
                    interactionSource = interactionSource,
                    container = {},
                )
            }
        )
    }
}

@Composable
private fun PostCard(
    postModel: PostModel,
    onClick: () -> Unit,
) {
    Card(
        modifier = Modifier
            .heightIn(150.dp)
            .padding(8.dp),
        onClick = onClick,
    ) {
        Row {
            Column(Modifier.padding(8.dp)) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(postModel.thumbnail ?: R.drawable.zsme).crossfade(true)
                        .diskCacheKey(postModel.id.toString())
                        .transformations(RoundedCornersTransformation(12f)).build(),
                    placeholder = painterResource(R.drawable.zsme),
                    contentDescription = stringResource(R.string.thumbnail),
                    modifier = Modifier.size(108.dp),
                    contentScale = ContentScale.Crop,
                )
                Column(
                    Modifier
                        .width(108.dp)
                        .paddingTop(8.dp)
                ) {
                    ProvideTextStyle(MaterialTheme.typography.bodyMedium) {
                        Text(Formatter.relativeDate(postModel.date))
                        Text(postModel.author)
                        Text(postModel.category)
                    }
                }
            }
            Column(Modifier.padding(8.dp)) {
                Html(
                    html = postModel.title,
                    style = MaterialTheme.typography.labelLarge.copy(
                        color = MaterialTheme.colorScheme.primary, textAlign = TextAlign.Center
                    ),
                    modifier = Modifier.fillMaxWidth(),
                )
                Html(
                    html = postModel.excerpt,
                    modifier = Modifier.paddingBottom(8.dp),
                    style = MaterialTheme.typography.bodyMedium
                )
            }

        }
    }
}

@Preview
@Composable
private fun PostCardPreview(@PreviewParameter(PostPreviewProvider::class) postModel: PostModel) {
    PostCard(postModel = postModel) {}
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
        ), PostModel(
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
        ), PostModel(
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
        ), PostModel(
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
