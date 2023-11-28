package pl.vemu.zsme.ui.timetable

import android.os.Build
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.SatelliteAlt
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import kotlinx.coroutines.launch
import pl.vemu.zsme.R
import pl.vemu.zsme.Result
import pl.vemu.zsme.data.model.LessonModel
import pl.vemu.zsme.ui.components.*
import java.time.LocalDate
import java.util.*

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

    Scaffold(
        topBar = {
            SimpleMediumAppBar(
                title = name, navController = navController
            )
        },
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            val result by vm.list.collectAsState()
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

                            LazyColumn(Modifier.fillMaxSize()) {
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

private fun getWeekDay() =
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val dayOfWeek = LocalDate.now().dayOfWeek.value
        if (dayOfWeek >= 6) 0 else dayOfWeek - 1
    } else {
        val dayOfWeek = Calendar.getInstance()[Calendar.DAY_OF_WEEK]
        if ((dayOfWeek == 1) or (dayOfWeek == 7)) 0 else dayOfWeek - 2
    }

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun LessonTabRow(pagerState: PagerState) {
    val coroutineScope = rememberCoroutineScope()

    SecondaryScrollableTabRow(
        selectedTabIndex = pagerState.currentPage,
        edgePadding = 0.dp,
        indicator = { tabPositions ->
            TabRowDefaults.SecondaryIndicator(
                Modifier.pagerTabIndicatorOffset(
                    pagerState = pagerState,
                    tabPositions = tabPositions,
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
                selectedContentColor = MaterialTheme.colorScheme.onSurface,
                unselectedContentColor = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
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
