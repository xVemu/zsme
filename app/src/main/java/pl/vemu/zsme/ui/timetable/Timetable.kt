package pl.vemu.zsme.ui.timetable

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.pulltorefresh.PullToRefreshContainer
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.firebase.Firebase
import com.google.firebase.remoteconfig.remoteConfig
import com.ramcosta.composedestinations.annotation.*
import com.ramcosta.composedestinations.annotation.parameters.DeepLink
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.navigation.EmptyDestinationsNavigator
import kotlinx.coroutines.launch
import pl.vemu.zsme.R
import pl.vemu.zsme.Result
import pl.vemu.zsme.data.model.TimetableModel
import pl.vemu.zsme.data.model.TimetableType
import pl.vemu.zsme.remembers.LinkProviderEffect
import pl.vemu.zsme.remembers.isLandscape
import pl.vemu.zsme.remembers.rememberDeclarativeRefresh
import pl.vemu.zsme.ui.components.Avatar
import pl.vemu.zsme.ui.components.CustomError
import pl.vemu.zsme.ui.components.RetrySnackbar
import pl.vemu.zsme.ui.components.SimpleLargeAppBar
import pl.vemu.zsme.ui.components.measureTabIndicatorOffset
import com.ramcosta.composedestinations.generated.destinations.LessonDestination
import pl.vemu.zsme.util.scheduleUrl
import java.util.Locale

@NavGraph<RootGraph>
annotation class TimetableNavGraph

