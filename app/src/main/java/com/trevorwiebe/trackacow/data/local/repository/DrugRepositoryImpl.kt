package com.trevorwiebe.trackacow.data.local.repository

import com.trevorwiebe.trackacow.data.local.cacheDao.CacheDrugDao
import com.trevorwiebe.trackacow.data.local.dao.DrugDao
import com.trevorwiebe.trackacow.data.mapper.toCacheDrugEntity
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

    override fun getDrugList(): Flow<List<DrugModel>> {
        return drugDao.getDrugList().map { drugList->
            drugList.map { it.toDrugModel() }
        }
    }

}