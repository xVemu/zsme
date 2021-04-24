package pl.vemu.zsme.data

import com.google.gson.annotations.SerializedName
import java.util.*


data class PostEntity(
        val id: Int,
        val date: Date,
        val link: String,
        val title: Title,
        val content: Content,
        val excerpt: Excerpt,
        val author: Int,
        @SerializedName("featured_media")
        val featuredMedia: Int,
        val categories: List<Int>,
)

data class Title(
        val rendered: String,
)

data class Content(
        val rendered: String,
)

data class Excerpt(
        val rendered: String,
)