package com.trevorwiebe.trackacow.domain.repository.remote

import com.trevorwiebe.trackacow.domain.models.ration.CacheRationModel
import com.trevorwiebe.trackacow.domain.models.ration.RationModel
import kotlinx.coroutines.flow.Flow

interface RationRepositoryRemote {
    suspend fun insertOrUpdateRationRemote(rationModel: RationModel)

    suspend fun insertRationListRemote(rationModelList: List<RationModel>)

    suspend fun insertCacheRationRemote(rationList: List<CacheRationModel>)

    fun readAllRations(): Flow<List<RationModel>>

    suspend fun deleteRationRemote(rationModel: RationModel)
}