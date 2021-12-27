package pl.vemu.zsme.data.model

import com.google.gson.annotations.SerializedName
import java.util.*


data class PostEntity(
    val id: Int,
    val date: Date,
    val link: String,
    val title: Title,
    val content: Content,
    val excerpt: Excerpt,
    @SerializedName("_embedded")
    val embedded: Embedded,
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

data class Embedded(
    val author: List<Author>,
    @SerializedName("wp:featuredmedia")
    val wpFeaturedmedia: List<WpFeaturedmedia>?,
    @SerializedName("wp:term")
    val category: List<List<Category>>,
)

data class WpFeaturedmedia(
    @SerializedName("media_details")
    val mediaDetails: MediaDetails,
)

data class MediaDetails(
    val sizes: Sizes,
)

data class Sizes(
    val medium: Image,
    val full: Image,
)

data class Image(
    @SerializedName("source_url")
    val sourceUrl: String?,
)

data class Author(
    val name: String,
)

data class Category(
    val name: String,
)