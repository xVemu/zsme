package pl.vemu.zsme.injection

import com.google.firebase.Firebase
import com.google.firebase.remoteconfig.remoteConfig
import de.jensklingenberg.ktorfit.Ktorfit
import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import org.koin.core.annotation.Module
import org.koin.core.annotation.Single
import pl.vemu.zsme.data.service.ZSMEService
import pl.vemu.zsme.data.service.createZSMEService
import pl.vemu.zsme.util.apiUrl

@Module
class RetrofitModule {

    @OptIn(ExperimentalSerializationApi::class)
    @Single
    fun provideHttpClient(): HttpClient = HttpClient {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                explicitNulls = false
            })
        }
    }

    @Single
    fun provideRetrofit(httpClient: HttpClient): Ktorfit = Ktorfit.Builder()
        .baseUrl(Firebase.remoteConfig.apiUrl + "/")
        .httpClient(httpClient)
        .build()

    @Single
    fun provideZSMEService(ktorfit: Ktorfit): ZSMEService =
        ktorfit.createZSMEService()
}
