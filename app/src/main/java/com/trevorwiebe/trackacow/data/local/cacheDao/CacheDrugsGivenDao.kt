package com.trevorwiebe.trackacow.data.local.cacheDao

import androidx.room.*
import com.trevorwiebe.trackacow.data.cacheEntities.CacheDrugsGivenEntity

@Dao
interface CacheDrugsGivenDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCacheDrugGiven(cacheDrugsGivenEntity: CacheDrugsGivenEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCacheDrugGivenList(cacheDrugGivenList: List<CacheDrugsGivenEntity>)

    @Deprecated("use suspend function")
    @Insert
    fun insertHoldingDrugsGiven(cacheDrugsGivenEntity: CacheDrugsGivenEntity?)

    @Deprecated("use suspend function")
    @Insert
    fun insertHoldingDrugsGivenList(holdingDrugsGivenEntities: List<CacheDrugsGivenEntity?>?)

    @Deprecated("use flow function")
    @Query("SELECT * FROM cache_drugs_given WHERE cowId = :cowId")
    fun getHoldingDrugsGivenByCowId(cowId: String?): List<CacheDrugsGivenEntity?>?

    @Deprecated("use flow function")
    @Query("SELECT * FROM cache_drugs_given")
    fun getHoldingDrugsGivenList(): List<CacheDrugsGivenEntity?>?

    @Deprecated("use suspend function")
    @Query("DELETE FROM cache_drugs_given")
    fun deleteHoldingDrugsGivenTable()

    @Deprecated("use suspend function")
    @Update
    fun updateHoldingDrugsGiven(cacheDrugsGivenEntity: CacheDrugsGivenEntity?)

    @Deprecated("use suspend function")
    @Delete
    fun deleteHoldingDrugsGiven(cacheDrugsGivenEntity: CacheDrugsGivenEntity?)
}