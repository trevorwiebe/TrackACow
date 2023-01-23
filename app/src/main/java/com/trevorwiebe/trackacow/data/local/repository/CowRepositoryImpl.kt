package com.trevorwiebe.trackacow.data.local.repository

import com.trevorwiebe.trackacow.data.local.cacheDao.CacheCowDao
import com.trevorwiebe.trackacow.data.local.dao.CowDao
import com.trevorwiebe.trackacow.data.mapper.toCacheCowEntity
import com.trevorwiebe.trackacow.data.mapper.toCowEntity
import com.trevorwiebe.trackacow.data.mapper.toCowModel
import com.trevorwiebe.trackacow.domain.models.cow.CacheCowModel
import com.trevorwiebe.trackacow.domain.models.cow.CowModel
import com.trevorwiebe.trackacow.domain.repository.local.CowRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

data class CowRepositoryImpl(
    private val cowDao: CowDao,
    private val cacheCowDao: CacheCowDao
): CowRepository{

    override fun getDeadCowsByLotId(lotId: String): Flow<List<CowModel>> {
        return cowDao.getDeadCowsByLotId(lotId).map { deadCowList ->
            deadCowList.map { it.toCowModel() }
        }
    }

    override fun getCowsByLotId(lotId: String): Flow<List<CowModel>> {
        return cowDao.getCowsByLotId(lotId).map { cowList ->
            cowList.map { it.toCowModel() }
        }
    }

    override suspend fun updateCow(cowModel: CowModel) {
        cowDao.updateCow(cowModel.toCowEntity())
    }

    override suspend fun deleteCow(cowModel: CowModel) {
        cowDao.deleteCow(cowModel.toCowEntity())
    }

    override suspend fun insertCacheCow(cacheCowModel: CacheCowModel) {
        cacheCowDao.insertCacheCow(cacheCowModel.toCacheCowEntity())
    }
}
