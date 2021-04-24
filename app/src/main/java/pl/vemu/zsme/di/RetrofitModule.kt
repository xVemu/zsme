package pl.vemu.zsme.di

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped
import pl.vemu.zsme.service.ZSMEService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

@Module
@InstallIn(ViewModelComponent::class)
class RetrofitModule {
    @Provides
    @ViewModelScoped //TODO can remove?
    fun provideGson(): Gson = GsonBuilder()
            .setDateFormat("yyyy-MM-dd'T'HH:mm:ss")
            .create()

    @Provides
    @ViewModelScoped
    fun provideRetrofit(gson: Gson): Retrofit = Retrofit.Builder()
            .baseUrl("https://zsme.tarnow.pl/wp/wp-json/wp/v2/")
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()

    @Provides
    @ViewModelScoped
    fun provideZSMEService(retrofit: Retrofit): ZSMEService = retrofit.create(ZSMEService::class.java)
}