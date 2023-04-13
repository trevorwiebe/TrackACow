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

    // Deprecated

    @Deprecated("use suspend function")
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertCow2(cowEntity: CowEntity?)

    @Deprecated("use suspend function")
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertCowList(cowEntityList: List<CowEntity?>?)

    @Deprecated("use flow function")
    @Query("SELECT * FROM Cow WHERE cowId = :id")
    fun getCowById(id: String?): CowEntity?

    @Deprecated("use flow function")
    @Query("SELECT * FROM Cow WHERE lotId IN(:ids)")
    fun getCowEntitiesByLotIds(ids: List<String?>?): List<CowEntity?>?

    @Deprecated("use flow function")
    @Query("SELECT * FROM Cow WHERE lotId IN(:ids) AND isAlive = 0")
    fun getDeadCowEntitiesByLotIds(ids: List<String?>?): List<CowEntity?>?

    @Deprecated("use suspend function")
    @Query("UPDATE Cow SET tagNumber = :tagNumber, date = :date, notes =:notes WHERE cowId = :id")
    fun updateCowById2(id: String?, tagNumber: Int, date: Long, notes: String?)

    @Deprecated("use suspend function")
    @Query("DELETE FROM Cow WHERE lotId = :lotId")
    fun deleteCowsByLotId(lotId: String?)

    @Deprecated("use suspend function")
    @Query("DELETE FROM cow")
    fun deleteCowTable()

    @Deprecated("use suspend function")
    @Update
    fun updateCow(cowEntity: CowEntity?)

    @Deprecated("use suspend function")
    @Delete
    fun deleteCow(cowEntity: CowEntity?)
}