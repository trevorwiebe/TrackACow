package com.trevorwiebe.trackacow.domain.use_cases.ration_use_cases

import com.trevorwiebe.trackacow.domain.models.RationModel
import com.trevorwiebe.trackacow.domain.repository.RationsRepository
import kotlinx.coroutines.flow.Flow

class GetAllRationsUC(
    private val rationsRepository: RationsRepository
) {
    operator fun invoke(): Flow<List<RationModel>> {
        return rationsRepository.getRations()
    }
}