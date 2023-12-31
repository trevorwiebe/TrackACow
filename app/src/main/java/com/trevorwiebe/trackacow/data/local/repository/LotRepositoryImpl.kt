package com.trevorwiebe.trackacow.data.local.repository

import com.trevorwiebe.trackacow.data.local.cacheDao.CacheLotDao
import com.trevorwiebe.trackacow.data.local.dao.LotDao
import com.trevorwiebe.trackacow.data.mapper.toCacheLotEntity
import com.trevorwiebe.trackacow.data.mapper.toCacheLotModel
import com.trevorwiebe.trackacow.data.mapper.toLotEntity
import com.trevorwiebe.trackacow.data.mapper.toLotModel
import com.trevorwiebe.trackacow.domain.models.lot.CacheLotModel
import com.trevorwiebe.trackacow.domain.models.lot.LotModel
import com.trevorwiebe.trackacow.domain.repository.local.LotRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class LotRepositoryImpl(
    private val lotDao: LotDao,
    private val cacheLotDao: CacheLotDao
): LotRepository {
    override suspend fun createLot(lotModel: LotModel): Long {
        return lotDao.createLot(lotModel.toLotEntity())
    }

    override fun readLotsByPenId(penId: String): Flow<List<LotModel>> {
        return lotDao.getLotEntitiesByPenId(penId).map { lotList ->
            lotList.map {
                it.toLotModel()
            }
        }
    }

    override suspend fun archiveLot(lotModel: LotModel) {
        lotDao.archiveLot(lotModel.lotPrimaryKey, System.currentTimeMillis())
    }

    override fun readArchivedLots(): Flow<List<LotModel>> {
        return lotDao.getArchivedLots().map { lotList ->
            lotList.map {
                it.toLotModel()
            }
        }
    }

    override fun readLots(): Flow<List<LotModel>> {
        return lotDao.getLotEntities()
            .map { lotList ->
                lotList.map {
                    it.toLotModel()
                }
            }
    }

    override fun readLotByLotId(lotCloudDatabaseId: String): Flow<LotModel?> {
        return lotDao.getLotByLotId(lotCloudDatabaseId)
            .map { it?.toLotModel() }
    }

    override suspend fun updateLotByLotIdWithNewPenID(lotId: String, penId: String) {
        lotDao.updateLotWithNewPenId(lotId, penId)
    }

    override suspend fun updateLot(lotModel: LotModel) {
        lotDao.updateLot(
            lotModel.lotPrimaryKey,
            lotModel.lotName,
            lotModel.customerName,
            lotModel.notes,
            lotModel.date
        )
    }

    override suspend fun updateLotWithNewRationId(rationId: String, lotId: String) {
        lotDao.updateLotWithRationId(rationId, lotId)
    }

    override suspend fun deleteLot(lotModel: LotModel) {
        lotDao.deleteLot(lotModel.toLotEntity())
    }

    override suspend fun deleteLotListById(lotModelIdList: List<String>) {
        lotDao.deleteLotList(lotModelIdList)
    }

    override suspend fun deleteAllLots() {
        lotDao.deleteAllLots()
    }

    override suspend fun syncCloudLots(lotList: List<LotModel>) {
        lotDao.syncCloudLots(lotList.map { it.toLotEntity() })
    }

    override suspend fun syncCloudLotsByLotId(lotList: List<LotModel>, lotId: String) {
        lotDao.syncCloudLotsByLotId(lotList.map { it.toLotEntity() }, lotId)
    }

    override suspend fun createCacheLot(cacheLotModel: CacheLotModel) {
        cacheLotDao.insertCacheLot(cacheLotModel.toCacheLotEntity())
    }

    override suspend fun getCacheLots(): List<CacheLotModel> {
        return cacheLotDao.getCacheLots().map { it.toCacheLotModel() }
    }

    override suspend fun deleteCacheLots() {
        cacheLotDao.deleteCacheLots()
    }
}