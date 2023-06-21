package com.trevorwiebe.trackacow.domain.repository.remote

import com.trevorwiebe.trackacow.domain.models.load.CacheLoadModel
import com.trevorwiebe.trackacow.domain.models.load.LoadModel

interface LoadRemoteRepository {

    fun insertOrUpdateRemoteLoad(loadModel: LoadModel)
    fun deleteRemoteLoad(loadModel: LoadModel)

    fun insertCacheLoadsRemote(loadList: List<CacheLoadModel>)

    suspend fun updateLoadWithNewLotIdRemote(lotIdToSave: String, lotIdsToDelete: List<String>)
}