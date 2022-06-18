package pl.vemu.zsme.ui.timetable

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jsoup.Jsoup
import org.jsoup.nodes.Element
import pl.vemu.zsme.DEFAULT_URL
import pl.vemu.zsme.data.model.TimetableModel
import javax.inject.Inject

class TimetableRepo @Inject constructor() {

    @Suppress("BlockingMethodInNonBlockingContext")
    suspend fun getTimetable(): List<List<TimetableModel>> =
        withContext(Dispatchers.IO) {
            val document = Jsoup.connect("$DEFAULT_URL/plan/lista.html").get()
            return@withContext listOf(
                document.selectFirst("#oddzialy")!!.makeArrayOfLinks(),
                document.selectFirst("#nauczyciele")!!.makeArrayOfLinks(),
                document.selectFirst("#sale")!!.makeArrayOfLinks(),
            )
        }

    private fun Element.makeArrayOfLinks() = children().map {
        TimetableModel(it.text(), it.child(0).attr("href").removePrefix("plany/"))
    }
}