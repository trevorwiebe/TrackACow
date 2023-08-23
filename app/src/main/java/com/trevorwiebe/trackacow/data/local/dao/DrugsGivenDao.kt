package com.trevorwiebe.trackacow.data.local.dao

import androidx.room.*
import com.trevorwiebe.trackacow.data.entities.DrugsGivenEntity
import com.trevorwiebe.trackacow.data.entities.compound_entities.DrugsGivenAndDrugEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface DrugsGivenDao {


    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertDrugsGivenList(drugsGivenList: List<DrugsGivenEntity>): List<Long>

    @Query(
            "SELECT * FROM drugs_given " +
                    "INNER JOIN drug ON drug.drugCloudDatabaseId = drugs_given.drugsGivenDrugId " +
                    "WHERE drugsGivenLotId = :lotId"
    )
    fun getDrugsGivenAndDrugByLotId(lotId: String): Flow<List<DrugsGivenAndDrugEntity>>

    @Query(
            "SELECT * FROM drugs_given " +
                    "INNER JOIN drug ON drug.drugCloudDatabaseId = drugs_given.drugsGivenDrugId " +
                    "WHERE drugsGivenLotId = :lotId AND drugsGivenDate BETWEEN :startDate AND :endDate"
    )
    fun getDrugsGivenAndDrugByLotIdAndDate(lotId: String, startDate: Long, endDate: Long):
            Flow<List<DrugsGivenAndDrugEntity>>

    @Query(
            "SELECT * FROM drugs_given " +
                    "INNER JOIN drug ON drug.drugCloudDatabaseId = drugs_given.drugsGivenDrugId " +
                    "WHERE drugsGivenCowId IN (:cowIdList)"
    )
    fun getDrugsGivenAndDrugByCowId(cowIdList: List<String>): Flow<List<DrugsGivenAndDrugEntity>>

    @Query("SELECT * FROM drugs_given WHERE drugsGivenCowId = :cowId")
    suspend fun getDrugsGivenByCowId(cowId: String): List<DrugsGivenEntity>

    @Query("DELETE FROM drugs_given WHERE drugsGivenCowId = :cowId")
    suspend fun deleteDrugsGivenByCowId(cowId: String)

    @Update
    suspend fun updateDrugGiven(drugsGivenEntity: DrugsGivenEntity)

    @Query("UPDATE drugs_given SET drugsGivenLotId =:lotId WHERE drugsGivenLotId IN (:oldLotIds)")
    suspend fun updateDrugsGivenWithNewLotId(lotId: String, oldLotIds: List<String>)

    @Update
    suspend fun updateDrugsGivenList(drugsGivenList: List<DrugsGivenEntity>)

    @Delete
    suspend fun deleteDrugGiven(drugsGivenEntity: DrugsGivenEntity)

    @Query("DELETE FROM drugs_given")
    suspend fun deleteAllDrugsGiven()

    @Query("DELETE FROM drugs_given WHERE drugsGivenLotId = :lotId")
    suspend fun deleteDrugsGivenByLotId(lotId: String)

    @Query("DELETE FROM drugs_given WHERE drugsGivenLotId = :lotId AND drugsGivenDate BETWEEN :startDate AND :endDate")
    suspend fun deleteDrugsGivenByLotIdAndDate(lotId: String, startDate: Long, endDate: Long)

    @Transaction
    suspend fun deleteDrugsGivenByCowIdTransaction(cowId: String): List<DrugsGivenEntity> {
        val drugsGivenList = getDrugsGivenByCowId(cowId)
        deleteDrugsGivenByCowId(cowId)
        return drugsGivenList
    }

    @Transaction
    suspend fun syncCloudDrugsGivenByCowId(drugsGivenList: List<DrugsGivenEntity>, cowId: String) {
        deleteDrugsGivenByCowId(cowId)
        insertDrugsGivenList(drugsGivenList)
    }

    @Transaction
    suspend fun syncCloudDrugsGivenByLotId(drugsGivenList: List<DrugsGivenEntity>, lotId: String) {
        deleteDrugsGivenByLotId(lotId)
        insertDrugsGivenList(drugsGivenList)
    }

    @Transaction
    suspend fun syncCloudDrugsGivenByLotIdAndDate(
        drugsGivenList: List<DrugsGivenEntity>,
        lotId: String,
        startDate: Long,
        endDate: Long
    ) {
        deleteDrugsGivenByLotIdAndDate(lotId, startDate, endDate)
        insertDrugsGivenList(drugsGivenList)
    }

}