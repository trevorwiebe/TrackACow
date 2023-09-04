package com.trevorwiebe.trackacow.domain.use_cases.lot_use_cases

import android.app.Application
import android.util.Log
import com.trevorwiebe.trackacow.data.mapper.toCacheLotModel
import com.trevorwiebe.trackacow.domain.models.lot.LotModel
import com.trevorwiebe.trackacow.domain.repository.local.LotRepository
import com.trevorwiebe.trackacow.domain.repository.remote.LotRepositoryRemote
import com.trevorwiebe.trackacow.domain.utils.Constants
import com.trevorwiebe.trackacow.domain.utils.Utility

class UpdateLotWithNewRation(
    private val lotRepository: LotRepository,
    private val lotRepositoryRemote: LotRepositoryRemote,
    private val context: Application
) {

    suspend operator fun invoke(lotModel: LotModel) {

        Log.d("TAG", "invoke: $lotModel")

        val rationId = lotModel.rationId ?: ""
        val lotId = lotModel.lotCloudDatabaseId

        lotRepository.updateLotWithNewRationId(rationId, lotId)

        if (Utility.haveNetworkConnection(context)) {
            lotRepositoryRemote.updateLotWithRationId(rationId, lotId)
        } else {
            Utility.setNewDataToUpload(context, true)
            lotRepository.createCacheLot(
                lotModel.toCacheLotModel(Constants.INSERT_UPDATE)
            )
        }
    }
}