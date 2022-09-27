package com.trevorwiebe.trackacow.domain.use_cases.ration_use_cases

import com.trevorwiebe.trackacow.domain.models.ration.RationModel
import com.trevorwiebe.trackacow.domain.repository.local.RationsRepository
import kotlinx.coroutines.flow.Flow

class ReadAllRationsUC(
    private val rationsRepository: RationsRepository
) {
    operator fun invoke(): Flow<List<RationModel>> {
        return rationsRepository.getRations()
    }
}