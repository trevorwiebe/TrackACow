package com.trevorwiebe.trackacow.data.local.repository

import com.trevorwiebe.trackacow.data.local.cacheDao.CacheDrugsGivenDao
import com.trevorwiebe.trackacow.data.local.dao.DrugsGivenDao
import com.trevorwiebe.trackacow.data.mapper.compound_mapper.toDrugsGivenAndDrugModel
import com.trevorwiebe.trackacow.data.mapper.toCacheDrugGivenEntity
import com.trevorwiebe.trackacow.data.mapper.toCacheDrugGivenModel
import com.trevorwiebe.trackacow.data.mapper.toDrugGivenEntity
import com.trevorwiebe.trackacow.data.mapper.toDrugGivenModel
import com.trevorwiebe.trackacow.domain.models.compound_model.DrugsGivenAndDrugModel
import com.trevorwiebe.trackacow.domain.models.drug_given.CacheDrugGivenModel
import com.trevorwiebe.trackacow.domain.models.drug_given.DrugGivenModel
import com.trevorwiebe.trackacow.domain.repository.local.DrugsGivenRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class DrugsGivenRepositoryImpl(
    private val drugsGivenDao: DrugsGivenDao,
    private val cacheDrugsGivenDao: CacheDrugsGivenDao
): DrugsGivenRepository {
    override suspend fun createDrugsGivenList(drugGivenList: List<DrugGivenModel>): List<Long> {
        return drugsGivenDao.insertDrugsGivenList(
            drugGivenList.map { it.toDrugGivenEntity() }
        )
    }

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

    override fun getDrugsGivenAndDrugsByCowId(cowIdList: List<String>): Flow<List<DrugsGivenAndDrugModel>> {
        return drugsGivenDao.getDrugsGivenAndDrugByCowId(cowIdList)
            .map { drugsGivenList ->
                drugsGivenList.map { it.toDrugsGivenAndDrugModel() }
            }
    }

    override suspend fun editDrugsGiven(drugGivenModel: DrugGivenModel) {
        drugsGivenDao.updateDrugGiven(drugGivenModel.toDrugGivenEntity())
    }

    override suspend fun updateDrugsGivenWithNewLot(lotId: String, oldLotIds: List<String>) {
        drugsGivenDao.updateDrugsGivenWithNewLotId(lotId, oldLotIds)
    }

    override suspend fun deleteDrugsGivenByCowIdTransaction(cowId: String): List<DrugGivenModel> {
        return drugsGivenDao.deleteDrugsGivenByCowIdTransaction(cowId)
                .map { it.toDrugGivenModel() }
    }

    override suspend fun deleteDrugGiven(drugGivenModel: DrugGivenModel) {
        drugsGivenDao.deleteDrugGiven(drugGivenModel.toDrugGivenEntity())
    }

    override suspend fun deleteAllDrugsGiven() {
        drugsGivenDao.deleteAllDrugsGiven()
    }

    override suspend fun syncCloudDrugsGivenToDatabaseByCowId(
        drugGivenList: List<DrugGivenModel>,
        cowId: String
    ) {
        drugsGivenDao.syncCloudDrugsGivenByCowId(drugGivenList.map {
            it.toDrugGivenEntity()
        }, cowId)
    }

    override suspend fun syncCloudDrugsGivenToDatabaseByLotId(
        drugGivenList: List<DrugGivenModel>,
        lotId: String
    ) {
        drugsGivenDao.syncCloudDrugsGivenByLotId(drugGivenList.map {
            it.toDrugGivenEntity()
        }, lotId)
    }

    override suspend fun syncCloudDrugsGivenToDatabaseByLotIdAndDate(
        drugGivenList: List<DrugGivenModel>,
        lotId: String,
        startDate: Long,
        endDate: Long
    ) {
        drugsGivenDao.syncCloudDrugsGivenByLotIdAndDate(drugGivenList.map {
            it.toDrugGivenEntity()
        }, lotId, startDate, endDate)
    }

    override suspend fun insertCacheDrugGiven(cacheDrugGivenModel: CacheDrugGivenModel) {
        cacheDrugsGivenDao.insertCacheDrugGiven(cacheDrugGivenModel.toCacheDrugGivenEntity())
    }

    override suspend fun createCacheDrugsGivenList(cacheDrugsGivenList: List<CacheDrugGivenModel>) {
        cacheDrugsGivenDao.insertCacheDrugGivenList(
            cacheDrugsGivenList.map { it.toCacheDrugGivenEntity() }
        )
    }

    override suspend fun getCacheDrugsGiven(): List<CacheDrugGivenModel> {
        return cacheDrugsGivenDao.getCacheDrugGiven().map { it.toCacheDrugGivenModel() }
    }

    override suspend fun deleteCacheDrugsGiven() {
        cacheDrugsGivenDao.deleteCacheDrugGiven()
    }
}