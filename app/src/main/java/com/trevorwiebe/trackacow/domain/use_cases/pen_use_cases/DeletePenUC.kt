package com.trevorwiebe.trackacow.domain.use_cases.pen_use_cases

import com.trevorwiebe.trackacow.domain.models.pen.PenModel
import com.trevorwiebe.trackacow.domain.repository.local.PenRepository

class DeletePenUC(
    private val penRepository: PenRepository
) {
    suspend operator fun invoke(penModel: PenModel){
        // TODO check for internet connectivity and delete pen in cloud or add to cache to delete later
        penRepository.deletePen(penModel)
    }
}