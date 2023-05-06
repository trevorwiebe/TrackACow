package com.trevorwiebe.trackacow.data.local.dao

import androidx.room.*
import com.trevorwiebe.trackacow.data.entities.LoadEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface LoadDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLoad(loadEntity: LoadEntity): Long

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

    @Query("DELETE FROM load")
    suspend fun deleteAllLoads()
}