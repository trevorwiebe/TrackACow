package com.trevorwiebe.trackacow.data.local.repository

import com.trevorwiebe.trackacow.data.local.dao.DrugsGivenDao
import com.trevorwiebe.trackacow.data.mapper.compound_mapper.toDrugsGivenAndDrugModel
import com.trevorwiebe.trackacow.data.mapper.toCacheDrugGivenEntity
import com.trevorwiebe.trackacow.data.mapper.toDrugGivenEntity
import com.trevorwiebe.trackacow.domain.models.compound_model.DrugsGivenAndDrugModel
import com.trevorwiebe.trackacow.domain.models.drug_given.CacheDrugGivenModel
import com.trevorwiebe.trackacow.domain.models.drug_given.DrugGivenModel
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

    override fun getDrugsGivenAndDrugsByLotIdAndDate(
        lotId: String,
        startDate: Long,
        endDate: Long
    ): Flow<List<DrugsGivenAndDrugModel>> {
        return drugsGivenDao.getDrugsGivenAndDrugByLotIdAndDate(lotId, startDate, endDate)
            .map { drugsGivenAndDrugsList ->
                drugsGivenAndDrugsList.map { it.toDrugsGivenAndDrugModel() }
            }
    }

    override fun getDrugsGivenAndDrugsByCowId(cowId: String): Flow<List<DrugsGivenAndDrugModel>> {
        return drugsGivenDao.getDrugsGivenAndDrugByCowId(cowId)
            .map { drugsGivenList ->
                drugsGivenList.map { it.toDrugsGivenAndDrugModel() }
            }
    }

    override suspend fun editDrugsGiven(drugGivenModel: DrugGivenModel) {
        drugsGivenDao.updateDrugGiven(drugGivenModel.toDrugGivenEntity())
    }

    override suspend fun deleteDrugsGivenByCowId(cowId: String) {
        drugsGivenDao.deleteDrugsGivenByCowId(cowId)
    }

    override suspend fun deleteDrugGiven(drugGivenModel: DrugGivenModel) {
        drugsGivenDao.deleteDrugGiven(drugGivenModel.toDrugGivenEntity())
    }

    override suspend fun insertCacheDrugGiven(cacheDrugGivenModel: CacheDrugGivenModel) {
        drugsGivenDao.insertCacheDrugGiven(cacheDrugGivenModel.toCacheDrugGivenEntity())
    }
}