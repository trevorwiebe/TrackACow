package com.trevorwiebe.trackacow.data.local.dao

import androidx.room.*
import com.trevorwiebe.trackacow.data.entities.CowEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CowDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCow(cowEntity: CowEntity): Long

    @Query("SELECT * FROM Cow WHERE lotId = :lotId AND isAlive = 0")
    fun getDeadCowsByLotId(lotId: String): Flow<List<CowEntity>>

    @Query("SELECT * FROM Cow WHERE lotId = :lotId")
    fun getCowsByLotId(lotId: String): Flow<List<CowEntity>>

    @Query("UPDATE Cow SET tagNumber = :tagNumber, date = :date, notes =:notes WHERE cowId = :id")
    suspend fun updateCowById(id: String?, tagNumber: Int, date: Long, notes: String?)

    @Update
    suspend fun updateCow(cowEntity: CowEntity)

    @Delete()
    suspend fun deleteCow(cowEntity: CowEntity)

    @Query("DELETE FROM cow")
    suspend fun deleteAllCows()
}