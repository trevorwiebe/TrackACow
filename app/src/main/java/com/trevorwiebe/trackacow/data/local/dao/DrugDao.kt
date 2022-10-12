package com.trevorwiebe.trackacow.data.local.dao

import androidx.room.*
import com.trevorwiebe.trackacow.data.entities.DrugEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface DrugDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDrug(drugEntity: DrugEntity): Long

    @Query("SELECT * FROM Drug")
    fun getDrugList(): Flow<List<DrugEntity>>

    @Query("UPDATE Drug SET defaultAmount = :defaultAmount, drugName = :drugName WHERE drugId = :drugId")
    fun updateDrugById(defaultAmount: Int, drugName: String, drugId: String)

    @Query("DELETE FROM Drug")
    fun deleteDrugTable()

    @Query("DELETE FROM Drug WHERE drugId = :drugId")
    fun deleteDrugById(drugId: String)

    @Update
    fun updateDrug(drugEntity: DrugEntity)

    @Delete
    fun deleteDrug(drugEntity: DrugEntity)

    // Deprecated
    @Deprecated("Use flow return type function")
    @Query("SELECT * FROM Drug WHERE drugId = :id")
    fun getDrugById2(id: String): DrugEntity?

    @Deprecated("Use suspend function")
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertListDrug2(drugEntities: List<DrugEntity>)

    @Deprecated("use suspend function")
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertDrug2(drugEntity: DrugEntity)

    @Deprecated("use flow return types")
    @Query("SELECT * FROM Drug")
    fun getDrugList2(): List<DrugEntity>
}