package com.trevorwiebe.trackacow.data.local.cacheDao

import androidx.room.*
import com.trevorwiebe.trackacow.data.cacheEntities.CacheDrugEntity

@Dao
interface CacheDrugDao {

    @Insert
    suspend fun insertCacheDrug(cacheDrugEntity: CacheDrugEntity)

    @Insert
    fun insertListHoldingDrug(holdingDrugEntities: List<CacheDrugEntity>)

    @Query("SELECT * FROM cache_drug WHERE drugCloudDatabaseId = :id")
    fun getHoldingDrugById(id: String): CacheDrugEntity?

    @Query("SELECT * FROM cache_drug")
    fun getHoldingDrugList(): List<CacheDrugEntity>

    @Query("DELETE FROM cache_drug WHERE drugCloudDatabaseId = :drugId")
    fun deleteHoldingDrugById(drugId: String)

    @Query("DELETE FROM cache_drug")
    fun deleteHoldingDrugTable()

    @Update
    fun updateDrug(cacheDrugEntity: CacheDrugEntity)

    @Delete
    fun deleteDrug(cacheDrugEntity: CacheDrugEntity)

    // Deprecated
    @Deprecated("Use suspend function")
    @Insert
    fun insertCacheDrug2(cacheDrugEntity: CacheDrugEntity)
}