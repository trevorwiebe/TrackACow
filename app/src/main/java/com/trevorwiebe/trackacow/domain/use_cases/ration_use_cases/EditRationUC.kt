package com.trevorwiebe.trackacow.domain.use_cases.ration_use_cases

import android.app.Application
import com.trevorwiebe.trackacow.data.mapper.toHoldingRationModel
import com.trevorwiebe.trackacow.domain.models.RationModel
import com.trevorwiebe.trackacow.domain.repository.RationsRepository
import com.trevorwiebe.trackacow.domain.utils.Constants
import com.trevorwiebe.trackacow.domain.utils.Utility

class EditRationUC(
    private val rationsRepository: RationsRepository,
    private val context: Application
) {
    suspend operator fun invoke(rationModel: RationModel){
        val isConnectionActive = Utility.haveNetworkConnection(context)

        if(isConnectionActive){
            // TODO set up firebase
        }else{
            rationsRepository.insertHoldingRation(rationModel.toHoldingRationModel(Constants.INSERT_UPDATE))
            Utility.setNewDataToUpload(context, true)
        }
        rationsRepository.updateRations(rationModel)
    }
}