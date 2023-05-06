package com.trevorwiebe.trackacow.domain.use_cases.call_use_cases

import com.google.firebase.database.FirebaseDatabase
import com.trevorwiebe.trackacow.data.mapper.toCallAndRationModel
import com.trevorwiebe.trackacow.domain.models.call.CallModel
import com.trevorwiebe.trackacow.domain.models.compound_model.CallAndRationModel
import com.trevorwiebe.trackacow.domain.models.ration.RationModel
import com.trevorwiebe.trackacow.domain.repository.local.CallRepository
import com.trevorwiebe.trackacow.domain.repository.local.RationsRepository
import com.trevorwiebe.trackacow.domain.utils.combineQueryDatabaseNodes
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onStart

data class ReadCallsAndRationsByLotIdUC(
    private val rationRepository: RationsRepository,
    private val callRepository: CallRepository,
    private val firebaseDatabase: FirebaseDatabase,
    private val callDatabaseString: String,
    private val rationDatabaseString: String
){
    @OptIn(FlowPreview::class)
    operator fun invoke(lotId: String): Flow<List<CallAndRationModel>> {
        val localCallAndRationFlow = callRepository.getCallsAndRationByLotId(lotId)

        val callQuery = firebaseDatabase
            .getReference(callDatabaseString)
            .orderByChild("lotId")
            .equalTo(lotId)
        val rationRef = firebaseDatabase.getReference(rationDatabaseString)

        return localCallAndRationFlow
            .flatMapConcat { localData ->
                combineQueryDatabaseNodes(
                    rationRef,
                    callQuery,
                    RationModel::class.java,
                    CallModel::class.java
                ).flatMapConcat { pair ->
                    rationRepository.insertOrUpdateRationList(pair.first)
                    callRepository.insertOrUpdateCallList(pair.second)
                    flow {
                        val combinedList = combineList(pair.first, pair.second)
                        emit(combinedList)
                    }
                }.onStart { emit(localData) }
            }
    }

    private fun combineList(
        rationList: List<RationModel>,
        callList: List<CallModel>
    ): List<CallAndRationModel> {
        val result = mutableListOf<CallAndRationModel>()
        callList.forEach { callModel ->
            val ration = rationList.find { it.rationPrimaryKey == callModel.callRationId }
            if (ration != null) {
                val callAndRationModel = callModel.toCallAndRationModel(ration)
                result.add(callAndRationModel)
            }
        }
        return result
    }
}
