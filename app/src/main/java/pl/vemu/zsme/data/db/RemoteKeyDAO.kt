package pl.vemu.zsme.data.db

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import pl.vemu.zsme.data.model.RemoteKeyModel

@Dao
interface RemoteKeyDAO {

    @Upsert
    suspend fun insertAll(remoteKeys: List<RemoteKeyModel>)

    @Query("SELECT * FROM remote_keys WHERE id = :id")
    suspend fun remoteKeyByQueryId(id: Int): RemoteKeyModel?

    @Query("DELETE FROM remote_keys")
    suspend fun clearAll()
}
