package com.trevorwiebe.trackacow.data.local.repository

import com.trevorwiebe.trackacow.data.local.cacheDao.CachePenDao
import com.trevorwiebe.trackacow.data.local.dao.PenDao
import com.trevorwiebe.trackacow.data.mapper.compound_mapper.toPenAndLotModel
import com.trevorwiebe.trackacow.data.mapper.toCachePenEntity
import com.trevorwiebe.trackacow.data.mapper.toPenEntity
import com.trevorwiebe.trackacow.data.mapper.toPenModel
import com.trevorwiebe.trackacow.domain.models.compound_model.PenAndLotModel
import com.trevorwiebe.trackacow.domain.models.pen.CachePenModel
import com.trevorwiebe.trackacow.domain.models.pen.PenModel
import com.trevorwiebe.trackacow.domain.repository.local.PenRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class PenRepositoryImpl(
    val penDao: PenDao,
    private val cachePenDao: CachePenDao
): PenRepository {

    override suspend fun insertPen(penModel: PenModel): Long {
        return penDao.insertPen(penModel.toPenEntity())
    }

    override suspend fun insertCachePen(cachePenModel: CachePenModel) {
        cachePenDao.insertCachePen(cachePenModel.toCachePenEntity())
    }

    override fun readPens(): Flow<List<PenModel>> {
        return penDao.getPenList().map { returnedPenList ->
            returnedPenList.map {
                it.toPenModel()
            }
        }
    }

    override fun readPensAndLotsIncludeEmptyPens(): Flow<List<PenAndLotModel>> {
        return penDao.getPenAndLotListIncludeEmptyPens().map { penAndLotList ->
            penAndLotList.map { it.toPenAndLotModel() }
        }
    }

    override fun readPensAndLotsExcludeEmptyPens(): Flow<List<PenAndLotModel>> {
        return penDao.getPenAndLotListExcludeEmptyPens().map { penAndLotList ->
            penAndLotList.map { it.toPenAndLotModel() }
        }
    }

    override fun readPensByPenId(penId: String): Flow<PenModel?> {
        return penDao.getPenById(penId).map {
            it?.toPenModel()
        }
    }

    override suspend fun deletePen(penModel: PenModel) {
        penDao.deletePen(penModel.toPenEntity())
    }

    override suspend fun updatePen(penModel: PenModel) {
        penDao.updatePen(penModel.penName, penModel.penPrimaryKey)
    }
}