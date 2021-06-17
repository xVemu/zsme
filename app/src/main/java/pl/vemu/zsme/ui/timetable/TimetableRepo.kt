package pl.vemu.zsme.ui.timetable

import org.jsoup.nodes.Element
import pl.vemu.zsme.data.model.TimetableModel
import pl.vemu.zsme.login
import javax.inject.Inject

class TimetableRepo @Inject constructor() {

    suspend fun getTimetable(): List<List<TimetableModel>> {
        val document = login("lista.html")
        return listOf(
                makeArrayOfLinks(document.selectFirst("#oddzialy")),
                makeArrayOfLinks(document.selectFirst("#nauczyciele")),
                makeArrayOfLinks(document.selectFirst("#sale")),
        )
    }

    private fun makeArrayOfLinks(element: Element) = element.children().map {
        TimetableModel(it.text(), it.child(0).attr("href"))
    }
}