package pl.vemu.zsme.ui.post

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Clear
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.SearchOff
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults.Indicator
import androidx.compose.material3.pulltorefresh.PullToRefreshState
import androidx.compose.material3.pulltorefresh.pullToRefresh
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import coil.compose.AsyncImage
import coil.request.ImageRequest
import coil.transform.RoundedCornersTransformation
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.NavGraph
import com.ramcosta.composedestinations.annotation.RootGraph
import com.ramcosta.composedestinations.generated.destinations.DetailDestination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import kotlinx.datetime.LocalDateTime
import org.koin.androidx.compose.koinViewModel
import pl.vemu.zsme.R
import pl.vemu.zsme.Result
import pl.vemu.zsme.data.model.PostModel
import pl.vemu.zsme.paddingBottom
import pl.vemu.zsme.remembers.LinkProviderEffect
import pl.vemu.zsme.ui.components.CustomError
import pl.vemu.zsme.ui.components.Html
import pl.vemu.zsme.ui.components.RetrySnackbar
import pl.vemu.zsme.util.Formatter
import pl.vemu.zsme.util.baseUrl

@NavGraph<RootGraph>(start = true)
annotation class PostNavGraph

@OptIn(ExperimentalMaterial3Api::class)
@Destination<PostNavGraph>("post/main", start = true)
@Composable
fun Post(
    navController: DestinationsNavigator,
    vm: PostVM = koinViewModel(),
) {
    LinkProviderEffect(Firebase.remoteConfig.baseUrl)

    val keyboardController = LocalSoftwareKeyboardController.current
    val topAppBarScrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()

    Scaffold(
        Modifier
            .clickable(interactionSource = null, indication = null) { keyboardController?.hide() }
            .statusBarsPadding(),
        topBar = {
            CenterAlignedTopAppBar(
                scrollBehavior = topAppBarScrollBehavior,
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    scrolledContainerColor = Color.Transparent,
                    containerColor = Color.Transparent
                ),
                title = {
                    val query by vm.query.collectAsStateWithLifecycle()

                    SearchBarDefaults.InputField(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                MaterialTheme.colorScheme.surfaceContainerHigh,
                                SearchBarDefaults.dockedShape
                            ),
                        query = query,
                        onQueryChange = vm::setQuery,
                        onSearch = { keyboardController?.hide() },
                        expanded = false,
                        placeholder = { Text(stringResource(R.string.search_post)) },
                        onExpandedChange = {},
                        leadingIcon = {
                            Icon(
                                Icons.Rounded.Search,
                                contentDescription = stringResource(R.string.search_post)
                            )
                        }
                    )
                },
            )
        },
    ) { padding ->
        /**
         * Possible states:
         * append (db or remote):
         *   - loading => loading circle at bottom
         *   - error => error at bottom
         *   - notLoading => nothing
         *
         * empty (db or remote):
         *   - loading => loading circle fullscreen
         *   - error => error fullscreen
         *   - notLoading (db and remote) => no results full screen
         *
         * remote error & not empty => error snackbar
         *
         * remote loading & db notLoading & not empty => refresh indicator at top
         * */
        val pagingItems = vm.posts.collectAsLazyPagingItems()
        val sourceRefresh = pagingItems.loadState.source.refresh
        val mediatorRefresh = pagingItems.loadState.mediator?.refresh
        val empty = pagingItems.itemCount <= 0

        PullToRefresh(
            isRefreshing = mediatorRefresh is LoadState.Loading && sourceRefresh !is LoadState.Loading && !empty,
            onRefresh = pagingItems::refresh,
            modifier = Modifier.padding(padding)
        ) {
            val closeKeyboardOnScroll = remember {
                object : NestedScrollConnection {
                    override fun onPreScroll(available: Offset, source: NestedScrollSource) =
                        Offset.Zero.also { keyboardController?.hide() }
                }
            }

            LazyColumn(
                Modifier
                    .fillMaxSize()
                    .nestedScroll(closeKeyboardOnScroll)
                    .nestedScroll(topAppBarScrollBehavior.nestedScrollConnection),
                contentPadding = padding,
            ) {
                item {
                    FilterBar(vm)
                }

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

                            is LoadState.Error -> Row(
                                modifier = Modifier
                                    .clickable { pagingItems.retry() }
                                    .paddingBottom(8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center,
                            ) {
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

                if (empty) {
                    item {
                        Box(
                            Modifier
                                .fillParentMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            when (pagingItems.loadState.refresh) {
                                is LoadState.Loading -> CircularProgressIndicator()
                                is LoadState.Error -> CustomError { pagingItems.retry() }

                                is LoadState.NotLoading -> {
                                    if (mediatorRefresh is LoadState.NotLoading && sourceRefresh is LoadState.NotLoading)
                                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                            Icon(
                                                Icons.Rounded.SearchOff,
                                                contentDescription = stringResource(R.string.no_results),
                                                modifier = Modifier.size(108.dp),
                                            )
                                            Text(
                                                stringResource(R.string.no_results),
                                                style = MaterialTheme.typography.displaySmall
                                            )
                                        }
                                }
                            }
                        }
                    }
                }
            }

            if (mediatorRefresh is LoadState.Error && !empty) RetrySnackbar(retry = pagingItems::retry)
        }
    }
}

