package com.trevorwiebe.trackacow.domain.use_cases.call_use_cases

import android.content.Context
import com.trevorwiebe.trackacow.data.mapper.toHoldingCallModel
import com.trevorwiebe.trackacow.domain.models.call.CallModel
import com.trevorwiebe.trackacow.domain.repository.local.CallRepository
import com.trevorwiebe.trackacow.domain.repository.remote.CallRepositoryRemote
import com.trevorwiebe.trackacow.domain.use_cases.GetCloudDatabaseId
import com.trevorwiebe.trackacow.domain.utils.Constants
import com.trevorwiebe.trackacow.domain.utils.Utility

data class CreateCallUC(
    private val callRepository: CallRepository,
    private val callRepositoryRemote: CallRepositoryRemote,
    private val getCloudDatabaseId: GetCloudDatabaseId,
    private val context: Context
){
    suspend operator fun invoke(callModel: CallModel, isConnected: Boolean) {

        callModel.callCloudDatabaseId = getCloudDatabaseId.invoke("")

        val id: Long = callRepository.insertCall(callModel)

        callModel.callPrimaryKey = id.toInt()

        if (isConnected) {
            callRepositoryRemote.insertOrUpdateCallRemote(callModel)
        } else {
            callRepository.insertCacheCall(callModel.toHoldingCallModel(Constants.INSERT_UPDATE))
            Utility.setNewDataToUpload(context, true)
        }
    }
}
