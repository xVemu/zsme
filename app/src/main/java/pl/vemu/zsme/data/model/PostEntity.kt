package pl.vemu.zsme.data.model

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import java.util.*

// TODO Remove when Retrofit 2.10.0 is released
@Keep
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

@Keep
data class Title(
    val rendered: String,
)

@Keep
data class Content(
    val rendered: String,
)

@Keep
data class Excerpt(
    val rendered: String,
)

@Keep
data class Embedded(
    val author: List<Author>,
    @SerializedName("wp:featuredmedia")
    val wpFeaturedmedia: List<WpFeaturedmedia>?,
    @SerializedName("wp:term")
    val category: List<List<Category>>,
)

@Keep
data class WpFeaturedmedia(
    @SerializedName("media_details")
    val mediaDetails: MediaDetails,
)

@Keep
data class MediaDetails(
    val sizes: Sizes,
)

@Keep
data class Sizes(
    val medium: Image,
    val full: Image,
)

@Keep
data class Image(
    @SerializedName("source_url")
    val sourceUrl: String?,
)

@Keep
data class Author(
    val name: String,
)

@Keep
data class Category(
    val name: String,
)
