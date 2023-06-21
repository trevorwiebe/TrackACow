package com.trevorwiebe.trackacow.data.local.cacheDao

import androidx.room.*
import com.trevorwiebe.trackacow.data.cacheEntities.CacheDrugEntity

@Dao
interface CacheDrugDao {

    @Insert
    suspend fun insertCacheDrug(cacheDrugEntity: CacheDrugEntity)

    @Query("SELECT * FROM cache_drug")
    suspend fun getCacheDrugs(): List<CacheDrugEntity>


}