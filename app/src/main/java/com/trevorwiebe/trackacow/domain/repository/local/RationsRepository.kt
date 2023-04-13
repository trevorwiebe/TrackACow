package com.trevorwiebe.trackacow.domain.repository.local

import com.trevorwiebe.trackacow.domain.models.ration.CacheRationModel
import com.trevorwiebe.trackacow.domain.models.ration.RationModel
import kotlinx.coroutines.flow.Flow

interface RationsRepository {

    suspend fun insertRation(rationModel: RationModel): Long

    fun getRations(): Flow<List<RationModel>>

    suspend fun updateRations(rationModel: RationModel)

    suspend fun deleteRationById(rationModel: RationModel)

    suspend fun deleteAllRations()

    suspend fun insertCacheRation(cacheRationModel: CacheRationModel)
}