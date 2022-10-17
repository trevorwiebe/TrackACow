package com.trevorwiebe.trackacow.domain.use_cases.pen_use_cases

data class PenUseCases (
        val readPens: ReadPens,
        val readPenByPenId: ReadPenByPenId,
        val readPenAndLotModelUC: ReadPenAndLotModelUC,
        val createPenUC: CreatePenUC,
        val deletePenUC: DeletePenUC,
        val updatePenUC: UpdatePenUC
)