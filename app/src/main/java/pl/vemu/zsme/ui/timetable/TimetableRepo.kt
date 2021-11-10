package pl.vemu.zsme.ui.timetable

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jsoup.Jsoup
import org.jsoup.nodes.Element
import pl.vemu.zsme.data.model.TimetableModel
import javax.inject.Inject

class TimetableRepo @Inject constructor() {

    @Suppress("BlockingMethodInNonBlockingContext")
    suspend fun getTimetable(): List<List<TimetableModel>> =
        withContext(Dispatchers.IO) {
            val document = Jsoup.connect("https://www.zsme.tarnow.pl/plan/lista.html").get()
            return@withContext listOf(
                makeArrayOfLinks(document.selectFirst("#oddzialy")!!),
                makeArrayOfLinks(document.selectFirst("#nauczyciele")!!),
                makeArrayOfLinks(document.selectFirst("#sale")!!),
            )
        }

    private fun makeArrayOfLinks(element: Element) = element.children().map {
        TimetableModel(it.text(), it.child(0).attr("href").removePrefix("plany/"))
    }
}