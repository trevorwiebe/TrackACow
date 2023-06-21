package com.trevorwiebe.trackacow.data.local.repository

import com.trevorwiebe.trackacow.data.local.dao.RationDao
import com.trevorwiebe.trackacow.data.local.cacheDao.CacheRationDao
import com.trevorwiebe.trackacow.data.mapper.toCacheRationEntity
import com.trevorwiebe.trackacow.data.mapper.toCacheRationModel
import com.trevorwiebe.trackacow.data.mapper.toRationEntity
import com.trevorwiebe.trackacow.data.mapper.toRationModel
import com.trevorwiebe.trackacow.domain.models.ration.CacheRationModel
import com.trevorwiebe.trackacow.domain.models.ration.RationModel
import com.trevorwiebe.trackacow.domain.repository.local.RationsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class RationRepositoryImpl(
    private val rationDao: RationDao,
    private val cacheRationDao: CacheRationDao
): RationsRepository {

    override suspend fun insertRation(rationModel: RationModel): Long {
        return rationDao.insertRation(rationModel.toRationEntity())
    }

    override fun getRations(): Flow<List<RationModel>> {
        return rationDao.getRations()
            .map { rationEntities ->
                rationEntities.map { it.toRationModel() }
            }
    }

    override suspend fun updateRations(rationModel: RationModel) {
        rationDao.updateRation(rationModel.toRationEntity())
    }

    override suspend fun deleteRationById(rationModel: RationModel) {
        rationDao.deleteRationById(rationModel.rationPrimaryKey)
    }

    override suspend fun deleteAllRations() {
        rationDao.deleteAllRations()
    }

    override suspend fun insertCacheRation(cacheRationModel: CacheRationModel) {
        cacheRationDao.insertHoldingRation(cacheRationModel.toCacheRationEntity())
    }

    override suspend fun insertOrUpdateRationList(rationList: List<RationModel>) {
        rationDao.insertOrUpdateRationList(rationList.map { it.toRationEntity() })
    }

    override suspend fun getCacheRations(): List<CacheRationModel> {
        return cacheRationDao.getCacheRations().map { it.toCacheRationModel() }
    }
}