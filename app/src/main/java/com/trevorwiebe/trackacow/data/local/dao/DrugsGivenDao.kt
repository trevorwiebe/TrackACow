package com.trevorwiebe.trackacow.data.local.dao

import androidx.room.*
import com.trevorwiebe.trackacow.data.entities.DrugsGivenEntity
import com.trevorwiebe.trackacow.data.entities.compound_entities.DrugsGivenAndDrugEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface DrugsGivenDao {

    @Query(
        "SELECT * FROM DrugsGiven " +
                "INNER JOIN drug ON drug.drugCloudDatabaseId = DrugsGiven.drugsGivenDrugId " +
                "WHERE drugsGivenLotId = :lotId"
    )
    fun getDrugsGivenAndDrugByLotId(lotId: String): Flow<List<DrugsGivenAndDrugEntity>>

    @Query("DELETE FROM DrugsGiven WHERE drugsGivenCowId = :cowId")
    suspend fun deleteDrugsGivenByCowId(cowId: String)

    @Deprecated("use suspend function")
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertDrugsGiven(drugsGivenEntity: DrugsGivenEntity?)

    @Deprecated("use suspend function")
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertDrugsGivenList(drugsGivenEntities: List<DrugsGivenEntity?>?)

    @Deprecated("use flow function")
    @Query("SELECT * FROM DrugsGiven WHERE drugsGivenCowId = :cowId")
    fun getDrugsGivenByCowId(cowId: String?): List<DrugsGivenEntity?>?

    @Deprecated("use flow function")
    @Query("SELECT * FROM DrugsGiven WHERE drugsGivenCowId IN(:cowIdList)")
    fun getDrugsGivenByCowIdList(cowIdList: List<String?>?): List<DrugsGivenEntity?>?

    @Deprecated("use flow function")
    @Query("SELECT * FROM DrugsGiven WHERE drugsGivenLotId IN(:lotIds)")
    fun getDrugsGivenByLotIds(lotIds: List<String?>?): List<DrugsGivenEntity?>?

    @Deprecated("use flow function")
    @Query("SELECT * FROM DrugsGiven WHERE drugsGivenId = :drugGivenId")
    fun getDrugGivenByDrugGivenId(drugGivenId: String?): DrugsGivenEntity?

    @Deprecated("use flow function")
    @Query("SELECT * FROM DrugsGiven WHERE (drugsGivenLotId = :lotId) AND (drugsGivenDate BETWEEN :startDate AND :endDate)")
    fun getDrugsGivenByLotIdAndDateRange(
        lotId: String?,
        startDate: Long,
        endDate: Long
    ): List<DrugsGivenEntity?>?

    @Deprecated("use suspend function")
    @Query("UPDATE DrugsGiven SET drugsGivenAmountGiven = :amountGiven WHERE drugsGivenId = :drugGivenId")
    fun updateDrugGivenAmountGiven(amountGiven: Int, drugGivenId: String?)

    @Deprecated("use suspend function")
    @Query("UPDATE DrugsGiven Set drugsGivenDate = :date WHERE drugsGivenCowId = :cowId")
    fun updateDrugsGivenDateByCowId(date: Long, cowId: String?)

    @Deprecated("use suspend function")
    @Query("DELETE FROM DrugsGiven WHERE drugsGivenLotId = :lotId")
    fun deleteDrugsGivenByLotId(lotId: String?)

    @Deprecated("use suspend function")
    @Query("DELETE FROM DrugsGiven WHERE drugsGivenCowId = :cowId")
    fun deleteDrugsGivenByCowId2(cowId: String?)

    @Deprecated("use suspend function")
    @Query("DELETE FROM DrugsGiven WHERE drugsGivenId = :drugId")
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