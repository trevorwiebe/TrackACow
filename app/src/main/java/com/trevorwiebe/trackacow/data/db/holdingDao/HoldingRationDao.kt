package com.trevorwiebe.trackacow.data.db.holdingDao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.trevorwiebe.trackacow.data.db.holdingUpdateEntities.HoldingRationEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface HoldingRationDao {

    @Insert
    suspend fun insertHoldingRation(holdingRationEntity: HoldingRationEntity)

    @Query("SELECT * FROM holding_ration")
    fun getHoldingRations(): Flow<List<HoldingRationEntity>>

    @Delete
    suspend fun deleteHoldingRation(holdingRationEntity: HoldingRationEntity)

    @Query("DELETE FROM holding_ration")
    suspend fun deleteHoldingRationTable()
}