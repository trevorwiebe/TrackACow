package com.trevorwiebe.trackacow.domain.use_cases.call_use_cases

import android.app.Application
import com.trevorwiebe.trackacow.data.mapper.toCallAndRationModel
import com.trevorwiebe.trackacow.domain.models.call.CallModel
import com.trevorwiebe.trackacow.domain.models.compound_model.CallAndRationModel
import com.trevorwiebe.trackacow.domain.models.ration.RationModel
import com.trevorwiebe.trackacow.domain.repository.local.CallRepository
import com.trevorwiebe.trackacow.domain.repository.local.RationsRepository
import com.trevorwiebe.trackacow.domain.repository.remote.CallRepositoryRemote
import com.trevorwiebe.trackacow.domain.utils.Utility
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onStart

data class ReadCallByLotIdAndDateUC(
        private val rationRepository: RationsRepository,
        private val callRepository: CallRepository,
        private val callRepositoryRemote: CallRepositoryRemote,
        private val context: Application
){

    // TODO: update this with data source identification

    @OptIn(FlowPreview::class)
    operator fun invoke(lotId: String, dateStart: Long, dateEnd: Long): Flow<CallAndRationModel?> {

        val localCallAndRationFlow = callRepository.getCallByLotIdAndDate(lotId, dateStart, dateEnd)
        val cloudCallAndRationFlow = callRepositoryRemote.readCallAndRationByLotIdRemote(lotId)

        if (Utility.haveNetworkConnection(context)) {
            return localCallAndRationFlow.flatMapConcat { localData ->
                cloudCallAndRationFlow.flatMapConcat { pair ->
                    rationRepository.insertOrUpdateRationList(pair.first)
                    callRepository.insertOrUpdateCallList(pair.second)
                    flow {
                        val callAndRationModel =
                                getCallAndRationModel(pair.first, pair.second, dateStart, dateEnd)
                        emit(callAndRationModel)
                    }
                }.onStart { emit(localData) }
            }
        } else {
            return localCallAndRationFlow
        }
    }

    private fun getCallAndRationModel(
        rationList: List<RationModel>,
        callList: List<CallModel>,
        startDate: Long,
        endDate: Long
    ): CallAndRationModel? {
        callList.forEach { callModel ->
            if (callModel.date in startDate..endDate) {
                val ration = rationList.find { it.rationPrimaryKey == callModel.callRationId }
                return callModel.toCallAndRationModel(ration)
            }
        }
        return null
    }
}
