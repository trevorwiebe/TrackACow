package com.trevorwiebe.trackacow.domain.use_cases.ration_use_cases

import android.app.Application
import com.trevorwiebe.trackacow.data.mapper.toCacheRationModel
import com.trevorwiebe.trackacow.domain.models.ration.RationModel
import com.trevorwiebe.trackacow.domain.repository.local.RationsRepository
import com.trevorwiebe.trackacow.domain.repository.remote.RationRepositoryRemote
import com.trevorwiebe.trackacow.domain.utils.Constants
import com.trevorwiebe.trackacow.domain.utils.Utility

class UpdateRationUC(
    private val rationsRepository: RationsRepository,
    private val rationsRepositoryRemote: RationRepositoryRemote,
    private val context: Application
) {
    suspend operator fun invoke(rationModel: RationModel){

        rationsRepository.updateRations(rationModel)

        if(Utility.haveNetworkConnection(context)){
            // TODO fix issue where can't update because don't have cloud database id
            rationsRepositoryRemote.updateRationRemote(rationModel)
        }else{
            rationsRepository.insertCacheRation(rationModel.toCacheRationModel(Constants.INSERT_UPDATE))
            Utility.setNewDataToUpload(context, true)
        }
    }
}