package com.trevorwiebe.trackacow.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.trevorwiebe.trackacow.data.entities.RationEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface RationDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRation(rationEntity: RationEntity): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertRationList(rationEntityList: List<RationEntity>): List<Long>

    @Query("SELECT * FROM ration")
    fun getRations(): Flow<List<RationEntity>>

    @Query("SELECT * FROM ration WHERE rationPrimaryKey = :id")
    fun getRationsById(id: Int): Flow<RationEntity>

    @Update
    suspend fun updateRation(rationEntity: RationEntity)

    @Update
    suspend fun updateRationList(rationList: List<RationEntity>)

    @Query("DELETE FROM ration WHERE rationPrimaryKey = :rationId")
    suspend fun deleteRationById(rationId: Int)

    @Query("DELETE FROM ration")
    suspend fun deleteAllRations()

    @Transaction
    suspend fun insertOrUpdateRationList(rationList: List<RationEntity>) {
        val insertResult = insertRationList(rationList)
        val updateList = mutableListOf<RationEntity>()
        for (i in insertResult.indices) {
            if (insertResult[i] == -1L) updateList.add(rationList[i])
        }
        if (updateList.isNotEmpty()) updateRationList(rationList)
    }
}