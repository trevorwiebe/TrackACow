package com.trevorwiebe.trackacow.domain.use_cases.pen_use_cases

import com.trevorwiebe.trackacow.domain.models.compound_model.PenAndLotModel
import com.trevorwiebe.trackacow.domain.repository.local.PenRepository
import kotlinx.coroutines.flow.Flow

class ReadPenAndLotModelIncludeEmptyPens(
    private val penRepository: PenRepository
) {
    operator fun invoke(): Flow<List<PenAndLotModel>> {
        return penRepository.readPensAndLotsIncludeEmptyPens()
    }
}