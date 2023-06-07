package com.trevorwiebe.trackacow.domain.use_cases.lot_use_cases

import android.app.Application
import com.trevorwiebe.trackacow.domain.repository.local.LotRepository
import com.trevorwiebe.trackacow.domain.repository.remote.LotRepositoryRemote
import com.trevorwiebe.trackacow.domain.utils.Utility

data class UpdateLotWithNewPenIdUC(
    val lotRepository: LotRepository,
    val lotRepositoryRemote: LotRepositoryRemote,
    val context: Application
){
    suspend operator fun invoke(lotId: String, penId: String) {

        lotRepository.updateLotByLotIdWithNewPenID(lotId, penId)

        if (Utility.haveNetworkConnection(context)) {
            lotRepositoryRemote.updateLotWithNewPenIdRemote(lotId, penId)
        } else {
            Utility.setNewDataToUpload(context, true)
            // TODO: fix this to make cache lot update with lotId and penId
//            lotRepository.createCacheLot(lotModel.toCacheLotModel(Constants.INSERT_UPDATE))
        }
    }
}
