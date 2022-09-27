package com.trevorwiebe.trackacow.domain.use_cases.call_use_cases

import com.trevorwiebe.trackacow.domain.models.call.CallModel
import com.trevorwiebe.trackacow.domain.repository.local.CallRepository
import kotlinx.coroutines.flow.Flow

data class ReadCallsByLotIdUC(
    private val callRepository: CallRepository
){
    operator fun invoke(lotId: String): Flow<List<CallModel>> {
        return callRepository.getCallsByLotId(lotId)
    }
}
