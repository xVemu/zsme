package pl.vemu.zsme.data.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize
import java.util.*

@Entity(tableName = "Posts")
@Parcelize
data class PostModel(
    @PrimaryKey
    val id: Int,
    val date: Date, /*TODO change to LocalDateTime / Instant, Time Zone problem?*/
    val link: String,
    val title: String,
    val content: String,
    val excerpt: String,
    val author: String,
    val thumbnail: String?,
    val fullImage: String?,
    val category: String,
) : Parcelable