package com.trevorwiebe.trackacow.domain.repository.local

import com.trevorwiebe.trackacow.domain.models.drug.CacheDrugModel
import com.trevorwiebe.trackacow.domain.models.drug.DrugModel
import kotlinx.coroutines.flow.Flow

interface DrugRepository {

    suspend fun insertDrug(drugModel: DrugModel): Long

    suspend fun insertCacheDrug(drugCacheModel: CacheDrugModel)

    suspend fun updateDrug(drugModel: DrugModel)

    suspend fun deleteDrug(drugModel: DrugModel)

    suspend fun deleteAllDrugs()

    fun getDrugList(): Flow<List<DrugModel>>

    suspend fun syncCloudDrugToDatabase(drugList: List<DrugModel>)

    // cache functions

    suspend fun getCacheDrugs(): List<CacheDrugModel>

    suspend fun deleteCacheDrugs()
}