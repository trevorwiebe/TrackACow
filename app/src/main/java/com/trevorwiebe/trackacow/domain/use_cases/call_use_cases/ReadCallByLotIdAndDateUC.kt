package com.trevorwiebe.trackacow.domain.use_cases.call_use_cases

import com.trevorwiebe.trackacow.domain.models.compound_model.CallAndRationModel
import com.trevorwiebe.trackacow.domain.repository.local.CallRepository
import kotlinx.coroutines.flow.Flow

data class ReadCallByLotIdAndDateUC(
    private val callRepository: CallRepository,
){
    operator fun invoke(lotId: String, dateStart: Long, dateEnd: Long): Flow<CallAndRationModel?> {
        return callRepository.getCallByLotIdAndDate(lotId, dateStart, dateEnd)
    }
}
