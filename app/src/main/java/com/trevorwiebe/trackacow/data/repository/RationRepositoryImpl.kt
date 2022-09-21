package com.trevorwiebe.trackacow.data.repository

import com.trevorwiebe.trackacow.data.db.dao.RationDao
import com.trevorwiebe.trackacow.data.mapper.toRationEntity
import com.trevorwiebe.trackacow.data.mapper.toRationModel
import com.trevorwiebe.trackacow.domain.models.RationModel
import com.trevorwiebe.trackacow.domain.repository.RationsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class RationRepositoryImpl(
    private val rationDao: RationDao
): RationsRepository {

    override suspend fun insertRation(rationModel: RationModel) {
        rationDao.insertRation(rationModel.toRationEntity())
    }

    override fun getRations(): Flow<List<RationModel>> {
        return rationDao.getRations()
            .map { rationEntities ->
                rationEntities.map { it.toRationModel() }
            }
    }

    override suspend fun updateRations(rationModel: RationModel) {
        rationDao.updateRation(rationModel.toRationEntity())
    }

    override suspend fun deleteRation(rationModel: RationModel) {
        rationDao.deleteRation(rationModel.toRationEntity())
    }
}