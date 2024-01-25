package pl.vemu.zsme.data.service

import pl.vemu.zsme.data.model.Author
import pl.vemu.zsme.data.model.Category
import pl.vemu.zsme.data.model.DetailImage
import pl.vemu.zsme.data.model.PostEntity
import retrofit2.http.GET
import retrofit2.http.Query

interface ZSMEService {
    @GET("posts?_embed&fields=id,date,link,title,content.rendered,excerpt.rendered,_links")
    suspend fun searchPosts(
        @Query("search") query: String?,
        @Query("page") page: Int,
        @Query("per_page") perPage: Int,
        @Query("categories[]") categories: List<Int> = emptyList(),
        @Query("author[]") author: List<Int> = emptyList(),
    ): List<PostEntity>

    @GET("media")
    suspend fun getPhotos(
        @Query("parent") id: Int, /* paging */
    ): List<DetailImage>

    @GET("categories?per_page=78")
    suspend fun getCategories(): List<Category>

    @GET("users?per_page=78")
    suspend fun getAuthors(): List<Author>
}
