package com.trevorwiebe.trackacow.domain.repository.remote

import com.trevorwiebe.trackacow.domain.models.lot.CacheLotModel
import com.trevorwiebe.trackacow.domain.models.lot.LotModel

interface LotRepositoryRemote {

    fun archiveLot(lotModel: LotModel)

    fun updateLotWithNewPenIdRemote(lotId: String, penId: String)

    fun insertCacheLotRemote(lotList: List<CacheLotModel>)

    fun insertAndUpdateLotRemote(lotModel: LotModel)

    fun deleteLotRemote(lotModel: LotModel)

    fun deleteLotsByIdRemote(lotIdList: List<String>)

}