package com.trevorwiebe.trackacow.data.local.dao

import androidx.room.*
import com.trevorwiebe.trackacow.data.entities.LoadEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface LoadDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLoad(loadEntity: LoadEntity): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertLoadList(loadList: List<LoadEntity>): List<Long>

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

    @Update
    suspend fun updateLoadList(loadList: List<LoadEntity>)

    @Query("UPDATE load SET lotId = :lotId WHERE lotId IN (:oldLotIds)")
    suspend fun updateLoadWithNewLotId(lotId: String, oldLotIds: List<String>)

    @Delete
    suspend fun deleteLoad(loadEntity: LoadEntity)

    @Query("DELETE FROM load")
    suspend fun deleteAllLoads()

    @Query("DELETE FROM load WHERE lotId = :lotId")
    suspend fun deleteLoadByLotId(lotId: String)

    @Transaction
    suspend fun syncCloudLoadsByLotId(loadList: List<LoadEntity>, lotId: String) {
        deleteLoadByLotId(lotId)
        insertLoadList(loadList)
    }
}