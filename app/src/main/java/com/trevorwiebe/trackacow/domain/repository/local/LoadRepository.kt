package com.trevorwiebe.trackacow.domain.repository.local

import com.trevorwiebe.trackacow.domain.models.load.CacheLoadModel
import com.trevorwiebe.trackacow.domain.models.load.LoadModel
import kotlinx.coroutines.flow.Flow

interface LoadRepository {

    suspend fun insertLoad(loadModel: LoadModel): Long

    fun readLoadsByLotId(lotId: String): Flow<List<LoadModel>>

    suspend fun insertCacheLoad(cacheLoadModel: CacheLoadModel)

    suspend fun updateLoad(loadModel: LoadModel)

    suspend fun updateLoadWithNewLot(lotId: String, oldLotIdList: List<String>)

    suspend fun deleteLoad(loadModel: LoadModel)

    suspend fun deleteAllLoads()

    suspend fun syncCloudLoadByLotId(loadList: List<LoadModel>, lotId: String)

    // cache functions

    suspend fun getCacheLoads(): List<CacheLoadModel>

    suspend fun deleteCacheLoads()
}