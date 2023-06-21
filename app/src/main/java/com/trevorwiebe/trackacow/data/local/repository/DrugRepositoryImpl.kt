package com.trevorwiebe.trackacow.data.local.repository

import com.trevorwiebe.trackacow.data.local.cacheDao.CacheDrugDao
import com.trevorwiebe.trackacow.data.local.dao.DrugDao
import com.trevorwiebe.trackacow.data.mapper.toCacheDrugEntity
import com.trevorwiebe.trackacow.data.mapper.toCacheDrugModel
import com.trevorwiebe.trackacow.data.mapper.toDrugEntity
import com.trevorwiebe.trackacow.data.mapper.toDrugModel
import com.trevorwiebe.trackacow.domain.models.drug.CacheDrugModel
import com.trevorwiebe.trackacow.domain.models.drug.DrugModel
import com.trevorwiebe.trackacow.domain.repository.local.DrugRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class DrugRepositoryImpl(
    private val drugDao: DrugDao,
    private val cacheDrugDao: CacheDrugDao
): DrugRepository {

    override suspend fun insertDrug(drugModel: DrugModel): Long {
        return drugDao.insertDrug(drugModel.toDrugEntity())
    }

    override suspend fun insertCacheDrug(drugCacheModel: CacheDrugModel) {
        cacheDrugDao.insertCacheDrug(drugCacheModel.toCacheDrugEntity())
    }

    override suspend fun updateDrug(drugModel: DrugModel) {
        drugDao.updateDrug(drugModel.drugName, drugModel.defaultAmount, drugModel.drugPrimaryKey)
    }

    override suspend fun deleteDrug(drugModel: DrugModel) {
        drugDao.deleteDrug(drugModel.toDrugEntity())
    }

    override suspend fun deleteAllDrugs() {
        drugDao.deleteAllDrugs()
    }

    override fun getDrugList(): Flow<List<DrugModel>> {
        return drugDao.getDrugList().map { drugList ->
            drugList.map { it.toDrugModel() }
        }
    }

    override suspend fun insertOrUpdateDrugList(drugList: List<DrugModel>) {
        drugDao.insertOrUpdate(drugList.map { it.toDrugEntity() })
    }

    override suspend fun getCacheDrugs(): List<CacheDrugModel> {
        return cacheDrugDao.getCacheDrugs().map { it.toCacheDrugModel() }
    }

}