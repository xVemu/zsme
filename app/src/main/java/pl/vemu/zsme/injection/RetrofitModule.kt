package pl.vemu.zsme.injection

import com.google.firebase.Firebase
import com.google.firebase.remoteconfig.remoteConfig
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import pl.vemu.zsme.data.model.DetailImage
import pl.vemu.zsme.data.model.DetailImageDeserializer
import pl.vemu.zsme.data.service.ZSMEService
import pl.vemu.zsme.util.apiUrl
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class RetrofitModule {

    @Singleton
    @Provides
    fun provideGson(): Gson = GsonBuilder()
        .setDateFormat("yyyy-MM-dd'T'HH:mm:ss")
        .registerTypeAdapter(
            DetailImage::class.java, DetailImageDeserializer(),
        )
        .create()

    @Singleton
    @Provides
    fun provideRetrofit(gson: Gson): Retrofit = Retrofit.Builder()
        .baseUrl(Firebase.remoteConfig.apiUrl + "/")
        .addConverterFactory(GsonConverterFactory.create(gson))
        .build()

    @Singleton
    @Provides
    fun provideZSMEService(retrofit: Retrofit): ZSMEService =
        retrofit.create(ZSMEService::class.java)
}
