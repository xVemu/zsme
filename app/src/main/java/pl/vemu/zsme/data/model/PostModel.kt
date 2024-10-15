package pl.vemu.zsme.data.model

import android.os.Parcelable
import androidx.compose.runtime.Immutable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.datetime.LocalDateTime
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.TypeParceler
import pl.vemu.zsme.data.db.LocalDateTimeClassParceler

@Entity(tableName = "post")
@Parcelize
@TypeParceler<LocalDateTime, LocalDateTimeClassParceler>()
@Immutable
data class PostModel(
    @PrimaryKey
    val id: Int,
    val date: LocalDateTime,
    val link: String,
    val title: String,
    val content: String,
    val excerpt: String,
    val author: String,
    val thumbnail: String?,
    val fullImage: String?,
    val category: String,
) : Parcelable
