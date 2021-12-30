package pl.vemu.zsme.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "remote_keys")
data class RemoteKeyModel(
    @PrimaryKey
    val id: Int,
    val prevKey: Int?,
    val nextKey: Int?,
)
