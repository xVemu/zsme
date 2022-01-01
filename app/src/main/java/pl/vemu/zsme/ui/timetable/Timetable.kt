package pl.vemu.zsme.ui.timetable

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.GridCells
import androidx.compose.foundation.lazy.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.pager.*
import kotlinx.coroutines.launch
import pl.vemu.zsme.R
import pl.vemu.zsme.Result
import pl.vemu.zsme.SimpleSnackbar
import pl.vemu.zsme.data.model.TimetableModel
import java.net.UnknownHostException

@OptIn(
    ExperimentalPagerApi::class,
    ExperimentalFoundationApi::class
)
@Composable
fun Timetable(
    navController: NavController,
    vm: TimetableVM = hiltViewModel(),
) {
    val names = listOf(
        stringResource(R.string.classes),
        stringResource(R.string.teachers),
        stringResource(R.string.classrooms)
    )
    val pagerState = rememberPagerState()
    val timetableResult by vm.list.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    Box(Modifier.fillMaxSize()) {
        Column(Modifier.fillMaxSize()) {
            TimetableTabRow(pagerState, names)
            when (timetableResult) {
                is Result.Success -> {
                    val timetableList =
                        (timetableResult as Result.Success).value
                    HorizontalPager(
                        count = names.size,
                        state = pagerState,
                    ) { page ->
                        LazyVerticalGrid(
                            cells = GridCells.Fixed(count = 3)
                        ) {
                            if (timetableList.isEmpty()) return@LazyVerticalGrid
                            items(timetableList[page]) { item ->
                                TimetablePageItem(navController, item)
                            }
                        }
                    }
                }
                is Result.Failure -> {
                    ShowSnackBarWithError(
                        result = timetableResult,
                        snackbarHostState = snackbarHostState
                    ) {
                        vm.downloadTimetable()
                    }
                }
            }
        }
        SimpleSnackbar(snackbarHostState)
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun TimetablePageItem(
    navController: NavController,
    item: TimetableModel
) {
    Card( // TODO change to material 3 card
        onClick = { navController.navigate("lesson/${item.url}") },
        elevation = 2.dp,
        backgroundColor = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier
            .height((LocalConfiguration.current.screenWidthDp / 3).dp)
            .padding(8.dp)
    ) {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = item.name,
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(8.dp)
            )
        }
    }
}

@OptIn(ExperimentalPagerApi::class)
@Composable
private fun TimetableTabRow(
    pagerState: PagerState,
    names: List<String>,
) {
    TabRow(
        selectedTabIndex = pagerState.currentPage,
        backgroundColor = MaterialTheme.colorScheme.surface,
        contentColor = MaterialTheme.colorScheme.primary,
        indicator = { tabPositions ->
            TabRowDefaults.Indicator(
                Modifier.pagerTabIndicatorOffset(pagerState, tabPositions)
            )
        }
    ) {
        Tabs(
            pagerState = pagerState,
            names = names
        )
    }
}

@OptIn(ExperimentalPagerApi::class)
@Composable
fun Tabs(
    pagerState: PagerState,
    names: List<String>
) {
    val coroutineScope = rememberCoroutineScope()
    names.forEachIndexed { index, name ->
        Tab( //TODO change to material3
            text = { androidx.compose.material.Text(name) },
            selected = pagerState.currentPage == index,
            unselectedContentColor = MaterialTheme.colorScheme.onSurface.copy(alpha = ContentAlpha.medium),
            onClick = {
                coroutineScope.launch {
                    pagerState.animateScrollToPage(index)
                }
            }
        )
    }
}

@Composable
fun ShowSnackBarWithError(
    result: Result<*>,
    snackbarHostState: SnackbarHostState,
    onActionPerformed: () -> Unit
) {
    val errorMsg = stringResource(R.string.error)
    val retryMsg = stringResource(R.string.retry)
    val noConnectionMsg = stringResource(R.string.no_connection)
    val error = (result as Result.Failure).error
    LaunchedEffect(error) {
        val snackbarResult = snackbarHostState.showSnackbar(
            message = if (error is UnknownHostException) noConnectionMsg else errorMsg,
            actionLabel = retryMsg,
            duration = SnackbarDuration.Indefinite
        )
        if (snackbarResult == SnackbarResult.ActionPerformed)
            onActionPerformed()
    }
}


class TimetableModelProvider : PreviewParameterProvider<TimetableModel> {
    override val values = sequenceOf(
        TimetableModel("3TIG", ""),
        TimetableModel("K.Kras(KK)", ""),
        TimetableModel("p1 pracownia komputerowa", "")
    )

}

@Preview(widthDp = 120, group = "items")
@Composable
private fun TimetablePageItemPreview(@PreviewParameter(TimetableModelProvider::class) item: TimetableModel) {
    TimetablePageItem(navController = rememberNavController(), item = item)
}

@OptIn(ExperimentalPagerApi::class)
@Preview
@Composable
private fun TimetableTabRowPreview() {
    val names = listOf(
        stringResource(R.string.classes),
        stringResource(R.string.teachers),
        stringResource(R.string.classrooms)
    )
    TimetableTabRow(pagerState = rememberPagerState(), names = names)
}
