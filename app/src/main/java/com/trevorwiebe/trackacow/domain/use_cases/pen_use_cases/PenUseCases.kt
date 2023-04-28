package com.trevorwiebe.trackacow.domain.use_cases.pen_use_cases

data class PenUseCases (
        val readPenAndLotModelIncludeEmptyPens: ReadPenAndLotModelIncludeEmptyPens,
        val readPenAndLotModelExcludeEmptyPens: ReadPenAndLotModelExcludeEmptyPens,
        val createPenUC: CreatePenUC,
        val deletePenUC: DeletePenUC,
        val updatePenUC: UpdatePenUC
)