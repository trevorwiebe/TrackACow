package com.trevorwiebe.trackacow.domain.repository.local

import com.trevorwiebe.trackacow.domain.models.call.CallModel
import com.trevorwiebe.trackacow.domain.models.call.CacheCallModel
import com.trevorwiebe.trackacow.domain.models.compound_model.CallAndRationModel
import kotlinx.coroutines.flow.Flow

interface CallRepository {

    suspend fun insertCall(callModel: CallModel): Long

    fun getCalls(): Flow<List<CallModel>>

    fun getCallByLotIdAndDate(
            lotId: String,
            dateStart: Long,
            dateEnd: Long
    ): Flow<CallAndRationModel?>

    fun getCallsAndRationByLotId(lotId: String): Flow<List<CallAndRationModel>>

    suspend fun updateCall(callModel: CallModel)

    suspend fun deleteCall(callModel: CallModel)

    suspend fun deleteAllCalls()

    suspend fun insertCacheCall(cacheCallModel: CacheCallModel)

    suspend fun insertOrUpdateCallList(callList: List<CallModel>)
}