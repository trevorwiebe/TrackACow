package com.trevorwiebe.trackacow.domain.use_cases.call_use_cases

import android.app.Application
import com.trevorwiebe.trackacow.data.mapper.toHoldingCallModel
import com.trevorwiebe.trackacow.domain.models.call.CallModel
import com.trevorwiebe.trackacow.domain.repository.CallRepository
import com.trevorwiebe.trackacow.domain.utils.Constants
import com.trevorwiebe.trackacow.domain.utils.Utility

data class UpdateCallUC(
    private val callRepository: CallRepository,
    private val context: Application
){
    suspend operator fun invoke(callModel: CallModel){
        val isConnected = Utility.haveNetworkConnection(context)
        if(isConnected){
            // TODO set up firebase
        }else{
            Utility.setNewDataToUpload(context, true)
            callRepository.insertHoldingCall(callModel.toHoldingCallModel(Constants.INSERT_UPDATE))
        }
        callRepository.updateCall(callModel)
    }
}