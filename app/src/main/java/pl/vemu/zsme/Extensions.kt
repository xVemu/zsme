package pl.vemu.zsme

import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jsoup.Connection
import org.jsoup.Jsoup
import org.jsoup.nodes.Document

fun RecyclerView.makeSimple() = apply {
    layoutManager = LinearLayoutManager(context)
    layoutParams = ViewGroup.LayoutParams(MATCH_PARENT, MATCH_PARENT) //TODO remove?
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