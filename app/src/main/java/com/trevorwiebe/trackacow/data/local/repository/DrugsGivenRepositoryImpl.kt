package com.trevorwiebe.trackacow.data.local.repository

import com.trevorwiebe.trackacow.data.local.dao.DrugsGivenDao
import com.trevorwiebe.trackacow.data.mapper.compound_mapper.toDrugsGivenAndDrugModel
import com.trevorwiebe.trackacow.domain.models.compound_model.DrugsGivenAndDrugModel
import com.trevorwiebe.trackacow.domain.repository.local.DrugsGivenRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class DrugsGivenRepositoryImpl(
    private val drugsGivenDao: DrugsGivenDao
): DrugsGivenRepository {

    override fun getDrugsGivenAndDrugs(lotId: String): Flow<List<DrugsGivenAndDrugModel>> {
        return drugsGivenDao.getDrugsGivenAndDrugByLotId(lotId)
            .map { drugsGivenAndDrugList ->
                drugsGivenAndDrugList.map { it.toDrugsGivenAndDrugModel() }
            }
    }
}