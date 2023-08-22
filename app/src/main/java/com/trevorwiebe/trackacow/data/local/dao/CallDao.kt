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
    suspend fun insertCallList(callEntities: List<CallEntity>): List<Long>

    @Query("SELECT * FROM call")
    fun getCalls(): Flow<List<CallEntity>>

    @Query("SELECT * FROM call WHERE callCloudDatabaseId = :id")
    fun getCallById(id: String): Flow<CallEntity>

    @Query(
        "SELECT * FROM call " +
                "LEFT JOIN ration ON ration.rationPrimaryKey == call.callRationId " +
                "WHERE date BETWEEN :dateStart AND :dateEnd AND lotId = :lotId LIMIT 1"
    )
    fun getCallAndRationByDateAndLotId(
        lotId: String,
        dateStart: Long,
        dateEnd: Long
    ): Flow<CallAndRationEntity?>

    @Query(
        "SELECT * FROM call " +
                "LEFT JOIN ration ON ration.rationPrimaryKey == call.callRationId " +
                "WHERE lotId = :lotId"
    )
    fun getCallsAndRationByLotId(lotId: String): Flow<List<CallAndRationEntity>>

    @Query("DELETE FROM call")
    suspend fun deleteCallTable()

    @Query(
        "UPDATE call " +
                "SET callAmount = :callAmount, callRationId = :rationId " +
                "WHERE callCloudDatabaseId = :callCloudId"
    )
    suspend fun updateCallAmount(callAmount: Int, rationId: Int?, callCloudId: String)

    @Query("UPDATE call SET lotId = :lotId WHERE lotId IN (:oldLotIds)")
    suspend fun updateCallsWithNewLotId(lotId: String, oldLotIds: List<String>)

    @Update
    suspend fun updateCallList(callList: List<CallEntity>)

    @Delete
    suspend fun deleteCall(callEntity: CallEntity?)

    @Query("DELETE FROM call WHERE lotId = :lotId")
    suspend fun deleteCallsByLotId(lotId: String)

    @Query("DELETE FROM call WHERE lotId = :lotId AND date BETWEEN :startDate AND :endDate")
    suspend fun deleteCallsByLotIdAndDate(lotId: String, startDate: Long, endDate: Long)

    @Transaction
    suspend fun syncCloudCallsWithLotId(callList: List<CallEntity>, lotId: String) {
        deleteCallsByLotId(lotId)
        insertCallList(callList)
    }

    @Transaction
    suspend fun syncCloudCallsWithLotIdAndDate(
        callList: List<CallEntity>,
        lotId: String,
        startDate: Long,
        endDate: Long
    ) {
        deleteCallsByLotIdAndDate(lotId, startDate, endDate)
        insertCallList(callList)
    }
}