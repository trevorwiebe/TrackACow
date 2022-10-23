package com.trevorwiebe.trackacow.domain.use_cases.load_use_cases

import com.trevorwiebe.trackacow.domain.models.load.LoadModel
import com.trevorwiebe.trackacow.domain.repository.local.LoadRepository
import kotlinx.coroutines.flow.Flow

data class ReadLoadsByLotId(
    private val loadRepository: LoadRepository
){
    operator fun invoke(lotId: String): Flow<List<LoadModel>> {
        return loadRepository.readLoadsByLotId(lotId)
    }
}
