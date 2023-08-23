package com.trevorwiebe.trackacow.data.local.repository

import com.trevorwiebe.trackacow.data.local.cacheDao.CacheLoadDao
import com.trevorwiebe.trackacow.data.local.dao.LoadDao
import com.trevorwiebe.trackacow.data.mapper.toCacheLoadEntity
import com.trevorwiebe.trackacow.data.mapper.toCacheLoadModel
import com.trevorwiebe.trackacow.data.mapper.toLoadEntity
import com.trevorwiebe.trackacow.data.mapper.toLoadModel
import com.trevorwiebe.trackacow.domain.models.load.CacheLoadModel
import com.trevorwiebe.trackacow.domain.models.load.LoadModel
import com.trevorwiebe.trackacow.domain.repository.local.LoadRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class LoadRepositoryImpl(
    private val loadDao: LoadDao,
    private val cacheLoadDao: CacheLoadDao
): LoadRepository {
    override suspend fun insertLoad(loadModel: LoadModel): Long {
        return loadDao.insertLoad(loadModel.toLoadEntity())
    }

    override fun readLoadsByLotId(lotId: String): Flow<List<LoadModel>> {
        return loadDao.readLoadsByLotId(lotId).map { loadList ->
            loadList.map { it.toLoadModel() }
        }
    }

    override suspend fun insertCacheLoad(cacheLoadModel: CacheLoadModel) {
        cacheLoadDao.insertCacheLoad(cacheLoadModel.toCacheLoadEntity())
    }

    override suspend fun updateLoad(loadModel: LoadModel) {
        loadDao.updateLoad(
            loadModel.primaryKey,
            loadModel.numberOfHead,
            loadModel.date,
            loadModel.description,
            loadModel.lotId,
            loadModel.loadId
        )
    }

    override suspend fun updateLoadWithNewLot(lotId: String, oldLotIdList: List<String>) {
        loadDao.updateLoadWithNewLotId(lotId, oldLotIdList)
    }

    override suspend fun deleteLoad(loadModel: LoadModel) {
        loadDao.deleteLoad(loadModel.toLoadEntity())
    }

    override suspend fun deleteAllLoads() {
        loadDao.deleteAllLoads()
    }

    override suspend fun syncCloudLoadByLotId(loadList: List<LoadModel>, lotId: String) {
        loadDao.syncCloudLoadsByLotId(loadList.map { it.toLoadEntity() }, lotId)
    }

    override suspend fun getCacheLoads(): List<CacheLoadModel> {
        return cacheLoadDao.getCacheLoads().map { it.toCacheLoadModel() }
    }

    override suspend fun deleteCacheLoads() {
        cacheLoadDao.deleteCacheLoads()
    }

}