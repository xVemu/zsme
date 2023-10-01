package pl.vemu.zsme.data.repo

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jsoup.Jsoup
import org.jsoup.nodes.Element
import pl.vemu.zsme.DEFAULT_URL
import pl.vemu.zsme.data.model.LessonModel
import javax.inject.Inject

class LessonRepo @Inject constructor() {

    suspend fun getLesson(url: String): List<List<LessonModel>> = withContext(Dispatchers.IO) {
        val document = Jsoup.connect("$DEFAULT_URL/plan/plany/$url").get()
        val table = document.selectFirst(".tabela")!!.child(0).children()
        table.removeAt(0)
        val lessonsList = List(5) { mutableListOf<LessonModel>() }
        table.forEach {
            val lessons = it.select(".l")
            val lessonIndex = it.selectFirst(".nr")!!.text().toInt()
            val (lessonTimeStart, lessonTimeFinish) = it.selectFirst(".g")!!.text()
                .replace("\\s".toRegex(), "").split("-")
            lessons.forEachIndexed { i, item ->
                if (item.text().isEmpty()) return@forEachIndexed
                val lesson1: LessonModel
                var lesson2: LessonModel? = null
                if (item.select("[style=font-size:85%]").isNotEmpty()) {
                    val lessonSpans = item.select("[style=font-size:85%]")
                    lesson1 = lessonSpans[0].buildLesson()
                    lesson2 = lessonSpans.getOrNull(1)?.buildLesson()
                } else lesson1 = item.buildLesson()
                lesson1.apply {
                    index = lessonIndex
                    timeStart = lessonTimeStart
                    timeFinish = lessonTimeFinish
                    lessonsList[i].add(this)
                }
                lesson2?.apply {
                    timeStart = lessonTimeStart
                    timeFinish = lessonTimeFinish
                    lessonsList[i].add(this)
                }
            }
        }
        return@withContext lessonsList
    }

    private fun Element.buildLesson() =
        LessonModel(
            name = (selectFirst(".p") ?: this).text(),
            teacher = (selectFirst(".n") ?: selectFirst(".o"))?.text(),
            room = (selectFirst(".s") ?: selectFirst(".o"))?.text()
        )

}
