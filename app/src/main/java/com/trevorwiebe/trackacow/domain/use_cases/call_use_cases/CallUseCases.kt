package com.trevorwiebe.trackacow.domain.use_cases.call_use_cases

data class CallUseCases (
        val readCallsByLotIdAndDateUC: ReadCallByLotIdAndDateUC,
        val readCallsAndRationsByLotId: ReadCallsAndRationsByLotIdUC,
        val createCallUC: CreateCallUC,
        val updateCallUC: UpdateCallUC
)