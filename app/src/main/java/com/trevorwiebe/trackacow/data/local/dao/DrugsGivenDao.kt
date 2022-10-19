package com.trevorwiebe.trackacow.data.local.dao

import androidx.room.*
import com.trevorwiebe.trackacow.data.entities.DrugsGivenEntity

@Dao
interface DrugsGivenDao {

    @Deprecated("use suspend function")
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertDrugsGiven(drugsGivenEntity: DrugsGivenEntity?)

    @Deprecated("use suspend function")
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertDrugsGivenList(drugsGivenEntities: List<DrugsGivenEntity?>?)

    @Deprecated("use flow function")
    @Query("SELECT * FROM DrugsGiven WHERE cowId = :cowId")
    fun getDrugsGivenByCowId(cowId: String?): List<DrugsGivenEntity?>?

    @Deprecated("use flow function")
    @Query("SELECT * FROM DrugsGiven WHERE cowId IN(:cowIdList)")
    fun getDrugsGivenByCowIdList(cowIdList: List<String?>?): List<DrugsGivenEntity?>?

    @Deprecated("use flow function")
    @Query("SELECT * FROM DrugsGiven WHERE lotId IN(:lotIds)")
    fun getDrugsGivenByLotIds(lotIds: List<String?>?): List<DrugsGivenEntity?>?

    @Deprecated("use flow function")
    @Query("SELECT * FROM DrugsGiven WHERE drugGivenId = :drugGivenId")
    fun getDrugGivenByDrugGivenId(drugGivenId: String?): DrugsGivenEntity?

    @Deprecated("use flow function")
    @Query("SELECT * FROM DrugsGiven WHERE (lotId = :lotId) AND (date BETWEEN :startDate AND :endDate)")
    fun getDrugsGivenByLotIdAndDateRange(
        lotId: String?,
        startDate: Long,
        endDate: Long
    ): List<DrugsGivenEntity?>?

    @Deprecated("use suspend function")
    @Query("UPDATE DrugsGiven SET amountGiven = :amountGiven WHERE drugGivenId = :drugGivenId")
    fun updateDrugGivenAmountGiven(amountGiven: Int, drugGivenId: String?)

    @Deprecated("use suspend function")
    @Query("UPDATE DrugsGiven Set date = :date WHERE cowId = :cowId")
    fun updateDrugsGivenDateByCowId(date: Long, cowId: String?)

    @Deprecated("use suspend function")
    @Query("DELETE FROM DrugsGiven WHERE lotId = :lotId")
    fun deleteDrugsGivenByLotId(lotId: String?)

    @Deprecated("use suspend function")
    @Query("DELETE FROM DrugsGiven WHERE cowId = :cowId")
    fun deleteDrugsGivenByCowId(cowId: String?)

    @Deprecated("use suspend function")
    @Query("DELETE FROM DrugsGiven WHERE drugGivenId = :drugId")
    fun deleteDrugGivenById(drugId: String?)

    @Deprecated("use suspend function")
    @Query("DELETE FROM DrugsGiven")
    fun deleteDrugsGivenTable()

    @Deprecated("use suspend function")
    @Update
    fun updateDrugsGiven(drugsGivenEntity: DrugsGivenEntity?)

    @Deprecated("use suspend function")
    @Delete
    fun deleteDrugsGiven(drugsGivenEntity: DrugsGivenEntity?)
}