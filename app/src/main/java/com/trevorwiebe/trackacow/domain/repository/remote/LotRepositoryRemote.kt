package com.trevorwiebe.trackacow.domain.repository.remote

import com.trevorwiebe.trackacow.domain.models.lot.CacheLotModel
import com.trevorwiebe.trackacow.domain.models.lot.LotModel
import com.trevorwiebe.trackacow.domain.models.pen.PenModel
import kotlinx.coroutines.flow.Flow

interface LotRepositoryRemote {

    fun archiveLot(lotModel: LotModel)

    fun updateLotWithNewPenIdRemote(lotId: String, penId: String)

    fun updateLotWithRationId(rationId: String, lotId: String)

    fun insertCacheLotRemote(lotList: List<CacheLotModel>)

    fun insertAndUpdateLotRemote(lotModel: LotModel)

    fun readPenAndLotsIncludeEmptyPens(): Flow<Pair<List<PenModel>, List<LotModel>>>

    fun readArchivedLots(): Flow<List<LotModel>>

    fun readLots(): Flow<List<LotModel>>

    fun readLotByLotId(lotId: String): Flow<LotModel>

    fun deleteLotRemote(lotModel: LotModel)

    fun deleteLotsByIdRemote(lotIdList: List<String>)

}