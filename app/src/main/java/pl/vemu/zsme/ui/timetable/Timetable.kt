package pl.vemu.zsme.ui.timetable

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.PagerState
import com.google.accompanist.pager.rememberPagerState
import com.ramcosta.composedestinations.annotation.DeepLink
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.NavGraph
import com.ramcosta.composedestinations.annotation.RootNavGraph
import com.ramcosta.composedestinations.navigation.navigate
import pl.vemu.zsme.DEFAULT_URL
import pl.vemu.zsme.R
import pl.vemu.zsme.Result
import pl.vemu.zsme.data.model.TimetableModel
import pl.vemu.zsme.ui.components.ExpandTransition
import pl.vemu.zsme.ui.components.ShowSnackBarWithError
import pl.vemu.zsme.ui.components.SimpleSnackbar
import pl.vemu.zsme.ui.components.Tabs
import pl.vemu.zsme.ui.destinations.LessonDestination

@RootNavGraph
@NavGraph
annotation class TimetableNavGraph(
    val start: Boolean = false
)

@TimetableNavGraph(start = true)
@Destination(
    route = "timetable/main",
    deepLinks = [DeepLink(uriPattern = "zsme://timetable"), DeepLink(uriPattern = "$DEFAULT_URL/plan/")],
    style = ExpandTransition::class
)
@OptIn(ExperimentalPagerApi::class, ExperimentalMaterial3Api::class)
@Composable
fun Timetable(
    navController: NavController,
    vm: TimetableVM = hiltViewModel(),
) {
    Scaffold(topBar = { CenterAlignedTopAppBar(title = { Text(stringResource(R.string.timetable)) }) }) { padding ->
        val names = listOf(
            stringResource(R.string.classes),
            stringResource(R.string.teachers),
            stringResource(R.string.classrooms)
        )
        val pagerState = rememberPagerState()
        val timetableResult by vm.list.collectAsState()
        val snackbarHostState = remember { SnackbarHostState() }
        Box(
            Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
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
                                columns = GridCells.Fixed(count = 3)
                            ) {
                                if (timetableList.isEmpty()) return@LazyVerticalGrid
                                items(timetableList[page]) { item ->
                                    TimetablePageItem(navController, item)
                                }
                            }
                        }
                    }
                    is Result.Failure -> {
                        snackbarHostState.ShowSnackBarWithError(
                            result = timetableResult
                        ) {
                            vm.downloadTimetable()
                        }
                    }
                }
            }
            SimpleSnackbar(snackbarHostState)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TimetablePageItem(
    navController: NavController,
    item: TimetableModel
) {
    OutlinedCard(
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
        onClick = { navController.navigate(LessonDestination(item.url)) },
        modifier = Modifier
            .height((LocalConfiguration.current.screenWidthDp / 3).dp)
            .padding(8.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = item.name,
                style = MaterialTheme.typography.bodyLarge,
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
        /*indicator = { tabPositions ->
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
