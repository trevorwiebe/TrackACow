package com.trevorwiebe.trackacow.domain.use_cases.lot_use_cases

data class LotUseCases(
    val readLotsByPenId: ReadLotsByPenId,
    val readLots: ReadLots,
    val readLotsByLotId: ReadLotsByLotId,
    val updateLotWithNewPenIdUC: UpdateLotWithNewPenIdUC,
    val updateLotWithLotId: UpdateLotWithLotId,
    val deleteLot: DeleteLot
)
