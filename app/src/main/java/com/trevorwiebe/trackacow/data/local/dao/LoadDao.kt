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

    // TODO: update this to delete
    @Transaction
    suspend fun insertOrUpdate(loadList: List<LoadEntity>) {
        val insertResult = insertLoadList(loadList)
        val updateList = mutableListOf<LoadEntity>()
        for (i in insertResult.indices) {
            if (insertResult[i] == -1L) updateList.add(loadList[i])
        }
        if (updateList.isNotEmpty()) updateLoadList(loadList)
    }
}