package com.trevorwiebe.trackacow.domain.use_cases.lot_use_cases

data class LotUseCases(
    val readLotsByPenId: ReadLotsByPenId,
    val readLots: ReadLots,
    val updateLotWithNewPenIdUC: UpdateLotWithNewPenIdUC
)
