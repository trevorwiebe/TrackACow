package com.trevorwiebe.trackacow.data.local.dao

import androidx.room.*
import com.trevorwiebe.trackacow.data.entities.CowEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CowDao {

    @Query("SELECT * FROM Cow WHERE lotId = :lotId AND isAlive = 0")
    fun getDeadCowsByLotId(lotId: String): Flow<List<CowEntity>>

    @Query("SELECT * FROM Cow WHERE lotId = :lotId")
    fun getCowsByLotId(lotId: String): Flow<List<CowEntity>>

    // Deprecated

    @Deprecated("use suspend function")
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertCow(cowEntity: CowEntity?)

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
    fun updateCowById(id: String?, tagNumber: Int, date: Long, notes: String?)

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