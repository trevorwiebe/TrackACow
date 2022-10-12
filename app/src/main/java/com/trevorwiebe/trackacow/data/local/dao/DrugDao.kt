package com.trevorwiebe.trackacow.data.local.dao

import androidx.room.*
import com.trevorwiebe.trackacow.data.entities.DrugEntity

@Dao
interface DrugDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertDrug(drugEntity: DrugEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertListDrug(drugEntities: List<DrugEntity>)

    @Query("SELECT * FROM Drug WHERE drugId = :id")
    fun getDrugById(id: String): DrugEntity?

    @get:Query("SELECT * FROM Drug")
    val drugList: List<DrugEntity>

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
}