package com.trevorwiebe.trackacow.domain.use_cases.ration_use_cases

import com.trevorwiebe.trackacow.domain.models.RationModel
import com.trevorwiebe.trackacow.domain.repository.RationsRepository

class AddRationUC(
    private val rationsRepository: RationsRepository
) {
    suspend operator fun invoke(rationModel: RationModel){
        rationsRepository.insertRation(rationModel)
    }
}