package pl.vemu.zsme.ui.timetable

import android.os.Build
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.ContentAlpha
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.PagerState
import com.google.accompanist.pager.rememberPagerState
import com.ramcosta.composedestinations.annotation.Destination
import pl.vemu.zsme.R
import pl.vemu.zsme.Result
import pl.vemu.zsme.data.model.LessonModel
import pl.vemu.zsme.paddingEnd
import pl.vemu.zsme.paddingStart
import pl.vemu.zsme.ui.components.*
import java.time.LocalDate
import java.util.*

@OptIn(ExperimentalPagerApi::class, ExperimentalMaterial3Api::class)
@TimetableNavGraph
@Destination(route = "timetable/lesson", style = ExpandTransition::class)
@Composable
fun Lesson(
    name: String,
    url: String,
    navController: NavController,
    vm: LessonVM = hiltViewModel(),
) {
    vm.init(url)
    Scaffold(
        topBar = simpleSmallAppBar(
            title = name,
            navController = navController
        ),
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            val lessonsResult by vm.list.collectAsState()
            val snackbarHostState = remember { SnackbarHostState() }
            when (lessonsResult) {
                is Result.Success -> {
                    Column(
                        Modifier
                            .fillMaxSize()
                    ) {
                        val lessonsList = (lessonsResult as Result.Success).value
                        val pagerState = rememberPagerState()
                        val names = listOf(
                            stringResource(R.string.monday),
                            stringResource(R.string.tuesday),
                            stringResource(R.string.wednesday),
                            stringResource(R.string.thursday),
                            stringResource(R.string.friday)
                        )
                        LessonTabRow(pagerState, names)
                        HorizontalPager(
                            count = names.size,
                            state = pagerState,
                        ) { page ->
                            LazyColumn(Modifier.fillMaxHeight()) {
                                if (lessonsList.isEmpty()) return@LazyColumn
                                val lessonsPage = lessonsList[page]
                                itemsIndexed(lessonsPage) { index, item ->
                                    LessonItem(
                                        item = item,
                                        divider = !(index >= lessonsPage.size - 1 || lessonsPage[index + 1].index == null)
                                    )
                                }
                            }
                        }
                        val day = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            val dayOfWeek = LocalDate.now().dayOfWeek.value
                            if (dayOfWeek >= 6) 0 else dayOfWeek - 1
                        } else {
                            val dayOfWeek = Calendar.getInstance()[Calendar.DAY_OF_WEEK]
                            if ((dayOfWeek == 1) or (dayOfWeek == 7)) 0 else dayOfWeek - 2
                        }
                        LaunchedEffect(day) { pagerState.scrollToPage(day) }
                    }
                }
                is Result.Failure -> {
                    snackbarHostState.ShowSnackBarWithError(
                        result = lessonsResult
                    ) {
                        vm.downloadLessons()
                    }
                }
            }
            SimpleSnackbar(snackbarHostState)
        }
    }
}

@OptIn(ExperimentalPagerApi::class)
@Composable
private fun LessonTabRow(
    pagerState: PagerState,
    names: List<String>
) {
    ScrollableTabRow(
        selectedTabIndex = pagerState.currentPage,
        edgePadding = 0.dp,
        /*indicator = { tabPositions -> TODO when they migrate to Material 3
            TabRowDefaults.Indicator(
                Modifier.pagerTabIndicatorOffset(pagerState, tabPositions)
            )
        }*/
    ) {
        Tabs(
            pagerState = pagerState,
            names = names
        )
    }
}

@OptIn(ExperimentalPagerApi::class)
@Preview
@Composable
private fun LessonTabRowPreview() {
    val names = listOf(
        stringResource(R.string.monday),
        stringResource(R.string.tuesday),
        stringResource(R.string.wednesday),
        stringResource(R.string.thursday),
        stringResource(R.string.friday)
    )
    LessonTabRow(pagerState = rememberPagerState(), names = names)
}

@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF, group = "items")
@Composable
private fun LessonItem(
    @PreviewParameter(LessonModelPreviewParameterProvider::class) item: LessonModel,
    divider: Boolean = true
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(52.dp)
            .padding(8.dp, 6.dp, 12.dp, 6.dp)
    ) {
        Text(
            text = (item.index ?: "").toString(),
            style = MaterialTheme.typography.headlineLarge,
            textAlign = TextAlign.Center,
            modifier = Modifier.size(40.dp)
        )
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .paddingStart(8.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            item.timeStart?.let {
                Text(
                    text = it,
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = ContentAlpha.medium)
                )
            }
            item.timeFinish?.let {
                Text(
                    text = it,
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = ContentAlpha.medium)
                )
            }
        }
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .paddingStart(10.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = item.name,
                fontSize = 15.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .paddingEnd(40.dp)
            )
            Row {
                item.room?.let {
                    Text(
                        text = it,
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = ContentAlpha.medium),
                        modifier = Modifier.paddingEnd(10.dp)
                    )
                }
                item.teacher?.let {
                    Text(
                        text = it,
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = ContentAlpha.medium)
                    )
                }
            }
        }
    }
    if (divider) Divider(modifier = Modifier.padding(horizontal = 8.dp))
}


class LessonModelPreviewParameterProvider : PreviewParameterProvider<LessonModel> {
    override val values = sequenceOf(
        LessonModel(
            name = "Lorem",
            room = null,
            teacher = "Agata Kowalska - Błaszczyk",
            timeStart = "11:11",
            timeFinish = "12:00",
            index = 5
        ),
        LessonModel(
            name = "Lorem",
            room = "22",
            teacher = null,
            timeStart = "11:11",
            timeFinish = "12:00",
        )
    )
}
