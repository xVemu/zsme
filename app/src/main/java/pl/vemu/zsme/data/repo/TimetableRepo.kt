package pl.vemu.zsme.data.repo

import com.google.firebase.Firebase
import com.google.firebase.remoteconfig.remoteConfig
import it.skrape.core.htmlDocument
import it.skrape.fetcher.AsyncFetcher
import it.skrape.fetcher.BasicAuth
import it.skrape.fetcher.response
import it.skrape.fetcher.skrape
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import pl.vemu.zsme.data.model.TimetableModel
import pl.vemu.zsme.util.scheduleLogin
import pl.vemu.zsme.util.schedulePassword
import pl.vemu.zsme.util.scheduleUrl
import javax.inject.Inject

class TimetableRepo @Inject constructor() {

    suspend fun getTimetable(): List<List<TimetableModel>> =
        withContext(Dispatchers.IO) {
            skrape(AsyncFetcher) {
                request {
                    val login: String = Firebase.remoteConfig.scheduleLogin
                    val password: String = Firebase.remoteConfig.schedulePassword
                    url = Firebase.remoteConfig.scheduleUrl + "/lista.html"
                    authentication = BasicAuth(login, password)
                }
                response {
                    htmlDocument {
                        listOf("#oddzialy", "#nauczyciele", "#sale").map { selector ->
                            findFirst(selector) {
                                children.map {
                                    TimetableModel(it.text, it.eachHref.first())
                                }.sortedBy { it.name }
                            }
                        }
                    }
                }
            }
        }
}
