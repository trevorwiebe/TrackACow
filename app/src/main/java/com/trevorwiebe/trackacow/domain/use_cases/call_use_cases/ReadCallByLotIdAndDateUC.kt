package com.trevorwiebe.trackacow.domain.use_cases.call_use_cases

import android.app.Application
import com.trevorwiebe.trackacow.data.mapper.toCallAndRationModel
import com.trevorwiebe.trackacow.domain.models.call.CallModel
import com.trevorwiebe.trackacow.domain.models.compound_model.CallAndRationModel
import com.trevorwiebe.trackacow.domain.models.ration.RationModel
import com.trevorwiebe.trackacow.domain.repository.local.CallRepository
import com.trevorwiebe.trackacow.domain.repository.local.RationsRepository
import com.trevorwiebe.trackacow.domain.repository.remote.CallRepositoryRemote
import com.trevorwiebe.trackacow.domain.utils.DataSource
import com.trevorwiebe.trackacow.domain.utils.SourceIdentifiedSingleFlow
import com.trevorwiebe.trackacow.domain.utils.Utility
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart

data class ReadCallByLotIdAndDateUC(
        private val rationRepository: RationsRepository,
        private val callRepository: CallRepository,
        private val callRepositoryRemote: CallRepositoryRemote,
        private val context: Application
){

    @OptIn(FlowPreview::class)
    operator fun invoke(lotId: String, dateStart: Long, dateEnd: Long): SourceIdentifiedSingleFlow {

        val localCallAndRationFlow = callRepository.getCallByLotIdAndDate(lotId, dateStart, dateEnd)
            .map { call -> call to DataSource.Local }
        val cloudCallAndRationFlow = callRepositoryRemote.readCallAndRationByLotIdRemote(lotId)
            .map { call -> call to DataSource.Cloud }

        val isFetchingFromCloud = Utility.haveNetworkConnection(context)
        val flowResult = if (isFetchingFromCloud) {
            localCallAndRationFlow.flatMapConcat { (localData, fromLocalSource) ->
                cloudCallAndRationFlow.flatMapConcat { (pair, fromCloudSource) ->
                    rationRepository.syncCloudRationListToDatabase(pair.first)
                    callRepository.syncCloudCallsByLotIdAndDate(
                        pair.second,
                        lotId,
                        dateStart,
                        dateEnd
                    )
                    flow {
                        val callAndRationModel =
                            getCallAndRationModel(pair.first, pair.second, dateStart, dateEnd)
                        emit(callAndRationModel to fromCloudSource)
                    }
                }.onStart { emit(localData to fromLocalSource) }
            }
        } else {
            localCallAndRationFlow
        }

        return SourceIdentifiedSingleFlow(flowResult, isFetchingFromCloud)
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
