package com.trevorwiebe.trackacow.domain.repository.local

import com.trevorwiebe.trackacow.domain.models.lot.LotModel
import kotlinx.coroutines.flow.Flow

interface LotRepository {

    fun readLotsByPenId(penId: String): Flow<List<LotModel>>
}