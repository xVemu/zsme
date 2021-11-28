package pl.vemu.zsme.ui.timetable

import android.os.Build
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.ContentAlpha
import androidx.compose.material.Divider
import androidx.compose.material.ScrollableTabRow
import androidx.compose.material.Tab
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
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
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import kotlinx.coroutines.launch
import pl.vemu.zsme.R
import pl.vemu.zsme.data.model.LessonModel
import pl.vemu.zsme.paddingEnd
import pl.vemu.zsme.paddingStart
import java.time.LocalDate
import java.util.*

@OptIn(ExperimentalPagerApi::class)
@Composable
fun Lesson(
    url: String,
    vm: LessonVM = hiltViewModel(),
) {
    vm.init(url)
    val day = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val dayOfWeek = LocalDate.now().dayOfWeek.value
        if (dayOfWeek >= 6) 0 else dayOfWeek - 1
    } else {
        val dayOfWeek = Calendar.getInstance()[Calendar.DAY_OF_WEEK]
        if ((dayOfWeek == 1) or (dayOfWeek == 7)) 0 else dayOfWeek - 2
    }
    val names = arrayOf(
        stringResource(R.string.monday),
        stringResource(R.string.tuesday),
        stringResource(R.string.wednesday),
        stringResource(R.string.thursday),
        stringResource(R.string.friday)
    )
    val coroutines = rememberCoroutineScope()
    val pagerState = rememberPagerState()
    val lessonsList by vm.list.collectAsState()
    LaunchedEffect("scrollToDay") { pagerState.scrollToPage(day) }
    Column(Modifier.fillMaxSize()) {
        ScrollableTabRow( //TODO change to material3
            selectedTabIndex = pagerState.currentPage,
            backgroundColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.primary,
            edgePadding = 0.dp
        ) {
            names.forEachIndexed { index, name ->
                Tab(
                    text = { androidx.compose.material.Text(text = name) },
                    selected = pagerState.currentPage == index,
                    unselectedContentColor = MaterialTheme.colorScheme.onSurface.copy(alpha = ContentAlpha.medium),
                    onClick = {
                        coroutines.launch {
                            pagerState.animateScrollToPage(index)
                        }
                    }
                )
            }
        }
        HorizontalPager(
            count = names.size,
            state = pagerState,
        ) { page ->
            LazyColumn(modifier = Modifier.fillMaxHeight()) {
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
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable
private fun LessonItem(
    @PreviewParameter(LessonModelPreviewParameterProvider::class) item: LessonModel,
    divider: Boolean = true//TODO
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
