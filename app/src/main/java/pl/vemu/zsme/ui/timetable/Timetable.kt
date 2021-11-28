package pl.vemu.zsme.ui.timetable

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.GridCells
import androidx.compose.foundation.lazy.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import kotlinx.coroutines.launch
import pl.vemu.zsme.R

@OptIn(
    ExperimentalPagerApi::class,
    ExperimentalFoundationApi::class,
    ExperimentalMaterialApi::class
)
@Composable
fun Timetable(
    navController: NavController,
    vm: TimetableVM = hiltViewModel(),
) {
    val coroutines = rememberCoroutineScope()
    val names = listOf(
        stringResource(R.string.classes),
        stringResource(R.string.teachers),
        stringResource(R.string.classrooms)
    )
    val pagerState = rememberPagerState()
    val timetableList by vm.list.collectAsState()
    Column(Modifier.fillMaxSize()) {
        TabRow(
            selectedTabIndex = pagerState.currentPage,
            backgroundColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.primary
        ) {
            names.forEachIndexed { index, name ->
                Tab( //TODO change to material3 card
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
            LazyVerticalGrid(
                cells = GridCells.Fixed(count = 3)
            ) {
                if (timetableList.isEmpty()) return@LazyVerticalGrid
                items(timetableList[page]) { item ->
                    Card( // TODO change to material 3 card
                        onClick = { navController.navigate("lesson/${item.url}") },
                        elevation = 2.dp,
                        backgroundColor = MaterialTheme.colorScheme.surface,
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
            }
        }
    }
}


/*private fun setupNetwork() {
    val cm = context?.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val networkRequest = NetworkRequest.Builder().build()
    if (cm.activeNetwork == null) Toast.makeText(
        context,
        "Brak połaczenia z internetem",
        Toast.LENGTH_LONG
    ).show()
    cm.registerNetworkCallback(networkRequest, object : NetworkCallback() {
        override fun onAvailable(network: Network) {
            viewModel.fetchTimetable()
        }
    })
}
}*/
