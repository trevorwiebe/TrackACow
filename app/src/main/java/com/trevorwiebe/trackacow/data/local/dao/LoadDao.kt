package com.trevorwiebe.trackacow.data.local.dao

import androidx.room.*
import com.trevorwiebe.trackacow.data.entities.LoadEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface LoadDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLoad(loadEntity: LoadEntity)

    @Query("SELECT * FROM load WHERE lotId = :lotId")
    fun readLoadsByLotId(lotId: String): Flow<List<LoadEntity>>

    @Query(
        "UPDATE load " +
                "SET numberOfHead = :numberOfHead, date = :date, description = :description, lotId = :lotId, loadId = :loadId " +
                "WHERE primaryKey = :primaryKey"
    )
    suspend fun updateLoad(
        primaryKey: Int,
        numberOfHead: Int,
        date: Long,
        description: String?,
        lotId: String?,
        loadId: String?
    )

    @Delete
    suspend fun deleteLoad(loadEntity: LoadEntity)

    // Deprecated
    @Deprecated("use suspend function")
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertLoad2(loadEntity: LoadEntity?)

    @Deprecated("use suspend function")
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertLoadList(loadEntities: List<LoadEntity?>?)

    @Deprecated("use flow return type")
    @Query("SELECT * FROM load WHERE lotId = :lotId")
    fun getLoadsByLotId(lotId: String?): List<LoadEntity?>?

    @Deprecated("use flow return type")
    @Query("SELECT * FROM load WHERE loadId = :loadId")
    fun getLoadByLoadId(loadId: String?): LoadEntity?

    @Deprecated("use suspend function")
    @Query("UPDATE load SET numberOfHead = :headCount, date = :date, description = :memo WHERE loadId = :loadId")
    fun updateLoadByFields(headCount: Int, date: Long, memo: String?, loadId: String?)

    @Deprecated("use suspend function")
    @Update
    fun updateLoad(loadEntity: LoadEntity?)

    @Deprecated("use suspend function")
    @Delete
    fun deleteLoad(loadEntity: LoadEntity?)

    @Deprecated("use suspend function")
    @Query("DELETE FROM load")
    fun deleteLoadTable()

    @Deprecated("use suspend function")
    @Query("DELETE FROM load WHERE loadId = :loadId")
    fun deleteLoadByLoadId(loadId: String?)
}