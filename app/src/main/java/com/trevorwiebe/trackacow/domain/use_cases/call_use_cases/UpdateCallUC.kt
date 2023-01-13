package com.trevorwiebe.trackacow.domain.use_cases.call_use_cases

import android.app.Application
import com.trevorwiebe.trackacow.data.mapper.toHoldingCallModel
import com.trevorwiebe.trackacow.domain.models.call.CallModel
import com.trevorwiebe.trackacow.domain.repository.local.CallRepository
import com.trevorwiebe.trackacow.domain.repository.remote.CallRepositoryRemote
import com.trevorwiebe.trackacow.domain.utils.Constants
import com.trevorwiebe.trackacow.domain.utils.Utility

data class UpdateCallUC(
    private val callRepository: CallRepository,
    private val callRepositoryRemote: CallRepositoryRemote,
    private val context: Application
){
    suspend operator fun invoke(callModel: CallModel){

        callRepository.updateCall(callModel)

        // TODO: add code to update remote cloud id

        val isConnected = Utility.haveNetworkConnection(context)
        if(isConnected){
            // TODO fix issue where can't update without cloud ID
            callRepositoryRemote.updateCallRemote(callModel)
        }else{
            Utility.setNewDataToUpload(context, true)
            callRepository.insertCacheCall(callModel.toHoldingCallModel(Constants.INSERT_UPDATE))
        }
    }
}