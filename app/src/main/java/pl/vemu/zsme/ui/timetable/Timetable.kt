package pl.vemu.zsme.ui.timetable

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import kotlinx.coroutines.launch
import pl.vemu.zsme.R

//TODO arrow back remove
@ExperimentalPagerApi
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
            /*TODO elevation same as TopAppBar*/
            selectedTabIndex = pagerState.currentPage,
            contentColor = MaterialTheme.colors.secondary,
        ) {
            names.forEachIndexed { index, name ->
                Tab(
                    text = { Text(text = name) },
                    selected = pagerState.currentPage == index,
                    selectedContentColor = MaterialTheme.colors.secondary,
                    unselectedContentColor = MaterialTheme.colors.onBackground.copy(alpha = ContentAlpha.medium),
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
            LazyColumn {
                if (timetableList.isNotEmpty())
                    items(timetableList[page]) { item ->
                        Text(
                            text = item.name,
                            fontSize = 18.sp,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp)
                                .clickable { navController.navigate("lesson/" + item.url) }
                        )
                    }
            }
        }
    }
}


/*

private fun setupNetwork() {
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
