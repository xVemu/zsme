package pl.vemu.zsme.data.model

import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PostEntity(
    val id: Int,
    @SerialName("date_gmt")
    val date: LocalDateTime,
    val link: String,
    val title: Title,
    val content: Content,
    val excerpt: Excerpt,
    @SerialName("_embedded")
    val embedded: Embedded,
)

@Serializable
data class Title(
    val rendered: String,
)

@Serializable
data class Content(
    val rendered: String,
)

@Serializable
data class Excerpt(
    val rendered: String,
)

@Serializable
data class Embedded(
    val author: List<Author>,
    @SerialName("wp:featuredmedia")
    val wpFeaturedmedia: List<WpFeaturedmedia>?,
    @SerialName("wp:term")
    val category: List<List<Category>>,
)

@Serializable
data class WpFeaturedmedia(
    @SerialName("media_details")
    val mediaDetails: MediaDetails?,
    @SerialName("source_url")
    val sourceUrl: String?,
)

@Serializable
data class MediaDetails(
    val sizes: Sizes,
)

@Serializable
data class Sizes(
    val thumbnail: Image?,
)

@Serializable
data class Image(
    @SerialName("source_url")
    val sourceUrl: String?,
)

@Serializable
data class Author(
    val id: Int,
    val name: String,
)

@Serializable
data class Category(
    val id: Int,
    val name: String,
)
