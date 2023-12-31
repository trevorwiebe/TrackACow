package com.trevorwiebe.trackacow.domain.repository.local

import com.trevorwiebe.trackacow.domain.models.compound_model.DrugsGivenAndDrugModel
import com.trevorwiebe.trackacow.domain.models.drug_given.CacheDrugGivenModel
import com.trevorwiebe.trackacow.domain.models.drug_given.DrugGivenModel
import kotlinx.coroutines.flow.Flow

interface DrugsGivenRepository {

    suspend fun createDrugsGivenList(drugGivenList: List<DrugGivenModel>): List<Long>

    fun getDrugsGivenAndDrugs(lotId: String): Flow<List<DrugsGivenAndDrugModel>>

    fun getDrugsGivenAndDrugsByLotIdAndDate(lotId: String, startDate: Long, endDate: Long):
            Flow<List<DrugsGivenAndDrugModel>>

    fun getDrugsGivenAndDrugsByCowId(cowIdList: List<String>): Flow<List<DrugsGivenAndDrugModel>>

    suspend fun editDrugsGiven(drugGivenModel: DrugGivenModel)

    suspend fun updateDrugsGivenWithNewLot(lotId: String, oldLotIds: List<String>)

    suspend fun deleteDrugsGivenByCowIdTransaction(cowId: String): List<DrugGivenModel>

    suspend fun deleteDrugGiven(drugGivenModel: DrugGivenModel)

    suspend fun deleteAllDrugsGiven()

    suspend fun syncCloudDrugsGivenToDatabaseByCowId(
        drugGivenList: List<DrugGivenModel>,
        cowId: String
    )

    suspend fun syncCloudDrugsGivenToDatabaseByLotId(
        drugGivenList: List<DrugGivenModel>,
        lotId: String
    )

    suspend fun syncCloudDrugsGivenToDatabaseByLotIdAndDate(
        drugGivenList: List<DrugGivenModel>,
        lotId: String,
        startDate: Long,
        endDate: Long
    )

    // cache functions

    suspend fun insertCacheDrugGiven(cacheDrugGivenModel: CacheDrugGivenModel)

    suspend fun createCacheDrugsGivenList(cacheDrugsGivenList: List<CacheDrugGivenModel>)

    suspend fun getCacheDrugsGiven(): List<CacheDrugGivenModel>

    suspend fun deleteCacheDrugsGiven()
}