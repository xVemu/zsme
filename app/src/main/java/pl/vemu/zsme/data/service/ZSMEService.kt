package pl.vemu.zsme.data.service

import pl.vemu.zsme.data.model.PostEntity
import retrofit2.http.GET
import retrofit2.http.Query

interface ZSMEService {
    @GET("posts?_embed&fields=id,date,link,title,content.rendered,excerpt.rendered,_links")
    suspend fun searchPosts(
        @Query("search") query: String?,
        @Query("page") page: Int,
        @Query("per_page") perPage: Int,
        @Query("categories") categories: List<Int>? = null,
        @Query("author") author: List<Int>? = null,
    ): List<PostEntity>
}
