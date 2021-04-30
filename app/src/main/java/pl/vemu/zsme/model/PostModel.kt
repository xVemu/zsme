package pl.vemu.zsme.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity(tableName = "Posts")
data class PostModel(
        @PrimaryKey(autoGenerate = false)
        val id: Int,
        val date: Date,
        val link: String,
        val title: String,
        val content: String,
        val excerpt: String,
        val author: String,
        val thumbnail: String,
        val category: String,
)