package com.trevorwiebe.trackacow.domain.use_cases.drug_use_cases

import com.trevorwiebe.trackacow.domain.models.drug.DrugModel
import com.trevorwiebe.trackacow.domain.repository.local.DrugRepository

class CreateDrug(
    private val drugRepository: DrugRepository
) {
    suspend operator fun invoke(drugModel: DrugModel){
        // TODO check if internet connectivity and push to internet or local cache
        drugRepository.insertDrug(drugModel)
    }
}