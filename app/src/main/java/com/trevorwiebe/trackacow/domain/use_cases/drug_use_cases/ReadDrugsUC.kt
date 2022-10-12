package com.trevorwiebe.trackacow.domain.use_cases.drug_use_cases

import com.trevorwiebe.trackacow.domain.models.drug.DrugModel
import com.trevorwiebe.trackacow.domain.repository.local.DrugRepository
import kotlinx.coroutines.flow.Flow

data class ReadDrugsUC (
    private val drugRepository: DrugRepository
){
    operator fun invoke(): Flow<List<DrugModel>> {
        return drugRepository.getDrugList()
    }
}