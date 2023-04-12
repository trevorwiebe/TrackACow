package com.trevorwiebe.trackacow.data.local.cacheDao

import androidx.room.*
import com.trevorwiebe.trackacow.data.cacheEntities.CacheCowEntity

@Dao
interface CacheCowDao {

    @Insert
    suspend fun insertCacheCow(cacheCowEntity: CacheCowEntity)

    // Deprecated functions
    @Deprecated("use suspend function")
    @Insert
    fun insertHoldingCow(cacheCowEntity: CacheCowEntity?)

    @Deprecated("use suspend function")
    @Insert
    fun insertHoldingCowList(cacheCowEntityList: List<CacheCowEntity?>?)

    @Deprecated("use flow function")
    @Query("SELECT * FROM cache_cow WHERE cowId = :id")
    fun getHoldingCowById(id: String?): CacheCowEntity?

    @Deprecated("use flow function")
    @Query("SELECT * FROM cache_cow")
    fun getHoldingCowEntityList(): List<CacheCowEntity?>?

    @Deprecated("use suspend function")
    @Query("DELETE FROM cache_cow")
    fun deleteHoldingCowTable()

    @Deprecated("use suspend function")
    @Update
    fun updateHoldingCow(cacheCowEntity: CacheCowEntity?)

    @Deprecated("use suspend function")
    @Delete
    fun deleteCow(cacheCowEntity: CacheCowEntity?)
}