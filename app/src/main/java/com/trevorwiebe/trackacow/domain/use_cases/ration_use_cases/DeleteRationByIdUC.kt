package com.trevorwiebe.trackacow.domain.use_cases.ration_use_cases

import android.app.Application
import com.trevorwiebe.trackacow.data.mapper.toCacheRationModel
import com.trevorwiebe.trackacow.domain.models.ration.RationModel
import com.trevorwiebe.trackacow.domain.repository.local.RationsRepository
import com.trevorwiebe.trackacow.domain.utils.Constants
import com.trevorwiebe.trackacow.domain.utils.Utility

class DeleteRationByIdUC(
    private val rationsRepository: RationsRepository,
    private val context: Application
) {
    suspend operator fun invoke(rationId: Int){

        val isConnectionActive = Utility.haveNetworkConnection(context)

        if(isConnectionActive){
            // TODO set up firebase
        }else{
            val rationModel = RationModel(rationId, "", "")
            rationsRepository.insertCacheRation(rationModel.toCacheRationModel(Constants.DELETE))
            Utility.setNewDataToUpload(context, true)
        }

        rationsRepository.deleteRationById(rationId)
    }
}