@Destination<TimetableNavGraph>(
    route = "timetable/main",
    start = true,
    deepLinks = [DeepLink("pl.vemu.zsme.shortcut.TIMETABLE")],
)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Timetable(
    navController: DestinationsNavigator,
    vm: TimetableVM = hiltViewModel(),
) {
    LinkProviderEffect(Firebase.remoteConfig.scheduleUrl)

    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

    Scaffold(topBar = {
        SimpleLargeAppBar(
            R.string.timetable,
            scrollBehavior = scrollBehavior,
            colors = TopAppBarDefaults.largeTopAppBarColors(scrolledContainerColor = MaterialTheme.colorScheme.surface)
        )
    }) { padding ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(padding),
        ) {
            val pagerState = rememberPagerState { TimetableType.entries.size }
            val result by vm.list.collectAsStateWithLifecycle()

            TimetableTabRow(pagerState)
            Crossfade(result, label = "Timetable") { data ->
                when (data) {
                    is Result.Loading -> {
                        Box(
                            modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center,
                        ) {
                            CircularProgressIndicator()
                        }
                    }

                    is Result.Failure -> {
                        CustomError { vm.downloadTimetable() }
                    }

                    is Result.Success -> {
                        Box {
                            val refreshState =
                                rememberDeclarativeRefresh(data.refreshing) { vm.downloadTimetable() }

                            HorizontalPager(
                                state = pagerState,
                                modifier = Modifier
                                    .fillMaxSize()
                                    .nestedScroll(refreshState.nestedScrollConnection),
                            ) { page ->
                                val type = TimetableType.entries[page]
                                val items =
                                    remember {
                                        data.value.filter { it.type == type }
                                            .sortedBy { it.name }
                                    }
                                LazyColumn(Modifier.nestedScroll(scrollBehavior.nestedScrollConnection)) {
                                    when (type) {
                                        TimetableType.GROUP -> groups(items, navController)
                                        TimetableType.TEACHER -> teachers(items, navController)
                                        TimetableType.ROOM -> rooms(items, navController)
                                    }
                                }
                            }

                            if (data.error != null)
                                RetrySnackbar { vm.downloadTimetable() }

                            PullToRefreshContainer(
                                refreshState,
                                Modifier.align(Alignment.TopCenter)
                            )
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
private fun LazyListScope.teachers(
    teachers: List<TimetableModel>,
    navigator: DestinationsNavigator,
) {
    val collactor = java.text.Collator.getInstance(Locale("pl", "PL"))
    val grouped = teachers.groupBy { it.name.take(1) }.toSortedMap(collactor)

    grouped.forEach { (group, list) ->
        stickyHeader {
            Header("$group.")
        }
        items(list.sortedWith(compareBy(collactor) { it.name })) {
            val label = it.name.substring(it.name.length - 3, it.name.length - 1)
            val name = it.name.substring(2, it.name.length - 5)
            ListItem(modifier = Modifier.clickable {
                navigator.navigate(LessonDestination(it.name, it.url))
            }, headlineContent = { Text(name) }, leadingContent = { Avatar(label) })
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
private fun LazyListScope.rooms(rooms: List<TimetableModel>, navigator: DestinationsNavigator) {
    val grouped = rooms.groupBy { it.name.first() }

    grouped.forEach { (group, list) ->
        stickyHeader {
            Header(group.toString())
        }
        items(list) {
            val (label, name) = it.name.split(" ", limit = 2)
            ListItem(modifier = Modifier.clickable {
                navigator.navigate(LessonDestination(it.name, it.url))
            }, headlineContent = { Text(name) }, leadingContent = { Avatar(label) })
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
private fun LazyListScope.groups(rooms: List<TimetableModel>, navigator: DestinationsNavigator) {
    val grouped = rooms.groupBy { it.name.first() }

    grouped.forEach { (group, list) ->
        item {
            Header(group.toString())
        }
        item {
            FlowRow(
                Modifier.padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                list.forEach {
                    OutlinedButton(onClick = {
                        navigator.navigate(LessonDestination(it.name, it.url))
                    }) {
                        Text(it.name.drop(1))
                    }
                }
            }
        }
    }
    item {
        Spacer(Modifier.height(16.dp))
    }
}

@Composable
private fun Header(title: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .background(MaterialTheme.colorScheme.surface)
            .padding(start = 16.dp, top = 16.dp, end = 24.dp, bottom = 8.dp)
    ) {
        Text(title, style = MaterialTheme.typography.titleLarge)
        Spacer(Modifier.width(16.dp))
        HorizontalDivider()
    }
}

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
private fun TimetableTabRow(
    pagerState: PagerState,
) {
    val coroutineScope = rememberCoroutineScope()

    PrimaryTabRow(
        modifier = Modifier.zIndex(1f), // TODO https://issuetracker.google.com/issues/314496282
        selectedTabIndex = pagerState.currentPage,
        indicator = {
            TabRowDefaults.PrimaryIndicator(
                Modifier.tabIndicatorLayout { measurable, constraints, tabPositions ->
                    measureTabIndicatorOffset(
                        tabPositions,
                        pagerState.currentPage,
                        pagerState.currentPageOffsetFraction,
                        measurable,
                        constraints,
                    )
                },
                width = Dp.Unspecified,
            )
        },
    ) {
        TimetableType.entries.forEachIndexed { index, type ->
            Tab(
                selected = pagerState.currentPage == index,
                onClick = {
                    coroutineScope.launch { pagerState.animateScrollToPage(index) }
                },
                icon = if (!isLandscape) ({
                    Icon(
                        type.icon,
                        stringResource(type.title)
                    )
                }) else null,
                text = { Text(stringResource(type.title)) },
                unselectedContentColor = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Preview(group = "items")
@Composable
private fun TeacherPagePreview() {
    val item = TimetableModel(name = "K.Kras(KK)", url = "", type = TimetableType.TEACHER)
    LazyColumn {
        teachers(listOf(item), EmptyDestinationsNavigator)
    }
}

@Preview(group = "items")
@Composable
private fun ClassPagePreview() {
    val item = TimetableModel(name = "3TIG", url = "", type = TimetableType.GROUP)
    LazyColumn {
        groups(listOf(item, item.copy(name = "1TA")), EmptyDestinationsNavigator)
    }
}

@Preview(group = "items")
@Composable
private fun RoomPagePreview() {
    val item =
        TimetableModel(name = "P1 pracownia komputerowa", url = "", type = TimetableType.ROOM)
    LazyColumn {
        rooms(listOf(item), EmptyDestinationsNavigator)
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Preview
@Composable
private fun TimetableTabRowPreview() {
    TimetableTabRow(rememberPagerState(pageCount = { 3 }))
}
