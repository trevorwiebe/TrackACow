package com.trevorwiebe.trackacow.data.local.cacheDao

import androidx.room.*
import com.trevorwiebe.trackacow.data.cacheEntities.CacheDrugsGivenEntity

@Dao
interface CacheDrugsGivenDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCacheDrugGiven(cacheDrugsGivenEntity: CacheDrugsGivenEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCacheDrugGivenList(cacheDrugGivenList: List<CacheDrugsGivenEntity>)

    @Query("SELECT * FROM cache_drugs_given")
    suspend fun getCacheDrugGiven(): List<CacheDrugsGivenEntity>

    @Query("DELETE FROM cache_drugs_given")
    suspend fun deleteCacheDrugGiven()
}