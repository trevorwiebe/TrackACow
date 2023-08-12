package com.trevorwiebe.trackacow.data.local.dao

import androidx.room.*
import com.trevorwiebe.trackacow.data.entities.CowEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CowDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCow(cowEntity: CowEntity): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertCowList(cowEntityList: List<CowEntity>): List<Long>

    @Query("SELECT * FROM cow WHERE cowId = :cowId")
    fun getCowByCowId(cowId: String): Flow<CowEntity?>

    @Query("SELECT * FROM Cow WHERE lotId = :lotId AND alive = 0")
    fun getDeadCowsByLotId(lotId: String): Flow<List<CowEntity>>

    @Query("SELECT * FROM Cow WHERE lotId = :lotId")
    fun getCowsByLotId(lotId: String): Flow<List<CowEntity>>

    @Query("UPDATE Cow SET tagNumber = :tagNumber, date = :date, notes =:notes WHERE cowId = :id")
    suspend fun updateCowById(id: String?, tagNumber: Int, date: Long, notes: String?)

    @Update
    suspend fun updateCow(cowEntity: CowEntity)

    @Query("UPDATE cow SET lotId = :lotId WHERE lotId IN (:lotIdList)")
    suspend fun updateCowWithNewLotId(lotId: String, lotIdList: List<String>)

    @Update
    suspend fun updateCowList(cowlist: List<CowEntity>)

    @Delete
    suspend fun deleteCow(cowEntity: CowEntity)

    @Query("DELETE FROM cow")
    suspend fun deleteAllCows()

    @Transaction
    suspend fun insertOrUpdate(cowList: List<CowEntity>) {
        val insertResult = insertCowList(cowList)
        val updateList = mutableListOf<CowEntity>()
        for (i in insertResult.indices) {
            if (insertResult[i] == -1L) updateList.add(cowList[i])
        }
        if (updateList.isNotEmpty()) updateCowList(cowList)
    }
}