package com.trevorwiebe.trackacow.data.local.dao

import androidx.room.*
import com.trevorwiebe.trackacow.data.entities.DrugEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface DrugDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDrug(drugEntity: DrugEntity): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertDrugList(drugList: List<DrugEntity>): List<Long>

    @Query("SELECT * FROM Drug")
    fun getDrugList(): Flow<List<DrugEntity>>

    @Query("UPDATE Drug SET drugName = :drugName, defaultAmount = :defaultAmount WHERE drugPrimaryKey = :id")
    suspend fun updateDrug(drugName: String, defaultAmount: Int, id: Int)

    @Update
    suspend fun updateDrugList(drugList: List<DrugEntity>)

    @Query("DELETE FROM Drug")
    suspend fun deleteAllDrugs()

    @Query("DELETE FROM Drug WHERE drugCloudDatabaseId = :drugId")
    fun deleteDrugById(drugId: String)

    @Delete
    suspend fun deleteDrug(drugEntity: DrugEntity)

    @Transaction
    suspend fun insertOrUpdate(drugList: List<DrugEntity>) {
        val insertResult = insertDrugList(drugList)
        val updateList = mutableListOf<DrugEntity>()
        for (i in insertResult.indices) {
            if (insertResult[i] == -1L) updateList.add(drugList[i])
        }
        if (updateList.isNotEmpty()) updateDrugList(drugList)
    }

}