package pl.vemu.zsme.data.repo

import com.google.firebase.Firebase
import com.google.firebase.remoteconfig.remoteConfig
import it.skrape.core.htmlDocument
import it.skrape.fetcher.AsyncFetcher
import it.skrape.fetcher.BasicAuth
import it.skrape.fetcher.response
import it.skrape.fetcher.skrape
import it.skrape.selects.DocElement
import it.skrape.selects.ElementNotFoundException
import pl.vemu.zsme.data.model.LessonModel
import pl.vemu.zsme.util.scheduleLogin
import pl.vemu.zsme.util.schedulePassword
import pl.vemu.zsme.util.scheduleUrl
import javax.inject.Inject

class LessonRepo @Inject constructor() {

    suspend fun getLesson(link: String): List<List<LessonModel>> =
        skrape(AsyncFetcher) {
            request {
                val login: String = Firebase.remoteConfig.scheduleLogin
                val password: String = Firebase.remoteConfig.schedulePassword
                url = Firebase.remoteConfig.scheduleUrl + "/$link"
                authentication = BasicAuth(login, password)
            }
            response {
                htmlDocument {
                    relaxed = true
                    findFirst(".tabela > tbody") {
                        val lessons = List(5) { mutableListOf<LessonModel>() }

                        children.drop(1).forEach { row ->
                            val index = row.children.first().text.toInt()
                            val (timeStart, timeFinish) = row.children[1].text.replace(
                                "\\s".toRegex(),
                                ""
                            ).split("-")

                            row.children.drop(2).forEachIndexed { i, lesson ->
                                if (lesson.text.isEmpty()) return@forEachIndexed

                                val list = try {
                                    lesson.findAll("[style=font-size:85%]")
                                } catch (e: ElementNotFoundException) {
                                    listOf(lesson)
                                }

                                list.forEachIndexed { singleIndex, singleLesson ->
                                    lessons[i].add(
                                        singleLesson.buildLesson(
                                            index.takeIf { singleIndex == 0 },
                                            timeStart,
                                            timeFinish
                                        )
                                    )
                                }
                            }
                        }

                        return@findFirst lessons
                    }
                }
            }
        }

    private fun DocElement.buildLesson(index: Int?, timeStart: String, timeFinish: String) =
        LessonModel(
            name = (findFirstOrNull(".p") ?: this).text,
            teacher = (findFirstOrNull(".n") ?: findFirstOrNull(".o"))?.text,
            room = (findFirstOrNull(".s") ?: findFirstOrNull(".o"))?.text,
            index = index,
            timeStart = timeStart,
            timeFinish = timeFinish,
        )

    private fun DocElement.findFirstOrNull(selector: String): DocElement? = try {
        findFirst(selector)
    } catch (e: ElementNotFoundException) {
        null
    }

}
