package pl.vemu.zsme.data.model

import android.os.Parcelable
import androidx.compose.runtime.Immutable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize
import java.util.Date

@Entity(tableName = "posts")
@Parcelize
@Immutable
data class PostModel(
    @PrimaryKey
    val id: Int,
    val date: Date,
    val link: String,
    val title: String,
    val content: String,
    val excerpt: String,
    val author: String,
    val thumbnail: String?,
    val fullImage: String?,
    val category: String,
) : Parcelable
