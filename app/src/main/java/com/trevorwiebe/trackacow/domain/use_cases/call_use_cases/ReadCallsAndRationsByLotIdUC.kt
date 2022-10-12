package com.trevorwiebe.trackacow.domain.use_cases.call_use_cases

import com.trevorwiebe.trackacow.domain.models.compound_model.CallAndRationModel
import com.trevorwiebe.trackacow.domain.repository.local.CallRepository
import kotlinx.coroutines.flow.Flow

data class ReadCallsAndRationsByLotIdUC(
    private val callRepository: CallRepository
){
    operator fun invoke(lotId: String): Flow<List<CallAndRationModel>> {
        return callRepository.getCallsAndRationByLotId(lotId)
    }
}
