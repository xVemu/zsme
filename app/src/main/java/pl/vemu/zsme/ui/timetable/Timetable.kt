package pl.vemu.zsme.ui.timetable

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.MeetingRoom
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.School
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ramcosta.composedestinations.annotation.DeepLink
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.NavGraph
import com.ramcosta.composedestinations.annotation.RootNavGraph
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.navigation.EmptyDestinationsNavigator
import kotlinx.coroutines.launch
import pl.vemu.zsme.R
import pl.vemu.zsme.Result
import pl.vemu.zsme.data.model.TimetableModel
import pl.vemu.zsme.ui.components.Avatar
import pl.vemu.zsme.ui.components.CustomError
import pl.vemu.zsme.ui.components.PrimaryPagerTabIndicator
import pl.vemu.zsme.ui.components.SimpleLargeAppBar
import pl.vemu.zsme.ui.destinations.LessonDestination
import java.util.Locale

@RootNavGraph
@NavGraph
annotation class TimetableNavGraph(
    val start: Boolean = false,
)

@TimetableNavGraph(start = true)
@Destination(
    route = "timetable/main",
    deepLinks = [DeepLink("pl.vemu.zsme.shortcut.TIMETABLE")],
)
@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun Timetable(
    navController: DestinationsNavigator,
    vm: TimetableVM = hiltViewModel(),
) {
    Scaffold(topBar = {
        SimpleLargeAppBar(R.string.timetable)
    }) { padding ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(padding),
        ) {
            val pagerState = rememberPagerState { 3 }
            val result by vm.list.collectAsState()

            TimetableTabRow(pagerState)
            Crossfade(result, label = "Timetable") { data ->
                when (data) {
                    is Result.Loading -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    }

                    is Result.Failure -> {
                        CustomError { vm.downloadTimetable() }
                    }

                    is Result.Success -> {
                        HorizontalPager(
                            state = pagerState,
                            modifier = Modifier.fillMaxSize()
                        ) { page ->
                            val value = data.value[page]
                            LazyColumn {
                                when (page) {
                                    0 -> classes(value, navController)
                                    1 -> teachers(value, navController)
                                    2 -> rooms(value, navController)
                                }
                            }
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
        items(list) {
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
private fun LazyListScope.classes(rooms: List<TimetableModel>, navigator: DestinationsNavigator) {
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


@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun TimetableTabRow(
    pagerState: PagerState,
) {
    val coroutineScope = rememberCoroutineScope()

    val icons = listOf(Icons.Rounded.School, Icons.Rounded.Person, Icons.Rounded.MeetingRoom)

    PrimaryTabRow(
        selectedTabIndex = pagerState.currentPage,
        indicator = { tabPositions ->
            PrimaryPagerTabIndicator(
                pagerState = pagerState,
                tabPositions = tabPositions,
            )
        },
    ) {
        stringArrayResource(R.array.timetable_tabs).zip(icons)
            .forEachIndexed { index, (name, icon) ->
                Tab(
                    selected = pagerState.currentPage == index,
                    onClick = {
                        coroutineScope.launch { pagerState.animateScrollToPage(index) }
                    },
                    icon = { Icon(icon, name) },
                    text = { Text(name) },
                    unselectedContentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
    }
}

@Preview(group = "items")
@Composable
private fun TeacherPagePreview() {
    val item = TimetableModel("K.Kras(KK)", "")
    LazyColumn {
        teachers(listOf(item), EmptyDestinationsNavigator)
    }
}

@Preview(group = "items")
@Composable
private fun ClassPagePreview() {
    val item = TimetableModel("3TIG", "")
    LazyColumn {
        classes(listOf(item, item.copy(name = "1TA")), EmptyDestinationsNavigator)
    }
}

@Preview(group = "items")
@Composable
private fun RoomPagePreview() {
    val item = TimetableModel("P1 pracownia komputerowa", "")
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
