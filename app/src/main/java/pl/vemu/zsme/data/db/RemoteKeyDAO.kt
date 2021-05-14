package pl.vemu.zsme.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import pl.vemu.zsme.data.model.RemoteKeyModel

@Dao
interface RemoteKeyDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(remoteKeys: List<RemoteKeyModel>)

    @Query("SELECT * FROM remote_keys WHERE id = :id")
    suspend fun remoteKeyByQueryId(id: Int): RemoteKeyModel?

    @Query("DELETE FROM remote_keys")
    suspend fun clearAll()
}