package com.trevorwiebe.trackacow.data.local.dao

import androidx.room.*
import com.trevorwiebe.trackacow.data.entities.DrugsGivenEntity
import com.trevorwiebe.trackacow.data.entities.compound_entities.DrugsGivenAndDrugEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface DrugsGivenDao {


    @Insert(onConflict = OnConflictStrategy.REPLACE)
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

    @Query("DELETE FROM drugs_given WHERE drugsGivenCowId = :cowId")
    suspend fun deleteDrugsGivenByCowId(cowId: String)

    @Update
    suspend fun updateDrugGiven(drugsGivenEntity: DrugsGivenEntity)

    @Delete
    suspend fun deleteDrugGiven(drugsGivenEntity: DrugsGivenEntity)

    @Query("DELETE FROM drugs_given")
    suspend fun deleteAllDrugsGiven()

}