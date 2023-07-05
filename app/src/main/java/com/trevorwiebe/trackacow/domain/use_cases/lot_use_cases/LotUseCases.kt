package com.trevorwiebe.trackacow.domain.use_cases.lot_use_cases

data class LotUseCases(
    val createLot: CreateLot,
    val readArchivedLots: ReadArchivedLots,
    val readLots: ReadLots,
    val readLotsByLotId: ReadLotsByLotId,
    val archiveLot: ArchiveLot,
    val mergeLots: MergeLots,
    val updateLot: UpdateLot,
    val deleteLot: DeleteLot
)
