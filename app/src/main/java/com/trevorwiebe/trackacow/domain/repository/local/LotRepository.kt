package com.trevorwiebe.trackacow.domain.repository.local

import com.trevorwiebe.trackacow.domain.models.lot.CacheLotModel
import com.trevorwiebe.trackacow.domain.models.lot.LotModel
import kotlinx.coroutines.flow.Flow

interface LotRepository {

    suspend fun createLot(lotModel: LotModel)

    fun readLotsByPenId(penId: String): Flow<List<LotModel>>

    suspend fun archiveLot(lotModel: LotModel)

    fun readArchivedLots(): Flow<List<LotModel>>

    fun readLots(): Flow<List<LotModel>>

    fun readLotByLotId(lotPrimaryKey: Int): Flow<LotModel?>

    suspend fun updateLotByLotIdWithNewPenID(lotId: String, penId: String)

    suspend fun updateLot(lotModel: LotModel)

    suspend fun deleteLot(lotModel: LotModel)

    // cache function
    suspend fun createCacheLot(cacheLotModel: CacheLotModel)
}