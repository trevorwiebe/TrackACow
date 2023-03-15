package com.trevorwiebe.trackacow.domain.use_cases.lot_use_cases

import android.app.Application
import com.trevorwiebe.trackacow.data.mapper.toCacheLotModel
import com.trevorwiebe.trackacow.domain.models.lot.LotModel
import com.trevorwiebe.trackacow.domain.repository.local.LotRepository
import com.trevorwiebe.trackacow.domain.repository.remote.LotRepositoryRemote
import com.trevorwiebe.trackacow.domain.use_cases.GetCloudDatabaseId
import com.trevorwiebe.trackacow.domain.utils.Constants
import com.trevorwiebe.trackacow.domain.utils.Utility

class CreateLot(
    private val lotRepository: LotRepository,
    private val lotRepositoryRemote: LotRepositoryRemote,
    private val getCloudDatabaseId: GetCloudDatabaseId,
    private val context: Application
) {

    suspend operator fun invoke(lotModel: LotModel): String {

        val lotCloudId = getCloudDatabaseId.invoke("")

        lotModel.lotCloudDatabaseId = lotCloudId

        lotRepository.createLot(lotModel)

        if (Utility.haveNetworkConnection(context)) {
            lotRepositoryRemote.insertAndUpdateLotRemote(lotModel)
        } else {
            Utility.setNewDataToUpload(context, true)
            lotRepository.createCacheLot(lotModel.toCacheLotModel(Constants.INSERT_UPDATE))
        }

        return lotCloudId

    }
}