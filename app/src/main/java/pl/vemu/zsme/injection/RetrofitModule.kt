package pl.vemu.zsme.injection

import com.google.firebase.Firebase
import com.google.firebase.remoteconfig.remoteConfig
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import de.jensklingenberg.ktorfit.Ktorfit
import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.*
import pl.vemu.zsme.data.service.ZSMEService
import pl.vemu.zsme.data.service.createZSMEService
import pl.vemu.zsme.util.apiUrl
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class RetrofitModule {

    @OptIn(ExperimentalSerializationApi::class)
    @Singleton
    @Provides
    fun provideHttpClient(): HttpClient = HttpClient {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                explicitNulls = false
            })
        }
    }

    @Singleton
    @Provides
    fun provideRetrofit(httpClient: HttpClient): Ktorfit = Ktorfit.Builder()
        .baseUrl(Firebase.remoteConfig.apiUrl + "/")
        .httpClient(httpClient)
        .build()

    @Singleton
    @Provides
    fun provideZSMEService(ktorfit: Ktorfit): ZSMEService =
        ktorfit.createZSMEService()
}
