package pl.vemu.zsme.service

import pl.vemu.zsme.data.PostEntity
import retrofit2.http.GET

interface ZSMEService {

    @GET("posts")
    suspend fun getPosts(): List<PostEntity>
}