package com.trevorwiebe.trackacow.domain.use_cases.call_use_cases

import com.trevorwiebe.trackacow.domain.models.call.CallModel
import com.trevorwiebe.trackacow.domain.repository.local.CallRepository
import kotlinx.coroutines.flow.Flow

data class ReadCallByLotIdAndDateUC(
    private val callRepository: CallRepository,
){
    operator fun invoke(lotId: String, date: Long): Flow<CallModel> {
        return callRepository.getCallsByLotIdAndDate(lotId, date)
    }
}
