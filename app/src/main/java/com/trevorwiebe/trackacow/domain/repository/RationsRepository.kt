package com.trevorwiebe.trackacow.domain.repository

import com.trevorwiebe.trackacow.domain.models.HoldingRationModel
import com.trevorwiebe.trackacow.domain.models.RationModel
import kotlinx.coroutines.flow.Flow

interface RationsRepository {

    suspend fun insertRation(rationModel: RationModel)

    fun getRations(): Flow<List<RationModel>>

    suspend fun updateRations(rationModel: RationModel)

    suspend fun deleteRationById(rationId: Int)

    suspend fun insertHoldingRation(holdingRationModel: HoldingRationModel)
}