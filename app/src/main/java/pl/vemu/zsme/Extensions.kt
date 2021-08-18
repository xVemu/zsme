package pl.vemu.zsme

import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jsoup.Connection
import org.jsoup.Jsoup
import org.jsoup.nodes.Document

fun RecyclerView.makeSimple() = apply {
    layoutManager = LinearLayoutManager(context)
    layoutParams = ViewGroup.LayoutParams(MATCH_PARENT, MATCH_PARENT)
    isVerticalScrollBarEnabled = false
    isHorizontalScrollBarEnabled = false
    setHasFixedSize(true)
}

//TODO
@Suppress("BlockingMethodInNonBlockingContext")
suspend fun login(aurl: String): Document = withContext(Dispatchers.IO) {
    Jsoup.connect("https://www.zsme.tarnow.pl/plan/$aurl").run {
        timeout(30000)
        method(Connection.Method.GET)
        userAgent("Mozilla/5.0 (Windows NT 10.0; WOW64; rv:41.0) Gecko/20100101 Firefox/41.0")
        execute()
    }.parse()
}

fun Modifier.paddingStart(size: Dp = 0.dp) = this.padding(size, 0.dp, 0.dp, 0.dp)
fun Modifier.paddingTop(size: Dp = 0.dp) = this.padding(0.dp, size, 0.dp, 0.dp)
fun Modifier.paddingEnd(size: Dp = 0.dp) = this.padding(0.dp, 0.dp, size, 0.dp)
fun Modifier.paddingBottom(size: Dp = 0.dp) = this.padding(0.dp, 0.dp, 0.dp, size)