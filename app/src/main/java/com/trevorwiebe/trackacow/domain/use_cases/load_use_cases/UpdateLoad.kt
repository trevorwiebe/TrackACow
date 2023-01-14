package com.trevorwiebe.trackacow.domain.use_cases.load_use_cases

import com.trevorwiebe.trackacow.domain.models.load.LoadModel
import com.trevorwiebe.trackacow.domain.repository.local.LoadRepository

data class UpdateLoad(
    private val loadRepository: LoadRepository
){
    suspend operator fun invoke(loadModel: LoadModel){
        // TODO: add code to update to remote database
        loadRepository.updateLoad(loadModel)
    }
}
