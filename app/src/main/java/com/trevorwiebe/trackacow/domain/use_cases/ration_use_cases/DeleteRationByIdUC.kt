package com.trevorwiebe.trackacow.domain.use_cases.ration_use_cases

import com.trevorwiebe.trackacow.domain.repository.RationsRepository

class DeleteRationByIdUC(
    private val rationsRepository: RationsRepository
) {
    suspend operator fun invoke(rationId: Int){
        rationsRepository.deleteRationById(rationId)
    }
}