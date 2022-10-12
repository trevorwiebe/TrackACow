package com.trevorwiebe.trackacow.data.local.repository

import com.trevorwiebe.trackacow.data.local.dao.RationDao
import com.trevorwiebe.trackacow.data.local.cacheDao.CacheRationDao
import com.trevorwiebe.trackacow.data.mapper.toCacheRationEntity
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

    override suspend fun insertRation(rationModel: RationModel) {
        rationDao.insertRation(rationModel.toRationEntity())
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

    override suspend fun deleteRationById(rationId: Int) {
        rationDao.deleteRationById(rationId)
    }

    override suspend fun insertCacheRation(cacheRationModel: CacheRationModel) {
        cacheRationDao.insertHoldingRation(cacheRationModel.toCacheRationEntity())
    }
}