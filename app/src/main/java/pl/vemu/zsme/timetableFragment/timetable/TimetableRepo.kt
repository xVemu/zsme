package pl.vemu.zsme.timetableFragment.timetable

import org.jsoup.nodes.Element
import pl.vemu.zsme.login

object TimetableRepo {

    fun downloadTimetable(): Array<Array<Timetable>> {
        val document = login("lista.html")
        return arrayOf(
                makeArrayOfLinks(document.selectFirst("#oddzialy")),
                makeArrayOfLinks(document.selectFirst("#nauczyciele")),
                makeArrayOfLinks(document.selectFirst("#sale")),
        )
    }

    private fun makeArrayOfLinks(element: Element) = element.children().map {
        Timetable(it.text(), it.child(0).attr("href"))
    }.toTypedArray()
}