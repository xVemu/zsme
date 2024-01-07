package pl.vemu.zsme.ui.timetable

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.SatelliteAlt
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SecondaryScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.firebase.Firebase
import com.google.firebase.remoteconfig.get
import com.google.firebase.remoteconfig.remoteConfig
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import kotlinx.coroutines.launch
import pl.vemu.zsme.R
import pl.vemu.zsme.Result
import pl.vemu.zsme.data.model.LessonModel
import pl.vemu.zsme.remembers.LinkProviderEffect
import pl.vemu.zsme.ui.components.CustomError
import pl.vemu.zsme.ui.components.SimpleMediumAppBar
import pl.vemu.zsme.ui.components.pagerTabIndicatorOffset
import java.time.LocalDate

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@TimetableNavGraph
@Destination("timetable/lesson")
@Composable
fun Lesson(
    name: String,
    url: String,
    navController: DestinationsNavigator,
    vm: LessonVM = hiltViewModel(),
) {
    /*TODO recreates when navigating*/
    LaunchedEffect(url) {
        vm.init(url)
    }

    LinkProviderEffect(Firebase.remoteConfig["scheduleUrl"].asString() + "/$url")

    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

    Scaffold(
        topBar = {
            SimpleMediumAppBar(
                title = name,
                navController = navController,
                scrollBehavior = scrollBehavior,
                colors = TopAppBarDefaults.mediumTopAppBarColors(scrolledContainerColor = MaterialTheme.colorScheme.surface),
            )
        },
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            val result by vm.list.collectAsStateWithLifecycle()
            val pagerState = rememberPagerState(initialPage = getWeekDay()) { 5 }

            LessonTabRow(pagerState)

            Crossfade(result, label = "Lessons") { lessonsResult ->
                when (lessonsResult) {
                    is Result.Loading -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    }

                    is Result.Failure -> {
                        CustomError { vm.downloadLessons() }
                    }

                    is Result.Success -> {
                        HorizontalPager(
                            state = pagerState, modifier = Modifier.fillMaxSize()
                        ) { page ->
                            val lessons = lessonsResult.value[page]

                            if (lessons.isEmpty()) return@HorizontalPager Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(horizontal = 16.dp),
                                verticalArrangement = Arrangement.Center,
                                horizontalAlignment = Alignment.CenterHorizontally,
                            ) {
                                Icon(
                                    Icons.Rounded.SatelliteAlt,
                                    contentDescription = stringResource(R.string.no_lessons),
                                    modifier = Modifier.size(128.dp)
                                )
                                Spacer(Modifier.height(12.dp))
                                Text(
                                    text = stringResource(R.string.no_lessons),
                                    style = MaterialTheme.typography.headlineSmall,
                                    textAlign = TextAlign.Center,
                                )
                            }

                            LazyColumn(
                                Modifier
                                    .fillMaxSize()
                                    .nestedScroll(scrollBehavior.nestedScrollConnection)
                            ) {
                                itemsIndexed(lessons) { index, item ->
                                    LessonItem(
                                        item = item,
                                        divider = index < lessons.lastIndex && lessons[index + 1].index != null
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

private fun getWeekDay(): Int =
    LocalDate.now().dayOfWeek.value.let { dayOfWeek ->
        if (dayOfWeek >= 6) 0 else dayOfWeek - 1
    }

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
private fun LessonTabRow(pagerState: PagerState) {
    val coroutineScope = rememberCoroutineScope()

    Column {
        SecondaryScrollableTabRow(
            selectedTabIndex = pagerState.currentPage,
            edgePadding = 0.dp,
            // Divider is not full width on landscape.
            divider = {},
            indicator = { tabPositions ->
                TabRowDefaults.SecondaryIndicator(
                    Modifier.pagerTabIndicatorOffset(
                        pagerState = pagerState,
                        tabPositions = tabPositions,
                        useFullWidth = true,
                    ),
                )
            }
        ) {
            stringArrayResource(R.array.weekdays).forEachIndexed { index, name ->
                Tab(
                    selected = pagerState.currentPage == index,
                    onClick = {
                        coroutineScope.launch { pagerState.animateScrollToPage(index) }
                    },
                    text = { Text(name) },
                    unselectedContentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
        HorizontalDivider()
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Preview
@Composable
private fun LessonTabRowPreview() {
    LessonTabRow(pagerState = rememberPagerState(pageCount = { 5 }))
}

@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF, group = "items")
@Composable
private fun LessonItem(
    @PreviewParameter(LessonModelPreviewParameterProvider::class) item: LessonModel,
    divider: Boolean = true,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp, 6.dp, 12.dp, 6.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = (item.index ?: "").toString(),
            style = MaterialTheme.typography.headlineLarge,
            textAlign = TextAlign.Center,
            modifier = Modifier.width(48.dp)
        )
        ProvideTextStyle(MaterialTheme.typography.bodyMedium) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(item.timeStart)
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = item.name,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        style = MaterialTheme.typography.bodyLarge,
                    )
                }
                Spacer(Modifier.height(4.dp))
                Row {
                    Text(item.timeFinish)
                    Spacer(Modifier.width(8.dp))
                    item.room?.let {
                        Text(it)
                        Spacer(Modifier.width(8.dp))
                    }
                    item.teacher?.let {
                        Text(it)
                    }
                }
            }
        }
    }
    if (divider) HorizontalDivider(modifier = Modifier.padding(horizontal = 8.dp))
}


class LessonModelPreviewParameterProvider : PreviewParameterProvider<LessonModel> {
    override val values = sequenceOf(
        LessonModel(
            name = "t_jęz an z-1/2",
            room = "36",
            teacher = "DR",
            timeStart = "11:11",
            timeFinish = "12:00",
            index = 5
        ), LessonModel(
            name = "fizyka-2/2",
            room = "22",
            teacher = null,
            timeStart = "11:11",
            timeFinish = "12:00",
            index = null,
        )
    )
}