/** Can't use [PullToRefreshBox] directly, because it passes modifier to all children instead of only indicator.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PullToRefresh(
    isRefreshing: Boolean,
    onRefresh: () -> Unit,
    modifier: Modifier = Modifier,
    state: PullToRefreshState = rememberPullToRefreshState(),
    content: @Composable BoxScope.() -> Unit
) {
    Box(
        Modifier.pullToRefresh(state = state, isRefreshing = isRefreshing, onRefresh = onRefresh),
        contentAlignment = Alignment.TopStart
    ) {
        content()
        Indicator(
            modifier = modifier.align(Alignment.TopCenter),
            isRefreshing = isRefreshing,
            state = state
        )
    }
}

@Composable
private fun FilterBar(vm: PostVM, modifier: Modifier = Modifier) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = modifier.horizontalScroll(rememberScrollState()),
    ) {
        Box {} // Padding 8dp
        val authors = vm.authors.collectAsStateWithLifecycle()

        when (val list = authors.value) {
            is Result.Success -> {
                var isOpened by remember { mutableStateOf(false) }
                val actives by vm.activeAuthors.collectAsStateWithLifecycle()
                val active = actives.isNotEmpty()

                FilterChip(
                    onClick = { isOpened = true },
                    selected = active,
                    label = {
                        if (active) Text(actives.distinctBy { it.name }
                            .joinToString(", ") { it.name })
                        else Text(stringResource(R.string.authors))
                    },
                    trailingIcon = {
                        if (active) Icon(
                            Icons.Rounded.Clear,
                            contentDescription = stringResource(R.string.clear),
                            modifier = Modifier
                                .clickable { vm.setAuthors(emptyList()) }
                                .size(18.dp),
                        )
                    }
                )

                if (isOpened)
                    ChoiceDialog(
                        stringResource(R.string.choose_authors),
                        list.value.map { it.name },
                        actives.map { it.name },
                    ) { items ->
                        isOpened = false
                        if (items != null) vm.setAuthors(list.value.filter {
                            items.contains(
                                it.name
                            )
                        })
                    }
            }

            else -> {}
        }

        val categories by vm.categories.collectAsStateWithLifecycle()
        when (val list = categories) {
            is Result.Success -> {
                var isOpened by remember { mutableStateOf(false) }
                val actives by vm.activeCategories.collectAsStateWithLifecycle()
                val active = actives.isNotEmpty()

                FilterChip(
                    onClick = { isOpened = true },
                    selected = active,
                    label = {
                        if (active) Text(actives.distinctBy { it.name }
                            .joinToString(", ") { it.name })
                        else Text(stringResource(R.string.categories))
                    },
                    trailingIcon = {
                        if (active) Icon(
                            Icons.Rounded.Clear,
                            contentDescription = stringResource(R.string.clear),
                            modifier = Modifier
                                .clickable { vm.setCategories(emptyList()) }
                                .size(18.dp),
                        )
                    }
                )
                if (isOpened)
                    ChoiceDialog(
                        stringResource(R.string.choose_categories),
                        list.value.map { it.name },
                        actives.map { it.name },
                    ) { items ->
                        isOpened = false
                        if (items != null) vm.setCategories(list.value.filter {
                            items.contains(
                                it.name
                            )
                        })
                    }
            }

            else -> {}
        }
        Box {} // Padding 8dp
    }
}

@Composable
private fun PostCard(
    postModel: PostModel,
    onClick: () -> Unit,
) {
    Card(
        modifier = Modifier.padding(8.dp),
        onClick = onClick,
    ) {
        Column {
            ListItem(
                leadingContent = {
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(postModel.thumbnail ?: postModel.fullImage ?: R.drawable.zsme)
                            .crossfade(true)
                            .diskCacheKey(postModel.id.toString())
                            .transformations(RoundedCornersTransformation(12f)).build(),
                        placeholder = painterResource(R.drawable.zsme),
                        contentDescription = stringResource(R.string.thumbnail),
                        modifier = Modifier.size(64.dp),
                        contentScale = ContentScale.Crop,
                    )
                },
                overlineContent = { Text(postModel.category) },
                headlineContent = { Text(postModel.author) },
                supportingContent = { Text(Formatter.relativeDate(postModel.date)) },
                colors = ListItemDefaults.colors(Color.Transparent),
            )
            Html(
                html = postModel.title,
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.padding(horizontal = 16.dp),
            )
            Html(
                html = postModel.excerpt,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
            )
        }
    }
}

// TODO predictive back https://issuetracker.google.com/issues/318537200
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ChoiceDialog(
    title: String,
    items: List<String>,
    initial: List<String> = emptyList(),
    onExit: (items: List<String>?) -> Unit,
) {
    BasicAlertDialog(
        onDismissRequest = { onExit(null) }
    ) {
        val checkedList = remember { initial.toMutableStateList() }
        Surface(shape = MaterialTheme.shapes.extraLarge) {
            Column(Modifier.padding(vertical = 24.dp)) {
                Text(
                    title,
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.padding(horizontal = 24.dp),
                )
                Spacer(Modifier.height(16.dp))
                HorizontalDivider()
                LazyColumn(Modifier.weight(1f, false)) {
                    items(items.distinct()) { item ->
                        ListItem(
                            modifier = Modifier.clickable {
                                if (checkedList.contains(item)) checkedList -= item
                                else checkedList += item
                            },
                            headlineContent = { Text(item) },
                            leadingContent = {
                                Checkbox(
                                    checked = checkedList.contains(item),
                                    onCheckedChange = { checked ->
                                        if (checked) checkedList += item
                                        else checkedList -= item
                                    },
                                )
                            },
                        )
                    }
                }
                HorizontalDivider()
                Spacer(Modifier.height(24.dp))
                CompositionLocalProvider(LocalTextStyle provides MaterialTheme.typography.labelLarge) {
                    Row(
                        Modifier
                            .align(Alignment.End)
                            .padding(horizontal = 24.dp)
                    ) {
                        TextButton(onClick = { onExit(emptyList()) }) {
                            Text(stringResource(R.string.cancel))
                        }
                        TextButton(onClick = { onExit(checkedList) }) {
                            Text(stringResource(R.string.confirm))
                        }
                    }
                }
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

    override val values = sequenceOf(
        PostModel(
            id = 27524,
            date = LocalDateTime.parse("2021-12-13T12:50:41"),
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
            date = LocalDateTime.parse("2021-12-12T10:18:04"),
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
            date = LocalDateTime.parse("2021-12-06T16:13:55"),
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
            date = LocalDateTime.parse("2021-11-07T12:49:33"),
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
