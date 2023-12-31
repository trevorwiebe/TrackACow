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
import com.trevorwiebe.trackacow.domain.utils.SourceIdentifiedListFlow
import com.trevorwiebe.trackacow.domain.utils.Utility
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart

data class ReadCallsAndRationsByLotIdUC(
        private val rationRepository: RationsRepository,
        private val callRepository: CallRepository,
        private val callRepositoryRemote: CallRepositoryRemote,
        private val context: Application
){

    @OptIn(FlowPreview::class)
    operator fun invoke(lotId: String): SourceIdentifiedListFlow {
        val localCallAndRationFlow = callRepository.getCallsAndRationByLotId(lotId)
            .map { call -> call to DataSource.Local }
        val cloudCallAndRationFlow = callRepositoryRemote.readCallAndRationByLotIdRemote(lotId)
            .map { call -> call to DataSource.Cloud }

        val isFetchingFromCloud = Utility.haveNetworkConnection(context)
        val dataResult = if (isFetchingFromCloud) {
            localCallAndRationFlow.flatMapConcat { (localData, fromLocalSource) ->
                cloudCallAndRationFlow.flatMapConcat { (pair, fromCloudSource) ->
                    rationRepository.syncCloudRationListToDatabase(pair.first)
                    callRepository.syncCloudCallsByLotId(pair.second, lotId)
                    flow {
                        val combinedList = combineList(pair.first, pair.second)
                        emit(combinedList to fromCloudSource)
                    }
                }.onStart { emit(localData to fromLocalSource) }
            }
        } else {
            localCallAndRationFlow
        }

        return SourceIdentifiedListFlow(dataResult, isFetchingFromCloud)
    }

    private fun combineList(
        rationList: List<RationModel>,
        callList: List<CallModel>
    ): List<CallAndRationModel> {
        val result = mutableListOf<CallAndRationModel>()
        callList.forEach { callModel ->
            val ration = rationList.find { it.rationPrimaryKey == callModel.callRationId }
            val callAndRationModel = callModel.toCallAndRationModel(ration)
            result.add(callAndRationModel)
        }
        return result
    }
}
