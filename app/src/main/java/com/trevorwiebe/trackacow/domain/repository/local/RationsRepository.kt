package com.trevorwiebe.trackacow.domain.repository.local

import com.trevorwiebe.trackacow.domain.models.ration.HoldingRationModel
import com.trevorwiebe.trackacow.domain.models.ration.RationModel
import kotlinx.coroutines.flow.Flow

interface RationsRepository {

    suspend fun insertRation(rationModel: RationModel)

    fun getRations(): Flow<List<RationModel>>

    suspend fun updateRations(rationModel: RationModel)

    suspend fun deleteRationById(rationId: Int)

    suspend fun insertHoldingRation(holdingRationModel: HoldingRationModel)
}