package com.trevorwiebe.trackacow.domain.repository.remote

import com.trevorwiebe.trackacow.domain.models.lot.LotModel

interface LotRepositoryRemote {

    fun archiveLot(lotModel: LotModel)

    fun updateLotWithNewPenIdRemote(lotId: String, penId: String)

    fun insertAndUpdateLotRemote(lotModel: LotModel)

    fun deleteLotRemote(lotModel: LotModel)

}