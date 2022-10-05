package com.trevorwiebe.trackacow.domain.use_cases.pen_use_cases

import com.trevorwiebe.trackacow.domain.models.pen.PenModel
import com.trevorwiebe.trackacow.domain.repository.local.PenRepository
import kotlinx.coroutines.flow.Flow

data class ReadPenByPenId (
    val penRepository: PenRepository
) {
    operator fun invoke(penId: String): Flow<PenModel?> {
        return penRepository.readPensByPenId(penId)
    }
}