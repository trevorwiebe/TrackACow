package com.trevorwiebe.trackacow.data.local.cacheDao

import androidx.room.*
import com.trevorwiebe.trackacow.data.cacheEntities.CacheCowEntity

@Dao
interface CacheCowDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCacheCow(cacheCowEntity: CacheCowEntity)

    @Query("SELECT * FROM cache_cow")
    suspend fun getCacheCows(): List<CacheCowEntity>

    @Query("DELETE FROM cache_cow")
    suspend fun deleteCacheCows()

}