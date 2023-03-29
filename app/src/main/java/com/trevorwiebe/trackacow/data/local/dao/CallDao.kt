package com.trevorwiebe.trackacow.data.local.dao

import androidx.room.*
import com.trevorwiebe.trackacow.data.entities.CallEntity
import com.trevorwiebe.trackacow.data.entities.compound_entities.CallAndRationEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CallDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCall(callEntity: CallEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCallList(callEntities: List<CallEntity>)

    @Query("SELECT * FROM call")
    fun getCalls(): Flow<List<CallEntity>>

    @Query("SELECT * FROM call WHERE callCloudDatabaseId = :id")
    fun getCallById(id: String): Flow<CallEntity>

    @Query("SELECT * FROM call " +
            "LEFT JOIN ration ON ration.rationPrimaryKey == call.callRationId " +
            "WHERE date = :date AND lotId = :lotId LIMIT 1")
    fun getCallAndRationByDateAndLotId(date: Long, lotId: String): Flow<CallAndRationEntity?>

    @Query("SELECT * FROM call " +
            "LEFT JOIN ration ON ration.rationPrimaryKey == call.callRationId " +
            "WHERE lotId = :lotId")
    fun getCallsAndRationByLotId(lotId: String): Flow<List<CallAndRationEntity>>

    @Query("DELETE FROM call")
    suspend fun deleteCallTable()

    @Query(
        "UPDATE call " +
                "SET callAmount = :callAmount, callRationId = :rationId " +
                "WHERE callCloudDatabaseId = :callCloudId"
    )
    suspend fun updateCallAmount(callAmount: Int, rationId: Int?, callCloudId: String)

    @Delete
    suspend fun deleteCall(callEntity: CallEntity?)
}