package pl.vemu.zsme.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity
data class Post(
        @PrimaryKey(autoGenerate = false)
        val id: Int,
        val date: Date,
        val link: String,
        val title: String,
        val content: String,
        val excerpt: String,
        val author: Int,
        @ColumnInfo(name = "featured_media")
        val featuredMedia: Int,
        val categories: List<Int>,
)
