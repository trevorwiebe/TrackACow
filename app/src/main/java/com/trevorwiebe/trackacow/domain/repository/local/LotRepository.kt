package com.trevorwiebe.trackacow.domain.repository.local

import com.trevorwiebe.trackacow.domain.models.lot.CacheLotModel
import com.trevorwiebe.trackacow.domain.models.lot.LotModel
import kotlinx.coroutines.flow.Flow

interface LotRepository {

    fun readLotsByPenId(penId: String): Flow<List<LotModel>>

    fun readLots(): Flow<List<LotModel>>

    fun readLotByLotId(lotPrimaryKey: Int): Flow<LotModel?>

    suspend fun updateLotByLotIdWithNewPenID(lotId: String, penId: String)

    // cache function
    suspend fun createCacheLot(cacheLotModel: CacheLotModel)
}