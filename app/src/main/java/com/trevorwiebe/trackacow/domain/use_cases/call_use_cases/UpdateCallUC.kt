package com.trevorwiebe.trackacow.domain.use_cases.call_use_cases

import android.content.Context
import com.trevorwiebe.trackacow.data.mapper.toHoldingCallModel
import com.trevorwiebe.trackacow.domain.models.call.CallModel
import com.trevorwiebe.trackacow.domain.repository.local.CallRepository
import com.trevorwiebe.trackacow.domain.repository.remote.CallRepositoryRemote
import com.trevorwiebe.trackacow.domain.utils.Constants
import com.trevorwiebe.trackacow.domain.utils.Utility

data class UpdateCallUC(
    private val callRepository: CallRepository,
    private val callRepositoryRemote: CallRepositoryRemote,
    private val context: Context
){
    suspend operator fun invoke(callModel: CallModel, isConnected: Boolean) {

        callRepository.updateCall(callModel)

        if (isConnected) {
            callRepositoryRemote.insertOrUpdateCallRemote(callModel)
        } else {
            Utility.setNewDataToUpload(context, true)
            callRepository.insertCacheCall(callModel.toHoldingCallModel(Constants.INSERT_UPDATE))
        }
    }
}