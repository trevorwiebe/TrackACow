package com.trevorwiebe.trackacow.domain.use_cases.call_use_cases

import com.trevorwiebe.trackacow.data.mapper.toCallAndRationModel
import com.trevorwiebe.trackacow.domain.models.call.CallModel
import com.trevorwiebe.trackacow.domain.models.compound_model.CallAndRationModel
import com.trevorwiebe.trackacow.domain.models.ration.RationModel
import com.trevorwiebe.trackacow.domain.repository.local.CallRepository
import com.trevorwiebe.trackacow.domain.repository.local.RationsRepository
import com.trevorwiebe.trackacow.domain.repository.remote.CallRepositoryRemote
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onStart

data class ReadCallsAndRationsByLotIdUC(
    private val rationRepository: RationsRepository,
    private val callRepository: CallRepository,
    private val callRepositoryRemote: CallRepositoryRemote,
){
    @OptIn(FlowPreview::class)
    operator fun invoke(lotId: String): Flow<List<CallAndRationModel>> {
        val localCallAndRationFlow = callRepository.getCallsAndRationByLotId(lotId)
        val cloudCallAndRationFlow = callRepositoryRemote.readCallAndRationByLotIdRemote(lotId)

        return localCallAndRationFlow
            .flatMapConcat { localData ->
                cloudCallAndRationFlow
                    .flatMapConcat { pair ->
                        rationRepository.insertOrUpdateRationList(pair.first)
                        callRepository.insertOrUpdateCallList(pair.second)
                        flow {
                            val combinedList = combineList(pair.first, pair.second)
                            emit(combinedList)
                        }
                    }.onStart { emit(localData) }
            }
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
