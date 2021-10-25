package pl.vemu.zsme.data.repo

import org.jsoup.nodes.Element
import pl.vemu.zsme.data.model.LessonModel
import pl.vemu.zsme.login
import javax.inject.Inject

class LessonRepo @Inject constructor() {

    //TODO
    suspend fun getLesson(url: String): List<List<LessonModel>> {
        val document = login("plany/$url")
        val table = document.selectFirst(".tabela")!!.child(0).children()
        table.removeAt(0)
        val lessonsList = List(5) { mutableListOf<LessonModel>() }
        //TODO rewrite
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
                    lesson1 = buildLesson(lessonSpans[0])
                    lesson2 = lessonSpans.getOrNull(1)?.let { span -> buildLesson(span) }
                } else lesson1 = buildLesson(item)
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
        return lessonsList
    }

    private fun buildLesson(lesson: Element) =
        LessonModel(
            name = (lesson.selectFirst(".p") ?: lesson).text(),
            teacher = (lesson.selectFirst(".n") ?: lesson.selectFirst(".o"))?.text(),
            room = (lesson.selectFirst(".s") ?: lesson.selectFirst(".o"))?.text()
        )

}