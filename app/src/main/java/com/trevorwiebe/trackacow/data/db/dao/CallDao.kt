package com.trevorwiebe.trackacow.data.db.dao

import androidx.room.*
import com.trevorwiebe.trackacow.data.db.entities.CallEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CallDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCall(callEntity: CallEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCallList(callEntities: List<CallEntity>)

    @Query("SELECT * FROM call")
    fun getCalls(): Flow<List<CallEntity>>

    @Query("SELECT * FROM call WHERE id = :id")
    fun getCallById(id: String): Flow<CallEntity>

    @Query("SELECT * FROM call WHERE date = :date AND lotId = :lotId LIMIT 1")
    fun getCallByDateAndLotId(date: Long, lotId: String): Flow<CallEntity>

    @Query("SELECT * FROM call WHERE lotId = :lotId")
    fun getCallByLotId(lotId: String): Flow<List<CallEntity>>

    @Query("DELETE FROM call")
    suspend fun deleteCallTable()

    @Update
    suspend fun updateCall(callEntity: CallEntity)

    @Delete
    suspend fun deleteCall(callEntity: CallEntity?)
}