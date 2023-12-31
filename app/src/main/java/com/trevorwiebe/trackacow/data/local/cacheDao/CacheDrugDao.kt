package com.trevorwiebe.trackacow.data.local.cacheDao

import androidx.room.*
import com.trevorwiebe.trackacow.data.cacheEntities.CacheDrugEntity

@Dao
interface CacheDrugDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCacheDrug(cacheDrugEntity: CacheDrugEntity)

    @Query("SELECT * FROM cache_drug")
    suspend fun getCacheDrugs(): List<CacheDrugEntity>

    @Query("DELETE FROM cache_drug")
    suspend fun deleteCacheDrugs()

}