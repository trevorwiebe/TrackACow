package com.trevorwiebe.trackacow.data.local.repository

import com.trevorwiebe.trackacow.data.local.dao.CallDao
import com.trevorwiebe.trackacow.data.local.cacheDao.CacheCallDao
import com.trevorwiebe.trackacow.data.mapper.compound_mapper.toCallAndRationModel
import com.trevorwiebe.trackacow.data.mapper.toCallEntity
import com.trevorwiebe.trackacow.data.mapper.toCallModel
import com.trevorwiebe.trackacow.data.mapper.toHoldingCallEntity
import com.trevorwiebe.trackacow.domain.models.call.CallModel
import com.trevorwiebe.trackacow.domain.models.call.CacheCallModel
import com.trevorwiebe.trackacow.domain.models.compound_model.CallAndRationModel
import com.trevorwiebe.trackacow.domain.repository.local.CallRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class CallRepositoryImpl(
    private val callDao: CallDao,
    private val cacheCallDao: CacheCallDao
): CallRepository {

    override suspend fun insertCall(callModel: CallModel): Long {
        return callDao.insertCall(callModel.toCallEntity())
    }

    override fun getCalls(): Flow<List<CallModel>> {
        return callDao.getCalls().map { callEntityList ->
            callEntityList.map {
                it.toCallModel()
            }
        }
    }

    override fun getCallByLotIdAndDate(
        lotId: String,
        dateStart: Long,
        dateEnd: Long
    ): Flow<CallAndRationModel?> {
        return callDao.getCallAndRationByDateAndLotId(lotId, dateStart, dateEnd)
            .map {
                it?.toCallAndRationModel()
            }
    }

    override fun getCallsAndRationByLotId(lotId: String): Flow<List<CallAndRationModel>> {
        return callDao.getCallsAndRationByLotId(lotId)
            .map { callAndRationList ->
                callAndRationList.map { it.toCallAndRationModel() }
            }
    }


    override suspend fun updateCall(callModel: CallModel) {
        val callId: String? = callModel.callCloudDatabaseId
        if (callId.isNullOrEmpty()) return
        callDao.updateCallAmount(callModel.callAmount, callModel.callRationId, callId)
    }

    override suspend fun updateCallsWithNewLot(
        savedLotModelId: String,
        deleteLotIdList: List<String>
    ) {
        callDao.updateCallsWithNewLotId(savedLotModelId, deleteLotIdList)
    }

    override suspend fun deleteCall(callModel: CallModel) {
        callDao.deleteCall(callModel.toCallEntity())
    }

    override suspend fun deleteAllCalls() {
        callDao.deleteCallTable()
    }

    override suspend fun insertCacheCall(cacheCallModel: CacheCallModel) {
        cacheCallDao.insertHoldingCall(cacheCallModel.toHoldingCallEntity())
    }

    override suspend fun insertOrUpdateCallList(callList: List<CallModel>) {
        callDao.insertOrUpdate(callList.map { it.toCallEntity() })
    }
}