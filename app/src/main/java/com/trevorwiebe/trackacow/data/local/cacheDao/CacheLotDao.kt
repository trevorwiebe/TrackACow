package com.trevorwiebe.trackacow.data.local.cacheDao

import androidx.room.*
import com.trevorwiebe.trackacow.data.cacheEntities.CacheLotEntity

@Dao
interface CacheLotDao {
    @Insert
    fun insertHoldingLot(cacheLotEntity: CacheLotEntity)

    @get:Query("SELECT * FROM holdingLot")
    val holdingLotList: List<CacheLotEntity>

    @Update
    fun updateHoldingLot(cacheLotEntity: CacheLotEntity)

    @Delete
    fun deleteHoldingLot(cacheLotEntity: CacheLotEntity)

    @Query("DELETE FROM holdingLot")
    fun deleteHoldingLotTable()
}