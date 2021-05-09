package pl.vemu.zsme.data.repo

import org.jsoup.nodes.Element
import pl.vemu.zsme.data.model.LessonModel
import pl.vemu.zsme.login
import java.util.*
import javax.inject.Inject

class LessonRepo @Inject constructor() {

    fun getLesson(url: String): List<List<LessonModel>> {
        val document = login(url)
        val table = document.selectFirst(".tabela").child(0).children()
        table.removeAt(0)
        val lessonsList = List(5) { mutableListOf<LessonModel>() }
        //TODO rewrite
        table.forEach {
            val lessons = it.select(".l")
            val lessonIndex = it.selectFirst(".nr").text().toInt()
            val (lessonTimeStart, lessonTimeFinish) = it.selectFirst(".g").text()
                    .replace("\\s".toRegex(), "").split("-")
            lessons.forEachIndexed { i, item ->
                if (item.text().isEmpty()) return@forEach
                val lesson1: LessonModel
                var lesson2: LessonModel? = null
                if (item.getElementsByAttributeValue("style", "font-size:85%").size > 1) {
                    val (lesson1Span, lesson2Span) = item.getElementsByAttributeValue("style", "font-size:85%")
                    lesson1 = buildLesson(lesson1Span)
                    lesson2 = buildLesson(lesson2Span)
                } else if (item.selectFirst(".p") != null && item.select(".p").eachText().contains("etyka")) {
                    lesson1 = buildLesson(item.selectFirst("[style=font-size:85%]"))
                    item.child(0).remove()
                    item.child(0).remove()
                    lesson2 = buildLesson(item)
                } else {
                    lesson1 = buildLesson(item)
                }
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

    //TODO rewrite
    private fun buildLesson(lesson: Element): LessonModel {
        return LessonModel(
                name = if (lesson.selectFirst(".p") != null)
                    lesson.selectFirst(".p").text()
                else lesson.text(),
                teacher = when {
                    lesson.selectFirst(".n") != null -> lesson.selectFirst(".n").text()
                    lesson.selectFirst(".o") != null -> lesson.selectFirst(".o").text()
                    else -> ""
                },
                room = when {
                    lesson.selectFirst(".s") != null -> lesson.selectFirst(".s").text()
                    lesson.selectFirst(".o") != null -> lesson.selectFirst(".o").text()
                    else -> ""
                }
        )
    }
}