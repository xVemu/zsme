package pl.vemu.zsme.data.repo

import com.google.firebase.Firebase
import com.google.firebase.remoteconfig.remoteConfig
import it.skrape.core.htmlDocument
import it.skrape.fetcher.AsyncFetcher
import it.skrape.fetcher.BasicAuth
import it.skrape.fetcher.response
import it.skrape.fetcher.skrape
import it.skrape.selects.DocElement
import kotlinx.datetime.DayOfWeek
import org.koin.core.annotation.Single
import pl.vemu.zsme.data.model.LessonModel
import pl.vemu.zsme.util.Parser
import pl.vemu.zsme.util.scheduleLogin
import pl.vemu.zsme.util.schedulePassword
import pl.vemu.zsme.util.scheduleUrl

@Single
class LessonRepo {
    suspend fun getLesson(link: String): List<LessonModel> =
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

                        children.drop(1).flatMap { row ->
                            val index = row.children.first().text.toInt()
                            val (timeStart, timeFinish) = row.children[1].text.replace(
                                "\\s".toRegex(),
                                ""
                            ).split("-")

                            row.children.drop(2).flatMapIndexed { dayIndex, lesson ->
                                if (lesson.text.isEmpty()) return@flatMapIndexed emptyList()

                                val list = lesson.findAll("[style=font-size:85%]").ifEmpty { listOf(lesson) }

                                val day = DayOfWeek.of(dayIndex + 1)
                                list.mapIndexed { singleIndex, singleLesson ->
                                    singleLesson.buildLesson(
                                        timeStart = timeStart,
                                        timeFinish = timeFinish,
                                        index = index,
                                        showIndex = singleIndex == 0,
                                        day = day,
                                        parentUrl = link,
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

    private fun DocElement.buildLesson(
        timeStart: String,
        timeFinish: String,
        index: Int,
        showIndex: Boolean,
        day: DayOfWeek,
        parentUrl: String,
    ) =
        LessonModel(
            name = (findFirstOrNull(".p") ?: this).text,
            teacher = (findFirstOrNull(".n") ?: findFirstOrNull(".o"))?.text,
            room = (findFirstOrNull(".s") ?: findFirstOrNull(".o"))?.text,
            timeStart = Parser.parseHourMinute(timeStart),
            timeFinish = Parser.parseHourMinute(timeFinish),
            index = index,
            showIndex = showIndex,
            day = day,
            parentUrl = parentUrl,
        )

    private fun DocElement.findFirstOrNull(selector: String) = findAll(selector).firstOrNull()
}
