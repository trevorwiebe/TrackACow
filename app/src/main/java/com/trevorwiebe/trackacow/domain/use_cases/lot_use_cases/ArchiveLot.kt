package com.trevorwiebe.trackacow.domain.use_cases.lot_use_cases

import android.app.Application
import com.trevorwiebe.trackacow.data.mapper.toCacheLotModel
import com.trevorwiebe.trackacow.domain.models.lot.LotModel
import com.trevorwiebe.trackacow.domain.repository.local.LotRepository
import com.trevorwiebe.trackacow.domain.repository.remote.LotRepositoryRemote
import com.trevorwiebe.trackacow.domain.utils.Constants
import com.trevorwiebe.trackacow.domain.utils.Utility

class ArchiveLot(
    private val lotRepository: LotRepository,
    private val lotRepositoryRemote: LotRepositoryRemote,
    private val context: Application
) {

    suspend operator fun invoke(lotModel: LotModel) {
        lotRepository.archiveLot(lotModel)

        if (Utility.haveNetworkConnection(context)) {
            lotRepositoryRemote.archiveLot(lotModel)
        } else {
            Utility.setNewDataToUpload(context, true)
            lotRepository.createCacheLot(lotModel.toCacheLotModel(Constants.INSERT_UPDATE))
        }
    }
}