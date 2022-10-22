package com.trevorwiebe.trackacow.domain.use_cases.drugs_given_use_cases

import com.trevorwiebe.trackacow.domain.models.compound_model.DrugsGivenAndDrugModel
import com.trevorwiebe.trackacow.domain.repository.local.DrugsGivenRepository
import kotlinx.coroutines.flow.Flow

data class ReadDrugsGivenAndDrugsByLotId (
    private val drugsGivenRepository: DrugsGivenRepository
){
    operator fun invoke(lotId: String): Flow<List<DrugsGivenAndDrugModel>> {
        return drugsGivenRepository.getDrugsGivenAndDrugs(lotId)
    }
}